package CodesMK2800;

import java.util.Iterator;
import java.awt.event.KeyEvent;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnAWTEvent;
import org.jogamp.java3d.TransformGroup;

public class FanControlBehavior extends Behavior {
    private WakeupOnAWTEvent keyEvent = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    private boolean paused = false;
    private boolean powered = true;

    @Override
    public void initialize() {
        wakeupOn(keyEvent); // Wake up on key press events
    }

    @Override
    public void processStimulus(Iterator<WakeupCriterion> criteria) {
        while (criteria.hasNext()) {
            WakeupCriterion criterion = criteria.next();
            if (criterion instanceof WakeupOnAWTEvent) {
                KeyEvent event = (KeyEvent) ((WakeupOnAWTEvent) criterion).getAWTEvent()[0];
                if (event.getKeyCode() == KeyEvent.VK_Z) {
                    paused = !paused;
                    // Add logic to pause/resume motor oscillation
                    System.out.println("Paused: " + paused);
                } else if (event.getKeyCode() == KeyEvent.VK_X) {
                    powered = !powered;
                    // Add logic to stop/resume motor oscillation and blade rotation
                    System.out.println("Powered: " + powered);
                }
            }
        }
        wakeupOn(keyEvent); // Continue listening for key events
    }
}