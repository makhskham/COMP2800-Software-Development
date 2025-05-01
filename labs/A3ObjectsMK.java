//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Asg 3
//22-02-2025
package CodesMK2800;

import java.io.FileNotFoundException;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.*;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.*;

public abstract class A3ObjectsMK {
	protected Alpha rotationAlpha; //made it protected so it can be used in derived classes
	protected BranchGroup objBG;  //load external objects to objBG
	protected TransformGroup objTG; //objTG for positioning objects                
	protected TransformGroup objRG; //objRG for rotating objects                    
	protected double scale;  //scale is for sizing                             
	protected Vector3f post; //post is for locations                            
	protected Shape3D obj_shape;

	public abstract TransformGroup position_Object();      // need to be defined in derived classes
	public abstract void add_Child(TransformGroup nextTG);
	
	public Alpha get_Alpha() { return rotationAlpha; };    // NOTE: keep for future use 

	/* a function to load and return object shape from the file named 'obj_name' */
	private Scene loadShape(String obj_name) {
		ObjectFile f = new ObjectFile(ObjectFile.RESIZE, (float) (60 * Math.PI / 180.0));
		Scene s = null;
		try {                                              // load object's definition file to 's'
			s = f.load("C:/Users/makhs/neweclipse-workspace/COMP2800MK/src/images/" + obj_name + ".obj");
		} catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(1);
		} catch (ParsingErrorException e) {
			System.err.println(e);
			System.exit(1);
		} catch (IncorrectFormatException e) {
			System.err.println(e);
			System.exit(1);
		}
		return s;                                          // return the object shape in 's'
	}
	
	/* function to set 'objTG' and attach object after loading the model from external file */
	protected void transform_Object(String obj_name) {
		Transform3D scaler = new Transform3D();
		scaler.setScale(scale);                            // set scale for the 4x4 matrix
		scaler.setTranslation(post);                       // set translations for the 4x4 matrix
		objTG = new TransformGroup(scaler);                // set the translation BG with the 4x4 matrix
		objBG = loadShape(obj_name).getSceneGroup();       // load external object to 'objBG'
		obj_shape = (Shape3D) objBG.getChild(0);           // get and cast the object to 'obj_shape'
		obj_shape.setName(obj_name);                       // use the name to identify the object 
	}
	
	protected Appearance app = new Appearance();
	private int shine = 32;                                // specify common values for object's appearance
	protected Color3f[] mtl_clr = {new Color3f(1.000000f, 1.000000f, 1.000000f),
			new Color3f(0.772500f, 0.654900f, 0.000000f),	
			new Color3f(0.175000f, 0.175000f, 0.175000f),
			new Color3f(0.000000f, 0.000000f, 0.000000f)};
	
    /* a function to define object's material and use it to set object's appearance */
	protected void obj_Appearance() {		
		Material mtl = new Material();                     // define material's attributes
		mtl.setShininess(shine);
		mtl.setAmbientColor(mtl_clr[0]);                   // use them to define different materials
		mtl.setDiffuseColor(mtl_clr[1]);
		mtl.setSpecularColor(mtl_clr[2]);
		mtl.setEmissiveColor(mtl_clr[3]);                  // use it to enlighten a button
		mtl.setLightingEnable(true);

		app.setMaterial(mtl);                              // set appearance's material
		obj_shape.setAppearance(app);                      // set object's appearance
	}	
}

class StandObject extends A3ObjectsMK { //FanStand
	public StandObject() {
		scale = 1d;  // use to scale up/down original size
		post = new Vector3f(0f, 0f, 0f); //at origin
		transform_Object("FanStand");                   
		mtl_clr[1] = new Color3f(0.58f, 0.69f, 0.11f);  //fanstand has a different colour than the ones already defined 		                                              
		obj_Appearance(); //set appearance
	}

	public TransformGroup position_Object() { //attach fan stand's BG to the objRG and then to objTG
		Transform3D r_axis = new Transform3D(); 
		r_axis.rotY(Math.PI); //rotate on y axis
		objRG = new TransformGroup(r_axis); 
		objTG.addChild(objRG); //attach RG to TG
		objRG.addChild(objBG); //attach BG to RG
		return objTG;                                      
	}

	public void add_Child(TransformGroup nextTG) {
		objRG.addChild(nextTG);   //attach next transform group to objRG
	}
}

class SwitchObject extends A3ObjectsMK {
    private Switch leftSwitch; // Left cube (pause/resume)
    private Switch rightSwitch; // Right cube (power ON/OFF)

    public SwitchObject() {
        scale = 0.3d;  // actual scale is 0.3 = 1.0 x 0.3
        post = new Vector3f(0.02f, -0.77f, -0.8f); 
        transform_Object("FanSwitch"); // set transformation to objTG and load object file
        obj_Appearance(); // set appearance after converting object node to Shape3D

        // Add the two cube buttons to the switch
        addCubeButtons();
    }

    private void addCubeButtons() {
        // Create the left cube (pause/resume)
        leftSwitch = createCubeSwitch(1); // Start with red (inactive/not paused)
        TransformGroup leftTG = new TransformGroup();
        leftTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND); // Enable adding children
        Transform3D leftPos = new Transform3D();
        leftPos.setTranslation(new Vector3f(-0.6f, 0.2f, 0f)); // Position the left cube
        leftTG.setTransform(leftPos);
        leftTG.addChild(leftSwitch);

        // Create the right cube (power ON/OFF)
        rightSwitch = createCubeSwitch(0); // Start with green (active/playing)
        TransformGroup rightTG = new TransformGroup();
        rightTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND); // Enable adding children
        Transform3D rightPos = new Transform3D();
        rightPos.setTranslation(new Vector3f(0.6f, 0.2f, 0f)); // Position the right cube
        rightTG.setTransform(rightPos);
        rightTG.addChild(rightSwitch);

        // Add both cubes to the switch's transform group
        objTG.addChild(leftTG);
        objTG.addChild(rightTG);
    }

    private Switch createCubeSwitch(int initialColor) {
        Switch cubeSwitch = new Switch();
        cubeSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE); // Enable switching between children
        for (int i = 0; i < 2; i++) { 
            Color3f clr = (i == 0) ? CommonsMK.Red : CommonsMK.Green;
            Appearance app = CommonsMK.set_Appearance(clr);
            
            // Use the correct flags for the Box constructor
            Box box = new Box(0.25f, 0.25f, 0.25f, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, app);
            
            box.setUserData(i); // 'UserData' retrievable at picking
            box.setName("box"); // NOTE: 'Name' is also retrievable

            // Apply a rotation transformation to the box to make it face forward
            Transform3D rotation = new Transform3D();
            rotation.rotX(Math.toRadians(90)); // Rotate 90 degrees around the X-axis to face forward
            TransformGroup tg = new TransformGroup(rotation);
            tg.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND); // Enable adding children
            tg.addChild(box);

            cubeSwitch.addChild(tg);
        }
        cubeSwitch.setWhichChild(initialColor); // start with the specified color
        return cubeSwitch;
    }

    public Switch getLeftSwitch() {
        return leftSwitch;
    }

    public Switch getRightSwitch() {
        return rightSwitch;
    }

    @Override
    public TransformGroup position_Object() {
        objTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND); // Enable adding children
        objTG.addChild(objBG); // attaching fan switch object to objTG
        return objTG; 
    }

    @Override
    public void add_Child(TransformGroup nextTG) {
        objTG.addChild(nextTG);  // attaching next transform group to objTG
    }
}

class ShaftObject extends A3ObjectsMK {
	public ShaftObject() {
		scale = 0.18d;
		post = new Vector3f(0f, 0.99f, 0.6f); //position of shaft is in relation to the stand
		transform_Object("FanShaft");
		mtl_clr[1] = new Color3f(1f, 0f, 0f); //the shaft object is red
		obj_Appearance();
		
		objRG = new TransformGroup(); //transform group for rotation
		objRG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		Transform3D tr = new Transform3D();
		tr.rotY(Math.PI/2);
				
		rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE, 0L, 0L, 5000L, 2500L, 200L, 5000L, 2500L, 200L ); 

		RotationInterpolator rot_beh = new RotationInterpolator(
				rotationAlpha, objRG, tr, (float)-Math.PI/2, (float) Math.PI/2);
		rot_beh.setSchedulingBounds(new BoundingSphere(new Point3d(), 20.0));
		objTG.addChild(rot_beh);
		objTG.addChild(objRG);
		
	}

	@Override
	public TransformGroup position_Object() {
		objTG.addChild(objBG);
		return objTG;
	}

	@Override
	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);
		
	}
	
}

class MotorObject extends A3ObjectsMK {
	public MotorObject() {
		scale = 3.5d;
		post = new Vector3f(0f, 2.5f, -0.9f); //position of motor is in relation to shaft
		transform_Object("FanMotor");
		obj_Appearance();
		
		
	}

	@Override
	public TransformGroup position_Object() {
		objTG.addChild(objBG);
		return objTG;
	}

	@Override
	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);
		
	}
	
}


class BladesObject extends A3ObjectsMK {
	public BladesObject() {
		scale = 2.9d;
		post = new Vector3f(0f, 0f, -1f); //translation in relation to the motor
		transform_Object("FanBlades");
		
		
		objRG = new TransformGroup(); //transform group for rotation
		objRG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D tr = new Transform3D();
		
		tr.rotX(-Math.PI/2);
		
		rotationAlpha = new Alpha(-1, 500); //continuous rotation at 500ms/round    
		RotationInterpolator rot_beh = new RotationInterpolator(
				rotationAlpha, objRG, tr, 0.0f, (float) Math.PI * 2.0f);
		rot_beh.setSchedulingBounds(new BoundingSphere(new Point3d(), 20.0));
		
		for(int i = 0; i<5; i++) {
			obj_shape = (Shape3D) objBG.getChild(i); 
			obj_Appearance(); //applying appearance to blades

		}
		
		objRG.addChild(objBG); //adding all blades + cylinder
		objTG.addChild(rot_beh); //adding rotation behaviour to the objTG
		objTG.addChild(objRG); //adding RG to the TG
		
		
	}

	@Override
	public TransformGroup position_Object() {
		return objTG;
	}

	@Override
	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);
		
	}
}



class GuardObject extends A3ObjectsMK {
	public GuardObject() {
		scale = 3.3d;
		post = new Vector3f(0f, 0f, -1.3f);
		transform_Object("FanGuard");
		
		for(int i = 0; i<objBG.numChildren(); i++) {
			obj_shape = (Shape3D) objBG.getChild(i); //for each child apply appearance
			obj_Appearance(); 
		}
	
	}
	
	
	@Override
	public TransformGroup position_Object() {
		objTG.addChild(objBG);
		return objTG;
	}

	@Override
	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);
		
	}
}



class BaseShape extends A3ObjectsMK {
	public BaseShape() {
		Transform3D translator = new Transform3D();
		translator.setTranslation(new Vector3d(0.0, -0.54, 0));
		objTG = new TransformGroup(translator); 

		objTG.addChild(create_Object());  //attach object to objTG
	}
	
	protected Node create_Object() {
		app = CommonsMK.set_Appearance(CommonsMK.White); //base appearance
		app.setTexture(textured_App("MarbleTexture"));  //base texture
		TransparencyAttributes ta =                      
				new TransparencyAttributes(TransparencyAttributes.SCREEN_DOOR, 0.5f);
		app.setTransparencyAttributes(ta);  //base is half transparent
		return new Box(0.5f, 0.04f, 0.5f, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, app);
	}
	
	private static Texture textured_App(String name) {
		String filename = "C:/Users/makhs/neweclipse-workspace/COMP2800MK/src/images/" + name + ".jpg"; //image location
		TextureLoader loader = new TextureLoader(filename, null);
		ImageComponent2D image = loader.getImage(); //load the image
		if (image == null)
			System.out.println("Cannot load file: " + filename);

		Texture2D texture = new Texture2D(Texture.BASE_LEVEL,
				Texture.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image); //set image for texture

		return texture;
	}

	public TransformGroup position_Object() {
		objTG.addChild(objBG);  //attach objBG to objTG
		return objTG; 
	}

	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG); //attach the next transformGroup to 'objTG'
	}
}
