/*
 * Copyright (c) 2012-2015 Bernold Kraft (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Initial commit by bernold @ 28.02.2013.
 */
package bitub.sgf.jreality.views.viewer;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.progress.UIJob;

import bitub.sgf.jreality.event.SceneChangeNotification;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphNode;
import de.jreality.scene.SceneGraphPath;
import de.jreality.util.EncompassFactory;
import de.jreality.util.SceneGraphUtility;

/**
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated NOT
 * @author bernold - 28.02.2013
 */
public class JRealitySceneGraphTreeViewer extends TreeViewer
{
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @author bernold - 30.08.2013
   */
  static class SceneGraphComponentDropListener extends ViewerDropAdapter
  {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public SceneGraphComponentDropListener(Viewer v)
    {
      super(v);
    }
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     * @see org.eclipse.swt.dnd.DropTargetAdapter#drop(org.eclipse.swt.dnd.DropTargetEvent)
     */
    @Override
    public void drop(DropTargetEvent event)
    {
//      Object source = event.getSource();      
//      Object determineTarget = determineTarget(event);
      
      super.drop(event);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang.Object)
     */
    @Override
    public boolean performDrop(Object data)
    {
      if(data instanceof SceneGraphComponent)
        return true;
            
      return Platform.getAdapterManager().hasAdapter(data, SceneGraphComponent.class.getName());
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang.Object, int, org.eclipse.swt.dnd.TransferData)
     */
    @Override
    public boolean validateDrop(Object target, int operation, TransferData transferType)
    {
      return (target instanceof SceneGraphComponent) && LocalSelectionTransfer.getTransfer().isSupportedType(transferType);
    }
            
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#dropAccept(org.eclipse.swt.dnd.DropTargetEvent)
     */
    @Override
    public void dropAccept(DropTargetEvent event)
    {
      super.dropAccept(event);
    }

  }
  
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @author bernold - 28.02.2013
   */
  public static class JRealitySceneNodeLabelProvider extends StyledCellLabelProvider
  {
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     * @see org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
     */
    @Override
    public void update(ViewerCell cell)
    {      
      Object element = cell.getElement();
      cell.setText(((SceneGraphComponent)element).getName());
      super.update(cell);
    }
    
    
  }
  
  
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @author bernold - 28.02.2013
   */
  public static class JRealitySceneTreeContentProvider implements ITreeContentProvider
  {
    SceneGraphComponent rootNode;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated NOT
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose()
    {
      rootNode = null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated NOT
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      assert newInput instanceof SceneGraphComponent;

      rootNode = (SceneGraphComponent) newInput;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated NOT
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(Object inputElement)
    {
      return getChildren(inputElement);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated NOT
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(Object parentElement)
    {
      return ((SceneGraphComponent)parentElement).getChildComponents().toArray();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated NOT
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    @Override
    public Object getParent(Object element)
    {     
      return getParentMatch(rootNode, (SceneGraphComponent)element);      
    }
    
    /**
     * 
     * <!-- begin-user-doc -->
     * Recursive search for parent node.
     * <!-- end-user-doc -->
     * @generated NOT
     * @param parent
     * @param x
     * @return
     */
    SceneGraphComponent getParentMatch(SceneGraphComponent parent, SceneGraphComponent x)
    {
      for(SceneGraphComponent s:parent.getChildComponents())
      {
        if(s.equals(x))
          return parent;
        
        SceneGraphComponent parentMatch = getParentMatch(s, x);
        if(null!=parentMatch)
          return parentMatch;
      }
      return null;
    }
   
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated NOT
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    @Override
    public boolean hasChildren(Object element)
    {
      return ((SceneGraphComponent)element).getChildComponentCount() > 0;
    }

  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param parent
   * @param style
   */
  public JRealitySceneGraphTreeViewer(Composite parent, int style)
  {
    super(parent, style);
    adoptSceneTree(this);
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param tree
   */
  public static void adoptSceneTree(TreeViewer tree)
  {
    tree.setContentProvider(new JRealitySceneTreeContentProvider());
    tree.setLabelProvider(new JRealitySceneNodeLabelProvider());
    tree.addDropSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{LocalTransfer.getInstance(), LocalSelectionTransfer.getTransfer()}, new SceneGraphComponentDropListener(tree));    
  }
  
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @param tree
   * @param notifier
   */
  public static void bind(final TreeViewer tree, final JRealityContentViewer notifier)
  {
    notifier.eAdapters().add(new AdapterImpl() {
      
      /**
       * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
       * @generated NOT
       * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
       */
      @Override
      public void notifyChanged(Notification msg)
      {
        if(msg instanceof SceneChangeNotification) {
                    
          new UIJob(tree.getControl().getDisplay(), "Refreshing...") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor)
            {
              tree.refresh(true);
              return Status.OK_STATUS;
            }
            
          }.schedule();       
        }
      }
    });
    
    tree.addSelectionChangedListener(new ISelectionChangedListener() {
      
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        Object firstElement = ((IStructuredSelection)event.getSelection()).getFirstElement();        
        if(firstElement instanceof SceneGraphComponent) {
          
          de.jreality.scene.Viewer viewer = notifier.getEmbeddedViewer();
          EncompassFactory ec = new EncompassFactory();
          ec.setAvatarPath(notifier.getAvatarPath());
          
          List<SceneGraphPath> pathsBetween = SceneGraphUtility.getPathsBetween(viewer.getSceneRoot(), (SceneGraphNode)firstElement);
          
          ec.setScenePath(pathsBetween.get(0));
          
          ec.setCameraPath(notifier.getCamaraPath());
          ec.setMargin(1.0);
          ec.setMetric(de.jreality.math.Pn.EUCLIDEAN);
          ec.update();
        }
      }
    });
  }
}
