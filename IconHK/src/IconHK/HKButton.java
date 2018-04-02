package IconHK;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import IconHK.util.Image;

public class HKButton extends JButton implements MouseListener,ActionListener {
    // Name of the button
    private String name;

    private Timer timer;

    // The dimension of the button
    private Dimension dimension = new Dimension(32, 32);

    // All files for animation
    private File[] iconFiles;
    private BufferedImage icon;

    private Color pressedColor = new Color(29,174,222);

    // A vector of buffered images ordered so it creates an animation from icon to symbol
    private Vector<BufferedImage> iconsVector;

    // Boolean telling whether or not a modifier is used in the hotkey
    private boolean useMeta, useCtrl, useAlt, useSft;
    private char hotkey;

    // Boolean testing if buttons pressed
    private boolean metaPressed = false;
    private boolean altPressed = false;
    private boolean ctrlPressed = false;
    private boolean shftPressed = false;

    // The image shown by default (if not in mouse nor keyboard mode)
    private int defaultFrame=0;
    // Size of folder
    private int vmaxDef;
    private int vmax = 20;

    // The current frame drawn
    private int currentFrame;

    // Control the movement
    private boolean spinner = true;

    //Animation lock
    public static boolean lockHotkey = false;
    public static boolean lockClick = false;

    // Mode d'animation
    private int mode;

    private boolean mousePressed = false;
    private boolean hotkeyUsed = false;

    // int value referring the length of one side of the square
    // used for modifiers. 0 by default, must be initialized in the constructer
    // and updated if needed
    private float modifierRadiusRatio=0.35f;

    public HKButton(Action a, int mode){
        super(a);
        KeyStroke ks = (KeyStroke)a.getValue(AbstractAction.ACCELERATOR_KEY);
        updateModifiersForKeystroke(ks);
        if (a instanceof HKAction) ((HKAction) a).setButton(this);
        timer = new Timer(50, this);
        timer.start();
        this.mode = mode;
        initForName((String) a.getValue(Action.NAME));
    }

    public HKButton(Action a, Dimension d, int mode){
        super(a);
        KeyStroke ks = (KeyStroke)a.getValue(AbstractAction.ACCELERATOR_KEY);
        updateModifiersForKeystroke(ks);
        if (a instanceof HKAction) ((HKAction) a).setButton(this);
        timer = new Timer(50, this);
        timer.start();
        this.mode = mode;
        this.dimension = d;
        initForName((String) a.getValue(Action.NAME));
    }

    public HKButton(String text, Dimension d, int mode) {
        timer = new Timer(50, this);
        timer.start();
        this.mode = mode;
        this.dimension = d;
        initForName(text);
    }

    public HKButton(Action a, Icon icon, KeyStroke ks){
        super(a);
        updateModifiersForKeystroke(ks);
        if (a instanceof HKAction) ((HKAction) a).setButton(this);
        timer = new Timer(50, this);
        timer.start();
        this.mode = Image.LINEAR;
        this.dimension = new Dimension(icon.getIconWidth(),icon.getIconHeight());
        iconsVector = new Vector<>();
        this.setText("");
        Border border = BorderFactory.createLineBorder(Color.DARK_GRAY);
        this.setBorder(border);

        iconFiles = null;
        this.icon = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = this.icon.createGraphics();
        icon.paintIcon(null, g, 0,0);
        g.dispose();

        fixAllImages();

        vmaxDef = iconsVector.size() - 1;
        vmax = iconsVector.size() - 1;
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
        this.setContentAreaFilled(false);
        this.setBorderPainted(true);
        this.addMouseListener(this);
        this.setOpaque(true);
        fixCurrentFrame();
        this.currentFrame=defaultFrame;
    }

    private void updateModifiersForKeystroke(KeyStroke ks){
        String s = ks.toString();
        hotkey = s.charAt(s.length()-1);
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
        iconsVector = new Vector<>();
        String iconFolder = "./resources/icons/" + text + "/";
        this.setText("");
        Border border = BorderFactory.createLineBorder(Color.DARK_GRAY);
        this.setBorder(border);
        //TODO à voir que fait cette ligne
        //this.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        File dir = new File(iconFolder);

        iconFiles = dir.listFiles((dir1, filename) -> filename.endsWith(".png"));
        // Files aren't in alphabetical order
        assert iconFiles != null;
        Arrays.sort(iconFiles);

        fixAllImages();

        vmaxDef = iconsVector.size() - 1;
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

    private void fixAllImages (){
        iconsVector.removeAllElements();
        if (mode == Image.DEFAULT){
            for (File f : iconFiles) {
                try {
                    BufferedImage img = ImageIO.read(f);
                    // Resize all images in order to fit dimension
                    float w = (float)dimension.getWidth();
                    float h = (float)dimension.getHeight();
                    BufferedImage scaledInstance = Image.resize(img,(int)(((w * Math.sqrt(2)) - (2 * w * modifierRadiusRatio)) * 0.8),
                            (int)(((h * Math.sqrt(2)) - (2 * h * modifierRadiusRatio)) * 0.8));
                    iconsVector.add(scaledInstance);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("image IOException for file " + f.getAbsolutePath());
                }
            }
        } else {
            try {
                float w = (float) dimension.getWidth();
                float h = (float) dimension.getHeight();
                if (iconFiles != null)
                    iconsVector = Image.generate(ImageIO.read(iconFiles[0]), (int) (((w * Math.sqrt(2)) - (2 * w * modifierRadiusRatio)) * 0.8),
                        (int) (((h * Math.sqrt(2)) - (2 * h * modifierRadiusRatio)) * 0.8), hotkey, mode);
                else
                    iconsVector = Image.generate(icon, (int) (((w * Math.sqrt(2)) - (2 * w * modifierRadiusRatio)) * 0.8),
                            (int) (((h * Math.sqrt(2)) - (2 * h * modifierRadiusRatio)) * 0.8), hotkey, mode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    }


    private void drawModifiers(Graphics2D g2) {
        float width = (float) this.getWidth();
        float height = (float) this.getHeight();
        float diameter =  (height * modifierRadiusRatio);
        float radius=  (diameter/2.0f);

        //Fill arcs
        g2.setColor(useCtrl? (ctrlPressed? pressedColor : Color.DARK_GRAY) : Color.WHITE);
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

    public Color getPressedColor() {
        return this.pressedColor;
    }

    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
    }

    public float getModifierRadiusRatio(){ return this.modifierRadiusRatio; }

    public void setModifierRadiusRatio(float modifierRadiusRatio) {
        this.modifierRadiusRatio = modifierRadiusRatio;
        fixAllImages();
    }

    public void setMetaPressed(boolean metaPressed) {
        this.metaPressed = metaPressed;
    }

    public void setAltPressed(boolean altPressed) {
        this.altPressed = altPressed;
    }

    public void setCtrlPressed(boolean ctrlPressed) {
        this.ctrlPressed = ctrlPressed;
    }

    public void setShftPressed(boolean shftPressed) {
        this.shftPressed = shftPressed;
    }


    /**
     * keep animating until it reaches the destination
     * return false if destination not reached
     * @return true if no more frame exist after this one
     */
    public boolean animateToDestination(int destination){
        switch(destination){
            case IconAnimation.ICON_STEP:
                this.decreaseCurrentFrame();
                return isAnimationCompleteForDestination(destination);
            case IconAnimation.DEFAULT_STEP:
                if(this.currentFrame>this.defaultFrame){
                    this.decreaseCurrentFrame();
                    return isAnimationCompleteForDestination(destination);
                } else if(this.currentFrame<this.defaultFrame){
                    this.increaseCurrentFrame();
                    return isAnimationCompleteForDestination(destination);
                }
                return true;
            case IconAnimation.HOTKEY_STEP:
                this.increaseCurrentFrame();
                return isAnimationCompleteForDestination(destination);
        }
        return false;
    }

    /**
     * check whether or not currentFrame is the one expected
     * by the animation
     * and return true if yes
     * no otherwise
     * @return true if currentFrame is the expected
     */
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
    }

    /**
     * decrease currentframe
     * and fix if out of bounds
     */

    public void decreaseCurrentFrame(){
        this.currentFrame--;
        if (this.currentFrame<=defaultFrame){
            currentFrame = defaultFrame;
            spinner = true;
        }
    }

    /**
     * increase currentframe
     * and fix if out of bounds
     */

    public void increaseCurrentFrame(){
        this.currentFrame++;
        if(currentFrame>=vmax) {
            currentFrame = vmax;
            spinner = false;
        }
    }

    public void changeCurrentFrame(){
        if (spinner)
            increaseCurrentFrame();
        else
            decreaseCurrentFrame();
    }



    // Redimension button
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
        if (!lockClick) {
            if (spinner) {
                while (currentFrame != vmax) {
                    changeCurrentFrame();
                    paintComponent(getGraphics());
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                while (currentFrame != defaultFrame) {
                    changeCurrentFrame();
                    paintComponent(getGraphics());
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        setHotkeyUsed(false);
        if (lockClick)
            mousePressed = true;
    }

    public void mouseReleased(MouseEvent e) {
        if (lockClick)
            mousePressed = false;
    }

    public void mouseEntered(MouseEvent e) {
        this.setToolTipText(name);
    }

    public void mouseExited(MouseEvent e) {

    }

    public boolean isActived (){
        return metaPressed || shftPressed || altPressed || ctrlPressed;
    }

    public int getDefaultFrame(){
        return this.defaultFrame;
    }

    public int getVMax(){
        return this.vmax;
    }

    public int getVmaxDef() {
        return vmaxDef;
    }

    public void setDefaultFrame(int val){
        this.defaultFrame = Math.min (val,iconsVector.size() - 1);
        //this.defaultFrame = val;
        this.currentFrame = Math.max(this.currentFrame,this.defaultFrame);
        //this.currentFrame = this.defaultFrame;
    }

    public void setVMax(int val){
        this.vmax = Math.min(val,this.vmax);
        //this.vmax = val;
        this.currentFrame = Math.min(this.currentFrame,this.vmax);
    }

    public int getNumberOfIcons(){
        return this.iconsVector.size();
    }

    public void setVMaxDefault(){
        this.vmax = iconsVector.size() - 1;
    }

    public BufferedImage getImage (int position){
        return iconsVector.get(position);
    }

    public void setHotkeyUsed(boolean hotkeyUsed) {
        this.hotkeyUsed = hotkeyUsed;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            if (!hotkeyUsed)
                if (lockClick) {
                    if (mousePressed) {
                        increaseCurrentFrame();
                    } else {
                        decreaseCurrentFrame();
                    }
                }
        }
    }
}
