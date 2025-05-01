//Makhsuma Khamzaliyeva - 110120302
//COMP2800 Lab 5
//02-02-2025
package CodesMK2800;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.image.TextureLoader;

public class TextureProcessorMK {
    
    /* Function to process image into quarters and swap/mirror them correctly */
    public static BufferedImage processTableTopImage(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        
        // Create new image same size as original
        BufferedImage processed = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = processed.createGraphics();

        int halfWidth = width / 2;
        int halfHeight = height / 2;

        // Move top-left to bottom-right
        g2d.drawImage(original, 
                      halfWidth, halfHeight, width, height,   // Dest: Bottom-right
                      0, 0, halfWidth, halfHeight, null);

        // Move bottom-right to top-left
        g2d.drawImage(original, 
                      0, 0, halfWidth, halfHeight,           // Dest: Top-left
                      halfWidth, halfHeight, width, height, null);

        // Move top-right to bottom-left
        g2d.drawImage(original, 
                      0, halfHeight, halfWidth, height,      // Dest: Bottom-left
                      halfWidth, 0, width, halfHeight, null);

        // Move bottom-left to top-right
        g2d.drawImage(original, 
                      halfWidth, 0, width, halfHeight,       // Dest: Top-right
                      0, halfHeight, halfWidth, height, null);

        g2d.dispose();
        
        // Flip the entire processed image vertically
        return flipVertically(processed);
    }

    /* Function to flip an image vertically */
    private static BufferedImage flipVertically(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = flipped.createGraphics();
        
        // Flip by drawing the image upside down
        g2d.drawImage(image, 0, height, width, 0, 0, 0, width, height, null);
        g2d.dispose();
        
        return flipped;
    }
}
