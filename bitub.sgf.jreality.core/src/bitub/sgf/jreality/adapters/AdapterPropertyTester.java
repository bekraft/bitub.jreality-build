package bitub.sgf.jreality.adapters;

import org.apache.log4j.Logger;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.Platform;

import de.jreality.scene.SceneGraphComponent;

/**
 * 
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated NOT
 * @author bernold - 04.09.2013
 */
public class AdapterPropertyTester extends PropertyTester
{
  
  final static Logger myLog = Logger.getLogger(AdapterPropertyTester.class);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
   */
  @Override
  public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
  {
    boolean hasAdapter = false;
    if(property.equals("isAdaptableSceneComponent")) { //$NON-NLS-1$
            
      hasAdapter = Platform.getAdapterManager().hasAdapter(receiver, SceneGraphComponent.class.getName());
      if(!hasAdapter) {
        
        myLog.debug(String.format("Transfered receiver \"%s\" has no adapter for \"%s\".",receiver, SceneGraphComponent.class.getName()));        
      }      
    }
    
    return Boolean.valueOf(expectedValue.toString()) == hasAdapter;
  }

}
