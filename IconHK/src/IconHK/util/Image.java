package IconHK.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Image {
    // Resize image
    public static BufferedImage resize(BufferedImage img, int width, int height) {
        java.awt.Image tmp = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, java.awt.Image.SCALE_SMOOTH);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, Color.WHITE, null);
        g2d.dispose();
        return resized;
    }
}
