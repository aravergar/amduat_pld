/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameStates;

import entities.MovingEntity;
import com.jme3.ai.navmesh.NavMesh;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import entities.Monster;
import entities.Player;

/**
 *
 * @author tanatarca
 */
//private Port          viewPort;
public class InWorldState extends com.jme3.app.state.AbstractAppState implements AnimEventListener {
    private myGame.Amduat0 app;
    private Node rootNode;
    private Node gameLevel;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private BulletAppState physics;
    private ChaseCamera camera;
    private Player playerControl;
    private NavMesh navMesh;
    private NavMeshPathfinder playerNav;
    private boolean isClicking = false;
    private MovingEntity target;
    private Player.Action action;
    private AnimControl animControl;
    private AnimChannel topChannel, bottomChannel, topCh, botCh;
    
    public InWorldState(Player playerControl, ChaseCamera chaseCam){
        this.playerControl = playerControl;
        this.camera = chaseCam;
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if(animName.equals("SliceVertical") || animName.equals("SliceHorizontal"))
            topChannel.setAnim("HandsRelaxed");
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {

    }
    
    public enum SpellSlots {
        Q, W, E, R, NULL
    }
    private SpellSlots activeSpell;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        this.app = (myGame.Amduat0) app; // can cast Application to something more specific
        this.rootNode = this.app.getRootNode();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.physics = this.stateManager.getState(BulletAppState.class);
        this.gameLevel = (Node)this.rootNode.getChild("gameLevel");
        navMesh = new NavMesh(((Geometry)this.gameLevel.getChild("navMesh")).getMesh());
        playerNav = new NavMeshPathfinder(navMesh);
        this.playerControl.setNavMesh(playerNav);
        action = Player.Action.NULL;
//        animControl = playerControl.getSpatial().getControl(AnimControl.class);
//        animControl.addListener(this);
//        topChannel = animControl.createChannel();
//        topChannel.setAnim("HandsRelaxed");
//        bottomChannel = animControl.createChannel();
//        bottomChannel.setAnim("IdleBase");
        
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Q", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("W", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("E", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("R", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("LClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("RClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("Left", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("Right", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("Down", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("Up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addListener(actionListener,"Jump","Move","Stop","View","LClick","RClick");
        inputManager.addListener(analogListener,"Left","Right","Down","Up","ZoomOut","ZoomIn");
    }
    
    public void enableAnim() {
        Node animNode = (Node)playerControl.getSpatial();
        animControl = animNode.getChild("playerModel").getControl(AnimControl.class);
//        animControl = playerControl.getSpatial().getControl(AnimControl.class);
        animControl.addListener((AnimEventListener)this);
        topChannel = animControl.createChannel();
        topChannel.setAnim("HandsRelaxed");
        bottomChannel = animControl.createChannel();
        bottomChannel.setAnim("IdleBase");
        botCh = playerControl.bottomChannel;
        topCh = playerControl.topChannel;
    }
    
    public void setPlayerControl(Player playerControl) {
        this.playerControl = playerControl;
//        System.out.println(playerNav);
        this.playerControl.setNavMesh(playerNav);
    }

    public MovingEntity getPlayerControl() {
        return playerControl;
    }

    public void setNavMesh(NavMesh navMesh) {
        this.navMesh = navMesh;
    }

    public NavMesh getNavMesh() {
        return navMesh;
    }
    
    public void setCamera(ChaseCamera camera) {
        this.camera = camera;
    }
    
    public ChaseCamera getCamera() {
        return this.camera;
    }
    
    private Vector3f lClick(){
        CollisionResult closest;
        Vector3f mark = Vector3f.ZERO;
        Vector3f origin =
            this.app.getCamera().getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
        Vector3f direction =
            this.app.getCamera().getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();
        
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(origin, direction);
        switch(action) {
                case CAST:
                    gameLevel.getChild("testTerrain").collideWith(ray, results);
                    if(results.size()>0) {
                        closest = results.getClosestCollision();
                        mark = closest.getContactPoint();
                    }
                    gameLevel.getChild("mobs").collideWith(ray, results);
                    if(results.size()>0) {
                        closest = results.getClosestCollision();
                        target = closest.getGeometry().getControl(null);
                    }
                    playerControl.doAction(action, target, mark);
                    break;
                default:
                    gameLevel.getChild("mobs").collideWith(ray, results);
                    if(results.size()>0) {
                        closest = results.getClosestCollision();
                        target = closest.getGeometry().getParent().getParent().getControl(Monster.class);
                        if(target != null) {
                            if(target.getLocation().distance(playerControl.getLocation()) <= playerControl.getAttackRange()) {
                                playerControl.attack(target);
                                playerControl.setView(target.getLocation().subtract(playerControl.getLocation()));
                                switch (FastMath.nextRandomInt(0, 1)) {
                                    case 0:
                                        topChannel.setAnim("SliceVertical");
                                        break;
                                    case 1:
                                        topChannel.setAnim("SliceHorizontal");
                                        break;
                                }
                            }
                        }
                        results.clear();

                        mark = null;
                    }
                    break;
        }
//        gameLevel.getChild("usables").collideWith(ray, results);
//        if(results.size()>0) {
//            closest = results.getClosestCollision();
//            target = closest.getGeometry().getControl(null);
//            mark = null;
//        }
//        gameLevel.getChild("mobs").collideWith(ray, results);
//        if(results.size()>0) {
//            closest = results.getClosestCollision();
//            target = closest.getGeometry().getControl(null);
//            mark = null;
//        }
//        else {
//            gameLevel.getChild("testTerrain").collideWith(ray, results);
//            if(results.size()>0){
//                closest = results.getClosestCollision();
//                mark = closest.getContactPoint();
//            }    
//        }
        
        return mark;
    }
    
    private Vector3f rClick(){
        CollisionResult closest;
        Vector3f mark = Vector3f.ZERO;
        Vector3f origin =
            this.app.getCamera().getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
        Vector3f direction =
            this.app.getCamera().getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();

        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(origin, direction);
//        gameLevel.getChild("usables").collideWith(ray, results);
//        if(results.size()>0) {
//            closest = results.getClosestCollision();
//            target = closest.getGeometry().getControl(null);
//            mark = null;
//        }
//        gameLevel.getChild("mobs").collideWith(ray, results);
//        if(results.size()>0) {
//            closest = results.getClosestCollision();
//            target = closest.getGeometry().getParent().getParent().getControl(Monster.class);
//            if(target != null)
//                playerControl.attack(target);
//            results.clear();
//            
//            mark = null;
//        }
//        else {
            gameLevel.getChild("testTerrain").collideWith(ray, results);
            if(results.size()>0){
                closest = results.getClosestCollision();
                mark = closest.getContactPoint();
                if(!botCh.getAnimationName().equals("RunBase")) {
                    botCh.setAnim("RunBase");
                }
//                topChannel.setAnim("RunTop");
                if(!topCh.getAnimationName().equals("RunTop")) {

                    topCh.setAnim("RunTop");
                }
            }    
//        }

        return mark;
    }
    
    private ActionListener actionListener = new ActionListener(){
        public void onAction(String name, boolean pressed, float tpf){
            if(name.equals("Jump")) {
                if(pressed) return;
                playerControl.betterControl.jump();
//                System.out.println(playerControl.betterControl.getJumpForce());
            }
            if(name.equals("Q")) {
                if(pressed) return;
                if(action == Player.Action.CAST)    action = Player.Action.NULL;
                else {
                    action = Player.Action.CAST;
                    activeSpell = SpellSlots.Q;
                }
            }
            if(name.equals("W")) {
                if(pressed) return;
                if(action == Player.Action.CAST)    action = Player.Action.NULL;
                else {
                    action = Player.Action.CAST;
                    activeSpell = SpellSlots.W;
                }
            }
            if(name.equals("E")) {
                if(pressed) return;
                if(action == Player.Action.CAST)    action = Player.Action.NULL;
                else {
                    action = Player.Action.CAST;
                    activeSpell = SpellSlots.E;
                }
            }
            if(name.equals("R")) {
                if(pressed) return;
                if(action == Player.Action.CAST)    action = Player.Action.NULL;
                else {
                    action = Player.Action.CAST;
                    activeSpell = SpellSlots.R;
                }
            }
            if(name.equals("LClick")) {
                if(pressed) return;
                Vector3f mark = lClick();
                playerControl.doAction(action, target, null);
            }
            if(name.equals("RClick")) {
//                if(pressed) return;
//                if(value) return;
                isClicking = !isClicking;
                Vector3f mark = rClick();
                playerControl.setMark(mark);
                playerControl.doAction(action, target, mark);
            }
        }
    };
    
    private AnalogListener analogListener = new AnalogListener(){
        public void onAnalog(String name, float value, float tpf){
            if(name.equals("ZoomOut")){
                if((camera.getDistanceToTarget()+value)<camera.getMaxDistance()){
                    camera.setDefaultDistance(camera.getDistanceToTarget()+value);
                    camera.setDefaultVerticalRotation(camera.getVerticalRotation()+0.02f);
                }
            }
            if(name.equals("ZoomIn")){
                if((camera.getDistanceToTarget()-value)>camera.getMinDistance()){
                    camera.setDefaultDistance(camera.getDistanceToTarget()-value);
                    camera.setDefaultVerticalRotation(camera.getVerticalRotation()-0.02f);
                }
            }
            if(name.equals("Left")||name.equals("Right")||name.equals("Down")||
                    name.equals("Up")){
                if(isClicking) {
                    Vector3f mark = rClick();
                    playerControl.setMark(mark);
                }
            }
        }
    };
}
