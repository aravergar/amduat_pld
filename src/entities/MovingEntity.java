/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.jme3.scene.control.AbstractControl;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.ai.navmesh.Path.Waypoint;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;

/**
 *
 * @author tanatarca
 */
public abstract class MovingEntity extends AbstractControl {
    public BetterCharacterControl betterControl;
    private Vector3f moveDir;
    private NavMeshPathfinder navPf;
    Waypoint waypoint = null;
    float distance;
    
    public MovingEntity(){        
    }    
    
    @Override
    public void setSpatial(Spatial spatial){
        super.setSpatial(spatial);
        if(spatial != null){
            betterControl = new BetterCharacterControl(this.getRadius(), this.getHeight(), this.getMass());
            spatial.addControl(betterControl);
            betterControl.setJumpForce(new Vector3f(0,7.5f,0));
            betterControl.setGravity(new Vector3f(0,1f,0));
        }else   {
            betterControl = null;
        }
    }
    
    public Vector3f getLocation(){
        return this.spatial.getWorldTranslation();
    }
    
    public void setLocation(Vector3f location){
        this.betterControl.warp(location);
    }
    
    public abstract float getHealth();
    
    public abstract float getArmor();
    
    public abstract float getRadius();
    
    public abstract float getHeight();
    
    public abstract float getMass();
    
    public abstract float getSpeed();
    
    public void setMark(Vector3f mark){
//        navPf.setPosition(this.spatial.getWorldTranslation());
        if(mark != null){
            navPf.setPosition(navPf.warpInside(spatial.getWorldTranslation()));
            navPf.computePath(mark);
            navPf.goToNextWaypoint();
            navPf.goToNextWaypoint();
            waypoint = navPf.getNextWaypoint();
        }
//        else    waypoint = null;
    }
    
    void moveCharacter(float tpf) {
        distance = waypoint.getPosition().distance(this.spatial.getWorldTranslation());
        if(distance <= 0.2f) {
            if(navPf.isAtGoalWaypoint()) {
                waypoint = null;
                stopChar();
            }
            else    {
                navPf.goToNextWaypoint();
                waypoint = navPf.getNextWaypoint();
                betterControl.setWalkDirection(Vector3f.ZERO);
            }
        } else {
            moveDir = waypoint.getPosition().subtract(this.spatial.getWorldTranslation()).normalizeLocal();
            betterControl.setViewDirection(moveDir);
            betterControl.setWalkDirection(moveDir.multLocal(getSpeed()).multLocal(tpf));
        }
    }
    
    public boolean isMoving() {
        if(betterControl.getWalkDirection() != Vector3f.ZERO)   return true;
        else    return false;
    }
    
    public abstract void stopChar();
    
    public void setView(Vector3f moveDir) {
        betterControl.setViewDirection(moveDir);
    }
    
    public void setNavMesh(NavMeshPathfinder navPf) {
        this.navPf = navPf;
    }

    public NavMeshPathfinder getNavMesh() {
        return this.navPf;
    }
    
    public boolean isAlive() {
        if(getHealth() <= 0)    return false;
        else    return true;
    }
    
    public abstract void receiveDamage(float dmg);
    
    public abstract void attack(MovingEntity target);
    
    @Override
    public abstract void controlUpdate(float tpf);
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp){
    /* Optional: rendering manipulation (for advanced users) */
    }
}