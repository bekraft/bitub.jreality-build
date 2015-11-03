package bitub.sgf.jreality.commands;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import bitub.sgf.jreality.views.JRealityBaseViewPart;
import bitub.sgf.jreality.views.JRealityViewPart;
import de.jreality.scene.SceneGraphComponent;

/**
 * <!-- begin-user-doc -->
 * A command which initiates a {@link SceneGraphComponent} adaption of selected objects.
 * <!-- end-user-doc -->
 * @generated NOT
 * @author bernold - 30.08.2013
 */
public class CommandAdaptSceneGraphComponent extends GenericJRealityHandler implements IHandler
{
  final static Logger myLog = Logger.getLogger(CommandAdaptSceneGraphComponent.class);
  
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
    
    JRealityViewPart viewPart = getView(event, JRealityBaseViewPart.VIEW_ID);
        
    if(currentSelection instanceof IStructuredSelection ) {

      for(@SuppressWarnings("unchecked")
      Iterator<Object> iterator = ((IStructuredSelection)currentSelection).iterator(); iterator.hasNext(); ) {
        
        Object object = iterator.next();
        if (object instanceof SceneGraphComponent) {
          
          SceneGraphComponent sgc = (SceneGraphComponent) object;
          viewPart.getContentComponent().addChild(sgc);
          
        } else if(Platform.getAdapterManager().hasAdapter(object, SceneGraphComponent.class.getName())) {
          
          SceneGraphComponent sgc = (SceneGraphComponent)Platform.getAdapterManager().loadAdapter(object, SceneGraphComponent.class.getName());
          viewPart.getContentComponent().addChild(sgc);
        }
      }
    }
    
    return Boolean.TRUE;
  }

}
