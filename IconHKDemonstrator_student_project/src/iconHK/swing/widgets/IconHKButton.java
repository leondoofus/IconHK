package iconHK.swing.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import iconHK.swing.CommandSelection;
import iconHK.swing.IconAnimation;
import iconHK.swing.MySimpleEditor;

public class IconHKButton extends JButton implements MouseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// A vector of buffered images ordered so it creates an animation from icon
	// to symbol
	private Vector<BufferedImage> iconsVector;
	
	
	private static final Color pressedColor= new Color(29,174,222);



	// the image shown by default (if not in mouse nor keyboard mode)
	private int defaultFrame=5;

	private int vmax= 20;
	
	
	
	// the current frame drawn
	private int currentFrame;

	// the dimension of the button
	private Dimension dimension = new Dimension(32, 32);

	// boolean telling whether or not a modifier is used in the hotkey
	private boolean useMeta, useCtrl, useAlt, useSft;

	// int value referring the length of one side of the square
	// used for modifiers. 0 by default, must be initialized in the constructer
	// and updated if needed
	private float modifierRadiusRatio=0.35f;

	// the border when overed
	private static Border overedBorder = new LineBorder(pressedColor, 2);
	   
	private static final Color translucentGrey = 	new Color(255,255, 255, 150); 
	
	private String name;
	
	//TODO fix the way buttons are handled
	// so they can have a "default" icon
	// defaut icon is updated when mouse selection (increasing toward hotkey) 
	// or keyboard selection (decreasing toward icon)
	// and then be animated by the window depending of mouse or keyboard mode
	
	public IconHKButton(Action a) {
		super(a);

		KeyStroke ks = (KeyStroke)a.getValue(AbstractAction.ACCELERATOR_KEY);
		updateModifiersForKeystroke(ks);
		initForName((String) a.getValue(Action.NAME));
	}

	
	private void updateModifiersForKeystroke(KeyStroke ks){
		String s = ks.toString();
		if(s.contains("ctrl")){
			useCtrl=true;
		}
		if(s.contains("shift")){
			useSft=true;
		}
		if(s.contains("alt")){
			useAlt=true;
		}
		if(s.contains("meta")){
			useMeta=true;
		}
	}
	
	
	public IconHKButton(String text) {
		initForName(text);

	}

	protected void initForName(String text) {
		this.name=text;
		iconsVector = new Vector<BufferedImage>();
		String iconFolder = "./resources/icons/" + text + "/";
		this.setText("");
		Border border = BorderFactory.createLineBorder(Color.DARK_GRAY);
		this.setBorder(border);
		//this.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		File dir = new File(iconFolder);

		File[] iconFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".png");
			}
		});

		for (File f : iconFiles) {
			try {
				iconsVector.add(ImageIO.read(f));
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("image IOException for file " + f.getAbsolutePath());
			}
		}

		if (iconsVector.size() != 0) {
			dimension.setSize(iconsVector.firstElement().getWidth()+10 , iconsVector.firstElement().getHeight() +10);
		}
		//this.modifierSquareLength = (int) ((float) dimension.getHeight() * 0.2f);
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
		this.setContentAreaFilled(false);
		this.setBorderPainted(true);
		this.addMouseListener(this);
		this.setOpaque(true);
		fixCurrentFrame();

		//Use Ctrl par default : not necessary every button has a shortcut
		//this.useCtrl=true;
		this.currentFrame=defaultFrame;
		
		
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
		}
		return value;
	}
	
	
	
	private float getNumberOfExtraMods(){
		float result = 0;
		if(MySimpleEditor.ctrlPressed&&!useCtrl){result++;}
		if(MySimpleEditor.altPressed&&!useAlt){result++;}
		if(MySimpleEditor.shftPressed&&!useSft){result++;}
		if(MySimpleEditor.metaPressed&&!useMeta){result++;}	
		return result;
	}
	
	
	
	
	private float getNumberOfMissingMods(){
		float result = 0;
		if(!MySimpleEditor.ctrlPressed&&useCtrl){result++;}
		if(!MySimpleEditor.altPressed&&useAlt){result++;}
		if(!MySimpleEditor.shftPressed&&useSft){result++;}
		if(!MySimpleEditor.metaPressed&&useMeta){result++;}	
		return result;
	}
	/*
	private boolean hasExtraModifierPressed(){
		return ((MySimpleEditor.ctrlPressed&&!useCtrl)||(MySimpleEditor.metaPressed&&!useMeta)
				||(MySimpleEditor.altPressed&&!useAlt)||(MySimpleEditor.shftPressed&&!useSft));
	}*/

	/**
	 * draw the squares in the corner for the modifiers
	 * 
	 * @param a
	 *            graphics2D context for drawing
	 */
	private void drawModifiers(Graphics2D g2) {
		float width = (float) this.getWidth();
		float height = (float) this.getHeight();
		float diameter =  (height * modifierRadiusRatio);
		float radius=  (diameter/2.0f);
//		if (MySimpleEditor.ctrlPressed == true) {
//			g2.setColor(Color.RED);
//		} else {
//			g2.setColor(Color.BLACK);
//		}
//		if (useCtrl) {
//			//g2.fillArc(+(int)radius, (int) (height-2*radius),(int)diameter, (int)diameter, 0, 90);
//			g2.fillArc(-(int)radius, (int) (height-radius),(int)diameter, (int)diameter, 0, 90);
//			//g2.fillRect(0, (int) (height * 0.8), (int) (height * 0.2), (int) (height * 0.2));
//		} else {
//			g2.drawArc(-(int)radius, (int) (height-radius),(int)diameter, (int)diameter, 0, 90);
//		}
//		if (MySimpleEditor.shftPressed == true) {
//			g2.setColor(Color.RED);
//		} else {
//			g2.setColor(Color.BLACK);
//		}
//		if (useSft) {
//			g2.fillArc(-(int)radius, -(int) (radius),(int)diameter, (int)diameter,  270, 90);
//		} else {
//			g2.drawArc(-(int)radius, -(int) (radius),(int)diameter, (int)diameter, 270, 90);
//		}
//		if (useAlt) {
//			//g2.fillRect((int) (height * 0.8), (int) (height * 0.8), (int) (height * 0.2), (int) (height * 0.2));
//			g2.fillArc((int)(width-radius), (int) (height-radius),(int)diameter, (int)diameter, 90, 90);
//		} else {
//			g2.drawArc((int)(width-radius), (int) (height-radius),(int)diameter, (int)diameter, 90, 90);
//			//g2.drawRect((int) (height * 0.8), (int) (height * 0.8), (int) (height * 0.2), (int) (height * 0.2));
//		}
//		if (useMeta) {
//			g2.fillArc((int)(width-radius), (int) (-radius),(int)diameter, (int)diameter, 180, 90);
//		} else {
//			g2.drawArc((int)(width-radius), (int) (-radius),(int)diameter, (int)diameter, 180, 90);
//		}
		
		//Fill arcs
		if(useCtrl){
			if (MySimpleEditor.ctrlPressed == true) {
				g2.setColor(pressedColor);
			} else {
				g2.setColor(Color.DARK_GRAY);
			}
		}
		else {
			g2.setColor(Color.WHITE);
		}
		g2.fillArc(-(int)radius, (int) (height-radius),(int)diameter, (int)diameter, 0, 90);
		
		if(useSft){
			if (MySimpleEditor.shftPressed == true) {
				g2.setColor(pressedColor);
			} else {
				g2.setColor(Color.DARK_GRAY);
			}
			
		}		
		else {
			g2.setColor(Color.WHITE);
		}
		g2.fillArc(-(int)radius, -(int) (radius),(int)diameter, (int)diameter, 270, 90);
		
		if(useAlt){
			if (MySimpleEditor.altPressed == true) {
				g2.setColor(pressedColor);
			} else {
				g2.setColor(Color.DARK_GRAY);
			}
			
		}
		else {
			g2.setColor(Color.WHITE);
		}
		g2.fillArc((int)(width-radius), (int) (height-radius),(int)diameter, (int)diameter, 90, 90);
		
		if(useMeta){
			if (MySimpleEditor.metaPressed == true) {
				g2.setColor(pressedColor);
			} else {
				g2.setColor(Color.DARK_GRAY);
			}
			
		}
		else {
			g2.setColor(Color.WHITE);
		}
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

	/**
	 * check whether or not currentFrame is still in correct bounds fix it if
	 * not and return true if has been fixed
	 * 
	 * @return true if currentFrame has been fixed
	 */
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
	private Color storedBGColor = null;
	private Border storedBorder = null;
	
	@Override
	public void mousePressed(MouseEvent e) {

		storedBGColor = this.getBackground();
		this.setBackground(Color.GRAY);
		this.currentFrame = vmax-1;
		this.repaint();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.setBackground(storedBGColor);
		this.repaint();
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.storedBorder = this.getBorder();
		this.setBorder(overedBorder);
	}	

	@Override
	public void mouseExited(MouseEvent e) {
		this.setBorder(storedBorder);
		
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
	

	public String getName(){
		return this.name;
	}
	
	
	
	
	/**
	 * decrease currentframe
	 * and fix if out of bounds
	 */
	private void decreaseCurrentFrame(){
		this.currentFrame--;
		if (this.currentFrame<0){
			this.currentFrame =0;
		}
	}
	
	/**
	 * increase currentframe
	 * and fix if out of bounds
	 */
	private void increaseCurrentFrame(){
		this.currentFrame++;
		if(currentFrame>=vmax){
			currentFrame = vmax;
		}
	}
	
	/**
	 * update the default frame based on the last
	 * modality selection
	 */
	public void updateForModality(int modality){
		if(modality==CommandSelection.HOTKEY){
			this.defaultFrame=this.defaultFrame-3;
		}
		else {
			this.defaultFrame = this.defaultFrame+3;
		}
		fixDefaultFrame();
		//this.currentFrame = defaultFrame;
	}
	
	

	/**
	 * check whether or not currentFrame is still in correct bounds fix it if
	 * not and return true if has been fixed
	 * 
	 * @return true if currentFrame has been fixed
	 */
	private boolean fixDefaultFrame() {
		if (defaultFrame < 0) {
			defaultFrame = 0;
			return true;
		} else if (defaultFrame >= iconsVector.size()) {
			defaultFrame = iconsVector.size() - 1;
			return true;
		}
		return false;
	}
	
	public int getDefaultFrame(){
		return this.defaultFrame;
	}
	
	public int getVMax(){
		return this.vmax;
	}
	
	public void setDefaultFrame(int val){
		this.defaultFrame = val;
		this.currentFrame= this.defaultFrame;
	}
	
	public void setVMax(int val){
		this.vmax = val;
	}
	
	
	public int getNumberOfIcons(){
		return this.iconsVector.size();
	}
	
	public int getCurrentFrame(){
		return this.currentFrame;
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
}
