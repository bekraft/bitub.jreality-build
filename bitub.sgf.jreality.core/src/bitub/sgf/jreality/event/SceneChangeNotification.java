/*
 * Copyright (c) 2012-2015 Bernold Kraft (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Initial commit by bernold @ 30.08.2013.
 */
package bitub.sgf.jreality.event;

import org.eclipse.emf.common.notify.Notification;

import bitub.sgf.jreality.views.viewer.JRealityContentViewer;
import de.jreality.scene.SceneGraphNode;

/**
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated NOT
 * @author bernold - 30.08.2013
 */
public class SceneChangeNotification implements Notification
{
  
  private JRealityContentViewer m_contentViewer;
  private SceneGraphNode m_oldSgc;
  private SceneGraphNode m_newSgc;
  private int m_flag;
  private boolean m_wasSet;
  private int m_position;
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public SceneChangeNotification(JRealityContentViewer notifier, int changeFlag, SceneGraphNode oldSgc, SceneGraphNode newSgc )
  {
    m_contentViewer = notifier;
    m_flag = changeFlag;
    m_oldSgc = oldSgc;
    
    m_wasSet = null!=oldSgc;
        
    m_newSgc = newSgc;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getNotifier()
   */
  @Override
  public Object getNotifier()
  {
    return m_contentViewer;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getEventType()
   */
  @Override
  public int getEventType()
  {
    return m_flag;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getFeatureID(java.lang.Class)
   */
  @Override
  public int getFeatureID(Class<?> expectedClass)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getFeature()
   */
  @Override
  public Object getFeature()
  {
    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getOldValue()
   */
  @Override
  public Object getOldValue()
  {
    return m_oldSgc;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getNewValue()
   */
  @Override
  public Object getNewValue()
  {
    return m_newSgc;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#wasSet()
   */
  @Override
  public boolean wasSet()
  {
    return m_wasSet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#isTouch()
   */
  @Override
  public boolean isTouch()
  {
    return false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#isReset()
   */
  @Override
  public boolean isReset()
  {
    return false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getPosition()
   */
  @Override
  public int getPosition()
  {
    return m_position;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#merge(org.eclipse.emf.common.notify.Notification)
   */
  @Override
  public boolean merge(Notification notification)
  {
    return false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getOldBooleanValue()
   */
  @Override
  public boolean getOldBooleanValue()
  {
    throw new UnsupportedOperationException();  
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getNewBooleanValue()
   */
  @Override
  public boolean getNewBooleanValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getOldByteValue()
   */
  @Override
  public byte getOldByteValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getNewByteValue()
   */
  @Override
  public byte getNewByteValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getOldCharValue()
   */
  @Override
  public char getOldCharValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getNewCharValue()
   */
  @Override
  public char getNewCharValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getOldDoubleValue()
   */
  @Override
  public double getOldDoubleValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getNewDoubleValue()
   */
  @Override
  public double getNewDoubleValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getOldFloatValue()
   */
  @Override
  public float getOldFloatValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getNewFloatValue()
   */
  @Override
  public float getNewFloatValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getOldIntValue()
   */
  @Override
  public int getOldIntValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getNewIntValue()
   */
  @Override
  public int getNewIntValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getOldLongValue()
   */
  @Override
  public long getOldLongValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getNewLongValue()
   */
  @Override
  public long getNewLongValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getOldShortValue()
   */
  @Override
  public short getOldShortValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getNewShortValue()
   */
  @Override
  public short getNewShortValue()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getOldStringValue()
   */
  @Override
  public String getOldStringValue()
  {
    return (null!=m_oldSgc? m_oldSgc.toString(): null);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see org.eclipse.emf.common.notify.Notification#getNewStringValue()
   */
  @Override
  public String getNewStringValue()
  {
    return (null!=m_newSgc? m_newSgc.toString():null);
  }
}
