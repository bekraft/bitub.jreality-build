/*
 * Copyright (c) 2012-2015 Bernold Kraft (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Initial commit by bernold @ 01.03.2013.
 */
package bitub.sgf.jreality.views;

import org.eclipse.swt.widgets.Composite;

import bitub.sgf.jreality.views.viewer.JRealityContentViewer;
import bitub.sgf.jreality.JRealityPlugin;
import bitub.sgf.jreality.JRealityPlugin.ViewCoordinationSystem;
import bitub.sgf.jreality.views.viewer.JRealityBaseContentViewer;

/**
 * <!-- begin-user-doc -->
 * A jReality view part with uses the base content viewer.
 * <!-- end-user-doc -->
 * 
 * @generated NOT
 * @author bernold - 01.03.2013
 */
public class JRealityBaseViewPart extends JRealityViewPart
{
  
  public final static String VIEW_ID = "bitub.sgf.jreality.views.JRealityBaseViewPart"; //$NON-NLS-1$

  /**
   * <!-- begin-user-doc -->
   * Default length unit is 1.0  and axes coordination system is set by {@link JRealityPlugin#DEFAULT_COORDINATESYSTEM}.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public JRealityBaseViewPart()
  {
    super(JRealityPlugin.DEFAULT_COORDINATESYSTEM, 1.0);
  }
  
  /**
   * <!-- begin-user-doc -->
   * Default length unit is 1.0.
   * <!-- end-user-doc -->
   * @param axesSystem
   * The desired axes system.
   * @generated NOT
   */
  protected JRealityBaseViewPart(ViewCoordinationSystem axesSystem)
  {
    super(axesSystem, 1.0);
  }
  
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @param unitLength
   */
  protected JRealityBaseViewPart(ViewCoordinationSystem axesSystem, double unitLength)
  {
    super(axesSystem, unitLength);
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see bitub.sgf.jreality.views.JRealityViewPart#createViewer(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected JRealityContentViewer createViewer(Composite parent)
  {
    JRealityBaseContentViewer baseContentViewer = new JRealityBaseContentViewer(parent, m_defaultUnitLength);
    return baseContentViewer;
  }
  
  
}
