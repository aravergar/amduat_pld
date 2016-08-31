/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author tanatarca
 */
public class PlayerControl extends AbstractControl{
    public BetterCharacterControl charControl;
    private Vector3f moveDir;
    private Vector3f mark;
    private boolean isMoving = false;
    
    public PlayerControl(Node playerNode){
        charControl = new BetterCharacterControl(1.5f,6f,1f);
        playerNode.addControl(charControl);
        charControl.setJumpForce(new Vector3f(0,5f,0));
        charControl.setGravity(new Vector3f(0,1f,0));
    }
    
    public void setMark(Vector3f mark){
        this.mark = mark;
        this.isMoving = true;
    }
    
    private void movePlayer(float tpf) {
        float distance = mark.subtract(this.spatial.getWorldTranslation()).length();
        if(isMoving) {
            if(distance <= 0.1f) {
                charControl.setWalkDirection(Vector3f.ZERO);
                isMoving = false;
            }
            else {
                Vector3f pos = this.spatial.getWorldTranslation();
                moveDir = mark.subtract(pos).normalizeLocal();
                charControl.setViewDirection(moveDir.mult(-1.0f));
                charControl.setWalkDirection(moveDir.multLocal(1000f).multLocal(tpf));
                this.isMoving = true;
            }
        }
    }
    
    @Override
    public void controlUpdate(float tpf){
        if(spatial != null) {
            if(this.isMoving) {
                this.movePlayer(tpf);
            }
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp){
    /* Optional: rendering manipulation (for advanced users) */
    }
}
