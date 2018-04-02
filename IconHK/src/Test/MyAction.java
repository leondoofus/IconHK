package Test;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MyAction extends AbstractAction {


	public MyAction(String text, Character hotkey) {
		super(text);
		KeyStroke ks = KeyStroke.getKeyStroke(hotkey, 0);
		
		int keyCode = KeyEvent.getExtendedKeyCodeForChar(hotkey);
		
		System.out.println(ks.getKeyCode()+ " - "+ keyCode);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyCode,KeyEvent.CTRL_MASK));

	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().getClass().equals(JButton.class)) {
			System.out.println("From button");
		}
		else if(e.getSource().getClass().equals(JMenuItem.class)) {
			if(e.getModifiers() == ActionEvent.CTRL_MASK) {
				System.out.println("Likely hotkey");
			}
			else {
				System.out.println("From menuItem");
			}
		}
		
		
	}
}
