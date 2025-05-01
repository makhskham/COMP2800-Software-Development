//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Lab 6
//11-02-2025
package CodesMK2800;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

public class CodeLab6MK extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static JFrame frame;
    
    private static String frame_name = "MK's Lab #6";
    private static boolean r_tag = true;
    private static final String OBJECT_NAME = "Textured Disk";
    
    // Animation control variables
    private static Alpha topAlpha, bottomAlpha;
    private static boolean isTopPaused = false;
    private static boolean isBottomPaused = false;
    private static int animationState = 0;
    
    /* a function to build and return the content branch */
    private static BranchGroup create_Scene() {
        BranchGroup sceneBG = new BranchGroup();
        
        // Create main TransformGroup for rotation
        TransformGroup sceneTG = new TransformGroup();
        sceneBG.addChild(CommonsMK.rotate_Behavior(7500, sceneTG));
        
        // Create the side surface (ring)
        String[] side_name = {"Top", "Bottom", "Side"};
        sceneTG.addChild(L5TextureSurfaceMK.ring_Shape(side_name[2], 60));
        
        // Create and setup top surface with animation
        TransformGroup topTG = new TransformGroup();
        topTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        Transform3D topTransform = new Transform3D();
        topTransform.setTranslation(new Vector3f(0.0f, 0.0f, 0.1f));
        TransformGroup topShapeTG = new TransformGroup(topTransform);
        topShapeTG.addChild(L5TextureSurfaceMK.ring_Shape("Top", 60));
        topTG.addChild(topShapeTG);
        
        Transform3D topAxisPosition = new Transform3D();
        topAxisPosition.rotY(-Math.PI / 2.0);
        topAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE,
                0, 0, 4000, 0000, 1000, 4000, 0000, 1000);
        PositionInterpolator topPositionInterpol = 
                new PositionInterpolator(topAlpha, topTG, topAxisPosition, 0.6f, 0.0f);
        topPositionInterpol.setSchedulingBounds(CommonsMK.twenty_BS);
        
        // Create and setup bottom surface with opposite animation
        TransformGroup bottomTG = new TransformGroup();
        bottomTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        Transform3D bottomTransform = new Transform3D();
        bottomTransform.setTranslation(new Vector3f(0.0f, 0.0f, -0.1f));
        TransformGroup bottomShapeTG = new TransformGroup(bottomTransform);
        // Create bottom surface with Extra texture
        bottomShapeTG.addChild(L5TextureSurfaceMK.ring_Shape("Bottom", 60));
        bottomTG.addChild(bottomShapeTG);
        
        Transform3D bottomAxisPosition = new Transform3D();
        bottomAxisPosition.rotY(-Math.PI / 2.0);
        bottomAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE,
                0, 0, 4000, 0000, 1000, 4000, 0000, 1000);
        PositionInterpolator bottomPositionInterpol = 
                new PositionInterpolator(bottomAlpha, bottomTG, bottomAxisPosition, -0.6f, 0.0f);
        bottomPositionInterpol.setSchedulingBounds(CommonsMK.twenty_BS);
        
        sceneTG.addChild(topTG);
        sceneTG.addChild(bottomTG);
        sceneTG.addChild(topPositionInterpol);
        sceneTG.addChild(bottomPositionInterpol);
        
        CommonsMK.control_Rotation(r_tag);
        sceneBG.addChild(sceneTG);
        
        return sceneBG;
    }
    
    /* Controls the animation state of the surfaces */
    private static void controlSurfaceAnimations() {
        switch(animationState) {
            case 0: // Pause top surface
                topAlpha.pause();
                isTopPaused = true;
                frame.setTitle(frame_name + ": Top Surface Paused");
                break;
            case 1: // Pause bottom surface
                bottomAlpha.pause();
                isBottomPaused = true;
                frame.setTitle(frame_name + ": Both Surfaces Paused");
                break;
            case 2: // Resume top surface
                topAlpha.resume();
                isTopPaused = false;
                frame.setTitle(frame_name + ": Bottom Surface Paused");
                break;
            case 3: // Resume bottom surface
                bottomAlpha.resume();
                isBottomPaused = false;
                frame.setTitle(frame_name + ": All Surfaces Moving");
                break;
        }
        animationState = (animationState + 1) % 4;
    }

    /* Constructor to set up the application */
    public CodeLab6MK(BranchGroup scene) {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setSize(800, 800);
        
        SimpleUniverse su = new SimpleUniverse(canvas3D);
        CommonsMK.define_Viewer(su, new Point3d(1.35, -0.35, 10.0));
        
        scene.addChild(CommonsMK.add_Lights(CommonsMK.White, 2));
        scene.compile();
        su.addBranchGraph(scene);
        
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
        frame = new JFrame(frame_name + ": Moving Textured Disks");
        frame.getContentPane().add(new CodeLab6MK(create_Scene()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }    

    @Override
    public void actionPerformed(ActionEvent e) {        
        switch(e.getActionCommand()) {
        case "Exit": 
            System.exit(0);
        case "Pause/Rotate":
            r_tag = !r_tag;
            CommonsMK.control_Rotation(r_tag);
            return;
        case OBJECT_NAME:
            controlSurfaceAnimations();
            return;
        default:
            return;
        }
    }
}