package IconHK;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;

public abstract class IconHKButton extends JButton implements MouseListener {
    // Name of the button
    private String name;

    // The dimension of the button
    private Dimension dimension = new Dimension(32, 32);

    //TODO setter
    private static Color pressedColor= new Color(29,174,222);

    // The border when overed
    private static Border overedBorder = new LineBorder(pressedColor, 2);

    // A vector of buffered images ordered so it creates an animation from icon to symbol
    private Vector<BufferedImage> iconsVector;

    // Boolean telling whether or not a modifier is used in the hotkey
    private boolean useMeta, useCtrl, useAlt, useSft;

    // The image shown by default (if not in mouse nor keyboard mode)
    private int defaultFrame=5;

    // The current frame drawn
    private int currentFrame;

    // int value referring the length of one side of the square
    // used for modifiers. 0 by default, must be initialized in the constructer
    // and updated if needed
    // TODO setter
    private float modifierRadiusRatio=0.35f;

    public IconHKButton (Action a, Dimension d){
        super(a);
        this.dimension = dimension;
        KeyStroke ks = (KeyStroke)a.getValue(AbstractAction.ACCELERATOR_KEY);
        updateModifiersForKeystroke(ks);
        initForName((String) a.getValue(Action.NAME));
    }

    public IconHKButton(String text, Dimension d) {
        this.dimension = d;
        initForName(text);
    }

    private void updateModifiersForKeystroke(KeyStroke ks){
        String s = ks.toString();
        if(s.contains("ctrl"))
            useCtrl=true;
        if(s.contains("shift"))
            useSft=true;
        if(s.contains("alt"))
            useAlt=true;
        if(s.contains("meta"))
            useMeta=true;
    }

    private void initForName(String text) {
        this.name=text;
        iconsVector = new Vector<BufferedImage>();
        String iconFolder = "./resources/icons/" + text + "/";
        this.setText("");
        Border border = BorderFactory.createLineBorder(Color.DARK_GRAY);
        this.setBorder(border);
        //TODO à voir que fait cette ligne
        //this.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        File dir = new File(iconFolder);

        File[] iconFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".png");
            }
        });

        for (File f : iconFiles) {
            try {
                //TODO resize all the icon in order to fit the dimension
                iconsVector.add(ImageIO.read(f));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("image IOException for file " + f.getAbsolutePath());
            }
        }

        //TODO Don't need this line bcas icon size fixed
        /*
        if (iconsVector.size() != 0)
            dimension.setSize(iconsVector.firstElement().getWidth()+10 , iconsVector.firstElement().getHeight() +10);
        */

        //TODO à voir des lignes suivantes
        //this.modifierSquareLength = (int) ((float) dimension.getHeight() * 0.2f);
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
        this.setContentAreaFilled(false);
        this.setBorderPainted(true);
        this.addMouseListener(this);
        this.setOpaque(true);
        fixCurrentFrame();
        this.currentFrame=defaultFrame;
    }

    private boolean fixCurrentFrame() {
        if (currentFrame < 0) {
            currentFrame = 0;
            return true;
        } else if (currentFrame >= iconsVector.size()) {
            currentFrame = iconsVector.size() - 1;
            return true;
        }
        return false;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        if (this.iconsVector != null) {
            BufferedImage icon = iconsVector.get(currentFrame);
            int x = (getWidth() - icon.getWidth()) / 2;
            int y = (getHeight() - icon.getHeight()) / 2;
            g2.drawImage(icon, x, y, this);
        }
        drawModifiers(g2);
        /*TODO this thing depends on Editor
        int opacityValue = getOpacityDependingOfModifiersPressed();
        //opacityValue=0;
        if(opacityValue>0){
            g2.setColor(new Color(255,255,255,opacityValue));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }*/
        g2.setColor(Color.DARK_GRAY);

    }

    private void drawModifiers(Graphics2D g2) {
        float width = (float) this.getWidth();
        float height = (float) this.getHeight();
        float diameter =  (height * modifierRadiusRatio);
        float radius=  (diameter/2.0f);

        //Fill arcs
        g2.fillArc(-(int)radius, (int) (height-radius),(int)diameter, (int)diameter, 0, 90);
        g2.fillArc(-(int)radius, -(int) (radius),(int)diameter, (int)diameter, 270, 90);
        g2.fillArc((int)(width-radius), (int) (height-radius),(int)diameter, (int)diameter, 90, 90);

        //commented cuz not considering meta shortcuts
        //g2.fillArc((int)(width-radius), (int) (-radius),(int)diameter, (int)diameter, 180, 90);

        g2.setColor(Color.DARK_GRAY);
        // draw contours

        g2.drawArc(-(int) radius, (int) (height - radius), (int) diameter, (int) diameter, 0, 90);

        g2.drawArc(-(int) radius, -(int) (radius), (int) diameter, (int) diameter, 270, 90);

        g2.drawArc((int) (width - radius), (int) (height - radius), (int) diameter, (int) diameter, 90, 90);

        if (useMeta) {
            g2.drawArc((int) (width - radius), (int) (-radius), (int) diameter, (int) diameter, 180, 90);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public boolean isUseMeta() {
        return useMeta;
    }

    public boolean isUseCtrl() {
        return useCtrl;
    }

    public boolean isUseAlt() {
        return useAlt;
    }

    public boolean isUseSft() {
        return useSft;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public static void setPressedColor(Color pressedColor) {
        IconHKButton.pressedColor = pressedColor;
    }

    public void setModifierRadiusRatio(float modifierRadiusRatio) {
        this.modifierRadiusRatio = modifierRadiusRatio;
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
}
