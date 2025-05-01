//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Asg 3
//22-02-2025
package CodesMK2800;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;


public class CodeA3MK extends JPanel{

	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private static boolean isPaused = false;
    private static boolean isPowered = true;

	private static final int OBJ_NUM = 7;
	private static A3ObjectsMK[] object3D = new A3ObjectsMK[OBJ_NUM];

	/* a public function to build the base labeled with 'str' */
	public static TransformGroup create_Base(String str) {
		BaseShape baseShape = new BaseShape();
		
		Transform3D scaler = new Transform3D();
		scaler.setScale(new Vector3d(4d, 2d, 4d)); //set scale for 4x4 matrix
		TransformGroup baseTG = new TransformGroup(scaler); 
		baseTG.addChild(baseShape.position_Object());

		ColorString clr_str = new ColorString(str, CommonsMK.Red, 0.06, 
				new Point3f(-str.length() / 4f, -9.4f, 8.2f));
		Transform3D r_axis = new Transform3D(); //rotate around y axis
		r_axis.rotY(Math.PI);                              
		TransformGroup objRG = new TransformGroup(r_axis); 
		objRG.addChild(clr_str.position_Object());       
		baseTG.addChild(objRG); //adding objRG to baseTG

		return baseTG;
	}
	
	/* a function to create the desk fan */
	private static TransformGroup create_Fan() {
		TransformGroup fanTG = new TransformGroup();

		object3D[0] = new StandObject();  //creating the fan stand
		fanTG = object3D[0].position_Object();
		
		//creating switch and attaching to fan stand:
		object3D[1] = new SwitchObject();                
		object3D[0].add_Child(object3D[1].position_Object());
		
		//creating shaft and attaching to fan stand:
		object3D[2] = new ShaftObject();
		object3D[0].add_Child(object3D[2].position_Object());
		
		//creating motor and attaching it to shaft objRG (so it rotates with the shaft):
		object3D[3] = new MotorObject();
		object3D[2].objRG.addChild(object3D[3].position_Object()); 
		
		//creating blades and attaching it to the motor
		object3D[4] = new BladesObject();
		object3D[3].add_Child(object3D[4].position_Object()); 
		
		//creating the guard and attaching it to the motor as well
		object3D[5] = new GuardObject(); 
		object3D[3].add_Child(object3D[5].position_Object());
		
		fanTG.addChild(create_Base("MK's Assignment 3")); //attaching base to the fan stand
		return fanTG;
	}

	/* a function to build the content branch, including the fan and other environmental settings */
	public static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();	

		//keeping sceneTG stationary
		sceneTG.addChild(create_Fan()); //adding fan to sceneTG

		sceneBG.addChild(sceneTG); //keep this as stationary
		sceneBG.addChild(CommonsMK.add_Lights(CommonsMK.White, 1));

		return sceneBG;
	}

	public CodeA3MK(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		canvas.addKeyListener(new KeyListener() { //adding the keylistener to the canvas
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_Z) { //when z is pressed
					if(object3D[2].get_Alpha().isPaused()) { //if the motor is already paused we resume it
						object3D[2].get_Alpha().resume();
					}
					else {
						object3D[2].get_Alpha().pause(); //otherwise paused
					}
				}
				else if(e.getKeyCode() == KeyEvent.VK_X) { //when x is pressed

					if(object3D[2].get_Alpha().isPaused() && object3D[4].get_Alpha().isPaused()) {
						object3D[2].get_Alpha().resume();
						object3D[4].get_Alpha().resume();
					}
					else {
						object3D[2].get_Alpha().pause();
						object3D[4].get_Alpha().pause();
					}
				
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

				
				
			}
		});
		
		SimpleUniverse su = new SimpleUniverse(canvas); //create simple universe
		CommonsMK.define_Viewer(su, new Point3d(0.25d, 0.25d, 10.0d)); //viewers location
		
		sceneBG.compile();	//optimizing branch group
		su.addBranchGraph(sceneBG);

		setLayout(new BorderLayout());
		add("Center", canvas);
		frame.setSize(800, 800);      
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		frame = new JFrame("MK's Assignment 3"); 
		frame.getContentPane().add(new CodeA3MK(create_Scene()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}

