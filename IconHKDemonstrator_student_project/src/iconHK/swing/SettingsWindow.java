package iconHK.swing;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import iconHK.swing.widgets.IconHKButton;

public class SettingsWindow extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<IconHKButton> iconHKButtons = null;
	
	JButton max4,min4;
	
	public SettingsWindow (Vector<IconHKButton> buttons){
		this.iconHKButtons = buttons;
		build();
	}
	
	private void build(){
		
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(0,1));
		
		
		
		
		for(IconHKButton button : this.iconHKButtons){

			JPanel main = new JPanel(new BorderLayout());
			JPanel center = new JPanel(new GridLayout(1,2));
			main.add(new JLabel(button.getName()),BorderLayout.WEST);
			int min = button.getDefaultFrame();
			int max = button.getVMax();

			JSlider minSlider = new JSlider(JSlider.HORIZONTAL, 0, max, min);
			// Turn on labels at major tick marks.
			minSlider.setMinorTickSpacing(1);
			minSlider.setPaintTicks(true);

			JSlider maxSlider = new JSlider(JSlider.HORIZONTAL, min, button.getNumberOfIcons()-1, max);
			maxSlider.setMinorTickSpacing(1);
			maxSlider.setPaintTicks(true);
			
			minSlider.addChangeListener(new ChangeListener(){

				public void stateChanged(ChangeEvent e) {
					int value = minSlider.getValue();
					button.setDefaultFrame(value);
					button.repaint();
					maxSlider.setMinimum(value);
				}
				
			});

			maxSlider.addChangeListener(new ChangeListener(){

				public void stateChanged(ChangeEvent e) {
					int value = maxSlider.getValue();
					button.setVMax(value);
					button.repaint();
				}
				
			});
			
			
			
			
			
			
			
			
			center.add(minSlider);
			center.add(maxSlider);
			main.add(center);
			centerPanel.add(main);
		}
		
		JPanel main = new JPanel(new BorderLayout());
		JPanel center = new JPanel(new GridLayout(1,2));
		String all = " All ";
		main.add(new JLabel(all),BorderLayout.WEST);
		max4 = new JButton("max for "+all);
		max4.addActionListener(this);
		min4 = new JButton("min for "+all);
		min4.addActionListener(this);
		center.add(min4);
		center.add(max4);
		main.add(center);
		centerPanel.add(main);
		
		this.add(centerPanel);
		
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==min4){
			
		}
		else if(e.getSource()==max4){
			
		}
		
	}
	

}
