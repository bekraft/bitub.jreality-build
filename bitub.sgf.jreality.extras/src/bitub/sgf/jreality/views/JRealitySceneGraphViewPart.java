/*
 * Copyright (c) 2012-2015 Bernold Kraft (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Initial commit by bernold @ 03.06.2013.
 */

package bitub.sgf.jreality.views;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.jreality.scene.Viewer;
import de.jreality.ui.viewerapp.Navigator;

/**
 * 
 * <!-- begin-user-doc -->
 * A jReality native scene graph navigator embedded into a view part. Customization has to declare
 * the attached {@link JRealityViewPart} which provides the scene graph content.
 * <!-- end-user-doc -->
 * @generated NOT
 * @author bernold - 11.01.2014
 */
public abstract class JRealitySceneGraphViewPart extends ViewPart
{
  final static Logger myLog = Logger.getLogger(JRealitySceneGraphViewPart.class);
  
  // Part listener
  //
  final private IPartListener m_partListener = new IPartListener() {
    
    @Override
    public void partOpened(IWorkbenchPart part)
    {
      if((part instanceof IViewPart) && jRealityViewPartID.equals(part.getSite().getId())) {
        
        myLog.debug(String.format("jReality view [%s] opened.", jRealityViewPartID));
        initializeNavigator((IViewPart)part);
      }      
    }
    
    @Override
    public void partDeactivated(IWorkbenchPart part)
    {
      if((part instanceof IViewPart) && jRealityViewPartID.equals(part.getSite().getId())) {
        
        myLog.debug(String.format("jReality view [%s] deactivated.", jRealityViewPartID));
      }
    }
    
    @Override
    public void partClosed(IWorkbenchPart part)
    {
      if((part instanceof IViewPart) && jRealityViewPartID.equals(part.getSite().getId())) {
        
        myLog.debug(String.format("jReality view [%s] closed.", jRealityViewPartID));
        disposeNavigator();
      }
    }
    
    @Override
    public void partBroughtToTop(IWorkbenchPart part)
    {
    }
    
    @Override
    public void partActivated(IWorkbenchPart part)
    {
    }
  };
  
  /**
   * The attached viewer part ID.
   */
  final public String jRealityViewPartID;
  
  /**
   * The scene graph navigator.
   */
  protected Navigator m_navigator;
  // The Frame
  private Frame m_frame;
  // The composite
  private Composite m_composite;
  
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @param viewer
   */
  protected JRealitySceneGraphViewPart(String partID)
  {
    jRealityViewPartID = partID;
  }
  
  // Initializes the navigator
  private void initializeNavigator(IViewPart jrealityViewPart) 
  {
    if(jrealityViewPart instanceof JRealityViewPart) {

      Viewer embeddedViewer = ((JRealityViewPart)jrealityViewPart).getViewer().getEmbeddedViewer();
      m_navigator = new Navigator(embeddedViewer);
      m_frame.add(m_navigator.getComponent(), BorderLayout.CENTER);
    } else {
      
      m_frame.add(new JLabel("No jReality viewer found."), BorderLayout.SOUTH);
    }
    
    m_frame.doLayout();
    m_frame.repaint();
  }

  // Removes navigator
  private void disposeNavigator()
  {
    m_frame.removeAll();
    m_navigator = null;
    
    m_frame.add(new JLabel("Open a specific jReality view to enable scene graph view."), BorderLayout.SOUTH );
    
    m_frame.doLayout();    
    m_frame.repaint();
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPartControl(Composite parent)
  {
    parent.setLayout(new FillLayout());
    m_composite = new Composite(parent, SWT.EMBEDDED);
    m_composite.setLayout(new FillLayout());
    
    m_frame = SWT_AWT.new_Frame(m_composite);    
    m_frame.setLayout(new BorderLayout());
    
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(m_partListener);
    
    IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(jRealityViewPartID);
    initializeNavigator(viewPart);
  }

  @Override
  public void setFocus()
  {
    m_composite.setFocus();
    m_frame.repaint();
  }

}
