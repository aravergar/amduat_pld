/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

//import com.jme3.scene.Node;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Avaron
 */
public class Swarg extends Monster {
    private static float radius = 0.25f, height = 1.2f, mass = 60f,
            speed = 200f, attackRange = 1f, actionRange = 10f,
            visionRange = 20f, armor = 0.15f, damage = 8f;
    private long coolDown;
    
    public Swarg(MovingEntity target){
        super(target);
        health = 50f;
        coolDown = System.currentTimeMillis();
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        
        
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
    
    @Override
    public float getVisionRange() {
        return visionRange;
    }
    
    @Override
    public float getActionRange() {
        return actionRange;
    }
    
    @Override
    public float getAttackRange() {
        return attackRange;
    }
    
    @Override
    protected void setHealth(float health) {
        this.health = health;
    }
    
    @Override
    public void receiveDamage(float dmg) {
        setHealth(health - (dmg - dmg*getArmor()));
//        System.out.println("AUCH!");
        if(health <= 0f) {
            this.spatial.removeFromParent();
            this.betterControl.getPhysicsSpace().remove(betterControl);
        }
    }
    
    @Override
    public void attack(MovingEntity target) {
        if(System.currentTimeMillis() - coolDown > 2000f) {
            coolDown = System.currentTimeMillis();
            target.receiveDamage(damage);
        }
    }
    
    private void moveRandom() {
        Vector3f random = this.getLocation().clone();
        random.addLocal(FastMath.nextRandomFloat()*40f-20f, 0f, FastMath.nextRandomFloat()*40f-20f);
        this.setMark(random);
    }
    
    @Override
    public void stopChar() {}
    
    @Override
    protected void behave(){
        //  Objetivo dentro de radio accion
        if(this.target != null && this.getLocation().distance(target.getLocation()) <= this.getActionRange()){
            //  Objetivo dentro de radio accion pero fuera radio ataque
            if(this.getLocation().distance(target.getLocation()) >= this.getAttackRange()){
                this.setMark(target.getLocation());
            }
            //  Objetivo dentro radio ataque
            else {
                this.setView(target.getLocation());
                this.setMark(this.getLocation());
                attack(target);
            }
        }
        //  Objetivo fuera radio accion
        else {
//            if(!isMoving())
                moveRandom();
        }
    }

    
}
