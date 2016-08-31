/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.jme3.ai.navmesh.NavMesh;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Avaron
 */
public class Spawner extends AbstractControl {
    private AssetManager assetManager;
    private int itWaves, waves, init, current;
    private float delta;
    private BoundingBox bounds;
    private Vector3f extent = null;
    private Player player;
    private NavMesh navMesh;
    private NavMeshPathfinder navPf;
    private BulletAppState bulletAppState;
    private ArrayList <Monster> monsterList;
    private boolean spawnEnabled = false;
    
    public enum MonsterType {
        SWARG
    }
    private MonsterType type;
    
    public Spawner(MonsterType monsterType, Player player, AssetManager assetManager, BulletAppState bulletAppState) {
        setType(monsterType);
        this.player = player;
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        monsterList = new ArrayList();
    }
    
    public void setNavMesh (NavMesh navMesh, NavMeshPathfinder navPf) {
        this.navMesh = navMesh;
        this.navPf = navPf;
    }
    
    public void setWaves (int numWaves, int numInit, float pctDelta) {
        waves = numWaves;
        itWaves = waves;
        init = numInit;
        current = init;
        delta = pctDelta;
//        actives = 0;
    }
    
    public void reset() {
        itWaves = waves;
        current = init;
    }
    
    public void enableSpawn (boolean enabled) {
        this.spawnEnabled = enabled;
    }

    public int getWaves() {
        return waves;
    }

    public float getDelta() {
        return delta;
    }

    public int getInit() {
        return init;
    }
    
    public boolean isSpawnEnabled() {
        return spawnEnabled;
    }
    
    private void setType (MonsterType monsterType) {
        type = monsterType;
    }
    
    private void waveSpawn (int numMonsters) {
        if(extent == null) {
            bounds = (BoundingBox)this.spatial.getParent().getChild("testTerrain").getWorldBound();
            extent = bounds.getExtent(null);
        }
        Vector3f origin, direction, mark;
        CollisionResults results = new CollisionResults();
        Monster monster;
        Spatial monsterModel;
        Node monsterNode;
        
        Node mobs = (Node)this.spatial.getParent().getChild("mobs");
        for(int i=0; i<numMonsters; i++) {
//            System.out.println(i+" sobre "+numMonsters);
            origin = new Vector3f((float)FastMath.nextRandomInt((int)bounds.getCenter().x - (int)extent.x, (int)extent.x)/8,
                    bounds.getYExtent(),
                    (float)FastMath.nextRandomInt((int)bounds.getCenter().z - (int)extent.z, (int)extent.z)/8);
//            System.out.println(origin);
            direction = new Vector3f(0, -1.0f, 0);
//            origin = new Vector3f((float)FastMath.nextRandomInt(-20, 20), bounds.getYExtent(), (float)FastMath.nextRandomInt(-20,20));
//            destiny = origin.clone();
//            destiny.y = destiny.y - 10f;
            Ray ray = new Ray(origin, direction);
            monsterNode = new Node();
            this.spatial.getParent().getChild("testTerrain").collideWith(ray, results);
            if(results.size() > 0) {
                mark = results.getClosestCollision().getContactPoint();
//                System.out.println("la marca es "+mark);
                switch (type) {
                    case SWARG:
                        monsterModel = assetManager.loadModel("Models/Oto.mesh.j3o");
                        monsterNode.attachChild(monsterModel);
                        monsterModel.setLocalTranslation(0, 0.5f, 0);
                        monsterModel.setLocalScale(0.12f);
                        monster = new Swarg(player);
                        monsterNode.addControl(monster);
                        monster.betterControl.warp(mark);
                        monster.setNavMesh(navPf);
                        mobs.attachChild(monsterNode);
                        bulletAppState.getPhysicsSpace().addAll(monsterNode);
                        monsterList.add(monster);
//                        System.out.println("creo monstruo");
                        break;
                    default:
                        break;
                }
                results.clear();
            }
        }
    }
    
    private void checkWaves() {
        
        if(monsterList.isEmpty()) {
            if(itWaves != 0) {
                waveSpawn(current);
                current = (int)Math.ceil(current*delta);
                itWaves--;
            }
            else    enableSpawn(false);
        }
        else {
            Iterator<Monster> iter = monsterList.iterator();
            while(iter.hasNext()) {
                if(!iter.next().isAlive()) {
                    iter.remove();
//                    System.out.println("uno menos");
                }
            }
        }
    }
    
    @Override
    public void controlUpdate(float tpf) {
        if(spawnEnabled) {
            checkWaves();
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp){
    /* Optional: rendering manipulation (for advanced users) */
    }
}
