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
package bitub.sgf.jreality.views.viewer;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import bitub.sgf.jreality.JRealityPlugin;
import bitub.sgf.jreality.event.JRealityPickingSupport;
import bitub.sgf.jreality.event.JRealityPickingSupport.Slot;
import bitub.sgf.jreality.util.JRealityUtil;
import de.jreality.jogl.JOGLViewer;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Rn;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Transformation;
import de.jreality.scene.event.CameraEvent;
import de.jreality.scene.event.CameraListener;
import de.jreality.shader.Color;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.DefaultTextShader;
import de.jreality.shader.RootAppearance;
import de.jreality.shader.ShaderUtility;
import de.jreality.toolsystem.ToolSystem;
import de.jreality.util.RenderTrigger;

/**
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated NOT
 * @author bernold - 03.06.2013
 */
public class JRealityBaseContentViewer extends JRealityContentViewer
{
  final static Logger myLog = Logger.getLogger(JRealityBaseContentViewer.class);

  /**
   * The embedded viewing component.
   */
  protected JRealityEmbeddedViewerComponent m_control;

  private de.jreality.scene.Viewer m_jrViewer;

  private SceneGraphComponent m_cameraComponent;
  private SceneGraphComponent m_lightsComponent;
  private SceneGraphComponent m_sceneRootComponent;
  private SceneGraphComponent m_root;

  private SceneGraphPath m_cameraPath;
  private SceneGraphPath m_avatarPath;

  private RenderTrigger m_renderTrigger;

  private Map<Slot, JRealityPickingSupport> m_pickSlotMap = new HashMap<>();
  
  final private double m_factor; 
  
  /**
   * <!-- begin-user-doc -->
   * A drop adapter which handles scene graph components dropped onto viewer.
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @author bernold - 03.06.2013
   */
  class DropLocalSelectionSupport extends ViewerDropAdapter
  {

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated NOT
     * @param viewer
     */
    protected DropLocalSelectionSupport(Viewer viewer)
    {
      super(viewer);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated NOT
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang.Object,
     *      int, org.eclipse.swt.dnd.TransferData)
     */
    @Override
    public boolean validateDrop(Object target, int operation, TransferData transferType)
    {
      return LocalSelectionTransfer.getTransfer().isSupportedType(transferType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang.Object)
     */
    @Override
    public boolean performDrop(Object data)
    {
      m_control.update();
      return false;
    }
  }


  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param parent
   * The parent.
   * @param factor
   * The factorization. Use > 1 to shift initial view position and far plane condition further away.
   * @generated NOT
   */
  public JRealityBaseContentViewer(Composite parent, double factor)
  {
    m_factor = factor;
    init();    
    m_control = new JRealityEmbeddedViewerComponent(parent, m_jrViewer);
    addDropTargetListener(new DropLocalSelectionSupport(this));
  }
  
  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @param parent
   */
  public JRealityBaseContentViewer(Composite parent)
  {
    this(parent, 1.0);
  }

  /**
   * 
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @param inputSlot
   * @return
   */
  public JRealityPickingSupport getPickingSupport(Slot inputSlot)
  {
    JRealityPickingSupport support = null;
    synchronized (this) {
      support = m_pickSlotMap.get(inputSlot);
      if (null == support) {
        m_pickSlotMap.put(inputSlot, support = createPickSupport(inputSlot));
      }
    }
    return support;
  }
  

  // Initialization
  //
  private void init()
  {
    m_root = new SceneGraphComponent("Root");
    m_root.addSceneGraphComponentListener(new SceneComponentStructureListener());
    
    m_lightsComponent = new SceneGraphComponent("Lights");
    initLights(m_lightsComponent, .90, .50);
    m_root.addChild(m_lightsComponent);

    m_sceneRootComponent = new SceneGraphComponent("Content root");
    MatrixBuilder.euclidean().assignTo(m_sceneRootComponent);
    m_root.addChild(m_sceneRootComponent);
    
    Appearance appearance = JRealityUtil.createDefaultAppearance(0, null, null, null);
    DefaultGeometryShader geometryShader = JRealityUtil.getGeometryShader(appearance);
    DefaultPointShader pointShader = (DefaultPointShader) geometryShader.getPointShader();
    DefaultPolygonShader polygonShader = (DefaultPolygonShader) geometryShader.getPolygonShader();
    
    pointShader.setDiffuseColor(Color.RED);
    pointShader.setSpheresDraw(true);
    
    DefaultTextShader textShader = (DefaultTextShader)polygonShader.getTextShader();
    textShader.setScale(0.004);
    textShader.setDiffuseColor(Color.BLUE);
    
    DefaultLineShader lineShader = (DefaultLineShader) geometryShader.getLineShader();
    lineShader.setDiffuseColor(Color.RED);
    lineShader.setTubeDraw(true); 
    
    m_sceneRootComponent.setAppearance(appearance);    

    // Create root appearance
    //
    final Appearance rootApp = new Appearance();
    RootAppearance rootAppearance = ShaderUtility.createRootAppearance(rootApp);
    switch(JRealityPlugin.DEFAULT_BACKGROUND.length) {
      case 0:
        rootAppearance.setBackgroundColor(new Color(255,255,255));
        break;
      case 1:
        rootAppearance.setBackgroundColor(JRealityPlugin.DEFAULT_BACKGROUND[0]);
        break;
      default:
        rootAppearance.setBackgroundColors(JRealityPlugin.DEFAULT_BACKGROUND);
        break;
    }    
    
    m_root.setAppearance(rootApp);

    SceneGraphComponent avatarSgc = new SceneGraphComponent("Avatar");
    m_root.addChild(avatarSgc);

    // Initial camera position
    MatrixBuilder.euclidean().translate(0., 0., m_factor*30.).assignTo(avatarSgc);

    m_cameraComponent = new SceneGraphComponent("Camera");
    avatarSgc.addChild(m_cameraComponent);

    Camera camera = initCamera(m_cameraComponent);
    m_cameraPath = new SceneGraphPath(m_root, avatarSgc, m_cameraComponent);
    m_cameraPath.push(camera);

    MatrixBuilder.euclidean().translate(0., m_factor*1.7, 0.).assignTo(m_cameraComponent);

    m_avatarPath = new SceneGraphPath(m_root, avatarSgc);

    // Init viewer
    m_jrViewer = new JOGLViewer();

    m_jrViewer.setSceneRoot(m_root);
    m_jrViewer.setCameraPath(m_cameraPath);
    ToolSystem toolSystem = ToolSystem.toolSystemForViewer(m_jrViewer);
    toolSystem.initializeSceneTools();
    
    SceneGraphPath path = new SceneGraphPath(m_root, m_sceneRootComponent);
    toolSystem.setEmptyPickPath(path);
    
    m_renderTrigger = new RenderTrigger();
    m_renderTrigger.addSceneGraphComponent(m_root);
    m_renderTrigger.addViewer(m_jrViewer);    
  }

  private void initLights(SceneGraphComponent lights, double sunIntensity, double skyIntensity)
  {
    SceneGraphComponent sun = new SceneGraphComponent("Sun");
    DirectionalLight sunLight = new DirectionalLight("Sun light");
    sunLight.setIntensity(skyIntensity);
    sun.setLight(sunLight);
    MatrixBuilder.euclidean().rotateFromTo(new double[] { 0, 0, 1 }, new double[] { 0, 1, 1 }).assignTo(sun);
    lights.addChild(sun);

    SceneGraphComponent sky = new SceneGraphComponent("Sky");
    DirectionalLight skyLight = new DirectionalLight();
    skyLight.setIntensity(skyIntensity);
    skyLight.setAmbientFake(true);
    skyLight.setName("Sky light");
    sky.setLight(skyLight);
    MatrixBuilder.euclidean().rotateFromTo(new double[] { 0, 0, 1 }, new double[] { 0, 1, 0 }).assignTo(sky);
    lights.addChild(sky);
  }

  private Camera initCamera(SceneGraphComponent cameraNode)
  {
    cameraNode.setName("Camera");
    cameraNode.setVisible(true);
    double[] trafoMatrix = Rn.identityMatrix(4);

    Transformation trafo = new Transformation("Camera trafo", trafoMatrix);
    cameraNode.setTransformation(trafo);
    Camera camera = new Camera("Camera");

    camera.setFar(m_factor*100.0);
    camera.setFieldOfView(60.0);
    camera.setFocus(3.0);
    camera.setNear(0.1);
    
    camera.setOnAxis(true);
    camera.setStereo(false);
    cameraNode.setCamera(camera);
    camera.addCameraListener(new CameraListener() {
      
      private boolean inNearFix = false;
      
      @Override
      public void cameraChanged(CameraEvent arg0)
      {
        if(!inNearFix && arg0.getCamera().getNear() > 0.1) {
          
          inNearFix = true;
          arg0.getCamera().setNear(0.1);
          inNearFix = false;
        } 
      }
    });

    return camera;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see org.eclipse.jface.viewers.Viewer#getControl()
   */
  @Override
  public Control getControl()
  {
    return m_control;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#getSceneNode()
   */
  @Override
  public SceneGraphComponent getSceneNode()
  {
    return m_sceneRootComponent;
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#getRootNode()
   */
  @Override
  public SceneGraphComponent getRootNode()
  {
    return m_root;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#transformRootContentNode(de.jreality.math.MatrixBuilder)
   */
  @Override
  public void transformRootContentNode(MatrixBuilder mb)
  {
    if (null != mb) {
      mb.assignTo(m_sceneRootComponent);
    }
  }

  /**
   * 
   * <!-- begin-user-doc -->
   * The length multiplier.
   * <!-- end-user-doc -->
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#getFactor()
   */
  @Override
  public double getFactor()
  {
    return m_factor;
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#getAvatarPath()
   */
  @Override
  public SceneGraphPath getAvatarPath()
  {
    return m_avatarPath;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#getEmbeddedViewer()
   */
  @Override
  public de.jreality.scene.Viewer getEmbeddedViewer()
  {
    return m_jrViewer;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated NOT
   * @see bitub.sgf.jreality.views.viewer.JRealityContentViewer#getCamaraPath()
   */
  @Override
  public SceneGraphPath getCamaraPath()
  {
    return m_cameraPath;
  }
}
