package IconHK;

import com.sun.scenario.effect.impl.sw.java.JSWLinearConvolvePeer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

public class IconHKSettingWindow extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private Vector<HKButton> iconHKButtons;

    private JButton maxall, minall,resetRadius;
    private ArrayList<JSlider> mins;
    private ArrayList<JSlider> maxs;
    private ArrayList<JSlider> radius;

    public IconHKSettingWindow(Vector<HKButton> iconHKButtons) throws HeadlessException {
        this.iconHKButtons = iconHKButtons;
        mins = new ArrayList<>();
        maxs = new ArrayList<>();
        radius = new ArrayList<>();
        build();
    }

    /*private void build(){
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
    }*/

    private void build(){
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0,1));

        JPanel panel = new JPanel(new SpringLayout());

        int rows = this.iconHKButtons.size()+1;
        int cols = 4;
        for(HKButton button : this.iconHKButtons){
            panel.add(new JLabel(button.getName()),BorderLayout.WEST);
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

            //System.out.println((int)(button.getModifierRadiusRatio()*100));

            JSlider r = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(button.getModifierRadiusRatio()*100));
            r.addChangeListener(e -> {
                float value = ((float)r.getValue())/100;
                button.setModifierRadiusRatio(value);
                button.repaint();
            });

            mins.add(minSlider);
            maxs.add(maxSlider);
            radius.add(r);

            panel.add(minSlider);
            panel.add(maxSlider);
            panel.add(r);
        }

        panel.add(new JLabel("All"),BorderLayout.WEST);
        maxall = new JButton("Default transition image");
        maxall.addActionListener(this);
        minall = new JButton("Default image");
        minall.addActionListener(this);
        resetRadius = new JButton("Reset Radius");
        resetRadius.addActionListener(this);
        panel.add(minall);
        panel.add(maxall);
        panel.add(resetRadius);


        //Lay out the panel.
        SpringUtilities.makeCompactGrid(panel, //parent
                rows, cols,
                3, 3,  //initX, initY
                5, 5); //xPad, yPad

        centerPanel.add(panel);

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
