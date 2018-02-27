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
import java.util.Arrays;
import java.util.Vector;

public class HKButton extends JButton implements MouseListener {
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

    // Boolean testing if buttons pressed
    public static boolean metaPressed = false;
    public static boolean altPressed = false;
    public static boolean ctrlPressed = false;
    public static boolean shftPressed = false;

    // The image shown by default (if not in mouse nor keyboard mode)
    private int defaultFrame=0;
    // Size of folder
    private int vmax = 20;

    // The current frame drawn
    private int currentFrame;

    // Control the movement
    private boolean spinner = true;

    // int value referring the length of one side of the square
    // used for modifiers. 0 by default, must be initialized in the constructer
    // and updated if needed
    // TODO setter
    private float modifierRadiusRatio=0.35f;

    public HKButton(Action a){
        super(a);
        KeyStroke ks = (KeyStroke)a.getValue(AbstractAction.ACCELERATOR_KEY);
        updateModifiersForKeystroke(ks);
        initForName((String) a.getValue(Action.NAME));
    }

    public HKButton(Action a, Dimension d){
        super(a);
        this.dimension = d;
        KeyStroke ks = (KeyStroke)a.getValue(AbstractAction.ACCELERATOR_KEY);
        updateModifiersForKeystroke(ks);
        initForName((String) a.getValue(Action.NAME));
    }

    public HKButton(String text, Dimension d) {
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
        // Files aren't in alphabetical order
        Arrays.sort(iconFiles);
        for (File f : iconFiles) {
            try {
                BufferedImage img = ImageIO.read(f);
                // Resize all images in order to fit dimension
                // TODO setter to modify the ratio
                BufferedImage scaledInstance = resize(img,(int)(dimension.getWidth() * 0.75), (int)(dimension.getHeight() * 0.75));
                iconsVector.add(scaledInstance);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("image IOException for file " + f.getAbsolutePath());
            }
        }
        vmax = iconsVector.size() - 1;

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

        /*TODO this thing depends on Editor*/
        int opacityValue = getOpacityDependingOfModifiersPressed();
        //opacityValue=0;
        if(opacityValue>0){
            g2.setColor(new Color(255,255,255,opacityValue));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.setColor(Color.DARK_GRAY);
    }

    private int getOpacityDependingOfModifiersPressed(){
        int value=0;
        /*
        if(!MySimpleEditor.ctrlPressed&&!MySimpleEditor.altPressed&&!MySimpleEditor.shftPressed&&!MySimpleEditor.metaPressed){
            return value;
        }

        float nbOfMissingMods = getNumberOfMissingMods();

        if(nbOfMissingMods>0){
            return (int)(nbOfMissingMods/4.0f*255.0f);
        }
        float nbOfExtraMods = getNumberOfExtraMods();

        if(nbOfExtraMods>0){
            return (int)(nbOfExtraMods/4.0f*255.0f);
        }*/
        return value;
    }

    private void drawModifiers(Graphics2D g2) {
        float width = (float) this.getWidth();
        float height = (float) this.getHeight();
        float diameter =  (height * modifierRadiusRatio);
        float radius=  (diameter/2.0f);

        //Fill arcs
        g2.setColor(useCtrl? (ctrlPressed? pressedColor : Color.DARK_GRAY) : Color.WHITE);
        //g2.setColor(useCtrl? Color.DARK_GRAY : Color.WHITE);
        g2.fillArc(-(int)radius, (int) (height-radius),(int)diameter, (int)diameter, 0, 90);
        g2.setColor(useSft? (shftPressed ? pressedColor : Color.DARK_GRAY) : Color.WHITE);
        g2.fillArc(-(int)radius, -(int) (radius),(int)diameter, (int)diameter, 270, 90);
        g2.setColor(useAlt? (altPressed? pressedColor : Color.DARK_GRAY) : Color.WHITE);
        g2.fillArc((int)(width-radius), (int) (height-radius),(int)diameter, (int)diameter, 90, 90);
        g2.setColor(useMeta? (metaPressed? pressedColor : Color.DARK_GRAY) : Color.WHITE);
        g2.fillArc((int)(width-radius), (int) (-radius),(int)diameter, (int)diameter, 180, 90);

        g2.setColor(Color.DARK_GRAY);
        // draw contours
        if (useCtrl)
            g2.drawArc(-(int) radius, (int) (height - radius), (int) diameter, (int) diameter, 0, 90);
        if (useSft)
            g2.drawArc(-(int) radius, -(int) (radius), (int) diameter, (int) diameter, 270, 90);
        if (useAlt)
            g2.drawArc((int) (width - radius), (int) (height - radius), (int) diameter, (int) diameter, 90, 90);
        if (useMeta)
            g2.drawArc((int) (width - radius), (int) (-radius), (int) diameter, (int) diameter, 180, 90);
    }

    @Override
    public String getName() {
        return name;
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
        HKButton.pressedColor = pressedColor;
    }

    public void setModifierRadiusRatio(float modifierRadiusRatio) {
        this.modifierRadiusRatio = modifierRadiusRatio;
    }

    public void setMetaPressed(boolean metaPressed) {
        HKButton.metaPressed = metaPressed;
        //paintComponent(getGraphics());
    }

    public void setAltPressed(boolean altPressed) {
        HKButton.altPressed = altPressed;
        //paintComponent(getGraphics());
    }

    public void setCtrlPressed(boolean ctrlPressed) {
        HKButton.ctrlPressed = ctrlPressed;
        //paintComponent(getGraphics());
    }

    public void setShftPressed(boolean shftPressed) {
        HKButton.shftPressed = shftPressed;
        //paintComponent(getGraphics());
    }

/**
     * keep animating until it reaches the destination
     * return false if destination not reached
     * @return true if no more frame exist after this one
     */
    /*
    public boolean animateToDestination(int destination){
        switch(destination){
            case IconAnimation.ICON_STEP:
                this.decreaseCurrentFrame();
                return isAnimationCompleteForDestination(destination);
            case IconAnimation.DEFAULT_STEP:
                if(this.currentFrame>this.defaultFrame){
                    this.decreaseCurrentFrame();
                    return isAnimationCompleteForDestination(destination);
                }
                else if(this.currentFrame<this.defaultFrame){
                    this.increaseCurrentFrame();
                    return isAnimationCompleteForDestination(destination);
                }
                return true;
            case IconAnimation.HOTKEY_STEP:
                this.increaseCurrentFrame();
                return isAnimationCompleteForDestination(destination);
        }
        return false;
    }*/

    /**
     * check whether or not currentFrame is the one expected
     * by the animation
     * and return true if yes
     * no otherwise
     * @return true if currentFrame is the expected
     */
    /*
    private boolean isAnimationCompleteForDestination(int destination){
        switch(destination){
            case IconAnimation.ICON_STEP:
                return(this.currentFrame==0);
            case IconAnimation.DEFAULT_STEP:
                return(this.currentFrame==this.defaultFrame);
            case IconAnimation.HOTKEY_STEP:
                return(currentFrame==vmax);
        }
        return false;
    }*/

    /**
     * decrease currentframe
     * and fix if out of bounds
     */

    private void decreaseCurrentFrame(){
        this.currentFrame--;
        if (this.currentFrame==0)
            spinner = !spinner;
    }

    /**
     * increase currentframe
     * and fix if out of bounds
     */

    private void increaseCurrentFrame(){
        this.currentFrame++;
        if(currentFrame==vmax)
            spinner = !spinner;
    }

    private void changeCurrentFrame(){
        if (spinner)
            increaseCurrentFrame();
        else
            decreaseCurrentFrame();
    }

    // Resize image
    private static BufferedImage resize(BufferedImage img, int width, int height) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, Image.SCALE_SMOOTH);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, Color.WHITE, null);
        g2d.dispose();
        return resized;
    }

    // Redimensioned button
    @Override
    public Dimension getPreferredSize() {
        return dimension;
    }

    @Override
    public Dimension getMinimumSize() {
        return dimension;
    }

    @Override
    public Dimension getMaximumSize() {
        return dimension;
    }

    public void mouseClicked(MouseEvent e) {
        (new Redesign()).run();
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    //TODO Ask prof if we can implement a thread like this
    //TODO Should we perform this ?
    private class Redesign implements Runnable {
        @Override
        public void run() {
            if (spinner) {
                while (currentFrame != vmax) {
                    changeCurrentFrame();
                    paintComponent(getGraphics());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                while (currentFrame != 0){
                    changeCurrentFrame();
                    paintComponent(getGraphics());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
