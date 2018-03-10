package IconHK.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class Image {
    public static final int LINEAR = 1;

    // Resize image
    public static BufferedImage resize(BufferedImage img, int width, int height) {
        java.awt.Image tmp = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, java.awt.Image.SCALE_SMOOTH);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, Color.WHITE, null);
        g2d.dispose();
        return resized;
    }

    public static BufferedImage superpose (BufferedImage img1, BufferedImage img2, int width, int height){
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics g = combined.getGraphics();
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, img1.getWidth(), img1.getHeight(), null);

        return combined;
    }

    public static Vector<BufferedImage> generate (BufferedImage root, int width, int height, char letter, int style){
        Vector<BufferedImage> res = new Vector<>();
        File f = new File("./resources/icons/Alphabet/"+letter+".png");
        try {
            BufferedImage c = ImageIO.read(f);
            switch (style){
                case LINEAR: // y = (1/25)x
                    double size = 25;
                    for (double i = 0; i <= size; i++){
                        double a = 1./size;
                        int d1 = Math.min((int)(((double)width) * (a * i)),width);
                        int d2 = Math.min((int)(((double)height) * (a * i)),height);
                        if (d1 == 0 || d2 == 0)
                            res.add(resize(root,width,height));
                        else if (d1 == width || d2 == height)
                            res.add(resize(c,width,height));
                        else {
                            BufferedImage b1 = resize(c, d1, d2);
                            BufferedImage b2 = resize(root, width - d1, height - d2);
                            res.add(superpose(b1, b2, width, height));
                        }
                    }
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
