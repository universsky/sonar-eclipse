package org.sonar.ide.eclipse;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.sonar.ide.eclipse.console.SonarConsole;

/**
 * @author Jérémie Lagarde
 */
public class SonarPlugin extends AbstractUIPlugin {

  // The plug-in ID
  public static final String        PLUGIN_ID        = "org.sonar.ide.eclipse";

  // Marker
  public static final String        MARKER_ID        = PLUGIN_ID + ".sonarMarker"; //$NON-NLS-1$

  // Images
  private static ImageDescriptor    SONARWIZBAN_IMG;
  private static ImageDescriptor    SONARCONSOLE_IMG;
  private static ImageDescriptor    SONARSYNCHRO_IMG;
  private static ImageDescriptor    SONARCLOSE_IMG;

  public static final String        IMG_SONARWIZBAN  = "sonar_wizban.gif";        //$NON-NLS-1$
  public static final String        IMG_SONARCONSOLE = "sonar.gif";               //$NON-NLS-1$
  public static final String        IMG_SONARSYNCHRO = "synced.gif";              //$NON-NLS-1$
  public static final String        IMG_SONARCLOSE   = "close.gif";              //$NON-NLS-1$

  // The shared instance
  private static SonarPlugin        plugin;

  private static SonarServerManager serverManager;

  private SonarConsole              console;

  public SonarPlugin() {
  }

  public static SonarServerManager getServerManager() {
    if (serverManager == null) {
      serverManager = new SonarServerManager();
    }
    return serverManager;
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
    try {
      console = new SonarConsole();
    } catch (RuntimeException e) {
      writeLog(IStatus.ERROR, "Errors occurred starting the Sonar console", e); //$NON-NLS-1$
    }
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
    console.shutdown();
  }

  /**
   * @return the shared instance
   */
  public static SonarPlugin getDefault() {
    return plugin;
  }

  public SonarConsole getConsole() {
    return this.console;
  }

  private IStatus createStatus(int severity, String msg, Throwable t) {
    return new Status(severity, PLUGIN_ID, msg, t);
  }

  public void writeLog(int severity, String msg, Throwable t) {
    super.getLog().log(createStatus(severity, msg, t));
  }

  public void writeLog(IStatus status) {
    super.getLog().log(status);
  }

  public void displayMessage(final int severity, final String msg) {
    final Display display = PlatformUI.getWorkbench().getDisplay();
    display.syncExec(new Runnable() {
      public void run() {
        switch (severity) {
          case IStatus.ERROR:
            MessageDialog.openError(display.getActiveShell(), Messages.getString("error"), msg); //$NON-NLS-1$
            break;
          case IStatus.WARNING:
            MessageDialog.openWarning(display.getActiveShell(), Messages.getString("warning"), msg); //$NON-NLS-1$
            break;
        }
      }
    });
  }

  public void displayError(int severity, final String msg, Throwable t, boolean shouldLog) {
    final IStatus status = createStatus(severity, msg, t);
    if (shouldLog) {
      writeLog(status);
    }
    final Display display = PlatformUI.getWorkbench().getDisplay();
    display.syncExec(new Runnable() {
      public void run() {
        ErrorDialog.openError(display.getActiveShell(), null, Messages.getString("error"), status); //$NON-NLS-1$
      }
    });
  }

  public static ImageDescriptor getImageDescriptor(String id) {
    ImageDescriptor img = getCachedImageDescriptor(id);
    if (img == null) {
      img = loadImageDescriptor(id);
    }
    return img;
  }

  private static ImageDescriptor loadImageDescriptor(String id) {
    String iconPath = "icons/"; //$NON-NLS-1$

    try {
      URL installURL = SonarPlugin.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
      URL url = new URL(installURL, iconPath + id);
      return ImageDescriptor.createFromURL(url);
    } catch (MalformedURLException e) {
      return ImageDescriptor.getMissingImageDescriptor();
    }
  }

  private static ImageDescriptor getCachedImageDescriptor(String id) {
    ImageDescriptor img = null;
    if (id.equals(IMG_SONARWIZBAN)) {
      if (SONARWIZBAN_IMG == null) {
        SONARWIZBAN_IMG = loadImageDescriptor(IMG_SONARWIZBAN);
      }
      img = SONARWIZBAN_IMG;
    }
    if (id.equals(IMG_SONARCONSOLE)) {
      if (SONARCONSOLE_IMG == null) {
        SONARCONSOLE_IMG = loadImageDescriptor(IMG_SONARCONSOLE);
      }
      img = SONARCONSOLE_IMG;
    }
    if (id.equals(IMG_SONARSYNCHRO)) {
      if (SONARSYNCHRO_IMG == null) {
        SONARSYNCHRO_IMG = loadImageDescriptor(IMG_SONARSYNCHRO);
      }
      img = SONARSYNCHRO_IMG;
    }
    if (id.equals(IMG_SONARCLOSE)) {
      if (SONARCLOSE_IMG == null) {
        SONARCLOSE_IMG = loadImageDescriptor(IMG_SONARCLOSE);
      }
      img = SONARCLOSE_IMG;
    }
    return img;
  }

}
