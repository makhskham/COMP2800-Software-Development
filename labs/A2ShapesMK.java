//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Asg 2
//08-02-2025
package CodesMK2800;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Node;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Texture2D;
import org.jogamp.java3d.Transform3D;

public class A2ShapesMK {
}

/**
 * Base class for rotatable 3D objects in the wind turbine simulation.
 * Handles rotation behavior and texture mapping for spherical components.
 */
class RotateObject extends BaseShapesMK {
    TransformGroup objRG;
    Texture t; // Texture object shared between NewYaw and NewBlades components
    
    /**
     * Configures rotation behavior for a 3D object.
     * @param x TransformGroup to be rotated
     * @param y Parent TransformGroup containing the rotation behavior
     * @param num Animation speed in milliseconds
     * @param rot Axis of rotation ('y' for yaw, 'z' for blade rotation)
     */
    public void rotate_Object(TransformGroup x, TransformGroup y, int num, char rot) {
        Transform3D tr = new Transform3D();
        float clockWise = 2.0f; // Positive for counter-clockwise, negative for clockwise rotation
        
        switch(rot) {
            case 'y':
                tr.rotY(0.0);
                break;
            case 'z':
                tr.rotZ(-Math.PI/2); // Initial rotation for blade alignment
                clockWise = -2.0f;   // Set clockwise rotation for blades
                break;
        }
    
        // Configure continuous rotation behavior
        Alpha rotationAlpha = new Alpha(-1, num);    
        RotationInterpolator rot_beh = new RotationInterpolator(
                rotationAlpha, x, tr, 0.0f, (float) Math.PI * clockWise);
        rot_beh.setSchedulingBounds(new BoundingSphere(new Point3d(), 20.0));
        y.addChild(rot_beh);
    }
    
    /**
     * Loads and configures texture for spherical components.
     * @return Configured Texture2D object
     */
    public Texture texture_App() {
        String filename = "C:/Users/makhs/neweclipse-workspace/COMP2800MK/src/images/ImageTop.jpg";
        TextureLoader loader = new TextureLoader(filename, null);
        ImageComponent2D image = loader.getImage();
        t = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
        t.setImage(0, image);
        return t;
    }

    @Override
    public TransformGroup position_Object() {
        return objTG;   
    }
    
    @Override
    protected Node create_Object() {
        return null;
    }
}

/**
 * Represents the yaw mechanism of the wind turbine.
 * Creates a textured sphere that rotates around the Y-axis.
 */
class NewYaw extends RotateObject {
    protected float radius;
    protected Texture2D texture;
    
    public NewYaw() {
        // Position the yaw mechanism above the tower
        Transform3D newYawTranslator = new Transform3D();
        newYawTranslator.setTranslation(new Vector3d(0.0f, 0.5f, 0.0f));
        objTG = new TransformGroup(newYawTranslator);
        objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        this.radius = 0.12f;
        
        // Configure rotation behavior
        objRG = new TransformGroup();
        objRG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRG.addChild(create_Object());
        
        rotate_Object(objRG, objTG, 20000, 'y'); // Rotate around Y-axis
        objTG.addChild(objRG);
    }

    @Override
    protected Node create_Object() {
        app = new Appearance();
        app.setTexture(texture_App());
        return new Sphere(radius, Primitive.GENERATE_TEXTURE_COORDS, app);
    }
}

/**
 * Represents the rotor hub of the wind turbine.
 * Creates a textured sphere that rotates around the Z-axis.
 */
class NewBlades extends RotateObject {
    protected float radius;

    public NewBlades() {
        // Position the rotor hub relative to the nacelle
        Transform3D newBladesTranslator = new Transform3D();
        newBladesTranslator.setTranslation(new Vector3d(0.3f, 0.0f, 0.0f));
        objTG = new TransformGroup(newBladesTranslator);
        objTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        this.radius = 0.07f;
        
        // Configure rotation behavior
        objRG = new TransformGroup();
        objRG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRG.addChild(create_Object());
        
        rotate_Object(objRG, objTG, 5000, 'z'); // Rotate around Z-axis
        objTG.addChild(objRG);
    }

    @Override
    protected Node create_Object() {
        app = new Appearance();
        app.setTexture(texture_App());
        return new Sphere(radius, Primitive.GENERATE_TEXTURE_COORDS, app);
    }
}