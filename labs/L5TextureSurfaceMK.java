//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Lab 5
//02-02-2025
package CodesMK2800;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Link;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.SharedGroup;
import org.jogamp.java3d.TexCoordGeneration;
import org.jogamp.java3d.Texture2D;
import org.jogamp.java3d.TextureAttributes;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.TriangleStripArray;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;

public class L5TextureSurfaceMK extends GroupObjects {

	/* a function to scale and position the linked item(s) at a particular location */
	public static TransformGroup link_OneDisk(Vector3f pos, Link link, Vector3d scl) {
		Transform3D trfm = new Transform3D();
		trfm.rotX(Math.PI);
		trfm.setTranslation(pos);  //specifying translation 
		trfm.setScale(scl); //^^ and scaling                           
		TransformGroup posTG = new TransformGroup(trfm); //initializing transformgroup
		posTG.addChild(link);  //positioning linked items
		return posTG; //returning posTG
	}

	/* a function to create a table by sharing five of the textured disks with scaling and positioning */
	public static TransformGroup round_Table(int n) {      
		// define scaling factors
		//scl[] is for the sizing of each table leg and the table top, all table legs are the same size
		Vector3d[] scl = {new Vector3d(0.25, 0.25, 2), new Vector3d(0.25, 0.25, 2), new Vector3d(0.25, 0.25, 2), new Vector3d(0.25, 0.25, 2), new Vector3d(1, 1, 1)};    
		
		//post[] is for positioning the table legs and table top, the table legs are at different x,y values (one pair of legs is at opposite ends of table)
		Vector3f[] post = {new Vector3f(1.4f, 0, -0.2f), new Vector3f(-1.4f, 0, -0.2f), new Vector3f(0f, 1.4f, -0.2f), new Vector3f(0, -1.4f, -0.2f), new Vector3f(0, 0, 0.1f)};
                                                           
		String[] side_name = {"Top", "Side", "Extra"}; //bottom is the face of the table 
		SharedGroup shared3D = new SharedGroup( );	
		for (int i = 0; i < side_name.length; i++) //sharing sides of the table
			shared3D.addChild(L5TextureSurfaceMK.ring_Shape(side_name[i], n)); 
		shared3D.compile(); //optimizing group to be shared

		TransformGroup linkTG = new TransformGroup();
		Link[] links = new Link[5];
		for (int i = 0; i < links.length; i++) {
			links[i] = new Link(shared3D);  //linking 5 disks (4 legs and 1 table top)
			linkTG.addChild(L5TextureSurfaceMK.link_OneDisk(post[i], links[i], scl[i]));
		}                                               

		return linkTG;   
	}
	
	/* a function to a surface of the disk with 'n' sides and with texture mapping */
	private static TriangleStripArray ring_Side(String shape_key, int n) {
		float r = 2.0f;
		int k;
		
		int v_num = (n + 1) * 2;   //setting n+1 points on the circle
		int vn_count[] = {v_num};   
		Point3f[] v_cdnts = new Point3f[v_num]; //allocating 3D coordinates for the surface
		Vector3f[] c_nmls = new Vector3f[v_num];   
		Vector3f nml; //declaring normal
		double nt; //variable to calculate normal		
		float x0, y0;
		                                                   
		Point3f c_pts[] = L2CircleMK.circle_Points(0, r, n);   //points on Top of table 
		Point3f c_pts2[] = L2CircleMK.circle_Points(0, r, n); //points for side of table
		
		//2 center points needed, each opposite the side of the table
		Point3f ctr_pt = new Point3f(0f, 0f, 0.1f);
		Point3f ctr_pt2 = new Point3f(0f, 0f, -0.1f);
		Point3f p1, p2;

		for (int i = 0; i <= n; i++) {
			k = (i < n) ? i : 0;                          
		
			if (shape_key == "Top") { //setting points for top surface
				p1 = new Point3f(c_pts[k].x, c_pts[k].y, 0.1f);
				p2 = ctr_pt;
				nml = new Vector3f(0f, 0f,  1f);
			}
			else if (shape_key == "Side"){  //setting points for side/curved surface
				p1 = new Point3f(c_pts[k].x, c_pts[k].y, -0.1f);
				p2 = new Point3f(c_pts[k].x, c_pts[k].y, 0.1f);
				x0 = c_pts[k].x;
				y0 = c_pts[k].y;
				nt = Math.sqrt(x0 * x0 + y0 * y0);         //normalize the normals of side surface points
				nml = new Vector3f((float) (x0 / nt), (float) (y0 / nt), 0f);
			}
			else {
				p1 = new Point3f(c_pts2[k].x, c_pts2[k].y, -0.1f); //setting points for the surface (everything opposite of the top)
				p2 = ctr_pt2;
				nml = new Vector3f(0f, 0f,  -1f);
				
			}
			v_cdnts[i * 2 + 1] = p1;  //setting coordinates for point 1 on surface
			v_cdnts[i * 2] = p2; //the same as above for point 2
			c_nmls[i * 2] = c_nmls[i * 2 + 1] = nml;  //setting the normals
		}
		
		TriangleStripArray object_geometry = new TriangleStripArray(v_num, 
				TriangleStripArray.COORDINATES | TriangleStripArray.TEXTURE_COORDINATE_3 |
				TriangleStripArray.NORMALS, vn_count);
		object_geometry.setStripVertexCounts(vn_count);    //create the object as a TriangleStripArray
		object_geometry.setCoordinates(0, v_cdnts, 0, v_num); 
		object_geometry.setNormals(0, c_nmls, 0, v_num);   //set the geometry's normals 
		
		return object_geometry;
	}
	
	public static Shape3D ring_Shape(String shape_key, int n) {
		Appearance app = set_Appearance(shape_key);        //set the appearance with texture mapping		
		return new Shape3D(ring_Side(shape_key, n), app);

	}
	
	/* a function to define the appearance with texture mapping */
	public static Appearance set_Appearance(String s) {
		Appearance app = CommonsMK.set_Appearance(CommonsMK.White);
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);  //show both sides
		app.setPolygonAttributes(pa);

		TexCoordGeneration tcg = new TexCoordGeneration(TexCoordGeneration.OBJECT_LINEAR,
				TexCoordGeneration.TEXTURE_COORDINATE_2);
		app.setTexCoordGeneration(tcg);
		app.setTexture(L5TextureSurfaceMK.texture_Appearance("Image" + s)); //all image names start with "Image"
		
		TextureAttributes textureAttrib= new TextureAttributes();
		textureAttrib.setTextureMode(TextureAttributes.REPLACE);
		app.setTextureAttributes(textureAttrib);
	
		float scl = 0.250f;                                
		Vector3d scale = new Vector3d(scl, scl, scl);
		Transform3D transMap = new Transform3D();
		transMap.setScale(scale);
		textureAttrib.setTextureTransform(transMap);
		
		return app;
	}

	/* a function to define the texture with a specific image */	
	private static Texture2D texture_Appearance(String f_name) {
		String file_name = "C:/Users/makhs/neweclipse-workspace/COMP2800MK/src/images/" + f_name + ".jpg";    // indicate the location of the image
		TextureLoader loader = new TextureLoader(file_name, null);
		ImageComponent2D image = loader.getImage();  //getting the image
		if (image == null)
			System.out.println("Cannot load file: " + file_name);

		Texture2D texture = new Texture2D(Texture2D.BASE_LEVEL,
				Texture2D.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image); //defining texture with the image

		return texture;
	}
	
	public L5TextureSurfaceMK(String s) {
		super(ring_Shape(s, 60));
	}
}
