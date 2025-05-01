package CodesMK2800;

import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.TriangleStripArray;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3f;

public class L3DiskMK extends GroupObjects {
    // Maximum number of points to approximate circular shapes
    private final static int MAX_PTS = 60;
    
    public static Shape3D ring_Side(float r) {
        int k, n = MAX_PTS;
        
        // Create three surfaces: top circle, bottom circle, and side surface
        int s_num = 3;
        int v_num = (MAX_PTS + 1) * 2;
        int vn_count[] = {v_num, v_num, v_num};
        Point3f[] v_cdnts = new Point3f[v_num * s_num];
        Color3f[] v_clrs = new Color3f[v_num * s_num];
        
        // Generate points for the three circular paths
        Point3f c_pts1[] = L2StarMK.circle_Points(-0.1f, r, MAX_PTS);
        Point3f c_pts2[] = L2StarMK.circle_Points(0.1f, r, MAX_PTS);
        Point3f ctr_pt1 = new Point3f(0f, 0f, -0.1f);
        Point3f ctr_pt2 = new Point3f(0f, 0f, 0.1f);

        // Create all three surfaces
        for (int i = 0; i <= n; i++) {
            k = (i < n) ? i : 0;
            
            // Front circular surface
            v_cdnts[i * 2] = c_pts1[k];
            v_cdnts[i * 2 + 1] = ctr_pt1;
            v_clrs[i * 2] = v_clrs[i * 2 + 1] = CommonsMK.Orange;

            // Side surface
            v_cdnts[v_num + i * 2] = c_pts2[k];
            v_cdnts[v_num + i * 2 + 1] = c_pts1[k];
            v_clrs[v_num + i * 2] = v_clrs[v_num + i * 2 + 1] = CommonsMK.Green;
            
            // Back circular surface
            v_cdnts[v_num * 2 + i * 2] = c_pts2[k];
            v_cdnts[v_num * 2 + i * 2 + 1] = ctr_pt2;
            v_clrs[v_num * 2 + i * 2] = v_clrs[v_num * 2 + i * 2 + 1] = CommonsMK.Orange;
        }
        
        // Create geometry with all surfaces
        TriangleStripArray object_geometry = new TriangleStripArray(v_num * s_num,
                TriangleStripArray.COORDINATES | TriangleStripArray.COLOR_3, vn_count);
        object_geometry.setCoordinates(0, v_cdnts);
        object_geometry.setColors(0, v_clrs);
        
        return new Shape3D(object_geometry);
    }
    
    public L3DiskMK(float r) {
        super(ring_Side(r));
    }
}