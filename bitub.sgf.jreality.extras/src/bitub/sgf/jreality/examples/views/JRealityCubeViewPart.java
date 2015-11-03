/*
 * Copyright (c) 2012-2015 Bernold Kraft (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Initial commit by bernold @ 27.06.2012.
 */

package bitub.sgf.jreality.examples.views;

import java.awt.Color;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import bitub.sgf.jreality.JRealityPlugin.ViewCoordinationSystem;
import bitub.sgf.jreality.util.JRealityUtil;
import bitub.sgf.jreality.views.JRealityBaseViewPart;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.IndexedFaceSet;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.Transformation;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.tools.DraggingTool;
import de.jreality.tools.RotateTool;
import de.jreality.tools.ShipNavigationTool;

/**
 * <!-- begin-user-doc -->
 * A simple JReality test view displaying a cube of cubes.
 * <!-- end-user-doc -->
 * 
 * @generated NOT
 * @author bernold - 27.06.2012
 */
public class JRealityCubeViewPart extends JRealityBaseViewPart
{
  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "bitub.sgf.jreality.examples.views.JRealityCubeViewPart";

  /**
   * Standard cube of width = 1.0.
   */
  double[][] defaultCubeVertices = new double[][] { 
	  { 0, 0, 0 }, { 1, 0, 0 }, { 1, 1, 0 }, { 0, 1, 0 }, { 0, 0, 1 }, { 1, 0, 1 },
      { 1, 1, 1 }, { 0, 1, 1 } };

  /**
   * Faces given counter clockwise.
   */
  int[][] defaultFaceIndices = new int[][] { 
	  { 3, 2, 1, 0 }, { 4, 5, 6, 7 }, { 0, 1, 5, 4 }, { 2, 3, 7, 6 }, { 1, 2, 6, 5 },
      { 3, 0, 4, 7 } };

      
  public JRealityCubeViewPart()
  {
    super(ViewCoordinationSystem.Axis, 1.5);
  }
      
  /**
   * <!-- begin-user-doc -->
   * Overridden. Adds own component.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see bitub.sgf.jreality.views.JRealityViewPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPartControl(Composite parent)
  {
    super.createPartControl(parent);

    SceneGraphComponent sgc = createCubes(1, 1.5, 10, 500);
    Appearance appearance = new Appearance();
    sgc.setAppearance(appearance);

    SceneGraphComponent component = getViewer().getAvatarPath().getLastComponent();
    MatrixBuilder.euclidean().translate(1.5 * 5, 1.5 * 5, 30).assignTo(component);

    getViewer().getSceneNode().addTool(new DraggingTool());
    component.addTool(new ShipNavigationTool());
    getViewer().getSceneNode().addTool(new RotateTool());

    // Forward to viewer
    getViewer().setInput(sgc);
  }

  /**
   * <!-- begin-user-doc -->
   * Cubes example. Generates a cube of cubes by given raster and cube count.
   * Operates as load test.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param cubeWidth
   * @param rasterWidth
   * @param rasterCount
   * @param cubeCount
   * @return
   */
  private SceneGraphComponent createCubes(double cubeWidth, double rasterWidth, int rasterCount, int cubeCount)
  {
    SceneGraphComponent sgcAll = new SceneGraphComponent("Cubes");

    Appearance appearance = new Appearance();
    DefaultGeometryShader geometryShader = JRealityUtil.enableGeometryDisplayFlags(appearance, true, false, false);
    DefaultPointShader pointShader = (DefaultPointShader) geometryShader.getPointShader();
    pointShader.setSpheresDraw(false);

    sgcAll.setAppearance(appearance);

    Random random = new Random(System.currentTimeMillis());
    Set<String> coordCache = new HashSet<>();

    for (int i = 0; i < cubeCount; i++) {
    	
      int c = 0;
      do {
        c++;

        int dx1 = (int) Math.floor(random.nextDouble() * rasterCount);
        int dx2 = (int) Math.floor(random.nextDouble() * rasterCount);
        int dx3 = (int) Math.floor(random.nextDouble() * rasterCount);

        String key = String.format("%1$d:%2$d:%3$d", dx1, dx2, dx3);

        if (!coordCache.contains(key)) {
        	
          SceneGraphComponent sgc = new SceneGraphComponent("Cube #" + i);
          double[] trans =
              new double[] { 1, 0, 0, dx1 * rasterWidth, 0, 1, 0, dx2 * rasterWidth, 0, 0, 1, dx3 * rasterWidth, 0, 0, 0, 1 };
          sgc.setTransformation(new Transformation(trans));
          IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();

          ifsf.setVertexCount(defaultCubeVertices.length);
          ifsf.setVertexCoordinates(defaultCubeVertices);

          final Color color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());

          ifsf.setFaceCount(defaultFaceIndices.length);
          ifsf.setFaceIndices(defaultFaceIndices);
          ifsf.setGenerateFaceNormals(true);
          ifsf.update();

          IndexedFaceSet indexedFaceSet = ifsf.getIndexedFaceSet();
          JRealityUtil.setFaceColors(indexedFaceSet, color);
          sgc.setGeometry(indexedFaceSet);

          sgcAll.addChild(sgc);

          coordCache.add(key);
          break;
        }
      } while (c < 1000);
    }

    return sgcAll;
  }

}