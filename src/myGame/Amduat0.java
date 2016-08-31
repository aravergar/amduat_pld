package myGame;

import com.jme3.ai.navmesh.NavMesh;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import entities.MovingEntity;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.input.ChaseCamera;
import com.jme3.scene.Node;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import entities.Player;
import entities.Spawner;
import entities.Swarg;

/**
 * test
 * @author normenhansen
 */
public class Amduat0 extends SimpleApplication {

    public static void main(String[] args) {
        Amduat0 app = new Amduat0();
        app.start();
    }

    Boolean isRunning=true;
    Boolean isRotating=false;
    protected ChaseCamera chaseCam;
    private Node gameLevel;
    private BulletAppState bulletAppState;
    private Node mobs;
    private Node playerNode;
    
    private Node monsNode;
    private MovingEntity monsControl;
    
    private AnimControl control;
    private AnimChannel channel;
    private Player player;
    private Node spawnerNode;
    private Spawner swargSpawner;
    
    public Amduat0() {
        super(new com.jme3.app.StatsAppState(), new com.jme3.app.DebugKeysAppState(),
                new gameStates.InGameState());
    }
    
//    private void setupKeys(){
//        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
//        inputManager.addListener(this,"Jump");
//    }
    
    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        inputManager.setCursorVisible(true);
        com.jme3.light.DirectionalLight sun = new com.jme3.light.DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);
        
        gameLevel = (Node) assetManager.loadModel("Scenes/testScene.j3o");
        
        CollisionShape sceneShape =
            CollisionShapeFactory.createMeshShape(gameLevel.getChild("testTerrain"));
        gameLevel.addControl(new RigidBodyControl(sceneShape, 0));
        rootNode.attachChild(gameLevel);
        
        createPlayer();
        player.betterControl.warp(new Vector3f(0,0,0));
        
//        Spatial swModel = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
//        monsNode = new Node();
//        monsNode.attachChild(swModel);
//        swModel.setLocalTranslation(0, 0.5f, 0);
//        swModel.setLocalScale(0.12f);
//        monsControl = new Swarg(player);
//        monsNode.addControl(monsControl);
//        monsNode.setName("swarg");
//        monsControl.betterControl.warp(new Vector3f(5,0,0));
        
        bulletAppState.getPhysicsSpace().add(gameLevel);
//        bulletAppState.getPhysicsSpace().addAll(monsNode);
        
        mobs = new Node();
        mobs.setName("mobs");
        gameLevel.attachChild(mobs);
        createSpawner();
        
//        mobs = new Node();
//        mobs.attachChild(monsNode);
//        mobs.setName("mobs");
//        gameLevel.attachChild(mobs);
//        NavMesh swargNav = new NavMesh(((Geometry)this.gameLevel.getChild("navMesh")).getMesh());
//        NavMeshPathfinder swargFinder = new NavMeshPathfinder(swargNav);
//        monsControl.setNavMesh(swargFinder);
        
        chaseCam = new ChaseCamera(cam, playerNode);
        chaseCam.setDefaultDistance(5f);
        chaseCam.setMinDistance(0.10f);
        chaseCam.setMaxDistance(15f);
        chaseCam.setTrailingEnabled(false);
        chaseCam.setDefaultVerticalRotation(0.50f);
        chaseCam.setLookAtOffset(chaseCam.getLookAtOffset().addLocal(2.5f, 2.5f, 0f));
        
        this.stateManager.attach(new gameStates.InWorldState(player, chaseCam));
        this.stateManager.getState(gameStates.InWorldState.class).setEnabled(true);
        this.stateManager.getState(gameStates.InWorldState.class).enableAnim();
        bulletAppState.setDebugEnabled(true);
        swargSpawner.enableSpawn(true);
    }
    
    private void createPlayer() {
        playerNode = new Node();
        Spatial playerModel = assetManager.loadModel("Models/Sinbad.mesh.j3o");
        playerModel.setName("playerModel");
        playerNode.attachChild(playerModel);
        playerModel.setLocalScale(0.2f);
        playerModel.setLocalTranslation(0, 1f, 0);
        player = new Player();
        playerNode.addControl(player);
        playerNode.setName("player");
        player.setAnim();
        bulletAppState.getPhysicsSpace().addAll(playerNode);
        gameLevel.attachChild(playerNode);
    }
    
    private void createSpawner() {
        spawnerNode = new Node();
        swargSpawner = new Spawner (Spawner.MonsterType.SWARG, player, assetManager, bulletAppState);
        spawnerNode.addControl(swargSpawner);
        spawnerNode.setName("spawner");
        gameLevel.attachChild(spawnerNode);
        swargSpawner.setWaves(5, 5, 1.5f);
        NavMesh swargNav = new NavMesh(((Geometry)this.gameLevel.getChild("navMesh")).getMesh());
        NavMeshPathfinder swargFinder = new NavMeshPathfinder(swargNav);
        swargSpawner.setNavMesh(swargNav, swargFinder);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
//        player.rotate(0f, 2f*tpf, 0f);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
