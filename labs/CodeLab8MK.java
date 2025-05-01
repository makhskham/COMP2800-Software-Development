//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Lab 8
//02-03-2025
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
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.PositionInterpolator;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.TransparencyAttributes;
import org.jogamp.java3d.utils.geometry.ColorCube;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3d;

public class CodeLab8MK extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	
	private static String frame_name = "MK's Lab #8";
	private static boolean r_tag = true;
	private static boolean object_tag = true;
	private static final String OBJECT_NAME = "Textured Disk";
	private static Alpha[] alpha = new Alpha[2];
	
	/* a function to make a ColorCube moving with transparency changed to indicate collision */ 
	public static TransformGroup move_Cube(Alpha alpha) {
		TransformGroup moveTG = new TransformGroup();      // need 'moveTG' to make cube moving
		moveTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		Transform3D axisPosition = new Transform3D();
		PositionInterpolator positionInterpol = new PositionInterpolator(alpha, moveTG, 
				axisPosition, -4f, 4f);		
		positionInterpol.setSchedulingBounds(CommonsMK.twenty_BS);
		moveTG.addChild(positionInterpol);	               // create the moving behavior
		
		// Create ColorCube (ColorCube is a Shape3D)
		ColorCube colorCube = new ColorCube(0.6);
		colorCube.setName("ColorCube");
		
		// Setup appearance with transparency capability
		Appearance app = new Appearance();                 
		app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		
		// Create initial transparency (fully opaque)
		TransparencyAttributes ta = new TransparencyAttributes(
				TransparencyAttributes.NICEST, 0f);
		ta.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
		app.setTransparencyAttributes(ta);

		colorCube.setAppearance(app);                      // set appearance for the cube
		
		// Add collision detection behavior to the cube
		TransparencyCollisionBehaviour collisionBehavior = 
				new TransparencyCollisionBehaviour(colorCube);  // ColorCube is already a Shape3D
		collisionBehavior.setSchedulingBounds(CommonsMK.twenty_BS);
		moveTG.addChild(collisionBehavior);
		
		moveTG.addChild(colorCube);                        // make the cube moving		
		
		return moveTG;
	}
	
	/* a function to build and return the content branch */
	private static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();

		TransformGroup sceneTG = new TransformGroup();     // introduce a TransformGroup for rotation 
		sceneBG.addChild(CommonsMK.rotate_Behavior(7500, sceneTG));

		CodeLab7MK.rotate_Side(sceneBG, sceneTG, alpha);   // make the two flipping (side) surfaces 
		CommonsMK.control_Rotation(r_tag);                 // make 'sceneTG' rotating by default
		sceneBG.addChild(sceneTG);
		
		// Create Alpha for the cube's movement
		Alpha cubeAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE |
				Alpha.DECREASING_ENABLE, 0, 0, 3000, 0000, 750, 3000, 0000, 750);
		sceneBG.addChild(move_Cube(cubeAlpha));            // add a moving ColorCube with collision detection
		
		return sceneBG;  
	}

	/* a constructor to set up for the application */
	public CodeLab8MK(BranchGroup scene) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas3D = new Canvas3D(config);
		canvas3D.setSize(800, 800);                        // set size of canvas
		SimpleUniverse su = new SimpleUniverse(canvas3D);  // create a SimpleUniverse
		                                                   // set the viewer's location
		CommonsMK.define_Viewer(su, new Point3d(1.35, -0.35, 12.0)); 		
		scene.addChild(CommonsMK.add_Lights(CommonsMK.White, 1));
		
		scene.compile();		                           // optimize the BranchGroup
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

	public static void main(String[] args) {              
		frame = new JFrame(frame_name + ": Rotating Textured Disks");
		frame.getContentPane().add(new CodeLab8MK(create_Scene()));
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
				alpha[0].pause();
				object_tag = false;
			}
			else {                                         // resume disk rotation
				alpha[0].resume();
				object_tag = true;
			}
			break;
		default:
			return;
		}
	}	
}