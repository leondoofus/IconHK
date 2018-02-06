package iconHK.swing;


import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Vector;
import java.awt.event.AWTEventListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import iconHK.swing.widgets.IconHKButton;

public class MySimpleEditor extends JFrame implements ActionListener, KeyEventDispatcher {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	private static final int persistence = 20;
	
	
	private static SettingsWindow settingsWindow;
	
	
	private JTextComponent textComp;
	private JToolBar toolbar;
	
	private CommandSelectionHistory history;
	
	
	private Vector<IconAnimation> animations = null;

	
	private Vector<IconHKButton> iconHKButtons = null;
	
	
	public static boolean metaPressed = false;
	public static boolean altPressed = false;
	public static boolean ctrlPressed = false;
	public static boolean shftPressed = false;
	
	
	int currentAnimation = 0;
	
	Timer timer;
	
	
	private Action cutAction, pasteAction, copyAction, saveAction, newAction, expandAction, rgbAction;
	
	private Action findAction, moveAction, pencilAction, increaseAction;
	
	

	public MySimpleEditor() {
		super("Swing Editor");
		

		this.init();
		this.build();
		this.addEvents();
		

		this.buildSettingsWindow();
		this.animateall();
		
	}

	
	
	
	private void animateall(){
		
		for (Component c : toolbar.getComponents()) {

			if (c.getClass() == IconHKButton.class) {
				IconHKButton but = (IconHKButton) c;
				
					int[] sequence = { IconAnimation.HOTKEY_STEP, IconAnimation.DEFAULT_STEP };
					animations.add(new IconAnimation(but, sequence, 40));
				
			}
		}
	}
	
	private void buildSettingsWindow(){
		settingsWindow = new SettingsWindow(this.iconHKButtons);
		settingsWindow.setVisible(true);
	}
	
	private void init(){
		history = new CommandSelectionHistory();
		animations = new Vector<IconAnimation>();
		iconHKButtons = new Vector<IconHKButton>();
		timer = new Timer(50, this);
		timer.start();
	}
	
	

	private void build(){
		textComp = this.createTextComponent();
		this.createActions();
		
		
		
		
		Container content = getContentPane();
		content.add(textComp, BorderLayout.CENTER);
		content.add(this.toolbar = createToolBar(), BorderLayout.NORTH);
		this.setJMenuBar(createMenuBar());
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	//add events except for timer
	private void addEvents(){
		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kfm.addKeyEventDispatcher(this);
		this.addMouseEvents();
	}
	
	
	private void addMouseEvents(){
		// On définit un "masque d’événements" qui nous intéresse
		long eventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK
						+ AWTEvent.MOUSE_EVENT_MASK + AWTEvent.KEY_EVENT_MASK;

		// On récupère le “toolkit” par défaut et on lui ajoute le listener avec ce masque
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			public void eventDispatched(AWTEvent event) {
		// On filtre les évènements par ID
				 if(event.getID() == MouseEvent.MOUSE_MOVED){
					 //animateMouse();
				 }
				}
			}, eventMask);
	}

	
	
	
	// Create the JTextComponent subclass.
	protected JTextComponent createTextComponent() {
		JTextArea ta = new JTextArea();
		ta.setLineWrap(true);
		return ta;
	}

	
	
	protected void createActions() {
		cutAction = new IconHKAction("Cut");
		cutAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		
		
		pasteAction = new IconHKAction("Paste");
		pasteAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		
		copyAction = new IconHKAction("Copy");
		copyAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_C, ActionEvent.CTRL_MASK));

		saveAction = new IconHKAction("Save");
		saveAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		newAction = new IconHKAction("New");
		newAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		
		expandAction = new IconHKAction("Expand");
		expandAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		
		rgbAction = new IconHKAction("RGB");
		rgbAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_E, ActionEvent.CTRL_MASK|ActionEvent.ALT_MASK));
		
		findAction = new IconHKAction("Find");
		findAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		
		moveAction = new IconHKAction("Move");
		moveAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		
		pencilAction = new IconHKAction("Pencil");
		pencilAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		
		increaseAction = new IconHKAction("Increase");
		increaseAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		        KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		
	}
	
	
	
	// Create a simple JToolBar with some buttons.
	protected JToolBar createToolBar() {
		JToolBar bar = new JToolBar();

		
		addButtonToToolbar(new IconHKButton(newAction),bar);
		addButtonToToolbar(new IconHKButton(saveAction),bar);
		bar.addSeparator();
		addButtonToToolbar(new IconHKButton(copyAction),bar);
		
		
		
		addButtonToToolbar(new IconHKButton(cutAction),bar);
		addButtonToToolbar(new IconHKButton(pasteAction),bar);
		bar.addSeparator();
		
		addButtonToToolbar(new IconHKButton(expandAction),bar);
		addButtonToToolbar(new IconHKButton(rgbAction),bar);
		
		addButtonToToolbar(new IconHKButton(findAction),bar);
		addButtonToToolbar(new IconHKButton(moveAction),bar);
		addButtonToToolbar(new IconHKButton(pencilAction),bar);
		addButtonToToolbar(new IconHKButton(increaseAction),bar);
		
	
		
		return bar;
	}
	
	
	protected void addButtonToToolbar(JButton button,JToolBar bar){
		button.addActionListener(this);
		bar.add(Box.createRigidArea(new Dimension(2,0)));
		bar.add(button);
		bar.add(Box.createRigidArea(new Dimension(2,0)));
		if(button.getClass() == IconHKButton.class){
			iconHKButtons.add((IconHKButton)button);
		}
	}

	// Create a JMenuBar with file & edit menus.
	protected JMenuBar createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu  other = new JMenu("Other");
		menubar.add(file);
		menubar.add(edit);
		menubar.add(other);
		
		JMenuItem newfile = new JMenuItem(newAction);
		newfile.addActionListener(this);
	//	newfile.setAccelerator(KeyStroke.getKeyStroke(
      //  KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		file.add(newfile);
		
		
		
		JMenuItem save = new JMenuItem(saveAction);
		save.addActionListener(this);
		//save.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_S, ActionEvent.CTRL_MASK|ActionEvent.ALT_MASK));
		file.add(save);
		
		JMenuItem copy = new JMenuItem(copyAction);
		copy.addActionListener(this);
	//	copy.setAccelerator(KeyStroke.getKeyStroke(
     //   KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		edit.add(copy);
		
		JMenuItem cut = new JMenuItem(cutAction);
		cut.addActionListener(this);
	//	cut.setAccelerator(KeyStroke.getKeyStroke(
      //  KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		edit.add(cut);
		
		JMenuItem paste = new JMenuItem(pasteAction);
		paste.addActionListener(this);
		//paste.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		edit.add(paste);
		
		JMenuItem rgb = new JMenuItem(rgbAction);
		rgb.addActionListener(this);
		other.add(rgb);
		
		JMenuItem expand = new JMenuItem(expandAction);
		expand.addActionListener(this);
		other.add(expand);
		
		JMenuItem find = new JMenuItem(findAction);
		find.addActionListener(this);
		other.add(find);
		
		JMenuItem move = new JMenuItem(moveAction);
		move.addActionListener(this);
		other.add(move);
		
		
		JMenuItem pencil = new JMenuItem(pencilAction);
		pencil.addActionListener(this);
		other.add(pencil);
		
		JMenuItem increase = new JMenuItem(increaseAction);
		increase.addActionListener(this);
		other.add(increase);

		
		
		
		
		
		return menubar;
	}

	
	
	private void animateToolbar() {

		if (animations.size() > 0) {
			Iterator<IconAnimation> i = animations.iterator();
			while (i.hasNext()) {
				IconAnimation animation = i.next();
				int objective = animation.getCurrentObjective();
				boolean over = false;
				switch (objective) {

				case IconAnimation.HOTKEY_STEP:
					over = animation.getButton().animateToDestination(IconAnimation.HOTKEY_STEP);
					animation.getButton().repaint();

					if (over) {
						
						if(animation.hasToPersist()){
							animation.increaseCurrentPersistency();
						}
						else {
							animation.nextAnimationStep();
						}
					}
					break;
				case IconAnimation.ICON_STEP:
					over = animation.getButton().animateToDestination(IconAnimation.ICON_STEP);
					animation.getButton().repaint();
					if (over) {
						animation.nextAnimationStep();
					}
					break;
				case IconAnimation.DEFAULT_STEP:
					over = animation.getButton().animateToDestination(IconAnimation.DEFAULT_STEP);
					animation.getButton().repaint();
					if (over) {
						animation.nextAnimationStep();
					}
					break;
				case IconAnimation.OVER_STEP:
					i.remove();
					break;
				}
			}
		}
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer) {
			
			animateToolbar();
			repaint();
		} else {
			
			String text = null;
			int modality=0;
			if (e.getSource().getClass() == IconHKButton.class) {
				IconHKButton but = (IconHKButton) e.getSource();
				text = but.getName();

				int[] sequence = { IconAnimation.HOTKEY_STEP, IconAnimation.DEFAULT_STEP };
				animations.add(new IconAnimation(but, sequence, persistence));
				modality = CommandSelection.TOOLBAR_BUTTON;
			} else if (e.getSource().getClass() == JMenuItem.class) {
				JMenuItem item = (JMenuItem) e.getSource();
				text = item.getText();
				modality = CommandSelection.MENU_ITEM;
				for (Component c : toolbar.getComponents()) {

					if (c.getClass() == IconHKButton.class) {
						IconHKButton but = (IconHKButton) c;
						if (but.getName().equals(text)) {
							int[] sequence = { IconAnimation.HOTKEY_STEP, IconAnimation.DEFAULT_STEP };
							animations.add(new IconAnimation(but, sequence, persistence));
						}
					}
				}
			}
			
			if(isModalityHotkey(e)){
				modality = CommandSelection.HOTKEY;
			}
			if((text!=null)&&(modality!=0)){
				history.addCommandSelection(new CommandSelection(text,modality));
				updateWidgetForCommand(text,modality);
			}
		}
	}
	
	
	private void updateWidgetForCommand(String name, int modality){
		for (Component c : toolbar.getComponents()) {
				
			if (c.getClass() == IconHKButton.class) {
				IconHKButton but = (IconHKButton) c;
				if (but.getName().equals(name)) {
					but.updateForModality(modality);
				}
			}
		}
	}
	
	
	
	private boolean isModalityHotkey(ActionEvent e){
		return (e.getModifiers() == InputEvent.CTRL_MASK);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		boolean anyModifPress= modifierPressed();
		if (e.getID() == KeyEvent.KEY_PRESSED) {
		      switch(e.getKeyCode()){
		      	case KeyEvent.VK_META:
		      		metaPressed = true;
		      		break;
		      	case KeyEvent.VK_CONTROL:
		      		ctrlPressed = true;
		      		break;
		      	case KeyEvent.VK_ALT:
		      		altPressed = true;
		      		break;
		      	case KeyEvent.VK_SHIFT:
		      		shftPressed=true;
		      		break;
		      }
		    }
		else if (e.getID() == KeyEvent.KEY_RELEASED) {
		      switch(e.getKeyCode()){
		      	case KeyEvent.VK_META:
		      		metaPressed = false;
		      		break;
		      	case KeyEvent.VK_CONTROL:
		      		ctrlPressed = false;
		      		break;
		      	case KeyEvent.VK_ALT:
		      		altPressed = false;
		      		break;
		      	case KeyEvent.VK_SHIFT:
		      		shftPressed=false;
		      		break;
		      }
		    }
		if(modifierPressed()){
			
			if(!anyModifPress){	
				
				//a modifier is pressed for the first time
				// until HK shown
				animations.removeAllElements();
				for(Component c : toolbar.getComponents()){
					if(c.getClass()==IconHKButton.class){
						IconHKButton but = (IconHKButton)c;
						int[] sequence = {IconAnimation.HOTKEY_STEP};
						animations.add(new IconAnimation(but,sequence));
					}
					
				}
			}
		}
		else {
			if(anyModifPress){
				// animate toolbar to
				// show icon again
				animations.removeAllElements();
				for(Component c : toolbar.getComponents()){
					if(c.getClass()==IconHKButton.class){
						IconHKButton but = (IconHKButton)c;
						int[] sequence = {IconAnimation.DEFAULT_STEP};
						animations.add(new IconAnimation(but,sequence));
					}
				}
			}
			
			
		}
		return false;
	}
	
	private boolean modifierPressed(){
		return (metaPressed|ctrlPressed|altPressed|shftPressed);
	}
	
	
	
	
	
}
