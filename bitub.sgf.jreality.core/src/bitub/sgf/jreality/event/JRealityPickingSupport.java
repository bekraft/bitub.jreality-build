/*
 * Copyright (c) 2012-2015 Bernold Kraft (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Initial commit by bernold @ 29.06.2012.
 */
package bitub.sgf.jreality.event;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;

import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.pick.PickResult;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;

/**
 * <!-- begin-user-doc --> 
 * A picking support tool. Registers itself as  
 * <!-- end-user-doc -->
 * 
 * @generated NOT
 * @author bernold - 29.06.2012
 */
public class JRealityPickingSupport extends AbstractTool
{
  final static Logger myLog = Logger.getLogger(JRealityPickingSupport.class);

  // Listener list
  private final ListenerList m_listenerList = new ListenerList();
  // Time flag
  private long m_systemTime;
  
  /**
   * <!-- begin-user-doc -->
   * The types of slots.
   * <!-- end-user-doc -->
   * @generated NOT
   * @author bernold - 29.06.2012
   */
  public enum Slot {
    LeftButton, ShiftLeftButton, RightButton, ShiftRightButton
  }

  /**
   * <!-- begin-user-doc -->
   * A new picking support. Registers itself into given slot.
   * <!-- end-user-doc -->
   * @generated NOT
   * @param slot
   */
  public JRealityPickingSupport(Slot slot)
  {
    super();
    init(slot);
  }

  @Override
  public void activate(final ToolContext tc)
  {
    perform(tc);
  }

  /**
   * <!-- begin-user-doc -->
   * Add a {@link IPickListener} instance to call back on pick event.
   * <!-- end-user-doc -->
   * @generated NOT
   * @param listener
   */
  public void addListener(final IPickListener listener)
  {
    m_listenerList.add(listener);
  }

  @Override
  public void perform(final ToolContext tc)
  {
    if ((System.currentTimeMillis() - m_systemTime) < 1000) {
    	
      m_systemTime = System.currentTimeMillis();
      return;
    }

    m_systemTime = System.currentTimeMillis();

    final PickResult pr = tc.getCurrentPick();

    if (pr == null) {
    	
      return;
    }

    fireSelectionChange(pr);
  }


  /**
   * <!-- begin-user-doc -->
   * Processes pick result and notifies listeners
   * <!-- end-user-doc -->
   * @generated NOT
   * @param pr
   */
  protected void fireSelectionChange(final PickResult pr)
  {
    SceneGraphComponent lastComponent = pr.getPickPath().getLastComponent();
    final PickEvent e = new PickEvent(this, lastComponent, pr);

    for (final Object listener : m_listenerList.getListeners()) {
    	
      // Run selection change
      //
      SafeRunner.run(new SafeRunnable() {
        
        @Override
        public void run() throws Exception
        {
          ((IPickListener) listener).selectionChanged(e);
        }
      });
    }
  }

  // Init, register slot for call, right here: LEFT BUTTON
  // 
  private void init(Slot slot)
  {
    addCurrentSlot(InputSlot.getDevice("SecondaryAction")); //$NON-NLS-1$
    
//    switch(slot)
//    {
//      case LeftButton:
//        addCurrentSlot(InputSlot.LEFT_BUTTON);
//        break;
//        
//      case RightButton:
//        addCurrentSlot(InputSlot.RIGHT_BUTTON);
//        break;
//
//      case ShiftLeftButton:
//        addCurrentSlot(InputSlot.SHIFT_LEFT_BUTTON);
//        break;
//
//      case ShiftRightButton:
//        addCurrentSlot(InputSlot.SHIFT_RIGHT_BUTTON);
//        break;
//
//        
//      default:
//        
//        Log.warn("Unknown slot register: "+slot);
//    }
    
  }

}
