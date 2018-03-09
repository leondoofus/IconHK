package IconHK;

import IconHK.rangeslider.RangeSlider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

public class IconHKSettingWindow extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private Vector<HKButton> iconHKButtons;

    private JButton resetRadius, resetColor, resetRange;
    private ArrayList<JSlider> radius;
    private ArrayList<JButton> colors;
    private ArrayList<RangeSlider> ranges;

    public IconHKSettingWindow(Vector<HKButton> iconHKButtons) throws HeadlessException {
        this.iconHKButtons = iconHKButtons;
        radius = new ArrayList<>();
        colors = new ArrayList<>();
        ranges = new ArrayList<>();
        build();
    }

    private void build(){
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0,1));

        JPanel panel = new JPanel(new SpringLayout());

        int rows = this.iconHKButtons.size()+1;
        int cols = 4;
        for(HKButton button : this.iconHKButtons){
            panel.add(new JLabel(button.getName()),BorderLayout.WEST);
            RangeSlider range = new RangeSlider();
            range.setPreferredSize(new Dimension(240, range.getPreferredSize().height));
            range.setMinimum(0);
            range.setMaximum(20);
            range.setValue(button.getDefaultFrame());
            range.setUpperValue(button.getVMax());
            range.addChangeListener(e -> {
                button.setDefaultFrame(range.getValue());
                button.setVMax(range.getUpperValue());
            });

            JSlider r = new JSlider(JSlider.HORIZONTAL, 0, 50, (int)(button.getModifierRadiusRatio()*100));
            r.addChangeListener(e -> {
                float value = ((float)r.getValue())/100;
                button.setModifierRadiusRatio(value);
                button.repaint();
            });

            JButton c = new JButton();
            c.setBackground(button.getPressedColor());
            c.addChangeListener(e -> {
                Color newColor = JColorChooser.showDialog(c, "Choose Modifier Color", button.getPressedColor());
                if(newColor != null) {
                    c.setBackground(newColor);
                    button.setPressedColor(newColor);
                }
            });

            ranges.add(range);
            radius.add(r);
            colors.add(c);

            panel.add(range);
            panel.add(r);
            panel.add(c);
        }

        panel.add(new JLabel("All"),BorderLayout.WEST);
        resetRadius = new JButton("Reset Radius");
        resetRadius.addActionListener(this);
        resetColor = new JButton("Reset Color");
        resetColor.addActionListener(this);
        resetRange = new JButton("Reset ranges");
        resetRange.addActionListener(this);
        panel.add(resetRange);
        panel.add(resetRadius);
        panel.add(resetColor);


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
        if(e.getSource() == resetRange){
            for(HKButton button : this.iconHKButtons) {
                button.setDefaultFrame(0);
                button.setVMax(20);
                for (RangeSlider s : ranges) {
                    s.setValue(0);
                    s.setUpperValue(20);
                }
            }
        } else if(e.getSource() == resetRadius){
            for(HKButton button : this.iconHKButtons) {
                button.setModifierRadiusRatio((float)0.35);
                for (JSlider s : radius)
                    s.setValue((int) (button.getModifierRadiusRatio() * 100));
            }
        } else if(e.getSource() == resetColor){
            for(HKButton button : this.iconHKButtons) {
                button.setPressedColor(new Color(29,174,222));
                for (JButton b : colors)
                    b.setBackground(button.getPressedColor());
            }
        }

    }
}
