package IconHK;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

public class IconHKSettingWindow extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private Vector<HKButton> iconHKButtons;

    JButton maxall, minall;
    ArrayList<JSlider> mins;
    ArrayList<JSlider> maxs;

    public IconHKSettingWindow(Vector<HKButton> iconHKButtons) throws HeadlessException {
        this.iconHKButtons = iconHKButtons;
        mins = new ArrayList<>();
        maxs = new ArrayList<>();
        build();
    }

    private void build(){
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0,1));

        for(HKButton button : this.iconHKButtons){

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

            minSlider.addChangeListener(e -> {
                int value = minSlider.getValue();
                button.setDefaultFrame(value);
                button.repaint();
                maxSlider.setMinimum(value);
            });

            maxSlider.addChangeListener(e -> {
                int value = maxSlider.getValue();
                button.setVMax(value);
                button.repaint();
            });
            mins.add(minSlider);
            maxs.add(maxSlider);
            center.add(minSlider);
            center.add(maxSlider);
            main.add(center);
            centerPanel.add(main);
        }

        JPanel main = new JPanel(new BorderLayout());
        JPanel center = new JPanel(new GridLayout(1,2));

        main.add(new JLabel("All"),BorderLayout.WEST);
        maxall = new JButton("Max for all");
        maxall.addActionListener(this);
        minall = new JButton("Min for all");
        minall.addActionListener(this);
        center.add(minall);
        center.add(maxall);
        main.add(center);
        centerPanel.add(main);

        this.add(centerPanel);

        this.pack();
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()== minall){
            for(HKButton button : this.iconHKButtons) {
                button.setDefaultFrame(0);
                for (JSlider s : mins)
                    s.setValue(0);
            }
        }
        else if(e.getSource()== maxall){
            for(HKButton button : this.iconHKButtons) {
                button.setVMaxDefault();
                for (JSlider s : maxs)
                    s.setValue(button.getVMax());
            }

        }

    }
}
