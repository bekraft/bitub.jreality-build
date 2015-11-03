/*
 * Copyright (c) 2012-2015 Bernold Kraft (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Initial commit by bernold @ 03.06.2013.
 */
package bitub.sgf.jreality.commands;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

import bitub.sgf.jreality.views.JRealityViewPart;
import bitub.sgf.jreality.views.viewer.JRealityContentViewer;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Camera;
import de.jreality.scene.SceneGraphPath;

/**
 * <!-- begin-user-doc -->
 * Resets the avatar position.
 * <!-- end-user-doc -->
 * 
 * @generated NOT
 * @author bernold - 03.06.2013
 */
public class CommandResetPositionAndCameraView extends GenericJRealityHandler implements IHandler
{
  final static Logger myLog = Logger.getLogger(CommandResetPositionAndCameraView.class);
  
  /**
   * Initial avatar position.
   */
  public static double[] INITIAL_AVATAR_POSITION = { 0, 2, 30 };

  /**
   * Initial view direction.
   */
  public static double[] INITIAL_VIEW_DIRECTION = { 0, 0, -1 };

  /**
   * The height of avatar.
   */
  public static double AVATAR_HEIGHT = 1.7;

  // Scales a vector
  static double[] scaleV(double[] x, double s)
  {
    for(int i=0;i<x.length;i++) {
      
      x[i] *= s;
    }
    return x;
  }
  
  /**
   * <!-- begin-user-doc -->
   * Resets the position of avatar. Resets the transformation of camera and
   * scene root.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param viewer
   */
  public static void resetTransformationOf(JRealityContentViewer viewer)
  {
    SceneGraphPath avatarPath = ((JRealityContentViewer) viewer).getAvatarPath();
    SceneGraphPath camaraPath = ((JRealityContentViewer) viewer).getCamaraPath();
    
    double[] avatarX = scaleV(Arrays.copyOf(INITIAL_AVATAR_POSITION,INITIAL_AVATAR_POSITION.length), viewer.getFactor());

    // Set initial viewer positions
    //
    if (avatarPath.getLastComponent() != camaraPath.getLastComponent()) {
      
      MatrixBuilder
          .euclidean()
          .translate(0, AVATAR_HEIGHT * viewer.getFactor(), 0)
          .rotateFromTo(new double[] { 0, 0, -1 }, INITIAL_VIEW_DIRECTION)
          .assignTo(camaraPath.getLastComponent());
      
      MatrixBuilder
          .euclidean()
          .translate(avatarX)
          .assignTo(avatarPath.getLastComponent());
    } else {
      
      MatrixBuilder
          .euclidean()
          .translate(avatarX)
          .translate(0, AVATAR_HEIGHT * viewer.getFactor(), 0)
          .rotateFromTo(new double[] { 0, 0, -1 }, INITIAL_VIEW_DIRECTION)
          .assignTo(camaraPath.getLastComponent());
    }

    // Rotate 90 deg by X axis (Z pointing upwards)
    //
    viewer.transformRootContentNode(MatrixBuilder.euclidean().rotate(-Math.PI / 2, new double[] { 1, 0, 0 }));

    Camera camera = camaraPath.getLastComponent().getCamera();
    if (null != camera) {
      camera.setFar(100.0 * viewer.getFactor());
    }
  }


  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    String activePartId = HandlerUtil.getActivePartId(event);
    myLog.debug(String.format("Reseting view on active part [%s].",activePartId));
    
    JRealityViewPart viewPart = getView(event,activePartId);
    
    if (null != viewPart) {

      resetTransformationOf(viewPart.getViewer());
      return Boolean.TRUE;
    }

    return Boolean.FALSE;
  }

}
