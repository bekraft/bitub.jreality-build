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

package bitub.sgf.jreality.adapters;

import java.util.Collection;
import java.util.EventListener;

import de.jreality.scene.SceneGraphComponent;

/**
 * <!-- begin-user-doc -->
 * An adapter factory for {@link SceneGraphComponent} instances.
 * <!-- end-user-doc -->
 * @generated NOT
 * @author bernold - 29.06.2012
 */
public interface SceneGraphComponentFactory
{
  /**
   * <!-- begin-user-doc -->
   * Adaption reactor. Callback from component factory.
   * <!-- end-user-doc -->
   * @generated NOT
   * @author bernold - 07.06.2013
   */
  public interface AdaptionReactor extends EventListener
  {
    /** 
     * <!-- begin-user-doc -->
     * Called whenever a collection of scene components have been adapted successfully.
     * <!-- end-user-doc -->
     * @generated NOT
     * @param adapted
     */
    void adapted(Collection<SceneGraphComponent> adapted);
    
    /**
     * <!-- begin-user-doc -->
     * True, whenever a call should by in sync with UI thread.
     * <!-- end-user-doc -->
     * @generated NOT
     * @return
     */
    boolean isUISensitive();
  }
  
  
  /**
   * <!-- begin-user-doc -->
   * Gets the root component.
   * <!-- end-user-doc -->
   * @generated NOT
   * @return
   */
  SceneGraphComponent getRoot();
    
  /**
   * <!-- begin-user-doc -->
   * Tries to adapt an arbitrary object to {@link SceneGraphComponent}.
   * <!-- end-user-doc -->
   * @generated NOT
   * @param o
   * @return
   */
  void adapt(Object o, AdaptionReactor reactor);
  
  /**
   * <!-- begin-user-doc -->
   * Add an adaptor.
   * <!-- end-user-doc -->
   * @generated NOT
   * @param reactor
   * A listener which will be notified on adaption.
   */
  void addAdaptionReactor(AdaptionReactor reactor);
  
  /**
   * <!-- begin-user-doc -->
   * Removes an adaptor.
   * <!-- end-user-doc -->
   * @generated NOT
   * @param reactor
   */
  void removeAdaptionReactor(AdaptionReactor reactor);
  
  /**
   * 
   * <!-- begin-user-doc -->
   * Resolves a given {@link SceneGraphComponent} to an adapted object.
   * <!-- end-user-doc -->
   * @generated NOT
   * @param component
   * @return
   */
  Object resolve(SceneGraphComponent component);
  
  /**
   * 
   * <!-- begin-user-doc -->
   * Refreshes the content. Does no structural changes at all.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  void refresh();
  
  
  /**
   * 
   * <!-- begin-user-doc -->
   * Does a refresh of existing, adapts new components of source and removes deprecated components.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  void synchronize();
  
  
  /**
   * Disposes the factory.
   * @generated NOT
   */
  void dispose();
}
