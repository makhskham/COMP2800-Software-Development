//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Lab 7
//22-02-2025
package CodesMK2800;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3f;

public class CodeLab7MK extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	
	private static String frame_name = "MK's Lab #7";
	private static boolean r_tag = true; // Flag to control rotation state
	private static final String OBJECT_NAME = "Textured Disk";
	private static Alpha[] alpha = new Alpha[2]; // Alphas for controlling rotation animations
	
	/* Function to make the disk's side surface rotate */
	public static void rotate_Side(BranchGroup snBG, TransformGroup snTG, Alpha[] aph) {

		String[] side_name = {"Top", "Side", "Extra"};
		Transform3D slide, slide2, plate, plate2, rotate_axis, rotate_axis2;
		TransformGroup slideTG, slideTG2, plateTG, plateTG2, hingeTG, hingeTG2;
		RotationInterpolator rotationInterpol, rotationInterpol2;
		
		// Set up translation for the first side
		slide = new Transform3D();
		slide.setTranslation(new Vector3f(-2.0f, 0, 0.1f));
		slideTG = new TransformGroup(slide);
		
		// Set up translation for the second side
		slide2 = new Transform3D();
		slide2.setTranslation(new Vector3f(-2.0f, 0, 0.05f));
		slideTG2 = new TransformGroup(slide2); 
		
		// Create TransformGroups for positioning the surfaces
		plate = new Transform3D();                    
		plate.setTranslation(new Vector3f(2.0f, 0, -0.1f));
		plateTG = new TransformGroup(plate);              
		plateTG.addChild(L5TextureSurfaceMK.ring_Shape(side_name[0], 60)); // Add top surface
		
		plate2 = new Transform3D();
		plate2.setTranslation(new Vector3f(2.0f, 0, -0.05f));
		plateTG2 = new TransformGroup(plate2);
		plateTG2.addChild(L5TextureSurfaceMK.ring_Shape(side_name[2], 55)); // Add extra surface
		
		// Set up Alpha objects for controlling rotation timing
		aph[0] = new Alpha(-1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE, 0, 0, 4000, 0000, 1000, 4000, 0000, 1000);
		aph[1] = new Alpha(-1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE, 0, 0, 4000, 0000, 1000, 4000, 0000, 1000); 
		
		// Set up rotation for the first side
		hingeTG = new TransformGroup();                    // Use 'hingeTG' for rotation
		hingeTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		rotate_axis = new Transform3D();                   // Rotate around 'hingeTG's y-axis
		rotationInterpol = new RotationInterpolator(aph[0], hingeTG, rotate_axis, 0, -(float) (Math.PI / 2.0)); 
		rotationInterpol.setSchedulingBounds(CommonsMK.twenty_BS);
		slideTG.addChild(rotationInterpol);               // Add rotation behavior to 'slideTG'
		
		// Set up rotation for the second side
		hingeTG2 = new TransformGroup();
		hingeTG2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		rotate_axis2 = new Transform3D();
		rotationInterpol2 = new RotationInterpolator(aph[1], hingeTG2, rotate_axis2, 0, (float)(Math.PI/2.0)); // Rotate in the opposite direction
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
		sideTG.addChild(L5TextureSurfaceMK.ring_Shape(side_name[1], 60)); // Add side surface
		sideTG.addChild(slideTG2);
		snTG.addChild(sideTG);
	}
	
	/* Function to build and return the content branch */
	private static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();

		TransformGroup sceneTG = new TransformGroup();     // Introduce a TransformGroup for rotation 
		sceneBG.addChild(CommonsMK.rotate_Behavior(7500, sceneTG)); // Add rotation behavior

		rotate_Side(sceneBG, sceneTG, alpha);              // Make the two side surfaces rotate
		CommonsMK.control_Rotation(r_tag);                 // Enable rotation by default
		sceneBG.addChild(sceneTG);
		
		return sceneBG;  
	}

	/* Constructor to set up the application */
	public CodeLab7MK(BranchGroup scene) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas3D = new Canvas3D(config);
		canvas3D.setSize(800, 800);                        // Set size of canvas
		SimpleUniverse su = new SimpleUniverse(canvas3D);  // Create a SimpleUniverse
		                                                   // Set the viewer's location
		CommonsMK.define_Viewer(su, new Point3d(1.35, -0.35, 10.0)); 		
		scene.addChild(CommonsMK.add_Lights(CommonsMK.White, 1)); // Add lighting
		
		scene.compile();		                           // Optimize the BranchGroup
		su.addBranchGraph(scene);                          // Attach 'scene' to 'su'

		Menu m = new Menu("Menu");                         // Set menu's label
		m.addActionListener(this);
		MenuBar menuBar = CodeLab2MK.build_MenuBar(m, OBJECT_NAME);
		frame.setMenuBar(menuBar);                         // Build and set the menu bar

		setLayout(new BorderLayout());
		add("Center", canvas3D);
		frame.setSize(810, 800);                           // Set the size of the frame
		frame.setVisible(true);
	}

	public static void main(String[] args) {               // Main method to start the application
		frame = new JFrame(frame_name + ": Rotating Textured Disks");     
		frame.getContentPane().add(new CodeLab7MK(create_Scene()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}	

	@Override
	public void actionPerformed(ActionEvent e) {
		// Handle menu item selections
		switch(e.getActionCommand()) {                     
		case "Exit": 
			System.exit(0);                                // Quit the application
		case "Pause/Rotate":
			r_tag = (r_tag == true)? false : true;         // Toggle rotation state
			CommonsMK.control_Rotation(r_tag);
			return;
		case OBJECT_NAME:			
			break;
		default:
			return;
		}
	}	
}