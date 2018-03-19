package IconHK;

import IconHK.rangeslider.RangeSlider;
import IconHK.util.Image;
import IconHK.util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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

        int rows = this.iconHKButtons.size()+2;
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

        panel.add(new JLabel("Other options"));
        panel.add(new JLabel());
        JCheckBox cb1 = new JCheckBox("Lock hotkey");
        cb1.setSelected(HKButton.lockHotkey);
        cb1.addItemListener(e -> {HKButton.lockHotkey = e.getStateChange() == 1; System.out.println(HKButton.lockHotkey);});
        panel.add(cb1);
        panel.add(new JLabel());
        JCheckBox cb2 = new JCheckBox("Lock click");
        cb2.setSelected(HKButton.lockClick);
        cb2.addItemListener(e -> {HKButton.lockClick = e.getStateChange() == 1; System.out.println(HKButton.lockClick);});
        panel.add(cb2);
        JCheckBox cb3 = new JCheckBox("Learn to use HK");
        cb3.setSelected(HKButton.animateOnce);
        cb3.addItemListener(e -> {HKButton.animateOnce = e.getStateChange() == 1; System.out.println(HKButton.animateOnce);});
        panel.add(cb3);


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
                button.setVMax(button.getVmaxDef());
                for (RangeSlider s : ranges) {
                    s.setValue(0);
                    s.setUpperValue(button.getVmaxDef());
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
