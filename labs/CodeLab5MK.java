//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Lab 5
//02-02-2025
package CodesMK2800;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3d;

public class CodeLab5MK extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static JFrame frame;
    
    private static String frame_name = "MK's Lab #5";
    private static BranchGroup alterableBG, shapeBG;
    private static GroupObjects groupObject;
    private static boolean r_tag = true;
    private static int currentSides = 4;  // Track current number of sides
    private static final String OBJECT_NAME = "Table";
    
    /* a function to build and return the content branch */
    private static BranchGroup create_Scene() {
        alterableBG = new BranchGroup();
        alterableBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        alterableBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        
        groupObject = new GroupObjects(L5TextureSurfaceMK.round_Table(currentSides));
        shapeBG = groupObject.get_ShapeBG();
        shapeBG.setCapability(BranchGroup.ALLOW_DETACH);
        
        return GroupObjects.scene_Group(alterableBG, shapeBG);
    }

    /* a constructor to set up for the application */
    public CodeLab5MK(BranchGroup scene) {
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
        frame = new JFrame(frame_name + ": " + currentSides + "-Sided Table");
        frame.getContentPane().add(new CodeLab5MK(create_Scene()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }    

    @Override
    public void actionPerformed(ActionEvent e) {
        String sub_title = null;
        
        switch(e.getActionCommand()) {
        case "Exit": 
            System.exit(0);
        case "Pause/Rotate":
            r_tag = !r_tag;
            CommonsMK.control_Rotation(r_tag);
            return;
        case OBJECT_NAME:            
            // Cycle through side counts: 4 -> 8 -> 12 -> 16 -> 4
            currentSides = (currentSides == 16) ? 4 : currentSides + 4;
            sub_title = ": " + currentSides + "-Sided Table";
            groupObject = new GroupObjects(L5TextureSurfaceMK.round_Table(currentSides));
            break;
        default:
            return;
        }
        
        if (sub_title != null) {
            frame.setTitle(frame_name + sub_title);
        }
        
        BranchGroup tmpBG = groupObject.get_ShapeBG();
        shapeBG.detach();
        shapeBG = tmpBG;
        shapeBG.setCapability(BranchGroup.ALLOW_DETACH);
        alterableBG.addChild(shapeBG);
    }    
}