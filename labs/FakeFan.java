package CodesMK2800;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

public class FakeFan extends JPanel implements KeyListener, MouseListener {
    private static final long serialVersionUID = 1L;
    private static JFrame frame;
    private static boolean isPaused = false;
    private static boolean isPowered = true;

    private static final int OBJ_NUM = 7;
    private static A3ObjectsMK[] object3D = new A3ObjectsMK[OBJ_NUM];

    // Add sceneTG as a class-level variable
    private static TransformGroup sceneTG;

    // Reference to the SwitchObject for mouse interaction
    private static SwitchObject switchObject;

    // Sound utility for playing wind sound
    private static SoundUtilityJOAL soundJOAL;
    private static String windSound = "wind"; // Name of the wind sound file

    /* a public function to build the base labeled with 'str' */
    public static TransformGroup create_Base(String str) {
        BaseShape baseShape = new BaseShape();

        Transform3D scaler = new Transform3D();
        scaler.setScale(new Vector3d(4d, 2d, 4d)); // set scale for 4x4 matrix
        TransformGroup baseTG = new TransformGroup(scaler);
        baseTG.addChild(baseShape.position_Object());

        ColorString clr_str = new ColorString(str, CommonsMK.Red, 0.06,
                new Point3f(-str.length() / 4f, -9.4f, 8.2f));
        Transform3D r_axis = new Transform3D(); // rotate around y axis
        r_axis.rotY(Math.PI);
        TransformGroup objRG = new TransformGroup(r_axis);
        objRG.addChild(clr_str.position_Object());
        baseTG.addChild(objRG); // adding objRG to baseTG

        return baseTG;
    }

    /* a function to create the desk fan */
    private static TransformGroup create_Fan() {
        TransformGroup fanTG = new TransformGroup();

        object3D[0] = new StandObject();  // creating the fan stand
        fanTG = object3D[0].position_Object();

        // creating switch and attaching to fan stand:
        switchObject = new SwitchObject(); // Initialize the switch object
        object3D[1] = switchObject;
        object3D[0].add_Child(object3D[1].position_Object());

        // creating shaft and attaching to fan stand:
        object3D[2] = new ShaftObject();
        object3D[0].add_Child(object3D[2].position_Object());

        // creating motor and attaching it to shaft objRG (so it rotates with the shaft):
        object3D[3] = new MotorObject();
        object3D[2].objRG.addChild(object3D[3].position_Object());

        // creating blades and attaching it to the motor
        object3D[4] = new BladesObject();
        object3D[3].add_Child(object3D[4].position_Object());

        // creating the guard and attaching it to the motor as well
        object3D[5] = new GuardObject();
        object3D[3].add_Child(object3D[5].position_Object());

        fanTG.addChild(create_Base("MK's Assignment 4")); // attaching base to the fan stand
        return fanTG;
    }

    /* a function to build the content branch, including the fan and other environmental settings */
    public static BranchGroup create_Scene() {
        BranchGroup sceneBG = new BranchGroup();
        sceneTG = new TransformGroup(); // Initialize sceneTG
        sceneTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        sceneTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        sceneTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND); // Enable adding children
        sceneTG.setCapability(Node.ENABLE_PICK_REPORTING);

        // Create the fan and add it to the scene
        sceneTG.addChild(create_Fan());
        sceneBG.addChild(sceneTG);
        sceneBG.addChild(CommonsMK.add_Lights(CommonsMK.White, 1));

        // Load the wind sound
        soundJOAL = new SoundUtilityJOAL();
        if (!soundJOAL.load(windSound, 0f, 0f, 10f, true)) { // Load the wind sound
            System.out.println("Could not load " + windSound);
        }

        return sceneBG;
    }

    public FakeFan(BranchGroup sceneBG, TransformGroup sceneTG) {
        this.sceneTG = sceneTG; // Assign the passed sceneTG to the class-level variable

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        canvas.addKeyListener(this); // Add key listener
        canvas.addMouseListener(this); // Add mouse listener

        SimpleUniverse su = new SimpleUniverse(canvas);
        CommonsMK.define_Viewer(su, new Point3d(0.25d, 0.25d, 10.0d));

        sceneBG.compile();
        su.addBranchGraph(sceneBG);

        setLayout(new BorderLayout());
        add("Center", canvas);
        frame.setSize(800, 800);
        frame.setVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key_code = e.getKeyCode();
        if (key_code == KeyEvent.VK_Z) {
            pauseFan(); // Left button (Z) can only pause
        } else if (key_code == KeyEvent.VK_X) {
            playFan(); // Right button (X) can only play
        }
    }

    private void pauseFan() {
        Switch leftSwitch = switchObject.getLeftSwitch();
        Switch rightSwitch = switchObject.getRightSwitch();

        // Set left button to green (active/paused)
        leftSwitch.setWhichChild(0); // Green (active/paused)

        // Set right button to red (inactive/not playing)
        rightSwitch.setWhichChild(1); // Red (inactive/not playing)

        // Pause the fan
        object3D[2].get_Alpha().pause(); // Pause shaft oscillation
        object3D[4].get_Alpha().pause(); // Pause blade rotation

        // Stop the wind sound
        soundJOAL.pause(windSound);
    }

    private void playFan() {
        Switch leftSwitch = switchObject.getLeftSwitch();
        Switch rightSwitch = switchObject.getRightSwitch();

        // Set right button to green (active/playing)
        rightSwitch.setWhichChild(0); // Green (active/playing)

        // Set left button to red (inactive/not paused)
        leftSwitch.setWhichChild(1); // Red (inactive/not paused)

        // Resume the fan
        object3D[2].get_Alpha().resume(); // Resume shaft oscillation
        object3D[4].get_Alpha().resume(); // Resume blade rotation

        // Play the wind sound
        soundJOAL.play(windSound);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Handle mouse clicks on the cubes
        int x = e.getX();

        // Check if the left or right cube was clicked
        if (x < frame.getWidth() / 2) {
            pauseFan(); // Left button (pause)
        } else {
            playFan(); // Right button (play)
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        frame = new JFrame("MK's Assignment 4");
        BranchGroup sceneBG = create_Scene();
        frame.getContentPane().add(new FakeFan(sceneBG, sceneTG));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}