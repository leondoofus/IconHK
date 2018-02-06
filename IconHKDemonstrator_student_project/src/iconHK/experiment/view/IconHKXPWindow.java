package iconHK.experiment.view;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

																																																																																																																																																																									import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;

import iconHK.experiment.logger.XPLogger;
import iconHK.experiment.model.BlockDescriptor;
import iconHK.experiment.model.ButtonDescription;
import iconHK.experiment.model.ConditionDescriptor;
import iconHK.experiment.model.IconHKXPModel;
import iconHK.experiment.model.ResultDescriptor;
import iconHK.experiment.model.TargetDescription;
import iconHK.experiment.model.TrialDescriptor;


public class IconHKXPWindow extends JFrame  implements IconHKXPModelListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static ImageIcon myImage = null;
	private static final Font myfont = new Font("Helvetica",Font.PLAIN,1);
	
	private static final Font labelFont = new Font("Helvetica",Font.PLAIN,16);
	
	Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE};
	
	private JPanel xpPanel;
	// label for test so far
	private JLabel xpLabel;
	
	private JToolBar toolbar;
	
	private IconHKXPModel model;
	
	private ArrayList<JMenuItem> menuItems=null;
	private ArrayList<TargetDescription> targets=null;
	
	XPLogger logger = null;
	
	long lastTime = System.currentTimeMillis();
	
	public IconHKXPWindow(){
		
		
	}
	
	
	public void createAndShowGUI(){
		int user = buildDialog();
		if(user==-1){
			System.exit(0);
		}
		loadAllCommands();
		init(user);
		build();
		model.run();
	}
	
	
	
	private void loadAllCommands(){
		menuItems = new ArrayList<JMenuItem>();
		targets = new ArrayList<TargetDescription>();
		
		
		for (int i=1;i<2;i++){
		String iconSetPath = "./resources/data/-iconset"+i+".txt";
		List<String> buttons = null;
		try {
			buttons = Files.readAllLines(Paths.get(iconSetPath), Charset.defaultCharset());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(String s: buttons){
			String [] split= s.split(",");
			JMenuItem item = new JMenuItem(split[0]);
			if(split.length>1){
			
			item.setAccelerator(KeyStroke.getKeyStroke(
        "control "+split[1].toUpperCase()));
			item.setFont(myfont);
			item.setForeground(Color.WHITE);
			targets.add(new TargetDescription(split[0],split[1].toUpperCase(),Integer.parseInt(split[2])));
			}
			menuItems.add(item);
			
			
		}
		
		
		String remain = "GILNOPTUVXZ";
		String[] splitRemain = remain.split("(?!^)"); 
		for(String s:splitRemain){
			JMenuItem item = new JMenuItem("Lorem"+s);
			item.setAccelerator(KeyStroke.getKeyStroke(
			        "control "+s.toUpperCase()));
			item.setFont(myfont);
			item.setForeground(Color.WHITE);
			menuItems.add(item);
		}
			
		}
	}
	
	// Must be public!!
	  public class MyApplicationListener implements ApplicationListener {

	     private void handle(ApplicationEvent event, String message) {
	        event.setHandled(true);
	     }

	     public void handleAbout(ApplicationEvent event) {
	        handle(event, "aboutAction");
	     }

	     public void handleOpenApplication(ApplicationEvent event) {
	        // Ok, we know our application started
	        // Not much to do about that..
	     }

	     public void handleOpenFile(ApplicationEvent event) {
	        handle(event, "openFileInEditor: " + event.getFilename());
	     }

	     public void handlePreferences(ApplicationEvent event) {
	        handle(event, "preferencesAction");
	     }

	     public void handlePrintFile(ApplicationEvent event) {
	        handle(event, "Sorry, printing not implemented");
	     }

	     public void handleQuit(ApplicationEvent event) {
	       // handle(event, "exitAction");
	        //System.exit(0);
	     }

	     public void handleReOpenApplication(ApplicationEvent event) {
	        event.setHandled(true);
	     }
	  }
	
	
	private void build(){
		Application app = new Application();
         
        app.addApplicationListener(new MyApplicationListener());
		xpPanel = new JPanel();
		xpPanel.setLayout(new BorderLayout());
		xpLabel = new JLabel("wait for test",SwingConstants.CENTER);
		xpLabel.setFont(labelFont);
		xpPanel.add(xpLabel);
		xpPanel.setSize(500,800);
		this.add(xpPanel);
		toolbar = new JToolBar();
		this.add(toolbar, BorderLayout.NORTH);
		
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu();
		for(JMenuItem menuitem : menuItems){
			
			menuitem.addActionListener(this);
			menu.add(menuitem);
		}

		MouseListener [] mouseL = menu.getMouseListeners();
		menu.setFont(myfont);

		// we remove mouse listeners from the menu
		for(MouseListener mous : mouseL){
			menu.removeMouseListener(mous);
		}
		
		menubar.add(menu);
		this.setJMenuBar(menubar);
		
		this.setSize(1024,800);
		this.setResizable(false);
		
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.setVisible(true);
		
	}

	
	private void updateToolBar(ConditionDescriptor condi, int blockPhase){

		toolbar.removeAll();
		
		for (ButtonDescription buttonDesc : condi.getButtons()){
			XPButton butt = new XPButton(buttonDesc.getName(),buttonDesc.getHotkey(),buttonDesc.getTrainingIconPath(),buttonDesc.getTestIconPath());


			butt.addActionListener(this);
			butt.updateForPhase(blockPhase);
			toolbar.add(butt);
		}
		this.repaint();
	}
	
	private void init(int user){
		model = new IconHKXPModel(user);
		model.addIconHKXPModelListener(this);
		File f = new File("./resources/icons/Cut/Cut_10.png");
		try {
			myImage = new ImageIcon(ImageIO.read(f));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		 logger = new XPLogger(user);
		
	}
	
	private void updateToolBarForPhase(int blockPhase){

		
		
		for (Component c: toolbar.getComponents()){
			if(c.getClass()==XPButton.class){
				XPButton butt = (XPButton)c;
				butt.updateForPhase(blockPhase);

			}
		}
		this.validate();
		this.repaint();
	}
	
	
	
	
	
	
	private void updateLabelFor(ConditionDescriptor condDes, BlockDescriptor blockDesc, TrialDescriptor trialDesc, int event){
		//Todo add dialog between conditions and breaks
		String msg="";
		switch (event){
			case start:
				JOptionPane.showMessageDialog(this,
					    "You are about to start the experiment");
				msg = "<html>Please select the command :<br/><font size=20 color='red'>"+trialDesc.getTarget()+"</font><br/>using its keyboard shortcut.<html>";
				xpLabel.setText(msg);
				
				break;
			case finish:
				xpLabel.setText("Thank you. The experiment is Over");
				xpLabel.setIcon(null);
				break;
			case nextCondition:
				JOptionPane.showMessageDialog(this,
						"Well done!\n"
					    + "You are moving on to the next condition."
					    + "This block is a "+blockDesc.getBlockTypeName()+"block.");
				
				msg = "<html>Please select the command :<br/><font size=20 color='red'>"+trialDesc.getTarget()+"</font><br/>using its keyboard shortcut.<html>";
				
				xpLabel.setText(msg);
				if(blockDesc.getBlockType()!=BlockDescriptor.ICON_TEST_BLOCK) {
					xpLabel.setIcon(null);
				}
				break;
			case nextBlock:
				JOptionPane.showMessageDialog(this,
					    "Well done!\n"
					    + "You are now moving on to the next block.\n"
					    + "Next block is a "+blockDesc.getBlockTypeName()+" block");
				if(blockDesc.getBlockType()==BlockDescriptor.TEXT_TEST_BLOCK){
					xpLabel.setIcon(null);
					msg = "<html>Please select the command :<br/><font size=20 color='red'>"+trialDesc.getTarget()+"</font><br/>using its keyboard shortcut.<html>";
					
					xpLabel.setText(msg);
				}
				else if(blockDesc.getBlockType()==BlockDescriptor.ICON_TEST_BLOCK) {
					
					ImageIcon icon = retrieveCorrectIconForCurrentTarget();
					
						
					xpLabel.setIcon(icon);
					msg = "<html>Please select this command using its keyboard shortcut.<html>";
					
					xpLabel.setText(msg);
				}
				else {
					xpLabel.setIcon(null);
					
					msg = "<html>Please select the command :<br/><font size=20 color='red'>"+trialDesc.getTarget()+"</font><br/>using its keyboard shortcut.<html>";
					
					xpLabel.setText(msg);
				}
				
				break;
			case nextTrial:

				if(blockDesc.getBlockType()==BlockDescriptor.TEXT_TEST_BLOCK){
					xpLabel.setIcon(null);
					msg = "<html>Please select the command :<br/><font size=20 color='red'>"+trialDesc.getTarget()+"</font><br/>using its keyboard shortcut.<html>";
					
					xpLabel.setText(msg);
				}
				else if(blockDesc.getBlockType()==BlockDescriptor.ICON_TEST_BLOCK) {
					ImageIcon icon = retrieveCorrectIconForCurrentTarget();
						
					xpLabel.setIcon(icon);
					msg = "<html>Please select this command using its keyboard shortcut.<html>";
					xpLabel.setText(msg);
				}
				else {
					msg = "<html>Please select the command :<br/><font size=20 color='red'>"+trialDesc.getTarget()+"</font><br/>using its keyboard shortcut.<html>";
					xpLabel.setText(msg);
				}
				break;
		}
		xpLabel.repaint();
		lastTime=System.currentTimeMillis();
	}
	
	
	
	
	private ImageIcon retrieveCorrectIconForCurrentTarget(){
		ImageIcon icon = myImage;
		String target = model.getCurrentTarget();
		for (Component c: toolbar.getComponents()){
			if(c.getClass()==XPButton.class){
				XPButton butt = (XPButton)c;
				if(butt.getText().equals(target)){
					icon = butt.getTestImageIcon();
					}
			}
		}
		return icon;
	}
	
	
	
	private static final int start = 1, finish=2,nextCondition=3, nextBlock=4, nextTrial=5;
	
	/**
	 * event methods below
	 */
	
	@Override
	public void start(int trial, int block, int conditionID) {
		
		lastTime=System.currentTimeMillis();
		ConditionDescriptor condDes = model.getConditions().get(conditionID);
		BlockDescriptor blockDes = condDes.getBlocks().get(block);
		TrialDescriptor trialDes = blockDes.getTrials().get(trial);
		this.updateToolBar(condDes,blockDes.getBlockType());
		this.updateLabelFor(condDes,blockDes, trialDes, start);
	}

	@Override
	public void finish() {
		
		this.toolbar.removeAll();
		updateLabelFor(null,null,null,finish);		
		
	}

	@Override
	public void nextTrial(int trial, int block, int conditionID) {
		lastTime=System.currentTimeMillis();
		ConditionDescriptor condDes = model.getConditions().get(conditionID);
		BlockDescriptor blockDes = condDes.getBlocks().get(block);
		TrialDescriptor trialDes = blockDes.getTrials().get(trial);
		this.updateLabelFor(condDes, blockDes, trialDes, nextTrial);
		//System.out.println("next trial for t:"+trial+" b:"+block+" c:"+conditionID);
		System.out.println("next");
		
	}

	@Override
	public void nextBlock(int trial, int block, int conditionID) {
		lastTime=System.currentTimeMillis();
		//System.out.println("next block for t:"+trial+" b:"+block+" c:"+conditionID);
		ConditionDescriptor condDes = model.getConditions().get(conditionID);
		BlockDescriptor blockDes = condDes.getBlocks().get(block);
		TrialDescriptor trialDes = blockDes.getTrials().get(trial);
		this.updateLabelFor(condDes, blockDes, trialDes, nextBlock);
		this.updateToolBarForPhase(blockDes.getBlockType());
	}

	@Override
	public void nextCondition(int trial, int block, int conditionID) {
		lastTime=System.currentTimeMillis();
		ConditionDescriptor condDes = model.getConditions().get(conditionID);
		BlockDescriptor blockDes = condDes.getBlocks().get(block);
		TrialDescriptor trialDes = blockDes.getTrials().get(trial);
		//System.out.println("next condition for t:"+trial+" target:"+trialDes.getTarget()+" b:"+block+" name:"+blockDes.getBlockTypeName()+" c:"+conditionID+" name:"+condDes.getConditionName());
		this.updateLabelFor(condDes, blockDes, trialDes, nextCondition);
		this.updateToolBar(condDes,blockDes.getBlockType());
	}
	
	
	
	
	
	public static void main(String [] args){

    	IconHKXPWindow window = new IconHKXPWindow();
    	window.createAndShowGUI();
	}
	
	
	
	
	
	private  int buildDialog(){
		String [] possibilities = new String[40];
		for (int i=0;i<40;i++){
			possibilities[i] = ""+(i+1);
		}

		String s = (String)JOptionPane.showInputDialog(
		                    null,
		                    "Select user number:",
		                    "Experiment",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    possibilities,
		                    "");

		//If a string was returned, say so.
		if ((s != null) && (s.length() > 0)) {
		    return Integer.parseInt(s);
		}
		
		
		return -1;

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String selection = null;
		

		ConditionDescriptor condDes = model.getConditions().get(model.condition);
		BlockDescriptor blockDes = condDes.getBlocks().get(model.block);
		
		long selectionTime = System.currentTimeMillis();
		int modality = 0;
		String selectedHotkey = "wrong 0";
		if(e.getSource().getClass()==JMenuItem.class){
			JMenuItem item = (JMenuItem)e.getSource();
			selection = item.getText();
			modality = ResultDescriptor.HK_SELECTION;
			selectedHotkey=item.getAccelerator().toString();
			
		}
		else if(e.getSource().getClass()==XPButton.class){
			XPButton button = (XPButton)e.getSource();
			selection = button.getText();
			modality = ResultDescriptor.MOUSE_SELECTION;
			selectedHotkey= getHotkeyForButton(button);
		}
		String target = model.getCurrentTarget();
		boolean correct = true;
		//System.out.println("target is "+target+ " selection is "+selection+" hotkey is "+selectedHotkey);
		if(selection.equals(target)){
			//System.out.println("selection correct");
		}
		else {
			correct = false;
			if(blockDes.getBlockType()==BlockDescriptor.TRAINING_BLOCK){
				JOptionPane.showMessageDialog(this,
						"Wrong command! Please pay more attention.");
			}
		}
		ResultDescriptor result = new ResultDescriptor(model.getUserNumber(), condDes, blockDes, blockDes.getTrials().get(model.trial), getTargetDescForTarget(target),selectedHotkey,
				(int)(selectionTime-lastTime), modality,  correct);
		
		if(logger!=null){
			logger.writeSelection(result);
		}
		
		
		
		
		model.doFromClick(true);
	}

	
	
	
	
	private TargetDescription getTargetDescForTarget(String target){
		for(TargetDescription decs: targets){
			if(decs.getName().equals(target)){
				return decs;
			}
		}
		return null;
	}
	
	
	private String getHotkeyForButton(XPButton button){
		for(JMenuItem item: menuItems ){
				if(item.getText().equals(button.getText())){
					return item.getAccelerator().toString();
				}
		}
		return "wrong 0";
	}
	
	
	
	
	
	
	
	
	

}
