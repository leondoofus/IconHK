/*
 * HomeAppletController.java 11 Oct. 2008
 *
 * Sweet Home 3D, Copyright (c) 2008 Emmanuel PUYBARET / eTeks <info@eteks.com>
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeApplication;
import com.eteks.sweethome3d.model.InterruptedRecorderException;
import com.eteks.sweethome3d.model.RecorderException;
import com.eteks.sweethome3d.plugin.HomePluginController;
import com.eteks.sweethome3d.plugin.PluginManager;
import com.eteks.sweethome3d.swing.FileContentManager;
import com.eteks.sweethome3d.swing.SwingTools;
import com.eteks.sweethome3d.viewcontroller.ContentManager;
import com.eteks.sweethome3d.viewcontroller.HomeView;
import com.eteks.sweethome3d.viewcontroller.ThreadedTaskController;
import com.eteks.sweethome3d.viewcontroller.UserPreferencesController;
import com.eteks.sweethome3d.viewcontroller.ViewFactory;

/**
 * Home applet pane controller.
 * @author Emmanuel Puybaret
 */
public class HomeAppletController extends HomePluginController {
  private final Home home;
  private final HomeApplication application;
  private final ViewFactory viewFactory;
  private final ContentManager contentManager;

  public HomeAppletController(Home home, 
                              HomeApplication application, 
                              ViewFactory viewFactory,
                              ContentManager  contentManager,
                              PluginManager   pluginManager,
                              boolean newHomeEnabled, 
                              boolean openEnabled, 
                              boolean saveEnabled, 
                              boolean saveAsEnabled) {
    super(home, application, viewFactory, contentManager, pluginManager);
    this.home = home;
    this.application = application;
    this.viewFactory = viewFactory;
    this.contentManager = contentManager;
    
    HomeView view = (HomeView)getView();
    view.setEnabled(HomeView.ActionType.EXIT, false);
    view.setEnabled(HomeView.ActionType.NEW_HOME, newHomeEnabled);
    view.setEnabled(HomeView.ActionType.OPEN, openEnabled);
    view.setEnabled(HomeView.ActionType.SAVE, saveEnabled);
    view.setEnabled(HomeView.ActionType.SAVE_AS, saveAsEnabled);
    
    // By default disabled Print to PDF, Export to SVG, Export to OBJ and Create photo actions 
    view.setEnabled(HomeView.ActionType.PRINT_TO_PDF, false);
    view.setEnabled(HomeView.ActionType.EXPORT_TO_SVG, false);
    view.setEnabled(HomeView.ActionType.EXPORT_TO_OBJ, false);
    view.setEnabled(HomeView.ActionType.CREATE_PHOTO, false);
    
    view.setEnabled(HomeView.ActionType.DETACH_3D_VIEW, false);
  }
  
  /**
   * Creates a new home after saving and deleting the current home.
   */
  @Override
  public void newHome() {
    close(new Runnable() {
        public void run() {
          HomeAppletController.super.newHome();
        }
      });
  }

  /**
   * Opens a home after saving and deleting the current home.
   */
  @Override
  public void open() {
    close(new Runnable() {
      public void run() {
        HomeAppletController.super.open();
      }
    });
  }
  
  /**
   * Displays Sweet Home user guide in a navigator window.
   */
  @Override
  public void help() {
    try { 
      String helpIndex = this.application.getUserPreferences().getLocalizedString(HomeAppletController.class, "helpIndex");
      SwingTools.showDocumentInBrowser(new URL(helpIndex)); 
    } catch (MalformedURLException ex) {
      ex.printStackTrace();
    } 
  }

  /**
   * Controls the export of home to a SH3D file.
   */
  public void exportToSH3D() {
    final String sh3dName = new FileContentManager(this.application.getUserPreferences()).showSaveDialog(getView(),
        this.application.getUserPreferences().getLocalizedString(HomeAppletController.class, "exportToSH3DDialog.title"), 
        ContentManager.ContentType.SWEET_HOME_3D, home.getName());    
    if (sh3dName != null) {
      // Export home in a threaded task
      Callable<Void> exportToObjTask = new Callable<Void>() {
            public Void call() throws RecorderException {
              new HomeFileRecorder(9).writeHome(home, sh3dName);
              return null;
            }
          };
      ThreadedTaskController.ExceptionHandler exceptionHandler = 
          new ThreadedTaskController.ExceptionHandler() {
            public void handleException(Exception ex) {
              if (!(ex instanceof InterruptedRecorderException)) {
                if (ex instanceof RecorderException) {
                  String message = application.getUserPreferences().getLocalizedString(
                      HomeAppletController.class, "exportToSH3DError", sh3dName);
                  getView().showError(message);
                } else {
                  ex.printStackTrace();
                }
              }
            }
          };
      new ThreadedTaskController(exportToObjTask, 
          this.application.getUserPreferences().getLocalizedString(HomeAppletController.class, "exportToSH3DMessage"), exceptionHandler, 
          this.application.getUserPreferences(), viewFactory).executeTask(getView());
    }
  }
  
  @Override
  public void editPreferences() {
    new UserPreferencesController(this.application.getUserPreferences(), 
        this.viewFactory, this.contentManager) {
      public boolean isPropertyEditable(UserPreferencesController.Property property) {
        switch (property) {
          case CHECK_UPDATES_ENABLED :
          case AUTO_SAVE_DELAY_FOR_RECOVERY :
          case AUTO_SAVE_FOR_RECOVERY_ENABLED :
            // No check updates and auto recovery with applet
            return false;
          default :
            return super.isPropertyEditable(property);
        }
      }
    }.displayView(getView());
  }
}

