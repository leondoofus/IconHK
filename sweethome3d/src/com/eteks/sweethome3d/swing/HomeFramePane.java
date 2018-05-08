/*
 * HomeFramePane.java 1 sept. 2006
 *
 * Sweet Home 3D, Copyright (c) 2006 Emmanuel PUYBARET / eTeks <info@eteks.com>
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
package com.eteks.sweethome3d.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.Timer;

import com.eteks.sweethome3d.HomeFrameController;
import com.eteks.sweethome3d.model.CollectionEvent;
import com.eteks.sweethome3d.model.CollectionListener;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeApplication;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.tools.OperatingSystem;
import com.eteks.sweethome3d.viewcontroller.ContentManager;
import com.eteks.sweethome3d.viewcontroller.HomeFrameView;
import com.eteks.sweethome3d.viewcontroller.HomeView;

/**
 * A pane that displays a 
 * {@link com.eteks.sweethome3d.swing.HomePane home pane} in a frame.
 * @author Emmanuel Puybaret
 */
public class HomeFramePane extends HomeFrameView {
  private static final String FRAME_X_VISUAL_PROPERTY         = "com.eteks.sweethome3d.SweetHome3D.FrameX";
  private static final String FRAME_Y_VISUAL_PROPERTY         = "com.eteks.sweethome3d.SweetHome3D.FrameY";
  private static final String FRAME_WIDTH_VISUAL_PROPERTY     = "com.eteks.sweethome3d.SweetHome3D.FrameWidth";
  private static final String FRAME_HEIGHT_VISUAL_PROPERTY    = "com.eteks.sweethome3d.SweetHome3D.FrameHeight";
  private static final String FRAME_MAXIMIZED_VISUAL_PROPERTY = "com.eteks.sweethome3d.SweetHome3D.FrameMaximized";
  private static final String SCREEN_WIDTH_VISUAL_PROPERTY    = "com.eteks.sweethome3d.SweetHome3D.ScreenWidth";
  private static final String SCREEN_HEIGHT_VISUAL_PROPERTY   = "com.eteks.sweethome3d.SweetHome3D.ScreenHeight";
  
  JRootPane                             rootPane;
  JFrame                                homeFrame;
  
  public HomeFramePane(Home home,
                       HomeApplication application,
                       ContentManager contentManager, 
                       HomeFrameController homeFrameController) {
    super(home, application, contentManager, homeFrameController);
    // Set controller view as content pane
    HomeView homeView = this.controller.getHomeController().getView();
    rootPane = new JRootPane();
    rootPane.setContentPane((JComponent)homeView);
  }

  @Override
  protected void showHomeFrame() {
    homeFrame.setVisible(true);
  }

  @Override
  protected void createHomeFrame() {
    homeFrame = new JFrame() {
      {
        // Replace frame rootPane by home controller view
        setRootPane(HomeFramePane.this.rootPane);
      }
    };
  }

  /**
   * Enable windows to update their content while window resizing
   */
  @Override
  protected void enableAutoResize() {
    rootPane.getToolkit().setDynamicLayout(true);
  }

  /**
   * Set text orientation
   * needed to support Bi-directional text
   */
  @Override
  protected void setOrientation() {
    rootPane.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
  }

  @Override
  protected void setMenuMacOs() {
    // The best MVC solution should be to avoid the following statements 
    // but Mac OS X accepts to display the menu bar of a frame in the screen 
    // menu bar only if this menu bar depends directly on its root pane  
    HomeView homeView = this.controller.getHomeController().getView();
    if (homeView instanceof JRootPane) {
      JRootPane homePane = (JRootPane)homeView;
      rootPane.setJMenuBar(homePane.getJMenuBar());
      homePane.setJMenuBar(null);
    }
  }

  @Override
  protected void setFrameImages(String [] resourceNames) {
    Image [] frameImages = new Image[resourceNames.length];
    for (int i=0; i<resourceNames.length; ++i) {
      frameImages[i] = new ImageIcon(HomeFrameView.class.getResource(resourceNames[i])).getImage();
    }
    try {
      // Call Java 1.6 setIconImages by reflection
      homeFrame.getClass().getMethod("setIconImages", List.class)
          .invoke(homeFrame, Arrays.asList(frameImages));
    } catch (Exception ex) {
      // Call setIconImage available in previous versions
      homeFrame.setIconImage(frameImages [0]);
    }
  }
  
  /**
   * Adds listeners to <code>frame</code> and model objects.
   */
  @Override
  protected void addListeners() {
    // Add a listener that keeps track of window location and size
    homeFrame.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent ev) {
          // Store new size only if frame isn't maximized
          if ((homeFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != JFrame.MAXIMIZED_BOTH) {
            controller.getHomeController().setVisualProperty(FRAME_WIDTH_VISUAL_PROPERTY, homeFrame.getWidth());
            controller.getHomeController().setVisualProperty(FRAME_HEIGHT_VISUAL_PROPERTY, homeFrame.getHeight());
          }

          Dimension userScreenSize = getUserScreenSize();
          controller.getHomeController().setVisualProperty(SCREEN_WIDTH_VISUAL_PROPERTY, userScreenSize.width);
          controller.getHomeController().setVisualProperty(SCREEN_HEIGHT_VISUAL_PROPERTY, userScreenSize.height);
        }
        
        @Override
        public void componentMoved(ComponentEvent ev) {
          // Store new location only if frame isn't maximized
          if ((homeFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != JFrame.MAXIMIZED_BOTH) {
            controller.getHomeController().setVisualProperty(FRAME_X_VISUAL_PROPERTY, homeFrame.getX());
            controller.getHomeController().setVisualProperty(FRAME_Y_VISUAL_PROPERTY, homeFrame.getY());
          }
        }
      });
    // Control frame closing and activation 
    homeFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    WindowAdapter windowListener = new WindowAdapter () {
        private Component mostRecentFocusOwner;

        @Override
        public void windowStateChanged(WindowEvent ev) {
          controller.getHomeController().setVisualProperty(FRAME_MAXIMIZED_VISUAL_PROPERTY, 
              (homeFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH);
        }
        
        @Override
        public void windowClosing(WindowEvent ev) {
          controller.getHomeController().close();
        }
        
        @Override
        public void windowDeactivated(WindowEvent ev) {
          // Java 3D 1.5 bug : windowDeactivated notifications should not be sent to this frame
          // while canvases 3D are created in a child modal dialog like the one managing 
          // ImportedFurnitureWizardStepsPanel. As this makes Swing loose the most recent focus owner
          // let's store it in a field to use it when this frame will be reactivated. 
          Component mostRecentFocusOwner = homeFrame.getMostRecentFocusOwner();          
          if (!(mostRecentFocusOwner instanceof JFrame)
              && mostRecentFocusOwner != null) {
            this.mostRecentFocusOwner = mostRecentFocusOwner;
          }
        }

        @Override
        public void windowActivated(WindowEvent ev) {                    
          // Java 3D 1.5 bug : let's request focus in window for the most recent focus owner when
          // this frame is reactivated
          if (this.mostRecentFocusOwner != null) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                  mostRecentFocusOwner.requestFocusInWindow();
                }
              });
          }
        } 
      };
    homeFrame.addWindowListener(windowListener);
    homeFrame.addWindowStateListener(windowListener);
    // Add a listener to preferences to apply component orientation to frame matching current language
    application.getUserPreferences().addPropertyChangeListener(UserPreferences.Property.LANGUAGE, 
        new LanguageChangeListener(homeFrame, this));
    // Dispose window when a home is deleted 
    application.addHomesListener(new CollectionListener<Home>() {
        public void collectionChanged(CollectionEvent<Home> ev) {
          if (ev.getItem() == home
              && ev.getType() == CollectionEvent.Type.DELETE) {
            application.removeHomesListener(this);
            homeFrame.dispose();
          }
        };
      });
    // Update title when the name or the modified state of home changes
    PropertyChangeListener frameTitleChangeListener = new PropertyChangeListener () {
        public void propertyChange(PropertyChangeEvent ev) {
          updateFrameTitle();
        }
      };
    home.addPropertyChangeListener(Home.Property.NAME, frameTitleChangeListener);
    home.addPropertyChangeListener(Home.Property.MODIFIED, frameTitleChangeListener);
    home.addPropertyChangeListener(Home.Property.RECOVERED, frameTitleChangeListener);
  }

  /**
   * Preferences property listener bound to this component with a weak reference to avoid
   * strong link between preferences and this component.  
   */
  private static class LanguageChangeListener implements PropertyChangeListener {
    private WeakReference<JFrame>        frame;
    private WeakReference<HomeFramePane> homeFramePane;

    public LanguageChangeListener(JFrame frame, HomeFramePane homeFramePane) {
      this.frame = new WeakReference<JFrame>(frame);
      this.homeFramePane = new WeakReference<HomeFramePane>(homeFramePane);
    }
    
    public void propertyChange(PropertyChangeEvent ev) {
      // If frame was garbage collected, remove this listener from preferences
      HomeFramePane homeFramePane = this.homeFramePane.get();
      UserPreferences preferences = (UserPreferences)ev.getSource();
      if (homeFramePane == null) {
        preferences.removePropertyChangeListener(
            UserPreferences.Property.LANGUAGE, this);
      } else {
        this.frame.get().applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        homeFramePane.updateFrameTitle();
      }
    }
  }
  
  /**
   * Computes <code>frame</code> size and location to fit into screen.
   */
  @Override
  protected void computeFrameBounds() {
    Integer x = (Integer)home.getVisualProperty(FRAME_X_VISUAL_PROPERTY);
    Integer y = (Integer)home.getVisualProperty(FRAME_Y_VISUAL_PROPERTY);
    Integer width = (Integer)home.getVisualProperty(FRAME_WIDTH_VISUAL_PROPERTY);
    Integer height = (Integer)home.getVisualProperty(FRAME_HEIGHT_VISUAL_PROPERTY);
    Boolean maximized = (Boolean)home.getVisualProperty(FRAME_MAXIMIZED_VISUAL_PROPERTY);
    Integer screenWidth = (Integer)home.getVisualProperty(SCREEN_WIDTH_VISUAL_PROPERTY);
    Integer screenHeight = (Integer)home.getVisualProperty(SCREEN_HEIGHT_VISUAL_PROPERTY);
    
    Dimension screenSize = getUserScreenSize();
    // If home frame bounds exist and screen resolution didn't reduce 
    if (x != null && y != null 
        && width != null && height != null 
        && screenWidth != null && screenHeight != null
        && screenWidth <= screenSize.width
        && screenHeight <= screenSize.height) {
      final Rectangle frameBounds = new Rectangle(x, y, width, height);
      if (maximized != null && maximized) {
        // Display first the frame at its maximum size to keep splitters location 
        Insets insets = homeFrame.getInsets();
        homeFrame.setSize(screenSize.width + insets.left + insets.right,
            screenSize.height + insets.bottom);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
              // Resize to home non maximized bounds
              homeFrame.setBounds(frameBounds);
              // Finally maximize
              if (OperatingSystem.isMacOSXLeopardOrSuperior()) {
                new Timer(200, new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                      // Maximize later otherwise it won't be taken into account
                      ((Timer)ev.getSource()).stop();
                      homeFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    }
                  }).start();
              } else {
                homeFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
              }
            }
          });
      } else {
        // Reuse home bounds
        homeFrame.setBounds(frameBounds);
        homeFrame.setLocationByPlatform(!SwingTools.isRectangleVisibleAtScreen(frameBounds));
      }
    } else {      
      homeFrame.setLocationByPlatform(true);
      homeFrame.pack();
      homeFrame.setSize(Math.min(screenSize.width * 4 / 5, homeFrame.getWidth()), 
              Math.min(screenSize.height * 4 / 5, homeFrame.getHeight()));
    }
  }

  /**
   * Returns the screen size available to user. 
   */
  private Dimension getUserScreenSize() {
    Dimension screenSize = rootPane.getToolkit().getScreenSize();
    Insets screenInsets = rootPane.getToolkit().getScreenInsets(rootPane.getGraphicsConfiguration());
    screenSize.width -= screenInsets.left + screenInsets.right;
    screenSize.height -= screenInsets.top + screenInsets.bottom;
    return screenSize;
  }
  
  protected void updateMacOsTitle(String homeName) {
    // Use black indicator in close icon for a modified home 
    Boolean homeModified = Boolean.valueOf(home.isModified() || home.isRecovered());
    // Set Mac OS X 10.4 property for backward compatibility
    rootPane.putClientProperty("windowModified", homeModified);
    
    if (OperatingSystem.isMacOSXLeopardOrSuperior()) {
      rootPane.putClientProperty("Window.documentModified", homeModified);
      
      if (homeName != null) {        
        File homeFile = new File(homeName);
        if (homeFile.exists()) {
          // Update the home icon in window title bar for home files
          rootPane.putClientProperty("Window.documentFile", homeFile);
        }
      }
    }

    if (!homeFrame.isVisible() 
        && OperatingSystem.isMacOSXLionOrSuperior()) {
      try {
        // Call Mac OS X specific FullScreenUtilities.setWindowCanFullScreen(homeFrame, true) by reflection 
        Class.forName("com.apple.eawt.FullScreenUtilities").
            getMethod("setWindowCanFullScreen", new Class<?> [] {Window.class, boolean.class}).
            invoke(null, homeFrame, true);
      } catch (Exception ex) {
        // Full screen mode is not supported
      }
    }
  }

  public Object getObject() {
    return homeFrame;
  }

  @Override
  protected void setTitle(String title) {
    homeFrame.setTitle(title);
  }
}
