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
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3d;

public class CodeLab3MK extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static JFrame frame;
    
    private static String frame_name = "MK's Lab #3";
    private static BranchGroup alterableBG, shapeBG;
    private static GroupObjects groupObject;
    private static boolean r_tag = true;
    private static final String OBJECT_NAME = "Disk";
    
    // Track disk size state
    private static int sizeState = 0; // 0=big, 1=medium, 2=small, 3=tiny
    private static final float[] DISK_SIZES = {2.0f, 1.5f, 1.0f, 0.5f};
    private static final String[] SIZE_NAMES = {"Big", "Medium", "Small", "Tiny"};
    
    /* Create the scene graph with initial disk */
    private static BranchGroup create_Scene() {
        alterableBG = new BranchGroup();
        groupObject = new GroupObjects(L3DiskMK.ring_Side(DISK_SIZES[sizeState]));
        shapeBG = groupObject.get_ShapeBG();
        shapeBG.setCapability(BranchGroup.ALLOW_DETACH);
        
        return GroupObjects.scene_Group(alterableBG, shapeBG);
    }

    /* Constructor to set up the application */
    public CodeLab3MK(BranchGroup scene) {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setSize(800, 800);
        SimpleUniverse su = new SimpleUniverse(canvas3D);
        CommonsMK.define_Viewer(su, new Point3d(1.35, -0.35, 10.0));
        scene.addChild(CommonsMK.add_Lights(CommonsMK.White, 1));
        
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
        frame = new JFrame(frame_name + ": " + SIZE_NAMES[sizeState] + " Disk");
        frame.getContentPane().add(new CodeLab3MK(create_Scene()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
            return;
        case OBJECT_NAME:
            // Update size state and create new disk
            sizeState = (sizeState + 1) % DISK_SIZES.length;
            groupObject = new GroupObjects(L3DiskMK.ring_Side(DISK_SIZES[sizeState]));
            
            // Update frame title to show current size
            frame.setTitle(frame_name + ": " + SIZE_NAMES[sizeState] + " Disk");
            break;
        default:
            return;
        }
        
        // Update scene with new disk
        BranchGroup tmpBG = groupObject.get_ShapeBG();
        shapeBG.detach();
        shapeBG = tmpBG;
        shapeBG.setCapability(BranchGroup.ALLOW_DETACH);
        alterableBG.addChild(shapeBG);
    }    
}