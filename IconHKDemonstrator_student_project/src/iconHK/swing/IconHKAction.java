package iconHK.swing;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class IconHKAction extends AbstractAction {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IconHKAction(String s){
		this.putValue(Action.NAME,s);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(this.getValue(Action.NAME));
	}
	

}
