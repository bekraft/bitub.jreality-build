package bitub.sgf.jreality.event;

import java.util.EventListener;

/**
 * 
 * <!-- begin-user-doc -->
 * A JReality pick listener. Forwards picking event capture from JReality.
 * <!-- end-user-doc -->
 * @generated NOT
 * @author bernold - 29.06.2012
 */
public interface IPickListener extends EventListener
{
  /**
   * 
   * <!-- begin-user-doc -->
   * Forwards the pick event.
   * <!-- end-user-doc -->
   * @generated NOT
   * @param event
   */
  void selectionChanged(PickEvent event);
}
