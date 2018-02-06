package iconHK.swing.widgets;


import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

import iconHK.swing.MySimpleEditor;

public class ModalButton extends JButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  Vector<BufferedImage> iconsVector;
	private int currentFrame;
	private Dimension dimension = new Dimension(32,32);
	
	public int animate = 0;
	
	private String name;
	
	
	private boolean useMeta,useCtrl,useAlt,useSft;
	
	
	/**
	 * construct a ModalButton with a text 
	 * and load the corresponding icons
	 * and set the id of the animation to the int given as parameter
	 * @param text the title of this button
	 * @param startingFrame the startingID of the animation
	 */
	public ModalButton(String text, int startingFrame){
		this.useCtrl=true;
		
		name = text;
		//super(text);
		iconsVector = new Vector<BufferedImage>();
		String iconFolder = "./resources/icons/"+text+"/";
		//String iconFolder = getClass().getResource("/icons/"+text+"/").getPath();
		Border border = BorderFactory.createLineBorder(Color.BLACK);

		this.setBorder(BorderFactory.createCompoundBorder(border, 
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		File dir = new File(iconFolder);


		

		
    	File[] iconFiles = dir.listFiles(new FilenameFilter() { 
    	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(".png"); }
    	} );
		
    	for (File f : iconFiles){
    		try {
				iconsVector.add(ImageIO.read(f));
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("image IOException for file "+f.getAbsolutePath());
			}
    	}
    	
    	if(iconsVector.size()!=0){
    		dimension.setSize(iconsVector.firstElement().getWidth()+20, iconsVector.firstElement().getHeight()+20);
    	}
    	this.setOpaque(false);
    	this.setContentAreaFilled(false);
    	this.setBorderPainted(true);
    	

    	this.currentFrame = startingFrame;
		fixCurrentFrame();
		
	}
	

	
	
	public static Vector<File> listFiles(File dir,boolean withprint){
		if(!dir.isDirectory()){
			return null;
		}
		Vector<File> res=new Vector<File>();
		File[] listOfFiles = dir.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				
				if(!listOfFiles[i].getName().equals(".DS_Store")){
				res.add(listOfFiles[i]);
				if(withprint){
					System.out.println("File " + listOfFiles[i].getName());}
				}
			}
		}
		return res;
	}
	

	/**
	 * check whether or not currentFrame is still in correct bounds
	 * fix it if not
	 * and return true if has been fixed
	 * @return true if currentFrame has been fixed
	 */
	private boolean fixCurrentFrame(){
		if (currentFrame<0){
			currentFrame =0;
			return true;
		}
		else if(currentFrame>=iconsVector.size()){
			currentFrame = iconsVector.size()-1;
			return true;
		}
		return false;
	}
	

	@Override
	public Dimension getPreferredSize(){
		return dimension;
	}
	
	@Override
	public Dimension getMinimumSize(){
		return dimension;
	}

	@Override
	public Dimension getMaximumSize(){
		return dimension;
	}

	
	
	   protected void paintComponent(Graphics g) {
		   super.paintComponent(g);
	      Graphics2D g2 = (Graphics2D) g.create();
	      if(this.iconsVector!=null){
	    	  BufferedImage icon = iconsVector.get(currentFrame);
	    	  int x = (getWidth() - icon.getWidth()) / 2;
              int y = (getHeight() - icon.getHeight()) / 2;
              g2.drawImage(icon, x, y, this);
	      }
	      drawModifiers(g2);
	      g2.setColor(Color.BLACK);

	    
	   } 
	
	
	   /**
	    * draw the squares in the corner for the modifiers
	    * @param a graphics2D context for drawing
	    */
	   private void drawModifiers(Graphics2D g2) {
		   float width = (float)this.getWidth();
		   float height = (float)this.getHeight();
		   
		   if(MySimpleEditor.ctrlPressed==true){
			   g2.setColor(Color.RED);
		   }
		   else {
			   g2.setColor(Color.BLACK);
		   }
		   if (useCtrl) {
			   g2.fillRect(0, (int)(height*0.8), (int)(height*0.2), (int)(height*0.2));
		   } else {
			   g2.drawRect(0, (int)(height*0.8), (int)(height*0.2), (int)(height*0.2));
		   }
		   if(MySimpleEditor.shftPressed==true){
			   g2.setColor(Color.RED);
		   }
		   else {
			   g2.setColor(Color.BLACK);
		   }
		   if (useSft) {
			   g2.fillRect(0, 0, (int)(height*0.2), (int)(height*0.2));
		   } else {
			   g2.drawRect(0, 0, (int)(height*0.2), (int)(height*0.2));
		   }
		   if (useAlt) {
			   g2.fillRect((int)(height*0.8), (int)(height*0.8), (int)(height*0.2), (int)(height*0.2));
		   } else {
			   g2.drawRect((int)(height*0.8), (int)(height*0.8), (int)(height*0.2), (int)(height*0.2));
		   }
		   if (useMeta) {

		   } else {

		   }
	   }
	   
	   
	
	 /**
	  * increase currentframe and return false if frame hasn't been fixed 
	  * which means there are still frame after this one
	  * @return true if no more frame exist after this one
	  */
	public boolean nextFrame() {
		this.currentFrame++;
		return fixCurrentFrame();
	}
	
	 /**
	  * decrease currentframe and return false if frame hasn't been fixed 
	  * which means there are still frame before this one
	  * @return true if no more frame exist before this one
	  */
	public boolean previousFrame() {
		this.currentFrame--;
		return fixCurrentFrame();
	}
	
	
	public void setFrameToFirst(){
		this.currentFrame = 0;
	}





	
	public int animate(){
		return animate;
	}

	public String getName(){
		return this.name;
	}

}
