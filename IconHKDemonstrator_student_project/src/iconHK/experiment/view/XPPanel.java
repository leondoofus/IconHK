package iconHK.experiment.view;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import iconHK.experiment.model.BlockDescriptor;

public class XPPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean displayIcon = true;
	private boolean displayText = true;
	private boolean hasFeedback = true;
	
	private String additionalText = null;
	private String targetName = null;
	private BufferedImage icon = null;
	
	

	private static int state = -1;
	
	
	public XPPanel(String s){
		this.additionalText = s;
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawCenteredString(g,this.additionalText,this.getBounds(),this.getFont());
		
		
		
	}
	
	
	/**
	 * Draw a String centered in the middle of a Rectangle.
	 *
	 * @param g The Graphics instance.
	 * @param text The String to draw.
	 * @param rect The Rectangle to center the text in.
	 */
	public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
	    // Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = (rect.width - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text
	    int y = ((rect.height - metrics.getHeight()) / 2) - metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    // Draw the String
	    g.drawString(text, x, y);
	    // Dispose the Graphics
	    g.dispose();
	}
	
	
	
	public void updateBlockPhase(int blockphase){
		//TODO here
		// We need to change what is displayed depending of the phase
		// that is, training displays text stimulus + provide feedback of correctness
		// textPhase displays text stimulus + no feedback
		// iconPhase displays icon stimulus + no feedback
		if(blockphase==BlockDescriptor.TRAINING_BLOCK){
			this.hasFeedback = true;
			this.displayIcon=true;
			this.displayText=true;
		}
		else if(blockphase==BlockDescriptor.TEXT_TEST_BLOCK){
			this.hasFeedback = false;
			this.displayIcon=false;
			this.displayText=true;
		}
		else if(blockphase==BlockDescriptor.ICON_TEST_BLOCK){
			this.hasFeedback = false;
			this.displayIcon=true;
			this.displayText=false;
		}
		
		this.repaint();
	}
	

	
	
}
