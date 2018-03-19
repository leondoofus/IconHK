package IconHK.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class Image {
    public static final int DEFAULT = 0, LINEAR = 1, QUADRATIC = 2, CUBIC = 3;

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
            double size = 25;
            switch (style){
                case LINEAR: // y = (1/size)x
                    double k = 1./size;
                    for (double i = 0; i <= size; i++){
                        int d1 = Math.min((int)(((double)width) * (k * i)),width);
                        int d2 = Math.min((int)(((double)height) * (k * i)),height);
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
                case QUADRATIC: // y = ax2 + bx
                    double alpha = 20, beta = 0.3;
                    double[][] array1 = {{1,size},{beta,alpha}};
                    double[][] array2 = {{size*size,1},{alpha*alpha,beta}};
                    double[][] d = {{size*size,size},{alpha*alpha,alpha}};
                    double a = determinant(2,array1)/determinant(2,d);
                    double b = determinant(2,array2)/determinant(2,d);
                    for (double i = 0; i <= size; i++){
                        int d1 = Math.min((int)(((double)width) * (a*i*i + b*i)),width);
                        int d2 = Math.min((int)(((double)height) * (a*i*i + b*i)),height);
                        if (d1 <= 0 || d2 <= 0)
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
                case CUBIC:
                    double x1 = 8, y1 = 0.9, x2 = 14, y2 = 0.7;
                    double[][] a1 = {{y1,x1*x1,x1},{y2,x2*x2,x2},{1,size*size,size}};
                    double[][] a2 = {{x1*x1*x1,y1,x1},{x2*x2*x2,y2,x2},{size*size*size,1,size}};
                    double[][] a3 = {{x1*x1*x1,x1*x1,y1},{x2*x2*x2,x2*x2,y2},{size*size*size,size*size,1}};
                    double[][] delta = {{x1*x1*x1,x1*x1,x1},{x2*x2*x2,x2*x2,x2},{size*size*size,size*size,size}};
                    double u = determinant(3,a1)/determinant(3,delta);
                    double v = determinant(3,a2)/determinant(3,delta);
                    double w = determinant(3,a3)/determinant(3,delta);
                    for (double i = 0; i <= size; i++){
                        int d1 = Math.min((int)(((double)width) * (u*i*i*i + v*i*i + w*i)),width);
                        int d2 = Math.min((int)(((double)height) * (u*i*i*i + v*i*i + w*i)),height);
                        if (d1 <= 0 || d2 <= 0)
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
                default:
                    return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static double determinant (int degree, double [][] array){
        switch (degree){
            case 2:
                return array[0][0]*array[1][1] - array[0][1]*array[1][0];
            case 3:
                return array[0][0]*(array[1][1]*array[2][2] - array[1][2]*array[2][1])
                        - array[1][0]*(array[0][1]*array[2][2] - array[0][2]*array[2][1])
                        + array[2][0]*(array[0][1]*array[1][2] - array[0][2]*array[1][1]);
        }
        return 0;
    }
}
