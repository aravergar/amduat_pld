/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
//import com.jme3.scene.Node;

/**
 *
 * @author tanatarca
 */
public class Player extends MovingEntity {
    private static float radius = 0.4f, height = 1.8f, mass = 60f, speed = 380f;
    private float maxHealht = 200f, health = 200f, armor = 0.4f, attackRange = 2.5f, damage = 10f;
    public AnimChannel bottomChannel, topChannel;
    public enum Action {
        NULL, CAST
    }
    private Action action = Action.NULL;
    
    public void doAction(Action action, MovingEntity target, Vector3f mark) {
//        if(action == Action.NULL) {
//            if(target != null)  {
//                if(getLocation().distance(target.getLocation()) <= this.getAttackRange())
//                    attack(target);
//            }
//            else    this.setMark(mark);
//        }
//        else {
//            
//        }
        
        
    }
    
    public void setAnim() {
        Node animNode = (Node)this.spatial;
        AnimControl animControl = animNode.getChild("playerModel").getControl(AnimControl.class);
        bottomChannel = animControl.createChannel();
        bottomChannel.setAnim("HandsRelaxed");
        topChannel = animControl.createChannel();
        topChannel.setAnim("IdleBase");
    }
    
    @Override
    public float getHealth() {
        return health;
    }
    
    @Override
    public float getArmor() {
        return armor;
    }
    
    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getMass() {
        return mass;
    }

    @Override
    public float getSpeed() {
        return speed;
    }
    
    public float getAttackRange() {
        return attackRange;
    }
    
    private void setHealth(float health) {
        this.health = health;
    }
    
    @Override
    public void stopChar() {
        bottomChannel.setAnim("IdleBase");
        topChannel.setAnim("HandsRelaxed");
//        System.out.println("I try...");
    }
    
    @Override
    public void receiveDamage(float dmg) {
        setHealth(health - (dmg - dmg*getArmor()));
        System.out.println("Salud: "+health);
        if(!isAlive()) {
            this.spatial.removeFromParent();
            this.betterControl.getPhysicsSpace().remove(betterControl);
//            this.
        }
    }
    
    @Override
    public void attack(MovingEntity target) {
//        System.out.println("muere!");
        target.receiveDamage(damage);
    }
    
    @Override
    public void controlUpdate(float tpf){
        if(spatial != null) {
            if(waypoint != null)    this.moveCharacter(tpf);
        }
    }
}
