package CodesMK2800;

import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

public class CodeA1MK extends JPanel {

    private static final long serialVersionUID = 1L; // Ensures compatibility during serialization
    private static JFrame frame; // JFrame for displaying the 3D scene
    private static final int OBJ_NUM = 7; // Number of objects in the 3D scene

    // Method to build the content branch of the 3D scene
    public static BranchGroup create_Scene() {
        BranchGroup sceneBG = new BranchGroup(); // Root node for the scene graph
        TransformGroup sceneTG = new TransformGroup(); // Group for transformations (e.g., rotation)

        // Array to hold various objects to be added to the scene
        BaseShapesMK[] baseShapes = new BaseShapesMK[OBJ_NUM];

        /*
         * Constructor explanations for BaseShapesMK:
         * - SquareShape: position (vecx, vecy, vecz), dimensions (xdim, ydim, zdim), color (c)
         * - SphereShape: position (vecx, vecy, vecz), radius
         * - CylinderShape: position (vecx, vecy, vecz), radius, height
         */

        // Creating and adding objects to the scene
        baseShapes[0] = new SquareShape(0.0f, -0.54f, 0f, 0.5f, 0.04f, 0.5f, CommonsMK.White); // A white square
        String str = "MK's A1"; // Text to be displayed
        baseShapes[1] = new ColorString(str, CommonsMK.White, 0.1, 
                new Point3f(-str.length() / 2f, 6.7f, 1.3f)); // White text centered horizontally
        baseShapes[2] = new CylinderShape(0.0f, 0.0f, 0.0f, 0.12f, 1.0f); // A cylinder
        baseShapes[3] = new SphereShape(0.0f, 0.5f, 0.0f, 0.12f); // A sphere
        baseShapes[4] = new SquareShape(0.2f, 0.70f, 0.0f, 0.31f, 0.08f, 0.12f, CommonsMK.Cyan); // Cyan square
        baseShapes[5] = new SphereShape(0.5f, 0.7f, 0.0f, 0.07f); // Another sphere
        baseShapes[6] = new SquareShape(0.56f, 0.7f, 0.0f, 0.01f, 0.06f, 0.5f, CommonsMK.Magenta); // Magenta square

        // Adding all objects to the transform group
        for (int i = 0; i < OBJ_NUM; i++) {
            sceneTG.addChild(baseShapes[i].position_Object());
        }

        // Adding lighting and rotation behavior to the scene
        sceneBG.addChild(CommonsMK.add_Lights(CommonsMK.White, 1)); // Adds a white light source
        sceneBG.addChild(CommonsMK.rotate_Behavior(7500, sceneTG)); // Rotates the transform group continuously
        sceneBG.addChild(sceneTG); // Adding the transform group to the branch group
        return sceneBG; // Returning the constructed branch group
    }

    // Constructor for initializing the 3D scene and GUI
    public CodeA1MK(BranchGroup sceneBG) {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration(); // Fetch preferred graphics settings
        Canvas3D canvas = new Canvas3D(config); // Canvas for rendering the 3D scene

        SimpleUniverse su = new SimpleUniverse(canvas); // Creating a SimpleUniverse for the scene
        CommonsMK.define_Viewer(su, new Point3d(4.0d, 0.0d, 1.0d)); // Positioning the viewer

        sceneBG.compile(); // Optimizing the branch group for better performance
        su.addBranchGraph(sceneBG); // Adding the branch group to the universe

        // Setting up the JPanel layout and adding the canvas
        setLayout(new BorderLayout());
        add("Center", canvas);
        frame.setSize(800, 800); // Setting the size of the JFrame
        frame.setVisible(true); // Making the JFrame visible
    }

    // Main method to launch the application
    public static void main(String[] args) {
        frame = new JFrame("MK's Assignment 1"); // Creating the JFrame with a title
        frame.getContentPane().add(new CodeA1MK(create_Scene())); // Adding the JPanel with the scene
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensuring the application exits on close
    }
}
