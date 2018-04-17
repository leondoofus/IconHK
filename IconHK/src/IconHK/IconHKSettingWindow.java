package IconHK;

import IconHK.util.RangeSlider;
import IconHK.util.Image;
import IconHK.util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;

public class IconHKSettingWindow extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private Vector<HKButton> iconHKButtons;

    public static int speedUp = 50;
    public static int speedDown = 50;

    private JButton resetRadius, resetColor, resetRange;
    private ArrayList<JSlider> radius;
    private ArrayList<JButton> colors;
    private ArrayList<RangeSlider> ranges;
    private Timer timer;

    private RangeSlider rangeAll;
    private JSlider radiusAll;
    private JButton colorAll;

    public IconHKSettingWindow(Vector<HKButton> iconHKButtons) {
        this.iconHKButtons = iconHKButtons;
        radius = new ArrayList<>();
        colors = new ArrayList<>();
        ranges = new ArrayList<>();
        build();
        ActionListener listener = e -> {
            if (e.getSource() == timer) {
                for (int i = 0; i < iconHKButtons.size(); i++){
                    ranges.get(i).setValue(iconHKButtons.get(i).getDefaultFrame());
                    ranges.get(i).setUpperValue(iconHKButtons.get(i).getVMax());
                }
            }
        };
        timer = new Timer(50,listener);
        timer.start();
    }

    private void build(){
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0,1));

        JPanel panel = new JPanel(new SpringLayout());

        int rows = this.iconHKButtons.size() + 4;
        int cols = 6;

        for(HKButton button : this.iconHKButtons){
            panel.add(new JLabel(button.getName()),BorderLayout.WEST);

            class Icon extends JButton{
                private BufferedImage image;
                private Icon(BufferedImage image) {
                    super();
                    this.image = image;
                }

                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    BufferedImage icon = Image.resize(image,18,18);
                    int x = (getWidth() - icon.getWidth()) / 2;
                    int y = (getHeight() - icon.getHeight()) / 2;
                    g2.drawImage(icon, x, y, this);
                }
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(20,20);
                }

                @Override
                public Dimension getMinimumSize() {
                    return new Dimension(20,20);
                }

                @Override
                public Dimension getMaximumSize() {
                    return new Dimension(20,20);
                }
                private void setImage (BufferedImage image){
                    this.image = image;
                }
            }
            Icon deb = new Icon(button.getImage(button.getDefaultFrame()));
            Icon fin = new Icon(button.getImage(button.getVMax()));
            deb.setBorder(BorderFactory.createEmptyBorder());
            fin.setBorder(BorderFactory.createEmptyBorder());
            deb.setEnabled(false);
            fin.setEnabled(false);

            RangeSlider range = new RangeSlider();
            range.setPreferredSize(new Dimension(240, range.getPreferredSize().height));
            range.setMinimum(0);
            range.setMaximum(button.getVmaxDef());
            range.setValue(button.getDefaultFrame());
            range.setUpperValue(button.getVMax());
            range.addChangeListener(e -> {
                button.setDefaultFrame(range.getValue());
                button.setVMax(range.getUpperValue());
                deb.setImage(button.getImage(range.getValue()));
                fin.setImage(button.getImage(range.getUpperValue()));
                deb.repaint();
                fin.repaint();
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
                    Color all = iconHKButtons.get(0).getPressedColor();
                    boolean sameColor = true;
                    for (HKButton b : iconHKButtons) {
                        if (!b.getPressedColor().equals(all)) {
                            sameColor = false;
                            break;
                        }
                    }
                    if (sameColor) {
                        colorAll.setBackground(all);
                        colorAll.setText("");
                    } else{
                        colorAll.setBackground(null);
                        colorAll.setText("Change modifiers' color");
                    }
                }
            });

            ranges.add(range);
            radius.add(r);
            colors.add(c);

            panel.add(deb);
            panel.add(range);
            panel.add(fin);
            panel.add(r);
            panel.add(c);
        }

        panel.add(new JLabel("All"),BorderLayout.WEST);
        rangeAll = new RangeSlider();
        rangeAll.setPreferredSize(new Dimension(240, rangeAll.getPreferredSize().height));
        rangeAll.setMinimum(0);
        rangeAll.setMaximum(10);
        rangeAll.setValue(0);
        rangeAll.setUpperValue(10);
        rangeAll.addChangeListener(e -> {
            for (int i = 0; i < iconHKButtons.size(); i++){
                iconHKButtons.get(i).setDefaultFrame((int)((double)rangeAll.getValue()*iconHKButtons.get(i).getVmaxDef()/10));
                iconHKButtons.get(i).setVMax((int)((double)rangeAll.getUpperValue()*iconHKButtons.get(i).getVmaxDef()/10));
                ranges.get(i).setValue((int)((double)rangeAll.getValue()*iconHKButtons.get(i).getVmaxDef()/10));
                ranges.get(i).setUpperValue((int)((double)rangeAll.getUpperValue()*iconHKButtons.get(i).getVmaxDef()/10));
            }
        });
        float r = iconHKButtons.get(0).getModifierRadiusRatio();
        boolean sameRadius = true;
        for (HKButton button : iconHKButtons){
            if (button.getModifierRadiusRatio() != r){
                sameRadius = false;
                break;
            }
        }
        radiusAll = new JSlider(JSlider.HORIZONTAL, 0, 50, sameRadius? (int)(r*100) : 35);
        radiusAll.addChangeListener(e -> {
            for(JSlider s : radius)
                s.setValue(radiusAll.getValue());
        });
        colorAll = new JButton();
        Color all = iconHKButtons.get(0).getPressedColor();
        boolean sameColor = true;
        for (HKButton button : iconHKButtons) {
            if (!button.getPressedColor().equals(all)) {
                sameColor = false;
                break;
            }
        }
        if (sameColor)
            colorAll.setBackground(all);
        else
            colorAll.setText("Change modifiers' color");
        colorAll.addChangeListener(e -> {
            Color newColor = JColorChooser.showDialog(colorAll, "Choose Modifier Color", colorAll.getBackground());
            if(newColor != null) {
                colorAll.setBackground(newColor);
                colorAll.setText("");
                for (JButton b : colors)
                    b.setBackground(newColor);
                for (HKButton button : iconHKButtons)
                    button.setPressedColor(newColor);
            }
        });
        panel.add(new JLabel());
        panel.add(rangeAll);
        panel.add(new JLabel());
        panel.add(radiusAll);
        panel.add(colorAll);

        panel.add(new JLabel());
        resetRadius = new JButton("Reset Radius");
        resetRadius.addActionListener(this);
        resetColor = new JButton("Reset Colors");
        resetColor.addActionListener(this);
        resetRange = new JButton("Reset Ranges");
        resetRange.addActionListener(this);
        panel.add(new JLabel());
        panel.add(resetRange);
        panel.add(new JLabel());
        panel.add(resetRadius);
        panel.add(resetColor);


        JCheckBox cb = new JCheckBox(HKButton.lockHotkey? "Hotkey locked" : "Hotkey unlocked");
        cb.setSelected(HKButton.lockHotkey);
        cb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED){
                HKButton.lockHotkey = true;
                cb.setText("Hotkey locked");
            } else {
                HKButton.lockHotkey = false;
                cb.setText("Hotkey unlocked");
            }});
        JSpinner spinner1 = new JSpinner(new SpinnerNumberModel(HKButton.rangeInf,1,5,1));
        JLabel click1 = new JLabel("click to change lower bound");
        spinner1.addChangeListener(e -> {
            HKButton.rangeInf = (int) spinner1.getValue();
            click1.setText(HKButton.rangeInf > 1? "clicks to change lower bound":"click to change lower bound");
        });
        JSpinner spinner2 = new JSpinner(new SpinnerNumberModel(speedUp,50,200,25));
        spinner2.addChangeListener(e -> {
            speedUp = ((int) spinner2.getValue());
        });

        JSpinner spinner3 = new JSpinner(new SpinnerNumberModel(speedDown,50,200,25));
        spinner3.addChangeListener(e -> {
            speedDown = ((int) spinner3.getValue());
        });

        panel.add(new JLabel("Other options"));
        panel.add(spinner1);
        panel.add(click1);
        panel.add(spinner2);
        panel.add(new JLabel("ms/increasing frame"));
        panel.add(cb);

        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(spinner3);
        panel.add(new JLabel("ms/decreasing frame"));
        panel.add(new JLabel());


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
            for (int i = 0; i < iconHKButtons.size(); i++){
                iconHKButtons.get(i).setDefaultFrame(0);
                iconHKButtons.get(i).setVMax(iconHKButtons.get(i).getVmaxDef());
                ranges.get(i).setValue(0);
                ranges.get(i).setUpperValue(iconHKButtons.get(i).getVmaxDef());
            }

            rangeAll.setValue(0);
            rangeAll.setUpperValue(10);
        } else if(e.getSource() == resetRadius){
            for(HKButton button : this.iconHKButtons)
                button.setModifierRadiusRatio((float)0.35);
            for (JSlider s : radius)
                s.setValue(35);
            radiusAll.setValue(35);
        } else if(e.getSource() == resetColor){
            for(HKButton button : this.iconHKButtons)
                button.setPressedColor(new Color(29,174,222));
            for (JButton b : colors)
                b.setBackground(new Color(29,174,222));
            colorAll.setBackground(new Color(29,174,222));
            colorAll.setText("");
        }
    }

}
