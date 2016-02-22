/*
 * Copyright (c) 2012-2015 Bernold Kraft (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Initial commit by bernold @ 31.10.2012.
 */
package bitub.sgf.jreality.util;

import java.awt.Color;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.Primitives;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.IndexedFaceSet;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.StorageModel;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.RenderingHintsShader;
import de.jreality.shader.ShaderUtility;

/**
 * <!-- begin-user-doc -->
 * A helper utility for JReality integration.
 * <!-- end-user-doc -->
 * 
 * @generated NOT
 * @author bernold - 31.10.2012
 */
public final class JRealityUtil
{

  /**
   * <!-- begin-user-doc --> 
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param faceSet
   * @param label
   */
  public static void labelFaces(final IndexedFaceSet faceSet, String... label)
  {
    final String[] labels = new String[faceSet.getNumFaces()];
    for (int i = 0; i < labels.length; i++) {
      if (i < label.length)
        labels[i] = label[i];
      else
        labels[i] = "";
    }
    faceSet.setFaceAttributes(Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly(labels));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param faceSet
   * @param label
   */
  public static void labelVertices(final IndexedFaceSet faceSet, String... label)
  {
    final String[] labels = new String[faceSet.getNumPoints()];
    for (int i = 0; i < labels.length; i++) {
      if (i < label.length)
        labels[i] = label[i];
      else
        labels[i] = "";
    }
    faceSet.setVertexAttributes(Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly(labels));
  }
  
  public static SceneGraphComponent createGrid(double wx, double wy, int nx, int ny, Color c)
  {
    SceneGraphComponent gridComponent = new SceneGraphComponent("XY Grid");

    IndexedFaceSetFactory faceSetFactory = new IndexedFaceSetFactory();
    int count = (nx + 1) * (ny + 1);

    faceSetFactory.setVertexCount(count);
    faceSetFactory.setFaceCount(nx*ny);

    double dx = wx / nx;
    double dy = wy / ny;

    double[][] gridPoints = new double[count][3];
    int[][] quads = new int[nx * ny][4];

    for (int i = 0; i <= ny; i++) {

      for (int j = 0; j <= nx; j++) {

        gridPoints[i * (nx+1) + j] = new double[] { i * dx, j * dy, 0 };
      }      
    }
    
    for(int i=0; i<ny; i++) {
    	
        for(int j = 0; j<nx; j++) {
      	  
      	  quads[i*nx + j] = new int[]{ i*(nx+1) + j, i*(nx+1) + j +1, (i+1)*(nx+1) + j +1, (i+1)*(nx+1) + j };
        }    	
    }    	

    faceSetFactory.setVertexCoordinates(gridPoints);    
    faceSetFactory.setFaceIndices(quads);
    faceSetFactory.update();
    
    gridComponent.setGeometry(faceSetFactory.getGeometry());
    
    return gridComponent;
  }

  /**
   * <!-- begin-user-doc -->
   * Generates an 3D arrow comprised of a cylinder and a cone at the direction
   * end.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param scale
   * @param gran
   * @param label
   * @param initialVector
   *          An initial direction vector. The length of vector will be the
   *          length of cylinder.
   * @return
   */
  public static SceneGraphComponent createConeArrow(SceneGraphComponent coneArrow, Color c, double scale, int gran, String label,
      double[] initialVector)
  {
    coneArrow.setName(label);
    Appearance appearance = JRealityUtil.createDefaultAppearance(0, true, false, false);
    JRealityUtil.enableRenderingFlags(appearance, false, false, false);
    coneArrow.setAppearance(appearance);

    SceneGraphComponent arrow = new SceneGraphComponent(label + "Cylinder"); //$NON-NLS-1$
    IndexedFaceSet cylinderGeometry = Primitives.cylinder(gran, 0.1, 0., 0.99, Math.PI * 2.);
    setFaceColors(cylinderGeometry, c);
    arrow.setGeometry(cylinderGeometry);

    SceneGraphComponent cone = new SceneGraphComponent(label + "Cone"); //$NON-NLS-1$
    IndexedFaceSet coneGeometry = Primitives.cone(gran, 1.0);
    setFaceColors(coneGeometry, c);
    labelFaces(coneGeometry, label);
    cone.setGeometry(coneGeometry);

    coneArrow.addChild(arrow);
    coneArrow.addChild(cone);

    double sqrt =
        Math.sqrt(initialVector[0] * initialVector[0] + initialVector[1] * initialVector[1] + initialVector[2] * initialVector[2]);
    // Scale cone and translate it to the end of cylinder 
    MatrixBuilder.euclidean().translate(new double[] { 0., 0., 1. * scale * sqrt }).scale(0.25, 0.25, 0.5).assignTo(cone);
    // Scale arrow along Z axis
    MatrixBuilder.euclidean().scale(1., 1., sqrt * scale).assignTo(arrow);
    // Rotate both to target vector
    MatrixBuilder.euclidean().rotateFromTo(new double[] { 0., 0., 1. }, initialVector).assignTo(coneArrow);

    return coneArrow;
  }

  /**
   * <!-- begin-user-doc -->
   * Creates an axis cross consisting of three coned arcs of length 1.0 tick.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param gran
   *          How many subdevision of circle cross section to do.
   * @return
   */
  public static SceneGraphComponent createAxisComponent(int gran, double scale, double unityLength, Color c)
  {
    SceneGraphComponent axis = new SceneGraphComponent("Axis");

    axis.addChild(createConeArrow(new SceneGraphComponent(), c, scale, gran, "x", new double[] { unityLength, 0., 0. })); //$NON-NLS-1$
    axis.addChild(createConeArrow(new SceneGraphComponent(), c, scale, gran, "y", new double[] { 0., unityLength, 0. })); //$NON-NLS-1$
    axis.addChild(createConeArrow(new SceneGraphComponent(), c, scale, gran, "z", new double[] { 0., 0., unityLength })); //$NON-NLS-1$

    return axis;
  }

  /**
   * <!-- begin-user-doc -->
   * Creates an axis cross consisting of three coned arcs of length 1.0 tick.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param gran
   *          How many subdevision of circle cross section to do.
   * @return
   */
  public static SceneGraphComponent createAxisComponent(int gran, Color c)
  {
    SceneGraphComponent axis = new SceneGraphComponent("Axis");

    axis.addChild(createConeArrow(new SceneGraphComponent(), c, 1.0, gran, "x", new double[] { 1., 0., 0. })); //$NON-NLS-1$
    axis.addChild(createConeArrow(new SceneGraphComponent(), c, 1.0, gran, "y", new double[] { 0., 1., 0. })); //$NON-NLS-1$
    axis.addChild(createConeArrow(new SceneGraphComponent(), c, 1.0, gran, "z", new double[] { 0., 0., 1. })); //$NON-NLS-1$

    return axis;
  }

  /**
   * <!-- begin-user-doc -->
   * Creates an axis cross consisting of three coned arcs of length 1.0 tick.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param scale
   * @return
   */
  public static SceneGraphComponent createAxisComponent(double scale, int gran, String[] labelXYZ)
  {
    final SceneGraphComponent axis = new SceneGraphComponent("Axis");

    assert labelXYZ.length == 3;

    final SceneGraphComponent x = new SceneGraphComponent(labelXYZ[0]);
    final SceneGraphComponent y = new SceneGraphComponent(labelXYZ[1]);
    final SceneGraphComponent z = new SceneGraphComponent(labelXYZ[2]);

    final IndexedFaceSet cylinderX = Primitives.cylinder(gran, 0.1 * scale, 0., 0.99 * scale, Math.PI * 2.);
    final IndexedFaceSet cylinderY = Primitives.cylinder(gran, 0.1 * scale, 0., 0.99 * scale, Math.PI * 2.);
    final IndexedFaceSet cylinderZ = Primitives.cylinder(gran, 0.1 * scale, 0., 0.99 * scale, Math.PI * 2.);

    labelFaces(cylinderX, labelXYZ[0]);
    labelFaces(cylinderY, labelXYZ[1]);
    labelFaces(cylinderZ, labelXYZ[2]);

    x.setGeometry(cylinderX);
    y.setGeometry(cylinderY);
    z.setGeometry(cylinderZ);

    final SceneGraphComponent coneX = new SceneGraphComponent("coneX"); //$NON-NLS-1$
    final SceneGraphComponent coneY = new SceneGraphComponent("coneY"); //$NON-NLS-1$
    final SceneGraphComponent coneZ = new SceneGraphComponent("coneZ"); //$NON-NLS-1$

    coneX.setGeometry(Primitives.cone(gran, scale));
    coneY.setGeometry(Primitives.cone(gran, scale));
    coneZ.setGeometry(Primitives.cone(gran, scale));

    MatrixBuilder.euclidean().rotate(-Math.PI / 2., new double[] { 0., -1., 0. }).assignTo(x);
    MatrixBuilder.euclidean().rotate(-Math.PI / 2., new double[] { 1., 0., 0. }).assignTo(y);

    MatrixBuilder.euclidean().translate(new double[] { 0., 0., 1. * scale }).scale(0.25 * scale).assignTo(coneX);

    MatrixBuilder.euclidean().translate(new double[] { 0., 0., 1. * scale }).scale(0.25 * scale).assignTo(coneY);

    MatrixBuilder.euclidean().translate(new double[] { 0., 0., 1. * scale }).scale(0.25 * scale).assignTo(coneZ);

    axis.addChild(x);
    axis.addChild(y);
    axis.addChild(z);

    x.addChild(coneX);
    y.addChild(coneY);
    z.addChild(coneZ);

    Appearance app = new Appearance();
    enableGeometryDisplayFlags(app, true, false, false);
    enableRenderingFlags(app, false, false, false);
    axis.setAppearance(app);

    return axis;
  }

  /**
   * <!-- begin-user-doc -->
   * Sets the point shader colors.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param app
   * @param diffColor
   * @param specColor
   * @param ambColor
   */
  public static void setPointShaderColors(Appearance app, Color diffColor, Color specColor, Color ambColor)
  {
    app.setAttribute(CommonAttributes.POINT_SHADER + "." + CommonAttributes.DIFFUSE_COLOR, diffColor); //$NON-NLS-1$
    app.setAttribute(CommonAttributes.POINT_SHADER + "." + CommonAttributes.SPECULAR_COLOR, specColor); //$NON-NLS-1$
    app.setAttribute(CommonAttributes.POINT_SHADER + "." + CommonAttributes.AMBIENT_COLOR, ambColor); //$NON-NLS-1$
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param app
   * @param diffColor
   * @param specColor
   * @param ambColor
   */
  public static void setLineShaderColors(Appearance app, Color diffColor, Color specColor, Color ambColor)
  {
    app.setAttribute(CommonAttributes.LINE_SHADER + "." + CommonAttributes.DIFFUSE_COLOR, diffColor); //$NON-NLS-1$
    app.setAttribute(CommonAttributes.LINE_SHADER + "." + CommonAttributes.SPECULAR_COLOR, specColor); //$NON-NLS-1$
    app.setAttribute(CommonAttributes.LINE_SHADER + "." + CommonAttributes.AMBIENT_COLOR, ambColor); //$NON-NLS-1$
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param app
   * @param diffColor
   * @param specColor
   * @param ambColor
   */
  public static void setPolygonShaderColors(Appearance app, Color diffColor, Color specColor, Color ambColor)
  {
    app.setAttribute(CommonAttributes.POLYGON_SHADER + "." + CommonAttributes.DIFFUSE_COLOR, diffColor); //$NON-NLS-1$
    app.setAttribute(CommonAttributes.POLYGON_SHADER + "." + CommonAttributes.SPECULAR_COLOR, specColor); //$NON-NLS-1$
    app.setAttribute(CommonAttributes.POLYGON_SHADER + "." + CommonAttributes.AMBIENT_COLOR, ambColor); //$NON-NLS-1$
  }

  /**
   * <!-- begin-user-doc -->
   * Generates an default appearance with backface culling enabled and
   * non-flipped face normals.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param transparency
   * @param showEdges
   * @param showVertices
   * @param showFaces
   * @return
   */
  public static Appearance createDefaultAppearance(double transparency, Boolean showFaces, Boolean showEdges, Boolean showVertices)
  {
    Appearance app = new Appearance();

    // Rendering hints
    enableRenderingFlags(app, (transparency > 0) ? true : null, null, null);

    // Geometry shader
    //
    DefaultGeometryShader defaultGeometryShader = enableGeometryDisplayFlags(app, showFaces, showEdges, showVertices);
    DefaultPolygonShader polygonShader = (DefaultPolygonShader) defaultGeometryShader.getPolygonShader();

    if (transparency > 0) {
      polygonShader.setTransparency(transparency);
      polygonShader.setSmoothShading(false);
    }

//    DefaultLineShader lineShader = (DefaultLineShader) defaultGeometryShader.getLineShader();
//    lineShader.setTubeDraw(false);
//
//    DefaultPointShader pointShader = (DefaultPointShader) defaultGeometryShader.getPointShader();
//    pointShader.setSpheresDraw(false);

    return app;
  }

  /**
   * <!-- begin-user-doc -->
   * Gets or creates the default geometry shader.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param app
   * @return
   */
  public static DefaultGeometryShader getGeometryShader(Appearance app)
  {
    DefaultGeometryShader geometryShader = ShaderUtility.createDefaultGeometryShader(app, true);
    return geometryShader;
  }

  /**
   * <!-- begin-user-doc -->
   * Sets the geometry flags.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param app
   * @param showFaces
   *          Show faces at all.
   * @param showEdges
   *          Show edges.
   * @param showVertices
   *          Show vertices.
   */
  public static DefaultGeometryShader enableGeometryDisplayFlags(Appearance app, Boolean showFaces, Boolean showEdges,
      Boolean showVertices)
  {
    DefaultGeometryShader geometryShader = ShaderUtility.createDefaultGeometryShader(app, true);
    if (null != showFaces) {
      geometryShader.setShowFaces(showFaces);
    }
    if (null != showEdges) {
      geometryShader.setShowLines(showEdges);
    }
    if (null != showVertices) {
      geometryShader.setShowPoints(showVertices);
    }

    return geometryShader;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param app
   * @param transparency
   * @param showFaces
   * @param showEdges
   * @param showVertices
   * @return
   */
  public static DefaultGeometryShader enableGeometryDisplayFlags(Appearance app, double transparency, Boolean showFaces,
      Boolean showEdges, Boolean showVertices)
  {
    DefaultGeometryShader defaultGeometryShader = enableGeometryDisplayFlags(app, showFaces, showEdges, showVertices);
    DefaultPolygonShader polygonShader = (DefaultPolygonShader) defaultGeometryShader.getPolygonShader();
    if (transparency > 0) {
      polygonShader.setTransparency(transparency);
      polygonShader.setSmoothShading(false);
    }

    return defaultGeometryShader;
  }

  /**
   * <!-- begin-user-doc -->
   * Sets the rendering flags.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param app
   * @param transparency
   *          Switch for transparency at all.
   * @param backFaceCulling
   *          Enable back face culling.
   * @param flipNormals
   *          Flip normals (true -> normals point to inside of complex).
   */
  public static RenderingHintsShader enableRenderingFlags(Appearance app, Boolean transparency, Boolean backFaceCulling,
      Boolean flipNormals)
  {
    RenderingHintsShader renderingHintsShader = ShaderUtility.createDefaultRenderingHintsShader(app, true);
    if (null != transparency) {
      renderingHintsShader.setTransparencyEnabled(transparency);
    }
    if (null != backFaceCulling) {
      renderingHintsShader.setBackFaceCulling(backFaceCulling);
    }
    if (null != flipNormals) {
      renderingHintsShader.setFlipNormals(flipNormals);
    }
    if (null != backFaceCulling) {
      renderingHintsShader.setZBufferEnabled(backFaceCulling);
    }

    return renderingHintsShader;
  }

  /**
   * <!-- begin-user-doc -->
   * Sets the face colors in a cycling order. If only one color is given, every
   * face has the same color.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param faceSet
   * @param colors
   */
  public static void setFaceColors(IndexedFaceSet faceSet, Color... colors)
  {
    setFaceColors(faceSet, 0, colors);
  }

  /**
   * <!-- begin-user-doc -->
   * Sets the face colors in a cycling order. If only one color is given, every
   * face has the same color.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @param faceSet
   * @param offset
   *          The first face to colorize.
   * @param colors
   */
  public static void setFaceColors(IndexedFaceSet faceSet, int offset, Color... colors)
  {
    final double[][] cArray = new double[faceSet.getNumFaces()][3];
    for (int i = offset; i < faceSet.getNumFaces(); i++) {

      cArray[i] =
          new double[] { (double) colors[i % colors.length].getRed() / 255.d,
              (double) colors[i % colors.length].getGreen() / 255.d, (double) colors[i % colors.length].getBlue() / 255.d };
    }

    faceSet.setFaceAttributes(Attribute.COLORS, StorageModel.DOUBLE_ARRAY.array(3).createReadOnly(cArray));
  }

}
