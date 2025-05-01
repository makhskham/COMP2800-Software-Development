//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Lab 9
//11-03-2025
package CodesMK2800;

import org.jdesktop.j3d.examples.morphing.MorphingBehavior;
import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.Morph;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.TriangleStripArray;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

public class L9MorphShapeMK {
    private final static int M_PTS = 64;                   // define maximum number of circle points

    @SuppressWarnings("deprecation")
    /* a function to define the morphing behavior of the textured disk with three different shapes for 
     * one of the three surfaces of the disk shape determined by string 's'; 
     * for the edge number of the morphing shape changes from n0->M_PTS->n1 and back n1->M_PTS->n0 continuously */
    public static Morph set_Morph(TransformGroup parentTG, String s, int n0, int n1) {
        int[] num = {n0, M_PTS, n1};                       // set edge number: 'n0' starting; 'n1' ending 
        TriangleStripArray[] geoArray = get_Objects(s, num);  // obtain the three geometry arrays for morphing
                                                           // apply texture mapping to the morphing object
        Morph morph = new Morph(geoArray, L5TextureSurfaceMK.set_Appearance(s));
        morph.setCapability(Morph.ALLOW_WEIGHTS_WRITE);
        
        Alpha morphAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE |
                Alpha.DECREASING_ENABLE, 0, 0, 4000, 0000, 1000, 4000, 0000, 1000);
        MorphingBehavior mBeh = new MorphingBehavior(morphAlpha, morph);
        mBeh.setSchedulingBounds(CommonsMK.twenty_BS);     // enable morphing behavior with a scheduling bound
        parentTG.addChild(mBeh);                           // attach morphing behavior to 'parentTG'
                        
        return morph;
    }
    
    /* a function to return three geometry definitions with different shapes but with the same number of edges */
    public static TriangleStripArray[] get_Objects(String s, int[] n) {
        int num = 3;                                       // always uses three objects
        TriangleStripArray[] geoArray = new TriangleStripArray[num];
        for (int i = 0; i < num; i++)                      // retrieve the objects' geometry
            geoArray[i] = ring_Side(s, n[i]);
    
        return geoArray;                                   // return the geometry array
    }
    
    /* a function to return one geometry definition in 'num' edges with M_PTS points */
    private static TriangleStripArray ring_Side(String shape_key, int num) {
        float r = 2.0f;                                    // place points on a circle with radius 'r'
        
        int v_num = (M_PTS + 1) * 2;                       // use 'M_PTS+1' points on two circles for the surface
        int vn_count[] = {v_num};                          // set point counters for the surface
        Point3f[] v_cdnts = new Point3f[v_num];            // allocate 3D coordinates for all surface points
        Vector3f[] c_nmls = new Vector3f[v_num];           // declare normals for the set of points
        Vector3f nml;
        double nt;                                         // declare variables for the calculation of normal
        float x0, y0;                           
                                                           // prepare points on the circle
        Point3f c_pts[] = L2StarMK.circle_Points(0, r, M_PTS);
        Point3f ctr_pt = new Point3f(0f, 0f, 0.1f);
        Point3f ctr_pt2 = new Point3f(0f, 0f, -0.1f);
        Point3f p1, p2;

        int k;
        double rpt = M_PTS / num;                          // repeated points, e.g., 16, 8, 4, 2 if 'num' is 4, 8, 16, 32
        for (int i = 0; i <= M_PTS; i++) {
            k = (i < M_PTS) ? i : 0;                       // NOTE: set the last two points as the first two points
    
            if (k != 0 && M_PTS != num) {                  // place multiple points at the same location if 'num'<M_PTS
                k = (int) (k / rpt);    
                k = k * (int) rpt;
            }

            if (shape_key == "Top") {                      // set for top (flat, circular) surface
                p1 = new Point3f(c_pts[k].x, c_pts[k].y, 0.1f);
                p2 = ctr_pt;
                nml = new Vector3f(0f, 0f,  1f);
            }
            else if (shape_key == "Side") {               // set for outside (vertical, curve) surface
                p1 = new Point3f(c_pts[k].x, c_pts[k].y, -0.1f);
                p2 = new Point3f(c_pts[k].x, c_pts[k].y, 0.1f);
                x0 = c_pts[k].x;
                y0 = c_pts[k].y;
                nt = Math.sqrt(x0 * x0 + y0 * y0);         // normalize the normals of side (vertical) surface points
                nml = new Vector3f((float) (x0 / nt), (float) (y0 / nt), 0f);
            }
            else {                                         // set for extra (opposite of top) surface
                p1 = new Point3f(c_pts[k].x, c_pts[k].y, -0.1f);
                p2 = ctr_pt2;
                nml = new Vector3f(0f, 0f,  -1f);
            }
            v_cdnts[i * 2 + 1] = p1;                       // set the coordinate for the point on a surface
            v_cdnts[i * 2] = p2;
            c_nmls[i * 2] = c_nmls[i * 2 + 1] = nml;       //     ... normal ... 
        }
        
        TriangleStripArray object_geometry = new TriangleStripArray(v_num, 
                TriangleStripArray.COORDINATES | TriangleStripArray.NORMALS, vn_count);
        object_geometry.setStripVertexCounts(vn_count);    // create the object as a TriangleStripArray
        object_geometry.setCoordinates(0, v_cdnts, 0, v_num); 
        object_geometry.setNormals(0, c_nmls, 0, v_num);   // set the geometry's normals 

        return object_geometry;
    }
}