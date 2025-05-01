//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Asg 2
//08-02-2025
package CodesMK2800;

import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

/**
 * Main application class for the 3D wind turbine simulation.
 * Creates and manages the scene graph containing all components of the wind turbine.
 */
public class Assignment2MK extends JPanel {
    private static final long serialVersionUID = 1L;
    private static JFrame frame;

    /**
     * Creates the main scene graph containing all wind turbine components.
     * Establishes the hierarchical relationship between components based on their
     * physical connections and movement dependencies.
     * 
     * @return BranchGroup containing the complete scene graph
     */
    public static BranchGroup create_Scene() {
        BranchGroup sceneBG = new BranchGroup();
        TransformGroup sceneTG = new TransformGroup();
        
        // Create wind turbine components
        BaseShapesMK base = new TransparentBase();
        
        // Add identification text
        String str = "MK's A2";
        BaseShapesMK s = new ColorString(str, CommonsMK.White, 0.1, 
                new Point3f(-str.length() / 3.5f, -0.2f, 1.3f));
        
        // Create structural components
        BaseShapesMK tower = new CylinderShape(0.0f, 0.0f, 0.0f, 0.12f, 1.0f);
        RotateObject yaw = new NewYaw();
        BaseShapesMK nacelle = new SquareShape(0.2f, 0.2f, 0.0f, 0.31f, 0.08f, 0.12f, CommonsMK.Cyan);
        RotateObject rotor = new NewBlades();
        BaseShapesMK blade = new SquareShape(0.06f, 0.02f, 0.0f, 0.01f, 0.06f, 0.5f, CommonsMK.Magenta);
        
        // Build scene graph hierarchy
        // Components are added based on their physical connections to maintain proper
        // transformation inheritance
        sceneTG.addChild(base.position_Object());
        sceneTG.addChild(tower.position_Object());
        (tower.position_Object()).addChild(yaw.position_Object());
        
        // Add components that rotate with the yaw mechanism
        (yaw.objRG).addChild(nacelle.position_Object());
        (nacelle.position_Object()).addChild(rotor.position_Object());
        (nacelle.position_Object()).addChild(s.position_Object());
        (rotor.objRG).addChild(blade.position_Object());
        
        // Add lighting to the scene
        sceneBG.addChild(CommonsMK.add_Lights(CommonsMK.White, 1));
        sceneBG.addChild(sceneTG);
        return sceneBG;
    }

    /**
     * Constructor for the wind turbine simulation.
     * Initializes the 3D viewing environment and configures the display canvas.
     * 
     * @param sceneBG BranchGroup containing the complete scene graph
     */
    public Assignment2MK(BranchGroup sceneBG) {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        
        SimpleUniverse su = new SimpleUniverse(canvas);
        CommonsMK.define_Viewer(su, new Point3d(4.0d, 0.0d, 1.0d));

        sceneBG.compile();
        su.addBranchGraph(sceneBG);

        setLayout(new BorderLayout());
        add("Center", canvas);
        frame.setSize(800, 800);
        frame.setVisible(true);
    }

    /**
     * Main entry point for the application.
     * Creates the application window and initializes the 3D scene.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        frame = new JFrame("MK's Assignment 2");
        frame.getContentPane().add(new Assignment2MK(create_Scene()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}