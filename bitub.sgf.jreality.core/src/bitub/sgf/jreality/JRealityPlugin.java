/*
 * Copyright (c) 2012-2015 Bernold Kraft (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Initial commit by bernold @ 27.06.2012.
 */
package bitub.sgf.jreality;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bitub.sgf.jreality.views.JRealityBaseViewPart;
import bitub.sgf.jreality.views.JRealityViewPart;
import de.jreality.shader.Color;

/**
 * <!-- begin-user-doc -->
 * JReality plugin activator.
 * <!-- end-user-doc -->
 * 
 * @generated NOT
 * @author bernold - 27.06.2012
 */
public class JRealityPlugin extends AbstractUIPlugin
{

  /**
   * The plug-in ID.
   */
  public static final String PLUGIN_ID = "bitub.sgf.jreality.core"; //$NON-NLS-1$

  /**
   * Default background color. If more than a single color are given, a gradient
   * will be shown.
   */
  public static Color[] DEFAULT_BACKGROUND = new Color[] { new Color(255, 255, 255) };

  /**
   * jReality standard backgradient.
   */
  public static Color[] EXAMPLE_BACKGRADIENT =
      new Color[] { new Color(225, 225, 225), new Color(225, 225, 225), new Color(255, 225, 180), new Color(255, 225, 180) 
  };

  public enum ViewCoordinationSystem
  {

    None, Axis, BoxedAxis, Grid
  }

  public static ViewCoordinationSystem DEFAULT_COORDINATESYSTEM = ViewCoordinationSystem.Axis;

  /**
   * The logger instance.
   */
  static final Logger myLog = Logger.getLogger(JRealityPlugin.class);

  // The shared instance
  private static JRealityPlugin m_plugin;

  /**
   * The constructor
   */
  public JRealityPlugin()
  {
  }

  /**
   * Initializes the logging settings from log4j.properties.
   * 
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception
  {
    // Super start
    super.start(context);

    // Remember the static instance.
    //
    m_plugin = this;
    doLoggerConfig(this, "log4j.properties");
  }

  @Override
  public void stop(BundleContext context) throws Exception
  {
    // Clear reference      
    m_plugin = null;

    // Super stop
    super.stop(context);
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static JRealityPlugin getDefault()
  {
    return m_plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in
   * relative path
   * 
   * @param path the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path)
  {
    return imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  /**
   * <!-- begin-user-doc -->
   * Gets or opens jReality's default view part in running workbench.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @return The default view part.
   */
  public JRealityViewPart getOrOpenDefaultView()
  {
    if (PlatformUI.isWorkbenchRunning()) {

      IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      JRealityViewPart viewPart = null;
      try {

        IViewPart shownView = activePage.showView(JRealityBaseViewPart.VIEW_ID);
        viewPart = (JRealityViewPart) shownView;
      }
      catch (PartInitException e) {

        myLog.error(e);
      }
      return viewPart;
    }

    throw new IllegalStateException("Platform UI not running.");
  }

  /**
   * <!-- begin-user-doc -->
   * Attempts to load a configuration file for LOG4j. Modifies an appender
   * file <em>log4j.appender.rolling.File</em> if it exists to be placed inside
   * {@link Plugin#getStateLocation()} container.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @return
   */
  public static boolean doLoggerConfig(Plugin p, String embeddedLogPropertiesFile)
  {
    // Read log4j properties
    //
    try {

      InputStream inputStream = FileLocator.openStream(m_plugin.getBundle(), new Path(embeddedLogPropertiesFile), false); 
      Properties log4jProperties = new Properties();

      log4jProperties.load(inputStream);

      String propertyFile = log4jProperties.getProperty("log4j.appender.rolling.File"); //$NON-NLS-1$
      if (null != propertyFile) {

        String newLogFile = m_plugin.getStateLocation().append(propertyFile).toString();
        log4jProperties.setProperty("log4j.appender.rolling.File", newLogFile); //$NON-NLS-1$
      }

      // Configure by properties
      //
      PropertyConfigurator.configure(log4jProperties);
    }
    catch (Throwable e) {

      // Check if there's a console appender yet
      //
      Enumeration<?> allAppenders = myLog.getAllAppenders();
      boolean hasConsoleAppenderYet = false;
      while (allAppenders.hasMoreElements()) {

        if (allAppenders.nextElement() instanceof ConsoleAppender) {
          hasConsoleAppenderYet = true;
          break;
        }
      }

      if (!hasConsoleAppenderYet) {
        BasicConfigurator.configure();
      }

      p.getLog()
          .log(new Status(IStatus.WARNING, PLUGIN_ID, 
              String.format("File %s not found. Using console output for logging only.", embeddedLogPropertiesFile)));
      return false;
    }    
    
    return true;
  }


}
