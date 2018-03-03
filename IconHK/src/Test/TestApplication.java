package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class TestApplication {
    public static void main(String[] args) {

        JDialog dialog = new JDialog();
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        JTextField text = new JTextField(4);
        text.setBounds(10, 10, 20, 20);
        int defaultValue = 50;
        JSlider slider = new JSlider(0,100,defaultValue);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> text.setText(String.valueOf(slider.getValue())));
        text.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent ke) {
                String typed = text.getText();
                slider.setValue(0);
                if(!typed.matches("\\d+") || typed.length() > 3) {
                    return;
                }
                int value = Integer.parseInt(typed);
                slider.setValue(value);
            }
        });
        text.setText(Integer.toString(defaultValue));
        panel.setBorder(BorderFactory.createTitledBorder("Button's dimension"));
        panel.add(slider);
        panel.add(text);
        JButton validate = new JButton("Validate");
        validate.addActionListener(e -> {
            dialog.setVisible(false);
            SimpleEditor.setDim(new Dimension(slider.getValue(),slider.getValue()));
            SimpleEditor editor = new SimpleEditor();
        });
        panel.add(validate);
        dialog.add(panel);
        //TODO ask how to use invoke later
        dialog.setSize(300, 100);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        //SimpleEditor.setDim(new Dimension(50,50));
        //SimpleEditor editor = new SimpleEditor();
    }
}
