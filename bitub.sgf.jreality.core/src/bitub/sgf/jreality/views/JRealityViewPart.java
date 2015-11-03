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

import java.awt.Color;
import java.util.EventObject;
import java.util.HashMap;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

import bitub.sgf.jreality.JRealityPlugin.ViewCoordinationSystem;
import bitub.sgf.jreality.util.JRealityUtil;
import bitub.sgf.jreality.views.viewer.JRealityContentViewer;
import bitub.sgf.jreality.views.viewer.JRealitySceneGraphTreeViewer;
import de.jreality.geometry.CoordinateSystemFactory;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.tool.InputSlot;
import de.jreality.tools.ClickWheelCameraZoomTool;
import de.jreality.tools.DraggingTool;
import de.jreality.tools.FlyTool;
import de.jreality.tools.RotateTool;
import de.jreality.util.CameraUtility;


/**
 * A JReality view instance which creates a new view portion of a model.
 * <p>
 * This view shows the scene graph content of a embedded controller instance of 
 * {@link JRealityViewerInstance#createInstance()}.
 * <p>
 */
public abstract class JRealityViewPart extends ViewPart
{
  /**
   * Forms tool kit.
   */
  protected FormToolkit m_toolkit;
  
  /**
   * This keeps track of the editing domain that is used to track all changes to the model.
   */
  protected AdapterFactoryEditingDomain m_editingDomain;

  /**
   * The unit length.
   */
  protected double m_defaultUnitLength = 1.0;
  
  // The content viewer.
  private JRealityContentViewer m_contentViewer;
  // The property page
  private PropertySheetPage m_propertySheetPage;
  // The adapter factory
  private ComposedAdapterFactory m_adapterFactory;
  
  private ViewCoordinationSystem m_axesSystem;
   
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected JRealityViewPart()
  {    
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected JRealityViewPart(ViewCoordinationSystem axesSystem, double unitLength)
  {
    m_defaultUnitLength = unitLength;
    m_axesSystem = axesSystem;
  }
  
  
  // Selection listener on public service
  //
  final private ISelectionListener m_myListener = new ISelectionListener() {
    
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
      if(selection instanceof IStructuredSelection)
      {
        // Set selection and reveal it
        //
        m_contentViewer.setSelection(selection, true);
      }        
    }
  };
  
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @param parent
   * @return
   */
  protected abstract JRealityContentViewer createViewer(Composite parent);
  
  
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPartControl(Composite parent)
  {
    parent.setLayout(new FillLayout());    
    m_toolkit = new FormToolkit(parent.getDisplay());
    
    SashForm sashForm = new SashForm(parent, SWT.NONE);
    sashForm.setOrientation(SWT.HORIZONTAL);
    
    final JRealityContentViewer contentViewer = createViewer(sashForm);
    m_toolkit.adapt((Composite)contentViewer.getControl());
    m_contentViewer = contentViewer;
    
    SceneGraphComponent sceneNode = contentViewer.getSceneNode();
    
    switch(m_axesSystem) {
      case Axis:
        sceneNode.addChild(JRealityUtil.createAxisComponent(20, 1.0, m_defaultUnitLength, Color.GRAY));
        break;
      case BoxedAxis:
        CoordinateSystemFactory csf = new CoordinateSystemFactory(m_defaultUnitLength, m_defaultUnitLength/10);
        csf.showAxes(true);
        csf.showBox(true);
        csf.showGrid(true);
        csf.showLabels(true);
        
        sceneNode.addChild(csf.getCoordinateSystem());
        break;
      case Grid:
        sceneNode.addChild(JRealityUtil.createGrid(m_defaultUnitLength, m_defaultUnitLength, 10, 10, Color.blue));
        break;
        
      default:
        break;
    }    
    
//    getViewer().getSceneNode().addTool(new DragEventTool());
    getViewer().getSceneNode().addTool(new FlyTool());    
    getViewer().getSceneNode().addTool(new RotateTool());
    getViewer().getSceneNode().addTool(new ClickWheelCameraZoomTool());
    
    DraggingTool draggingTool = new DraggingTool(InputSlot.RIGHT_BUTTON);
    draggingTool.addCurrentSlot(InputSlot.getDevice("PointerEvolution"));
    draggingTool.addCurrentSlot(InputSlot.RIGHT_BUTTON);//InputSlot.getDevice("DragAlongViewDirection"));
        
    getViewer().getSceneNode().addTool(draggingTool);
    
    SceneGraphComponent graphComponent = new SceneGraphComponent("Content");
    Appearance defaultAppearance = JRealityUtil.createDefaultAppearance(0., true, false, false);
    graphComponent.setAppearance(defaultAppearance);
    
    getViewer().setContentNode(graphComponent);
    
    JRealitySceneGraphTreeViewer sceneGraphViewer = new JRealitySceneGraphTreeViewer(sashForm, SWT.NONE);
    sceneGraphViewer.setInput(contentViewer.getSceneNode());
    JRealitySceneGraphTreeViewer.bind(sceneGraphViewer, contentViewer);
    
    sashForm.setWeights(new int[]{90,10});
    
    // Set selection provider        
    getSite().setSelectionProvider(m_contentViewer);
    
    // Add dispose listener
    //
    parent.addDisposeListener(new DisposeListener() {
      
      @Override
      public void widgetDisposed(DisposeEvent e)
      {
        m_toolkit.dispose();
      }
    });
    
    // Add selection listener
    //
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(m_myListener);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAdapter(Class<T> adapter)
  {    
	  if (adapter.equals(IPropertySheetPage.class)) {
	    return (T)getPropertySheetPage();
	  }
	
	  return super.getAdapter(adapter);
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.ui.part.WorkbenchPart#dispose()
   */
  @Override
  public void dispose()
  {
    if(!PlatformUI.getWorkbench().isClosing()) {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(m_myListener);
    }
    super.dispose();
  }
  
  
  // Inits the editing domain
  //
  private void initEditingDomain()
  {
    // Create an adapter factory that yields item providers.
    //
    m_adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

    m_adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    m_adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

    // Create the command stack that will notify this editor as commands are executed.
    //
    BasicCommandStack commandStack = new BasicCommandStack();
    commandStack.addCommandStackListener(new CommandStackListener() {
      
      @Override
      public void commandStackChanged(EventObject event)
      {
        // TODO Auto-generated method stub        
      }
    });
    
    // Create the editing domain with a special command stack.
    //
    m_editingDomain = new AdapterFactoryEditingDomain(m_adapterFactory, commandStack, new HashMap<Resource, Boolean>());
  }

  
  /**
   * This accesses a cached version of the property sheet.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public IPropertySheetPage getPropertySheetPage()
  {
    if (m_propertySheetPage == null) {
      initEditingDomain();
      
      m_propertySheetPage = new ExtendedPropertySheetPage(m_editingDomain);
      m_propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(m_adapterFactory));
    }

    return m_propertySheetPage;
  }

  
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
   */
  @Override
  final public void setFocus()
  {
    m_contentViewer.getControl().setFocus();
  }
    
  /**
   * 
   * <!-- begin-user-doc -->
   * Returns a viewer attached to this view.
   * <!-- end-user-doc -->
   * @generated NOT
   * @return
   */
  final public JRealityContentViewer getViewer()
  {
    return m_contentViewer;
  }
  
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @return
   */
  public SceneGraphComponent getContentComponent()
  {
    return m_contentViewer.getContentNode();
  }
  
  /**
   * 
   * <!-- begin-user-doc -->
   * Encompasses whole scene.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  final public void encompassScene()
  {
    CameraUtility.encompass(m_contentViewer.getEmbeddedViewer());
  }
  

}