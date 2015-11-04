package bitub.sgf.jreality.commands;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.handlers.HandlerUtil;

import bitub.sgf.jreality.JRealityPlugin;
import bitub.sgf.jreality.views.JRealityViewPart;
import de.jreality.reader.Readers;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.util.Input;

public class CommandLoadObjModelIntoView extends AbstractHandler implements IHandler
{
  final static Logger myLog = Logger.getLogger(CommandLoadObjModelIntoView.class);

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    JRealityViewPart defaultView = JRealityPlugin.getDefault().getOrOpenDefaultView();
    if(null==defaultView) {
      
      MessageDialog.openError(HandlerUtil.getActiveShell(event), 
          "Error open jReality view", "jReality default view seems not to be available. See error log.");
      return null;
    }
    
    ISelectionService selectionService = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService();
    ISelection currentSelection = selectionService.getSelection("org.eclipse.ui.views.ResourceNavigator"); //$NON-NLS-1$
    
    if(null==currentSelection) {
      currentSelection = selectionService.getSelection("org.eclipse.ui.navigator.ProjectExplorer"); //$NON-NLS-1$
    }
    
    if(currentSelection instanceof IStructuredSelection) {
      
      for(Iterator<?> fileIt = ((IStructuredSelection) currentSelection).iterator(); fileIt.hasNext();) {
        
        IResource r = (IResource)fileIt.next();
        if (r instanceof IFile) {
                    
          IFile f = (IFile) r;
          myLog.info("Loading model from "+f.getLocation());
          
          SceneGraphComponent content = null;
          try {
            
            Input input = Input.getInput(f.getName(), f.getContents());
            content = Readers.read(input);
            content.setName(f.getName());
            
            defaultView.getContentComponent().addChild(content);
            
          } catch(IOException | CoreException e) {
                        
            myLog.error(e);
          }          
        }        
      }
    }
    return null;
  }

}
