//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Lab 4
//28-01-2025
package CodesMK2800;

/* Transform class for Lab 4 - Handles creation of disk surfaces
 * Modified by: [Your Name]
 * This class contains methods for creating the geometric shapes that make up the disk:
 * - Ring-shaped side surface
 * - Circular top and bottom surfaces
 */

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.TriangleStripArray;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

public class L4TransformMK extends GroupObjects {

    private final static int MAX_PTS = 60;
    
    public static Shape3D ring_Side(int shape_key, Color3f clr) {
        float r = 2.0f;
        int k, n = MAX_PTS;
        
        // Calculate number of vertices needed
        int v_num = (MAX_PTS + 1) * 2;
        int vn_count[] = {v_num};
        Point3f[] v_cdnts = new Point3f[v_num];
        Vector3f[] c_nmls = new Vector3f[v_num];
        Vector3f nml;
        double nt;
        float x0, y0;
        
        // Generate points for the circle
        Point3f c_pts[] = L2StarMK.circle_Points(0, r, MAX_PTS);
        Point3f ctr_pt = new Point3f(0f, 0f, 0.1f);
        Point3f p1, p2;

        // Generate vertices and normals
        for (int i = 0; i <= n; i++) {
            k = (i < n) ? i : 0;
        
            if (shape_key < 1) {  // Circular surface (top or bottom)
                p1 = new Point3f(c_pts[k].x, c_pts[k].y, 0.1f);
                p2 = ctr_pt;
                nml = new Vector3f(0f, 0f, 1f);
            }
            else {  // Side surface (ring)
                p1 = new Point3f(c_pts[k].x, c_pts[k].y, -0.1f);
                p2 = new Point3f(c_pts[k].x, c_pts[k].y, 0.1f);
                x0 = c_pts[k].x;
                y0 = c_pts[k].y;
                nt = Math.sqrt(x0 * x0 + y0 * y0);
                nml = new Vector3f((float) (x0 / nt), (float) (y0 / nt), 0f);
            }
            
            // Set coordinates and normals
            v_cdnts[i * 2 + 1] = p1;
            v_cdnts[i * 2] = p2;
            c_nmls[i * 2] = c_nmls[i * 2 + 1] = nml;
        }
        
        // Create geometry
        TriangleStripArray object_geometry = new TriangleStripArray(v_num, 
                TriangleStripArray.COORDINATES | 
                TriangleStripArray.NORMALS, vn_count);
        object_geometry.setStripVertexCounts(vn_count);
        object_geometry.setCoordinates(0, v_cdnts, 0, v_num);
        object_geometry.setNormals(0, c_nmls, 0, v_num);
        
        // Set appearance
        Appearance app = CommonsMK.set_Appearance(clr);
        PolygonAttributes pa = new PolygonAttributes();
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        app.setPolygonAttributes(pa);

        return new Shape3D(object_geometry, app);
    }
    
    public L4TransformMK(int key) {
        super(ring_Side(key, CommonsMK.Green));
    }
}