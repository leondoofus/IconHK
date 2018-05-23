package iconHK.experiment.view;


import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import iconHK.experiment.model.BlockDescriptor;

public class XPButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ImageIcon training =null;
	private ImageIcon test = null;
	private String tooltip;
	//private Dimension dimension = new Dimension (64,64);
	
	private static ImageIcon empty;
	
	static{
		try {
			empty = new ImageIcon(ImageIO.read(new File("./resources/icons/xp/nullimage.png")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public XPButton(String s, String hotkey, String trainingPath, String testPath){
		super(s);

		tooltip = s;
		if(hotkey!=null){
			tooltip+=", Ctrl "+hotkey;
		}
		this.setToolTipText(tooltip);
		 this.setVerticalTextPosition(SwingConstants.BOTTOM);
		    this.setHorizontalTextPosition(SwingConstants.CENTER);
		    File ftest, ftraining;
		    
		   
		try {
			 ftest = new File(testPath);
			ftraining = new File(trainingPath);
			  training = new ImageIcon(ImageIO.read(ftraining));
			  test = new ImageIcon(ImageIO.read(ftest));
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		this.setIcon(training);
		this.setDisabledIcon(training);
	
	}
	
	
	
	
	public void updateForPhase(int phase){
		if(phase==BlockDescriptor.TRAINING_BLOCK){
			this.setIcon(training);
			this.setDisabledIcon(training);

		}
		else if(phase==BlockDescriptor.TEXT_TEST_BLOCK){
			this.setIcon(empty);
			this.setDisabledIcon(empty);
			
		}
		else if(phase==BlockDescriptor.ICON_TEST_BLOCK){
			this.setIcon(empty);
			this.setDisabledIcon(empty);

		}
		if(phase==BlockDescriptor.TRAINING_BLOCK){
			this.setEnabled(true);
		}
		else {
			this.setEnabled(false);
		}
		this.repaint();
	}
	
	
	 @Override
	  public void setToolTipText(String text)
	  {
	    super.setToolTipText(text);
	    if (null != text) tooltip = text;
	  }

	  @Override
	  public void setEnabled(boolean b)
	  {
	    super.setEnabled(b);
	    super.setToolTipText(b ? tooltip : null);
	  }
	  
	  
	  public ImageIcon getTestImageIcon(){
		  return this.test;
	  }
	

	  

}
