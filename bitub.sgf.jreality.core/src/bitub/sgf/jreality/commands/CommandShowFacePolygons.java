package bitub.sgf.jreality.commands;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import bitub.sgf.jreality.util.JRealityUtil;
import bitub.sgf.jreality.views.JRealityViewPart;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.DefaultGeometryShader;

/**
 * 
 * <!-- begin-user-doc -->
 * A handler which toggles the visibility of face polyons by steering the appearance of root component of
 * default jReality view part.
 * <!-- end-user-doc -->
 * @generated NOT
 * @author bernold - 31.10.2013
 */
public class CommandShowFacePolygons extends GenericJRealityHandler implements IHandler
{
  final static Logger myLog = Logger.getLogger(CommandShowFacePolygons.class);
  
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
    JRealityViewPart viewPart = getView(event);

    final SceneGraphComponent contentComponent = viewPart.getContentComponent();
    Appearance appearance = contentComponent.getAppearance();
    DefaultGeometryShader geometryShader = JRealityUtil.getGeometryShader(appearance);
    
    Boolean showFaces = geometryShader.getShowFaces();
    if(null==showFaces) {
      showFaces = Boolean.FALSE;
    }
    
    myLog.debug(String.format("Toggling faces %s (%s).", (!showFaces ? "on" : "off"), contentComponent.getName()));
    geometryShader.setShowFaces(!showFaces);
        
    return Boolean.TRUE;
  }

}
