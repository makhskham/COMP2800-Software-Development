//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Lab 9
//11-03-2025
package CodesMK2800;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Switch;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3f;

public class CodeLab9MK extends JPanel implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;
    private static JFrame frame;
    
    private static String frame_name = "MK's Lab #9";
    private static boolean r_tag = true;
    private static boolean object_tag = true;
    private static int select_key = 0;
    private static final String OBJECT_NAME = "Morphing Object";
    private static Alpha[] diskAlpha = new Alpha[2];  // Two Alphas for top and extra
    private static Switch[] morphSwitch = new Switch[3];  // Three switches for top, side, and extra

    /* a function to create a switch of morphing behavior for (disk) side with different edges;
     * the three switch children are three morphing objects, one for top, side, and extra. */
    private static Switch morph_Switch(int i, TransformGroup priorTG, String s, Switch[] morph) {
        morph[i] = new Switch();
        morph[i].setCapability(Switch.ALLOW_SWITCH_WRITE);
        for (int j = 0; j < 3; j++)                        // add three morphing surfaces to switch 'morph[i]'
            morph[i].addChild(L9MorphShapeMK.set_Morph(priorTG, s, 4, 8 * (1 + j)));
        morph[i].setWhichChild(0);                         // set default with changing edges 4<>-64<->8

        return morph[i];
    }
    
    /* a function to the disk's three side surface rotating  */
    public static void morph_Shapes(TransformGroup sceneTG, Alpha[] alpha, Switch[] morph) {
        String[] side_name = {"Top", "Side", "Extra"};
        Transform3D slide, slide2, plate, plate2, rotate_axis, rotate_axis2;
        TransformGroup slideTG, slideTG2, plateTG, plateTG2, hingeTG, hingeTG2;
        RotationInterpolator rotationInterpol, rotationInterpol2;
        
        // Set up translation for the first side (top)
        slide = new Transform3D();
        slide.setTranslation(new Vector3f(-2.0f, 0, 0.1f));
        slideTG = new TransformGroup(slide);
        
        // Set up translation for the second side (extra)
        slide2 = new Transform3D();
        slide2.setTranslation(new Vector3f(-2.0f, 0, 0.05f));
        slideTG2 = new TransformGroup(slide2); 
        
        // Create TransformGroups for positioning the surfaces
        plate = new Transform3D();                    
        plate.setTranslation(new Vector3f(2.0f, 0, -0.1f));
        plateTG = new TransformGroup(plate);              
        plateTG.addChild(morph_Switch(0, plateTG, side_name[0], morph)); // Add top surface
        
        plate2 = new Transform3D();
        plate2.setTranslation(new Vector3f(2.0f, 0, -0.05f));
        plateTG2 = new TransformGroup(plate2);
        plateTG2.addChild(morph_Switch(2, plateTG2, side_name[2], morph)); // Add extra surface
        
        // Set up Alpha objects for controlling rotation timing
        alpha[0] = new Alpha(-1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE, 0, 0, 4000, 0000, 1000, 4000, 0000, 1000);
        alpha[1] = new Alpha(-1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE, 0, 0, 4000, 0000, 1000, 4000, 0000, 1000); 
        
        // Set up rotation for the first side (top)
        hingeTG = new TransformGroup();                    // Use 'hingeTG' for rotation
        hingeTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        rotate_axis = new Transform3D();                   // Rotate around 'hingeTG's y-axis
        rotationInterpol = new RotationInterpolator(alpha[0], hingeTG, rotate_axis, 0, -(float) (Math.PI / 2.0)); 
        rotationInterpol.setSchedulingBounds(CommonsMK.twenty_BS);
        slideTG.addChild(rotationInterpol);               // Add rotation behavior to 'slideTG'
        
        // Set up rotation for the second side (extra)
        hingeTG2 = new TransformGroup();
        hingeTG2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        rotate_axis2 = new Transform3D();
        rotationInterpol2 = new RotationInterpolator(alpha[1], hingeTG2, rotate_axis2, 0, (float)(Math.PI/2.0)); // Rotate in the opposite direction
        rotationInterpol2.setSchedulingBounds(CommonsMK.twenty_BS);
        slideTG2.addChild(rotationInterpol2);
        
        // Attach the surfaces to their respective hinge TransformGroups
        hingeTG.addChild(plateTG);
        hingeTG2.addChild(plateTG2);
        
        // Attach the rotating surfaces to the slide TransformGroups
        slideTG.addChild(hingeTG);        
        slideTG2.addChild(hingeTG2);
        
        // Combine all components into the scene's TransformGroup
        TransformGroup sideTG = new TransformGroup();
        sideTG.addChild(slideTG);
        sideTG.addChild(morph_Switch(1, sideTG, side_name[1], morph)); // Add side surface
        sideTG.addChild(slideTG2);
        sceneTG.addChild(sideTG);
    }
    
    /* a function to build and return the content branch */
    private static BranchGroup create_Scene() {
        BranchGroup sceneBG = new BranchGroup(); 
        
        TransformGroup sceneTG = new TransformGroup();     // introduce a TransformGroup for rotation 
        sceneBG.addChild(CommonsMK.rotate_Behavior(7500, sceneTG));
        
        morph_Shapes(sceneTG, diskAlpha, morphSwitch);
        
        CommonsMK.control_Rotation(r_tag);                 // make 'sceneTG' rotating by default
        sceneBG.addChild(sceneTG);
        Alpha cubeAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE |
                Alpha.DECREASING_ENABLE, 0, 0, 3000, 0000, 750, 3000, 0000, 750);
        sceneBG.addChild(CodeLab8MK.move_Cube(cubeAlpha));

        return sceneBG;
    }

    /* a constructor to set up for the application */
    public CodeLab9MK(BranchGroup scene) {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.addKeyListener(this);                     // NOTE: enable key events     
        canvas3D.setSize(800, 800);                        // set size of canvas
        SimpleUniverse su = new SimpleUniverse(canvas3D);  // create a SimpleUniverse
                                                           // set the viewer's location
        CommonsMK.define_Viewer(su, new Point3d(1.35, -0.35, 12.0));         
        scene.addChild(CommonsMK.add_Lights(CommonsMK.White, 1));
        
        scene.compile();                                   // optimize the BranchGroup
        su.addBranchGraph(scene);                          // attach 'scene' to 'su'

        Menu m = new Menu("Menu");                         // set menu's label
        m.addActionListener(this);
        MenuBar menuBar = CodeLab2MK.build_MenuBar(m, OBJECT_NAME);
        frame.setMenuBar(menuBar);                         // build and set the menu bar

        setLayout(new BorderLayout());
        add("Center", canvas3D);
        frame.setSize(810, 800);                           // set the size of the frame
        frame.setVisible(true);
    }

    public static void main(String[] args) {               // NOTE: copyright material
        frame = new JFrame(frame_name + ": Rotating Textured Disks");
        frame.getContentPane().add(new CodeLab9MK(create_Scene()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }    

    @Override
    public void actionPerformed(ActionEvent e) {
        
        switch(e.getActionCommand()) {                     // handle the selected menu item
        case "Exit": 
            System.exit(0);                                // quit the application
        case "Pause/Rotate":
            r_tag = (r_tag == true)? false : true;
            CommonsMK.control_Rotation(r_tag);
            return;
        case OBJECT_NAME:
            if (object_tag) {                              // pause disk rotation
                diskAlpha[0].pause();
                diskAlpha[1].pause();
                object_tag = false;
            }
            else {                                         // resume disk rotation
                diskAlpha[0].resume();
                diskAlpha[1].resume();
                object_tag = true;
            }
            break;
        default:
            return;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k;

        switch(e.getKeyCode()) {
        case KeyEvent.VK_1:
            k = 0; break;
        case KeyEvent.VK_2:
            k = 1; break;
        case KeyEvent.VK_3:
            k = 2; break;
        case KeyEvent.VK_4:
            k = 3; break;
        default: 
            return;                                        // accept only the specific keys as inputs
        }
        
        if (select_key == k)
            return;                                        // make the change only when different from current
        else
            select_key = k;                                // update the selected switch child
        
        for (int i = 0; i < 3; i++)  // Updated to handle 3 parts
            morphSwitch[i].setWhichChild(k);               // use the selected switch child

    }

    public void keyReleased(KeyEvent arg0) { }
    public void keyTyped(KeyEvent e) { }    
}