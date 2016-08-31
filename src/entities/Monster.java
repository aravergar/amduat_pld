/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

//import com.jme3.scene.Node;

/**
 *
 * @author Avaron
 */
public abstract class Monster extends MovingEntity {
    MovingEntity target;
    float health;
    public Monster(){
        super();
    }
    
    public Monster(MovingEntity target){
        super();
        this.target = target;
    }
    
    public abstract float getVisionRange();
    
    public abstract float getActionRange();
    
    public abstract float getAttackRange();
    
    protected abstract void setHealth(float f);
    
    public void setTarget(MovingEntity target){
        this.target = target;
    }
    
    public MovingEntity getTarget(){
        return this.target;
    }
    
    public void receiveDamage(float dmg) {
        setHealth(health - (dmg - dmg*getArmor()));
        if(health <= 0f) {
            this.spatial.removeFromParent();
            this.betterControl.getPhysicsSpace().remove(betterControl);
        }
    }
    
    abstract void behave();
    
    @Override
    public void controlUpdate(float tpf){
        if(isAlive()) {
            this.behave();
            if(waypoint != null) {
                this.moveCharacter(tpf);
            }
        }
        else {
            
        }
    }
}
