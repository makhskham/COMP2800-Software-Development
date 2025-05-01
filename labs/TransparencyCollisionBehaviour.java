//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Lab 8
//02-03-2025
package CodesMK2800;

import java.util.Iterator;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.TransparencyAttributes;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnCollisionEntry;
import org.jogamp.java3d.WakeupOnCollisionExit;

public class TransparencyCollisionBehaviour extends Behavior {
    private boolean inCollision;
    private Shape3D shape;
    private Appearance shapeAppearance;
    private TransparencyAttributes originalTransparency;
    private TransparencyAttributes collisionTransparency;
    private WakeupOnCollisionEntry wEnter;
    private WakeupOnCollisionExit wExit;

    public TransparencyCollisionBehaviour(Shape3D s) {
        shape = s;                                         // save reference to the shape
        shapeAppearance = shape.getAppearance();
        originalTransparency = shapeAppearance.getTransparencyAttributes();
        
        // Create transparency for collision state (75% transparent)
        collisionTransparency = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.75f);
        
        inCollision = false;
    }

    @Override
    public void initialize() {
        wEnter = new WakeupOnCollisionEntry(shape, WakeupOnCollisionEntry.USE_GEOMETRY);
        wExit = new WakeupOnCollisionExit(shape, WakeupOnCollisionExit.USE_GEOMETRY);
        wakeupOn(wEnter);                                  // initialize the behavior
    }

    @Override
    public void processStimulus(Iterator<WakeupCriterion> criteria) {
        inCollision = !inCollision;                        // toggle collision state

        if (inCollision) {                                 // when collision occurs
            shapeAppearance.setTransparencyAttributes(collisionTransparency);
            wakeupOn(wExit);                               // wait for collision to end
        } else {                                           // when collision ends
            shapeAppearance.setTransparencyAttributes(originalTransparency);
            wakeupOn(wEnter);                              // wait for next collision
        }
    }
}