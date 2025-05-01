//Makhsuma Khamzaliyeva - 110120302
//COMP2800 
package CodesMK2800;

import java.awt.Font;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Cylinder;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.vecmath.*;

public abstract class BaseShapesMK {
	protected TransformGroup objTG = new TransformGroup(); //objTG used to position the objects

	protected abstract Node create_Object(); //this abstract method will be implemented in the derived classes
	
	public TransformGroup position_Object() { //obj_shape will be attached to objTG, this method returns the transform group
		return objTG;   
	}
	
	protected Appearance app; //app will be initialized in the derived classes
	public void add_Child(TransformGroup nextTG) {
		objTG.addChild(nextTG);   //attach nextTG to objTG
	}
}


//derived class to make Squares/Boxes:
class SquareShape extends BaseShapesMK {
	//declaring instance variables so they can be used in all methods of the class (values found through constructor)
	//this also makes the shapes adjustable
	protected float xdim;
	protected float ydim;
	protected float zdim;
	protected Color3f colour; //2 square shapes in two colours
	
	public SquareShape(float vecx, float vecy, float vecz, float xdim, float ydim, float zdim, Color3f c) {
		Transform3D translator = new Transform3D();
		translator.setTranslation(new Vector3d(vecx, vecy, vecz)); //the position of the square will be determined through arguments in constructor
		objTG = new TransformGroup(translator);            
		
		//using constructor arguments to initialize instance variables (using this. ):
		//now they can be used in the create_Object() method
		this.xdim = xdim;
		this.ydim = ydim;
		this.zdim = zdim;
		this.colour = c;
		
		objTG.addChild(create_Object()); //attaching object to the objTG
		//the overriden create_Object() is called
	}
	@Override //overriding BaseShapesKK method
	protected Node create_Object() { 
		app = CommonsMK.set_Appearance(colour);   // set the appearance for the base, using colour arg
		return new Box(xdim, ydim, zdim, Primitive.GENERATE_NORMALS, app);
		
	}
}

class TransparentBase extends BaseShapesMK {
	public TransparentBase() {
		Transform3D translator = new Transform3D();
		translator.setTranslation(new Vector3d(0.0f, -0.54f, 0.0f)); //same dimensions as previous (white) base
		objTG = new TransformGroup(translator); 
	
		
		objTG.addChild(create_Object());
	}
    
	@Override
	protected Node create_Object() {
		Box baseBox = new Box(0.5f, 0.04f, 0.5f, CommonsMK.set_Appearance(CommonsMK.White)); //creating a new box
		
		Color3f surfaceColours[] = {CommonsMK.Red, CommonsMK.Yellow, CommonsMK.Green, CommonsMK.Blue, CommonsMK.Purple, CommonsMK.Magenta};

		for (int i = 0; i < 6; i++) { //setting transparency and colour for all surfaces
			app = CommonsMK.set_Appearance(surfaceColours[i]); //setting the colour first
			
		    TransparencyAttributes transparency = new TransparencyAttributes();
		    transparency.setTransparencyMode(TransparencyAttributes.FASTEST); //using blended transparency
		    transparency.setTransparency(0.5f); //semi transparent
		    
		    
		    app.setTransparencyAttributes(transparency); //set transparency on the appearance
		    baseBox.getShape(i).setAppearance(app); //set the appearance on the surface
		}

		
		
		return baseBox;
	}
	
}


//derived class to make Cylinders:
class CylinderShape extends BaseShapesMK {
	//instance variables for cylinder shape:
	//even though there is only one cylinder in this case, it's more flexible to get measurements through constructor
	protected float radius;
	protected float height;
	
	public CylinderShape(float vecx, float vecy, float vecz, float radius, float height) {
		Transform3D cylTranslator = new Transform3D(); //translators for each shape have their prefixes to not confuse anything
		cylTranslator.setTranslation(new Vector3d(vecx, vecy, vecz));
		objTG = new TransformGroup(cylTranslator);
		
		//setting radius and height:
		this.radius = radius; 
		this.height = height;
		
		objTG.addChild(create_Object());
	}
	@Override
	protected Node create_Object() {
		app = CommonsMK.set_Appearance(CommonsMK.Orange);
		// TODO Auto-generated method stub
		return new Cylinder(radius, height, Primitive.GENERATE_NORMALS, app);
		//using Cylinder constructor for Java3D to create cylinder
	
	}
	
}


//derived class to make Spheres:
class SphereShape extends BaseShapesMK {
	protected float radius;
	
	
	public SphereShape(float vecx, float vecy, float vecz, float radius) {
		Transform3D sphTranslator = new Transform3D();
		sphTranslator.setTranslation(new Vector3d(vecx, vecy, vecz));
		objTG = new TransformGroup(sphTranslator);
		
		this.radius = radius; //setting radius for Sphere
		
		objTG.addChild(create_Object());
	}
	@Override
	protected Node create_Object() {
		// TODO Auto-generated method stub
		app = CommonsMK.set_Appearance(CommonsMK.Red); //both spheres are red
		return new Sphere(radius, Primitive.GENERATE_NORMALS, app);
		//Sphere constructor used
	}
	
}



//derived class to create a Coloured String:
class ColorString extends BaseShapesMK {
	//instance variables: can be used in all methods, make the label adjustable
	private String str;
	private Color3f colour;
	private double scl;
	private Point3f pos;    
	public ColorString(String str_ltrs, Color3f str_clr, double s, Point3f p) {
		str = str_ltrs;	
		colour = str_clr;
		scl = s;
		pos = p;

		Transform3D scaler = new Transform3D();
		scaler.setScale(scl);   //scaling 4x4 matrix 
		Transform3D rotator = new Transform3D(); //4x4 matrix for rotation
		rotator.rotY(Math.PI);
		Transform3D trfm = new Transform3D(); //4x4 matrix for composition
		
		//applying rotation AFTER scaling 
		trfm.mul(rotator);                                 
		trfm.mul(scaler); 
		
		//setting combined transformation:
		objTG = new TransformGroup(trfm);  
		
		//attaching the Shape3D object to the obj transform group:
		objTG.addChild(create_Object());                 		
	}
	
	protected Node create_Object() {
		Font my2DFont = new Font("Arial", Font.PLAIN, 1); //font name, style and size
		FontExtrusion myExtrude = new FontExtrusion();
		Font3D font3D = new Font3D(my2DFont, myExtrude);	
		Text3D text3D = new Text3D(font3D, str, pos);   //creating text3D for font3D for String str at position pos
		
		Appearance app = CommonsMK.set_Appearance(colour); //specifying text colour 
		return new Shape3D(text3D, app); //returning Shape3D String label (with set appearance)
	}
}
