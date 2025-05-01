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

public class CodeLab2MK extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static JFrame frame;
    
    private static String frame_name = "MK's Lab #2";
    private static BranchGroup alterableBG, shapeBG;
    private static GroupObjects groupObject;
    private static boolean r_tag = true;
    private static boolean object_tag = true;
    private static final String OBJECT_NAME = "Circle"; // Changed from Star to Circle
    
    /* Creates and returns the content branch */
    private static BranchGroup create_Scene() {
        alterableBG = new BranchGroup();
        // Create initial white circle with radius 0.6 and 60 segments
        groupObject = new GroupObjects(L2CircleMK.line_Shape(0.6f, 60, CommonsMK.White));
        shapeBG = groupObject.get_ShapeBG();
        
        return GroupObjects.scene_Group(alterableBG, shapeBG);
    }

    /* Constructor to set up the application */
    public CodeLab2MK(BranchGroup scene) {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setSize(800, 800);
        SimpleUniverse su = new SimpleUniverse(canvas3D);
        CommonsMK.define_Viewer(su, new Point3d(1.35, -0.35, 1.5));
        scene.addChild(CommonsMK.add_Lights(CommonsMK.White, 1));
        
        scene.compile();
        su.addBranchGraph(scene);

        Menu m = new Menu("Menu");
        m.addActionListener(this);
        MenuBar menuBar = build_MenuBar(m, OBJECT_NAME);
        frame.setMenuBar(menuBar);

        setLayout(new BorderLayout());
        add("Center", canvas3D);
        frame.setSize(810, 800);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        frame = new JFrame(frame_name + ": One Circle"); // Updated title to show initial state
        frame.getContentPane().add(new CodeLab2MK(create_Scene()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /* Builds the menu bar of the application */
    public static MenuBar build_MenuBar(Menu m, String s) {
        MenuBar menuBar = new MenuBar();
        m.add("Exit");
        m.add("Pause/Rotate");
        m.addSeparator();
        m.add(s);
        menuBar.add(m);
        return menuBar;
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
            if (object_tag) {
                // Create scene with two circles
                groupObject = new GroupObjects(L2CircleMK.double_circle_Shape(0.6f, 0.5f, 60));
                frame.setTitle(frame_name + ": Two Circles");
            } else {
                // Create scene with one circle
                groupObject = new GroupObjects(L2CircleMK.line_Shape(0.6f, 60, CommonsMK.White));
                frame.setTitle(frame_name + ": One Circle");
            }
            object_tag = !object_tag;
            break;
        default:
            return;
        }
        
        BranchGroup tmpBG = groupObject.get_ShapeBG();
        shapeBG.detach();
        shapeBG = tmpBG;
        shapeBG.setCapability(BranchGroup.ALLOW_DETACH);
        alterableBG.addChild(shapeBG);
    }
}