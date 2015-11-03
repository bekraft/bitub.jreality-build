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
import de.jreality.shader.DefaultLineShader;

/**
 * <!-- begin-user-doc -->
 * A handler which toggles the visibility of face edges by steering the
 * appearance of root component of
 * default jReality view part.
 * <!-- end-user-doc -->
 * 
 * @generated NOT
 * @author bernold - 31.10.2013
 */
public class CommandShowFaceEdges extends GenericJRealityHandler implements IHandler
{
  final static Logger myLog = Logger.getLogger(CommandShowFaceEdges.class);
  
  public static boolean ENABLE_USE_STATIC_FACTOR = false;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    JRealityViewPart viewPart = getView(event);
    
    if(null==viewPart) {
      
      myLog.warn("Found no jReality view part.");
      return Boolean.FALSE;
    }
    
    final double factor = viewPart.getViewer().getFactor();

    final SceneGraphComponent contentComponent = viewPart.getContentComponent();
    Appearance appearance = contentComponent.getAppearance();
    DefaultGeometryShader geometryShader = JRealityUtil.getGeometryShader(appearance);
    
    DefaultLineShader lineShader = (DefaultLineShader) geometryShader.getLineShader();
  
    if(ENABLE_USE_STATIC_FACTOR) {
    
      final double tubeRadius = 0.15 * factor;
      myLog.debug(String.format("Using factor %g of view. Setting tube radius to %g.", factor, tubeRadius));
      //lineShader.setLineWidth(tubeRadius);   
      lineShader.setTubeRadius(tubeRadius);
    }
    
    lineShader.setDiffuseColor(Color.RED);

    Boolean showLines = geometryShader.getShowLines();
    if (null == showLines) {
      showLines = Boolean.FALSE;
    }

    myLog.debug(String.format("Toggling edges %s (%s).", (!showLines ? "on" : "off"), contentComponent.getName()));

    geometryShader.setShowLines(!showLines.booleanValue());

    return Boolean.TRUE;
  }

}
