package CodesMK2800;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageProcessorMK {
    public static void main(String[] args) {
        try {
            // Load the original image
            BufferedImage original = ImageIO.read(new File("C:/Users/makhs/neweclipse-workspace/COMP2800MK/src/images/ImageTop.jpg"));
            int width = original.getWidth();
            int height = original.getHeight();

            // Divide into four quadrants
            int halfWidth = width / 2;
            int halfHeight = height / 2;

            BufferedImage topLeft = original.getSubimage(0, 0, halfWidth, halfHeight);
            BufferedImage topRight = original.getSubimage(halfWidth, 0, halfWidth, halfHeight);
            BufferedImage bottomLeft = original.getSubimage(0, halfHeight, halfWidth, halfHeight);
            BufferedImage bottomRight = original.getSubimage(halfWidth, halfHeight, halfWidth, halfHeight);

            // Create a new swapped image
            BufferedImage swappedImage = new BufferedImage(width, height, original.getType());
            Graphics2D g = swappedImage.createGraphics();

            // Swap diagonals
            g.drawImage(bottomRight, 0, 0, null);   // Move Bottom-Right to Top-Left
            g.drawImage(bottomLeft, halfWidth, 0, null);   // Move Bottom-Left to Top-Right
            g.drawImage(topRight, 0, halfHeight, null);   // Move Top-Right to Bottom-Left
            g.drawImage(topLeft, halfWidth, halfHeight, null);   // Move Top-Left to Bottom-Right

            g.dispose();

            // Save the modified image
            ImageIO.write(swappedImage, "jpg", new File("C:/Users/makhs/neweclipse-workspace/COMP2800MK/src/images/ImageTop_Swapped.jpg"));
            System.out.println("Image processing completed. Saved as ImageTop_Swapped.jpg");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}