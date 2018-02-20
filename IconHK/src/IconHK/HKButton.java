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

    // The image shown by default (if not in mouse nor keyboard mode)
    private int defaultFrame=0;

    // The current frame drawn
    private int currentFrame;
    private int vmax= 20;

    // int value referring the length of one side of the square
    // used for modifiers. 0 by default, must be initialized in the constructer
    // and updated if needed
    // TODO setter
    private float modifierRadiusRatio=0.35f;

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
            System.out.println(getWidth());
            System.out.println(getHeight());
            System.out.println(icon.getWidth());
            System.out.println(icon.getHeight());
            int x = (getWidth() - icon.getWidth()) / 2;
            int y = (getHeight() - icon.getHeight()) / 2;
            //TODO bug here
            g2.drawImage(icon, x, y, this);
        }
        //drawModifs
        //drawModifiers(g2);
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
        HKButton.pressedColor = pressedColor;
    }

    public void setModifierRadiusRatio(float modifierRadiusRatio) {
        this.modifierRadiusRatio = modifierRadiusRatio;
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
    /*
    private void decreaseCurrentFrame(){
        this.currentFrame--;
        if (this.currentFrame<0){
            this.currentFrame =0;
        }
    }*/

    /**
     * increase currentframe
     * and fix if out of bounds
     */
    /*
    private void increaseCurrentFrame(){
        this.currentFrame++;
        if(currentFrame>=vmax){
            currentFrame = vmax;
        }
    }*/

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
