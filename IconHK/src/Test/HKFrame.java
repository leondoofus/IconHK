package Test;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

public class HKFrame extends JFrame {
	Character[] keys = {'J','K','L','M'};
	
	public HKFrame() {
		this.setSize(800,600);
		JMenuBar menuBar = new JMenuBar();
		
		JMenu menu = new JMenu("MyMenu");
		menuBar.add(menu);

		JToolBar toolBar = new JToolBar("");
		this.addButtons(toolBar,menu);
		this.add(toolBar, BorderLayout.PAGE_START);
		
		this.setJMenuBar(menuBar);
		
		
		
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}




	protected void addButtons(JToolBar toolBar, JMenu menu) {
		JButton button = null;
		JMenuItem item = null;

		for (int i=0; i<4; i++) {
			MyAction act = new MyAction("Ctrl "+keys[i], keys[i]);
			item = new JMenuItem();
			item.setAction(act);
			menu.add(item);
			button = new JButton();
			button.setAction(act);
			toolBar.add(button);
			
		}

		

	}



	public static void main(String[] args) {
		HKFrame fr = new HKFrame();
	}

}
