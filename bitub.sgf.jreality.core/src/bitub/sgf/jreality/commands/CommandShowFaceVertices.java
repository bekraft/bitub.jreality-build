package bitub.sgf.jreality.commands;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import bitub.sgf.jreality.util.JRealityUtil;
import bitub.sgf.jreality.views.JRealityViewPart;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.Color;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;

/**
 * 
 * <!-- begin-user-doc -->
 * A handler which toggles the visibility of face vertices by steering the appearance of root component of
 * default jReality view part.
 * <!-- end-user-doc -->
 * @generated NOT
 * @author bernold - 31.10.2013
 */
public class CommandShowFaceVertices extends GenericJRealityHandler implements IHandler
{
  final static Logger myLog = Logger.getLogger(CommandShowFaceVertices.class);
  
  public static boolean ENABLE_USE_STATIC_FACTOR = false;
  
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

    final double factor = viewPart.getViewer().getFactor();    
    
    final SceneGraphComponent contentComponent = viewPart.getContentComponent();
    Appearance appearance = contentComponent.getAppearance();
    DefaultGeometryShader geometryShader = JRealityUtil.getGeometryShader(appearance);
    Boolean showPoints = geometryShader.getShowPoints();
    
    DefaultPointShader pointShader = (DefaultPointShader) geometryShader.getPointShader();
    
    if(ENABLE_USE_STATIC_FACTOR) {
      
      final double sphereRadius = 0.35 * factor;
      myLog.debug(String.format("Using factor %g of view. Setting sphere radius to %g.", factor, sphereRadius));
      pointShader.setPointRadius(sphereRadius);
    }
    
    pointShader.setDiffuseColor(Color.RED);
    
    if(null==showPoints) {
      showPoints = Boolean.FALSE;
    }
    
    myLog.debug(String.format("Toggling vertices %s (%s).", (!showPoints ? "on" : "off"), contentComponent.getName()));
    geometryShader.setShowPoints(!showPoints.booleanValue());
        
    return Boolean.TRUE;
  }

}
