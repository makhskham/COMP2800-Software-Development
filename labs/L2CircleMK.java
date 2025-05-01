package CodesMK2800;

import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.LineArray;
import org.jogamp.java3d.Shape3D;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3f;

class L2CircleMK extends GroupObjects {
    /* Creates and returns points on a circle */
    public static Point3f[] circle_Points(float z, float r, int n) {
        float x, y;
        Point3f c_pts[] = new Point3f[n];

        for (int i = 0; i < n; i++) {
            x = (float) Math.cos(Math.PI / 180 * (360.0 * i / n)) * r;
            y = (float) Math.sin(Math.PI / 180 * (360.0 * i / n)) * r;
            c_pts[i] = new Point3f(x, y, z);
        }
        return c_pts;
    }
    
    /* Creates and returns the geometry of a circle */
    private static LineArray one_Circle(float r, int segments, Color3f clr) {
        LineArray lineArr = new LineArray(segments * 2, 
                GeometryArray.COLOR_3 | GeometryArray.COORDINATES);

        Point3f c_pts[] = circle_Points(0.0f, r, segments);

        for (int i = 0; i < segments; i++) {
            lineArr.setCoordinate(i * 2, c_pts[i]);
            lineArr.setCoordinate(i * 2 + 1, c_pts[(i + 1) % segments]);
            lineArr.setColor(i * 2, clr);
            lineArr.setColor(i * 2 + 1, clr);
        }
        return lineArr;
    }
            
    /* Creates and returns a Shape3D with one circle */
    public static Shape3D line_Shape(float r, int segments, Color3f color) {
        LineArray circleGeometry = one_Circle(r, segments, color);
        return new Shape3D(circleGeometry);
    }
    
    /* Creates and returns a Shape3D with two circles */
    public static Shape3D double_circle_Shape(float r1, float r2, int segments) {
        // Create a geometry array for both circles
        LineArray doubleCircleGeometry = new LineArray(segments * 4, 
                GeometryArray.COLOR_3 | GeometryArray.COORDINATES);
        
        // Get points for both circles
        Point3f outer_pts[] = circle_Points(0.0f, r1, segments);
        Point3f inner_pts[] = circle_Points(0.0f, r2, segments);
        
        // Add outer circle (white)
        for (int i = 0; i < segments; i++) {
            doubleCircleGeometry.setCoordinate(i * 2, outer_pts[i]);
            doubleCircleGeometry.setCoordinate(i * 2 + 1, outer_pts[(i + 1) % segments]);
            doubleCircleGeometry.setColor(i * 2, CommonsMK.White);
            doubleCircleGeometry.setColor(i * 2 + 1, CommonsMK.White);
        }
        
        // Add inner circle (purple)
        int offset = segments * 2;
        for (int i = 0; i < segments; i++) {
            doubleCircleGeometry.setCoordinate(offset + i * 2, inner_pts[i]);
            doubleCircleGeometry.setCoordinate(offset + i * 2 + 1, inner_pts[(i + 1) % segments]);
            doubleCircleGeometry.setColor(offset + i * 2, CommonsMK.Purple);
            doubleCircleGeometry.setColor(offset + i * 2 + 1, CommonsMK.Purple);
        }
        
        return new Shape3D(doubleCircleGeometry);
    }
    
    public L2CircleMK(float r) {
        super(line_Shape(r, 60, CommonsMK.White));
    }
}