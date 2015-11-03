package bitub.sgf.jreality.event;

import java.util.EventObject;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.pick.PickResult;

/**
 * <!-- begin-user-doc -->
 * JReality pick event.
 * <!-- end-user-doc -->
 * @generated NOT
 * @author bernold - 29.06.2012
 */
public class PickEvent extends EventObject
{
  // --- PRIVATE PROPERTIES -----------------------------------------------
  
  private static final long serialVersionUID = 1L;

  // --- PROTECTED PROPERTIES ---------------------------------------------

  // --- INTERNAL PROPERTIES ----------------------------------------------

  // --- PUBLIC PROPERTIES ------------------------------------------------
  
  final public SceneGraphComponent component;
  
  final public PickResult result;

  // --- CONSTRUCTORS -----------------------------------------------------
   
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @param source
   */
  public PickEvent(Object source, SceneGraphComponent component, PickResult result)
  {
    super(source);
    this.component = component;
    this.result = result;
  }

  // --- PUBLIC MEMBERS ---------------------------------------------------

  // --- PROTECTED MEMBERS ------------------------------------------------

  // --- PRIVATE MEMBERS ---------------------------------------------------
}
