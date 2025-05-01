//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Lab 4
//28-01-2025
package CodesMK2800;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3f;

public class CodeLab4MK extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static JFrame frame;
    
    private static String frame_name = "MK's Lab #4";
    private static boolean r_tag = true;
    private static final String OBJECT_NAME = "Disk";
    private static TransformGroup topTG;    // Transform group for top surface
    private static TransformGroup bottomTG;  // Transform group for bottom surface
    private static int gap_state = 0;       // 0: no gap, 1: small, 2: medium, 3: large
    
    /* Creates and returns the main scene graph */
    private static BranchGroup create_Scene() {
        BranchGroup sceneBG = new BranchGroup();
        TransformGroup sceneTG = new TransformGroup();
        
        // Colors for different surfaces
        Color3f[] colors = {CommonsMK.Orange, CommonsMK.Green, CommonsMK.Blue};
        
        // Create the side surface (ring)
        sceneTG.addChild(L4TransformMK.ring_Side(1, colors[1]));
        
        // Create transform groups for both circular surfaces
        topTG = new TransformGroup();
        bottomTG = new TransformGroup();
        topTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        bottomTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        // Add circular surfaces
        topTG.addChild(L4TransformMK.ring_Side(0, colors[0]));     // Top surface
        bottomTG.addChild(L4TransformMK.ring_Side(0, colors[2]));  // Bottom surface
        
        // Add surfaces to scene
        sceneTG.addChild(topTG);
        sceneTG.addChild(bottomTG);
        
        // Add rotation behavior
        sceneBG.addChild(CommonsMK.rotate_Behavior(7500, sceneTG));
        CommonsMK.control_Rotation(r_tag);
        
        sceneBG.addChild(sceneTG);
        return sceneBG;
    }

    /* Constructor to set up the application */
    public CodeLab4MK(BranchGroup scene) {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setSize(800, 800);
        SimpleUniverse su = new SimpleUniverse(canvas3D);
        
        CommonsMK.define_Viewer(su, new Point3d(1.35, -0.35, 10.0));
        scene.addChild(CommonsMK.add_Lights(CommonsMK.White, 2));
        
        scene.compile();
        su.addBranchGraph(scene);

        // Set up menu
        Menu m = new Menu("Menu");
        m.addActionListener(this);
        MenuBar menuBar = CodeLab2MK.build_MenuBar(m, OBJECT_NAME);
        frame.setMenuBar(menuBar);

        setLayout(new BorderLayout());
        add("Center", canvas3D);
        frame.setSize(810, 800);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        frame = new JFrame(frame_name + ": No Gap");
        frame.getContentPane().add(new CodeLab4MK(create_Scene()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }    

    /* Updates the frame title based on current gap state */
    private void updateFrameTitle() {
        String gapText = switch(gap_state) {
            case 0 -> "No Gap";
            case 1 -> "Small Gap (0.25)";
            case 2 -> "Medium Gap (0.5)";
            case 3 -> "Large Gap (0.75)";
            default -> "No Gap";
        };
        frame.setTitle(frame_name + ": " + gapText);
    }

    /* Handles the gap animation by moving surfaces */
    private void updateGap() {
        float gapSize = switch(gap_state) {
            case 0 -> 0.0f;
            case 1 -> 0.25f;
            case 2 -> 0.5f;
            case 3 -> 0.75f;
            default -> 0.0f;
        };
        
        // Create transforms for top and bottom surfaces
        Transform3D topTF = new Transform3D();
        Transform3D bottomTF = new Transform3D();
        
        // Move surfaces in opposite directions
        topTF.setTranslation(new Vector3f(0f, 0f, gapSize));
        bottomTF.setTranslation(new Vector3f(0f, 0f, -gapSize));
        
        // Apply transforms
        topTG.setTransform(topTF);
        bottomTG.setTransform(bottomTF);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String chosen_item = e.getActionCommand();        

        switch(chosen_item) {
            case "Exit":
                System.exit(0);
            case "Pause/Rotate":
                r_tag = !r_tag;
                CommonsMK.control_Rotation(r_tag);
                break;
            case OBJECT_NAME:
                // Cycle through gap states (0->1->2->3->0)
                gap_state = (gap_state + 1) % 4;
                updateGap();
                updateFrameTitle();
                break;
        }
    }    
}