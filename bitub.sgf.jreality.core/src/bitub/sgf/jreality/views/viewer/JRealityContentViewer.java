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
package bitub.sgf.jreality.views.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.NotifierImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import bitub.sgf.jreality.adapters.SceneGraphComponentFactory;
import bitub.sgf.jreality.adapters.SceneGraphComponentFactory.AdaptionReactor;
import bitub.sgf.jreality.event.IPickListener;
import bitub.sgf.jreality.event.JRealityPickingSupport;
import bitub.sgf.jreality.event.JRealityPickingSupport.Slot;
import bitub.sgf.jreality.event.PickEvent;
import bitub.sgf.jreality.event.SceneChangeNotification;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Scene;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.event.SceneGraphComponentEvent;
import de.jreality.scene.event.SceneGraphComponentListener;
import de.jreality.util.CameraUtility;

/**
 * A JReality content viewer. The input data uses an adaption mechanism and
 * requests for a <em>SceneGraphComponent</em> of JReality which has to be the
 * root node of viewer.
 * 
 * @author bernold - 27.06.2012
 */
public abstract class JRealityContentViewer extends Viewer implements Notifier
{
  final static Logger myLog = Logger.getLogger(JRealityContentViewer.class);

  // The current selected Object
  private ISelection m_selectedObject = StructuredSelection.EMPTY;

  // The adapter factory, if set 
  protected SceneGraphComponentFactory m_factory;

  // The input object
  protected Object m_inputObject;

  // Auto centering when changing or adding
  protected boolean m_autoCentering = false;

  // The notification delegate
  protected Notifier m_notifierDelegete = new NotifierImpl();
  
  // The content node
  private SceneGraphComponent m_contentNode;

  /**
   * A Scene graph listener. Acts on new children (attaches itself, encompasses if
   * autoCentering is enabled).
   * 
   * @generated NOT
   * @author bernold - 04.01.2012
   */
  class SceneComponentStructureListener implements SceneGraphComponentListener
  {
    // Recursive add
    private void recursiveAdd(SceneGraphComponent child)
    {
      for (SceneGraphComponent childOfChild : child.getChildComponents()) {
        recursiveAdd(childOfChild);
      }
      child.addSceneGraphComponentListener(this);
    }

    // Recursive remove
    private void recursiveRemove(SceneGraphComponent child)
    {
      for (SceneGraphComponent childOfChild : child.getChildComponents()) {
        recursiveRemove(childOfChild);
      }
      child.removeSceneGraphComponentListener(this);
    }

    @Override
    public void childAdded(SceneGraphComponentEvent arg0)
    {
      if (SceneGraphComponentEvent.CHILD_TYPE_COMPONENT == arg0.getChildType()) {

        final SceneGraphComponent child = (SceneGraphComponent) arg0.getNewChildElement();

        if (null != getEmbeddedViewer()) {
          Scene.executeReader(arg0.getSceneGraphComponent(), new Runnable() {

            @Override
            public void run()
            {
              if (m_autoCentering) {
                CameraUtility.encompass(getEmbeddedViewer(), child, true, de.jreality.math.Pn.EUCLIDEAN);
              } else {
                CameraUtility.encompassNew(getEmbeddedViewer());
              }
            }
          });
        }

        myLog.trace("Scene child component added: " + child.getName());

        recursiveAdd(child);
        m_notifierDelegete
            .eNotify(new SceneChangeNotification(JRealityContentViewer.this, Notification.ADD, null, child));
      }
    }

    @Override
    public void childRemoved(SceneGraphComponentEvent arg0)
    {
      if (SceneGraphComponentEvent.CHILD_TYPE_COMPONENT == arg0.getChildType()) {

        SceneGraphComponent child = (SceneGraphComponent) arg0.getOldChildElement();

        myLog.trace("Scene child component removed: " + child.getName());
        recursiveRemove(child);
        m_notifierDelegete.eNotify(new SceneChangeNotification(JRealityContentViewer.this, Notification.REMOVE, child,
            null));
      }
    }

    @Override
    public void childReplaced(SceneGraphComponentEvent arg0)
    {
      if (arg0.getOldChildElement() instanceof SceneGraphComponent) {
        recursiveRemove((SceneGraphComponent) arg0.getOldChildElement());
      }

      if (SceneGraphComponentEvent.CHILD_TYPE_COMPONENT == arg0.getEventType()) {

        SceneGraphComponent child = arg0.getSceneGraphComponent().getChildComponent(arg0.getChildIndex());

        myLog.trace("Scene child component replaced: " + child.getName());
        recursiveAdd(child);
        m_notifierDelegete.eNotify(new SceneChangeNotification(JRealityContentViewer.this, Notification.SET, arg0
            .getOldChildElement(), child));
      }
    }

    @Override
    public void visibilityChanged(SceneGraphComponentEvent arg0)
    {
      if (null != getEmbeddedViewer() && m_autoCentering && arg0.getSceneGraphComponent().isVisible()) {

        final SceneGraphComponent component = arg0.getSceneGraphComponent();
        Scene.executeReader(arg0.getSceneGraphComponent(), new Runnable() {

          @Override
          public void run()
          {
            CameraUtility.encompass(getEmbeddedViewer(), component, true, de.jreality.math.Pn.EUCLIDEAN);
          }
        });
      }
    }
  }


  /**
   * <!-- begin-user-doc -->
   * A picking handler. Forwards selection to {@link ContentViewer} instance.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @author bernold - 03.07.2012
   */
  class PickHandler implements IPickListener
  {
    @Override
    public void selectionChanged(PickEvent event)
    {
      ISelection selection = StructuredSelection.EMPTY;

      // Find a matching factory
      //
      Object resolved = m_factory.resolve(event.component);
      if ((null != m_factory) && (null != resolved)) {

        selection = new StructuredSelection(resolved);
      }

      // If no factory found, forward SGC itself
      //
      if (selection.isEmpty()) {
        selection = new StructuredSelection(event.component);
      }

      m_selectedObject = selection;

      // Fire selection event
      //
      JRealityContentViewer.this.fireSelectionChanged(new SelectionChangedEvent(JRealityContentViewer.this,
          selection));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @author bernold - 03.06.2013
   */
  class JRealityEmbeddedViewerComponent extends Composite
  {
    // The containing frame
    final Frame awtFrame;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated NOT
     * @param parent
     */
    public JRealityEmbeddedViewerComponent(Composite parent)
    {
      super(parent, SWT.EMBEDDED);
      setLayout(new FillLayout());

      awtFrame = SWT_AWT.new_Frame(this);
      awtFrame.setLayout(new BorderLayout());
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public JRealityEmbeddedViewerComponent(Composite parent, de.jreality.scene.Viewer jRealityViewer)
    {
      this(parent);
      setContentPanel((Component) jRealityViewer.getViewingComponent());
    }

    public void setContentPanel(Component panel)
    {
      awtFrame.add(panel);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  protected void addDropTargetListener(DropTargetListener dndListener)
  {
    DropTarget dropTarget = new DropTarget(getControl(), DND.DROP_COPY | DND.DROP_MOVE);
    dropTarget.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
    dropTarget.addDropListener(dndListener);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param pickAction
   * @return
   */
  protected JRealityPickingSupport createPickSupport(Slot pickAction)
  {
    JRealityPickingSupport pickingSupport = new JRealityPickingSupport(pickAction);
    pickingSupport.addListener(new PickHandler());

    return pickingSupport;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#getSceneNode()
   */
  public abstract SceneGraphComponent getSceneNode();
  
  /**
   * 
   * <!-- begin-user-doc -->
   * The current content root. Be aware, that this node is removed from tree when input changes. Might be null, if
   * there's is no content.
   * <!-- end-user-doc -->
   * @generated NOT
   * @return
   */
  public SceneGraphComponent getContentNode()
  {
    return m_contentNode;
  }
  
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @param contentComp
   */
  public void setContentNode(SceneGraphComponent contentComp)
  {    
    removeContentNode();
    appendNode(contentComp);    
  }
  
  /**
   * 
   * <!-- begin-user-doc -->
   * Returns the extent multiplier. A scale factor which reflects the ratio between a jReality unit and real
   * length unit.
   * <!-- end-user-doc -->
   * @generated NOT
   * @return
   */
  public abstract double getFactor();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#transformRootContentNode(de.jreality.math.MatrixBuilder)
   */
  public abstract void transformRootContentNode(MatrixBuilder mb);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#getCamaraPath()
   */
  public abstract SceneGraphPath getCamaraPath();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#getAvatarPath()
   */
  public abstract SceneGraphPath getAvatarPath();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#getEmbeddedViewer()
   */
  public abstract de.jreality.scene.Viewer getEmbeddedViewer();

  /**
   * <!-- begin-user-doc --> Get the current selection <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.jface.viewers.Viewer#getSelection()
   */
  @Override
  public ISelection getSelection()
  {
    return m_selectedObject;
  }

  /**
   * <!-- begin-user-doc --> Refreshes the viewer. <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.jface.viewers.Viewer#refresh()
   */
  @Override
  public void refresh()
  {
    if (null == getControl()) {
      return;
    }

    // Refresh if factory defined
    if (null != m_factory) {

      new Job("Refreshing viewer...") {

        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
          try {

            m_factory.refresh();
          }
          catch (Throwable e) {

            myLog.error(e);
          }
          return Status.OK_STATUS;
        }
      };
    }
  }

  /**
   * <!-- begin-user-doc --> Sets the selection to specifued selection. <!--
   * end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.jface.viewers.Viewer#setSelection(org.eclipse.jface.viewers.ISelection,
   *      boolean)
   */
  @Override
  public void setSelection(final ISelection selection, final boolean reveal)
  {
    if (!selection.isEmpty()) {
      Object o = null;
      if (selection instanceof IStructuredSelection) {
        o = ((IStructuredSelection) selection).getFirstElement();
      }

      if (null != m_factory) {
        
        m_factory.adapt(o, new AdaptionReactor() {

          @Override
          public void adapted(Collection<SceneGraphComponent> adapted)
          {
            if (adapted.isEmpty())
              return;

            if (reveal && (null != getControl())) {

              if (adapted.size() > 1)
                CameraUtility.encompass(getEmbeddedViewer());
              else
                CameraUtility.encompass(getEmbeddedViewer(), adapted.iterator().next(), true, de.jreality.math.Pn.EUCLIDEAN);
            }

            SelectionChangedEvent event = new SelectionChangedEvent(JRealityContentViewer.this, selection);
            fireSelectionChanged(event);
          }

          @Override
          public boolean isUISensitive()
          {
            return true;
          }
        });
      }
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#getAdapterFactory()
   */
  public SceneGraphComponentFactory getAdapterFactory()
  {
    return m_factory;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.jface.viewers.Viewer#setInput(java.lang.Object)
   */
  @Override
  public void setInput(Object input)
  {
    inputChanged(input, m_inputObject);
    m_inputObject = input;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.jface.viewers.Viewer#getInput()
   */
  @Override
  public Object getInput()
  {
    return m_inputObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notifier#eAdapters()
   */
  @Override
  public EList<Adapter> eAdapters()
  {
    return m_notifierDelegete.eAdapters();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notifier#eNotify(org.eclipse.emf.common.notify.Notification)
   */
  @Override
  public void eNotify(Notification notification)
  {
    m_notifierDelegete.eNotify(notification);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notifier#eDeliver()
   */
  @Override
  public boolean eDeliver()
  {
    return m_notifierDelegete.eDeliver();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notifier#eSetDeliver(boolean)
   */
  @Override
  public void eSetDeliver(boolean deliver)
  {
    m_notifierDelegete.eSetDeliver(deliver);
  }
  
  // Removes the current content node
  //
  private void removeContentNode()
  {
    if (null != getControl()) {

      final SceneGraphComponent sceneNode = getSceneNode();
      if (null != sceneNode && null != m_contentNode) {

        Scene.executeWriter(sceneNode, new Runnable() {

          @Override
          public void run()
          {
            sceneNode.removeChild(m_contentNode);
          }
        });
      }
    }
  }

  /**
   * <!-- begin-user-doc -->
   * Called whenever a model changes.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.jface.viewers.Viewer#inputChanged(java.lang.Object,
   *      java.lang.Object)
   */
  @Override
  protected void inputChanged(Object input, Object oldInput)
  {
    if (input != oldInput) {
      
      removeContentNode();
      fetchModel(input);
    }

    super.inputChanged(input, oldInput);
  }
  
  // Appends a node to scene
  //
  private void appendNode(final SceneGraphComponent contentComp)
  {
    if(null==contentComp) {
      return;
    }
    
    final SceneGraphComponent sceneNode = getSceneNode();
    Runnable graphChange = new Runnable() {

      @Override
      public void run()
      {
        sceneNode.addChild(m_contentNode = contentComp);
      }
    };
    
    Scene.executeWriter(sceneNode, graphChange);
  }

  /**
   * <!-- begin-user-doc -->
   * Fetches the information from given object. Attempts
   * to cast to {@link SceneGraphComponent} first. If this fails, tries to find
   * an adapter for either {@link SceneGraphComponentFactory} or
   * {@link SceneGraphComponent}.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param input
   */
  protected void fetchModel(final Object input)
  {
    if (null == getControl() || null == getSceneNode()) {

      myLog.info("No active scene graph or viewer found.");
      return;
    }

    SceneGraphComponent newContent = null;
    if (input instanceof SceneGraphComponent) {

      newContent = (SceneGraphComponent) input;
    } else {

      SceneGraphComponentFactory adapter = ensureActivationOfAdapter(input, SceneGraphComponentFactory.class);

      myLog.info("Adaption of scene graph factory succeeded = " + (adapter != null));

      // Adapt factory
      if (null != adapter) {

        m_factory = adapter;
        newContent = m_factory.getRoot();
      } else {

        newContent = ensureActivationOfAdapter(input, SceneGraphComponent.class);
        myLog.info("Adaption of scene graph component succeeded = " + (adapter != null));
      }
    }

    appendNode(newContent);
  }

  /**
   * <!-- begin-user-doc -->
   * Adds event listener hooks to the given control.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.jface.viewers.ContentViewer#hookControl(org.eclipse.swt.widgets.Control)
   */
  protected void hookControl(Control control)
  {
    control.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent event)
      {

        handleDispose(event);
      }
    });
  }

  /**
   * <!-- begin-user-doc --> Handles the dispose. <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.jface.viewers.ContentViewer#handleDispose(org.eclipse.swt.events.DisposeEvent)
   */
  protected void handleDispose(DisposeEvent event)
  {
    // Reset factory
    m_factory = null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param o
   * @param adapterType
   * @return
   */
  @SuppressWarnings("unchecked")
  protected <T> T ensureActivationOfAdapter(Object o, Class<?> adapterType)
  {
    Object adapter = Platform.getAdapterManager().getAdapter(o, adapterType);
    if (null == adapter) {
      if (Platform.getAdapterManager().hasAdapter(o, adapterType.getName())) {
        adapter = Platform.getAdapterManager().loadAdapter(o, adapterType.getName());
      }
    }
    return (T) adapter;
  }
}
