/*
 * ViewerHelper.java 31 mars 2009
 *
 * Sweet Home 3D, Copyright (c) 2009 Emmanuel PUYBARET / eTeks <info@eteks.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.eteks.sweethome3d.applet;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.eteks.sweethome3d.io.DefaultHomeInputStream;
import com.eteks.sweethome3d.j3d.Component3DManager;
import com.eteks.sweethome3d.j3d.ModelManager;
import com.eteks.sweethome3d.j3d.TextureManager;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.InterruptedRecorderException;
import com.eteks.sweethome3d.model.Library;
import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.swing.HomeComponent3D;
import com.eteks.sweethome3d.swing.ThreadedTaskPanel;
import com.eteks.sweethome3d.tools.OperatingSystem;
import com.eteks.sweethome3d.viewcontroller.HomeController3D;
import com.eteks.sweethome3d.viewcontroller.ThreadedTaskController;
import com.eteks.sweethome3d.viewcontroller.ThreadedTaskView;
import com.eteks.sweethome3d.viewcontroller.View;
import com.eteks.sweethome3d.viewcontroller.ViewFactory;
import com.eteks.sweethome3d.viewcontroller.ViewFactoryAdapter;

/**
 * Helper for {@link SweetHome3DViewer SweetHome3DViewer}. This class is public 
 * because it's loaded by applet viewer class loader as a start point.
 * @author Emmanuel Puybaret
 */
public final class ViewerHelper {
  private static final String HOME_URL_PARAMETER     = "homeURL";
  private static final String IGNORE_CACHE_PARAMETER = "ignoreCache";
  private static final String NAVIGATION_PANEL       = "navigationPanel";
  
  public ViewerHelper(final JApplet applet) {
    // Create default user preferences with no catalog
    final UserPreferences preferences = new UserPreferences() {
        @Override
        public void addLanguageLibrary(String languageLibraryName) throws RecorderException {
          throw new UnsupportedOperationException();
        }
  
        @Override
        public boolean languageLibraryExists(String languageLibraryName) throws RecorderException {
          throw new UnsupportedOperationException();
        }

        @Override
        public void addFurnitureLibrary(String furnitureLibraryName) throws RecorderException {
          throw new UnsupportedOperationException();
        }
  
        @Override
        public boolean furnitureLibraryExists(String furnitureLibraryName) throws RecorderException {
          throw new UnsupportedOperationException();
        }
  
        @Override
        public boolean texturesLibraryExists(String name) throws RecorderException {
          throw new UnsupportedOperationException();
        }

        @Override
        public void addTexturesLibrary(String name) throws RecorderException {
          throw new UnsupportedOperationException();
        }

        @Override
        public List<Library> getLibraries() {
          throw new UnsupportedOperationException();
        }

        @Override
        public void write() throws RecorderException {
          throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean isNavigationPanelVisible() {
          return "true".equalsIgnoreCase(applet.getParameter(NAVIGATION_PANEL));
        }
      };
    
    // Create a view factory able to instantiate only a 3D view and a threaded task view
    final ViewFactory viewFactory = new ViewFactoryAdapter() {
        public ThreadedTaskView createThreadedTaskView(String taskMessage, UserPreferences preferences,
                                                       ThreadedTaskController controller) {
          return new ThreadedTaskPanel(taskMessage, preferences, controller) {
              private boolean taskRunning;
  
              public void setTaskRunning(boolean taskRunning, View executingView) {
                if (taskRunning && !this.taskRunning) {
                  // Display task panel directly at applet center if it's empty 
                  this.taskRunning = taskRunning;
                  JPanel contentPane = new JPanel(new GridBagLayout());
                  contentPane.add(this, new GridBagConstraints());
                  applet.setContentPane(contentPane);
                  applet.getRootPane().revalidate();
                } 
              }
            };
        }

        public View createView3D(final Home home, UserPreferences preferences, final HomeController3D controller) {
          HomeComponent3D homeComponent3D = new HomeComponent3D(home, preferences, controller);
          // Add tab key to input map to change camera
          InputMap inputMap = homeComponent3D.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
          inputMap.put(KeyStroke.getKeyStroke("SPACE"), "changeCamera");
          ActionMap actionMap = homeComponent3D.getActionMap();
          actionMap.put("changeCamera", new AbstractAction() {
              public void actionPerformed(ActionEvent ev) {
                if (home.getCamera() == home.getTopCamera()) {
                  controller.viewFromObserver();
                } else {
                  controller.viewFromTop();
                }
              }
            });
          return homeComponent3D;
        }
      };

    // Force offscreen in 3D view under Plugin 2 / Java 6 / Mac OS X
    if (OperatingSystem.isMacOSX()            
        && applet.getAppletContext() != null
        && applet.getAppletContext().getClass().getName().startsWith("sun.plugin2.applet.Plugin2Manager")) {
      System.setProperty("com.eteks.sweethome3d.j3d.useOffScreen3DView", "true");
    }

    initLookAndFeel();

    addComponent3DRenderingErrorObserver(applet.getRootPane(), preferences);

    // Retrieve displayed home 
    String homeUrlParameter = applet.getParameter(HOME_URL_PARAMETER);
    if (homeUrlParameter == null) {
      homeUrlParameter = "default.sh3d";
    }
    // Retrieve ignoreCache parameter value
    String ignoreCacheParameter = applet.getParameter(IGNORE_CACHE_PARAMETER);
    final boolean ignoreCache = ignoreCacheParameter != null 
        && "true".equalsIgnoreCase(ignoreCacheParameter);
    try {
      final URL homeUrl = new URL(applet.getDocumentBase(), homeUrlParameter);
      // Read home in a threaded task
      Callable<Void> openTask = new Callable<Void>() {
            public Void call() throws RecorderException {
              // Read home with application recorder
              Home openedHome = readHome(homeUrl, ignoreCache);
              displayHome(applet.getRootPane(), openedHome, preferences, viewFactory);
              return null;
            }
          };
      ThreadedTaskController.ExceptionHandler exceptionHandler = 
          new ThreadedTaskController.ExceptionHandler() {
            public void handleException(Exception ex) {
              if (!(ex instanceof InterruptedRecorderException)) {
                if (ex instanceof RecorderException) {
                  showError(applet.getRootPane(), 
                      preferences.getLocalizedString(ViewerHelper.class, "openError", homeUrl));
                } else {
                  ex.printStackTrace();
                }
              }
            }
          };
      new ThreadedTaskController(openTask, 
          preferences.getLocalizedString(ViewerHelper.class, "openMessage"), exceptionHandler, 
          null, viewFactory).executeTask(null);
    } catch (MalformedURLException ex) {
      showError(applet.getRootPane(), 
          preferences.getLocalizedString(ViewerHelper.class, "openError", homeUrlParameter));
      return;
    } 
  }
  
  /**
   * Clears all the resources used by the applet.
   * This method is called when an applet is destroyed.  
   */
  public void destroy() {
    // Collect deleted objects (seems to be required under Mac OS X when the applet is being reloaded)
    System.gc();
    // Stop managers threads
    TextureManager.getInstance().clear();
    ModelManager.getInstance().clear();
  }
  
  private void initLookAndFeel() {
    try {
      // Apply current system look and feel
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      // Enable applets to update their content while window resizing
      Toolkit.getDefaultToolkit().setDynamicLayout(true);
    } catch (Exception ex) {
      // Too bad keep current look and feel
    }
  }

  /**
   * Sets the rendering error listener bound to Java 3D 
   * to avoid default System exit in case of error during 3D rendering. 
   */
  private void addComponent3DRenderingErrorObserver(final JRootPane rootPane,
                                                    final UserPreferences preferences) {
    // Instead of adding a RenderingErrorListener directly to VirtualUniverse, 
    // we add it through Canvas3DManager, because offscreen rendering needs to check 
    // rendering errors with its own RenderingErrorListener
    Component3DManager.getInstance().setRenderingErrorObserver(
        new Component3DManager.RenderingErrorObserver() {
          public void errorOccured(int errorCode, String errorMessage) {
            System.err.print("Error in Java 3D : " + errorCode + " " + errorMessage);
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                  String message = preferences.getLocalizedString(
                      ViewerHelper.class, "3DErrorMessage");
                  showError(rootPane, message);
                }
              });
          }
        });
  }

  /**
   * Shows the given text in a label.
   */
  private static void showError(final JRootPane rootPane, String text) {
    JLabel label = new JLabel(text, JLabel.CENTER);
    rootPane.setContentPane(label);
    rootPane.revalidate();
  }

  /**
   * Reads a home from its URL.
   */
  private Home readHome(URL homeUrl, boolean ignoreCache) throws RecorderException {
    URLConnection connection = null;
    DefaultHomeInputStream in = null;
    try {
      // Open a home input stream to server 
      connection = homeUrl.openConnection();
      connection.setRequestProperty("Content-Type", "charset=UTF-8");
      connection.setUseCaches(!ignoreCache);
      in = new DefaultHomeInputStream(connection.getInputStream());
      // Read home with HomeInputStream
      Home home = in.readHome();
      return home;
    } catch (InterruptedIOException ex) {
      throw new InterruptedRecorderException("Read " + homeUrl + " interrupted");
    } catch (IOException ex) {
      throw new RecorderException("Can't read home from " + homeUrl, ex);
    } catch (ClassNotFoundException ex) {
      throw new RecorderException("Missing classes to read home from " + homeUrl, ex);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        throw new RecorderException("Can't close stream", ex);
      }
    }
  }
  
  /**
   * Displays the given <code>home</code> in the main pane of <code>rootPane</code>. 
   */
  private void displayHome(final JRootPane rootPane, final Home home, 
                           final UserPreferences preferences, 
                           final ViewFactory viewFactory) {
    EventQueue.invokeLater(new Runnable() {
        public void run() {
          HomeController3D controller = 
              new HomeController3D(home, preferences, viewFactory, null, null);
          rootPane.setContentPane((JComponent)controller.getView());
          rootPane.revalidate();
        }
      });
  }
}
