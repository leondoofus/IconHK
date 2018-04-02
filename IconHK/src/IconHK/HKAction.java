package IconHK;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class HKAction extends AbstractAction {
    private int mask = 0;
    public static int range = 1;
    private int click = 0;
    private HKButton button;

    public HKAction(String s){
        this.putValue(Action.NAME,s);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(this.getValue(Action.NAME));
        //System.out.println(this.getValue(AbstractAction.ACCELERATOR_KEY));
        if(e.getSource().getClass().equals(HKButton.class)) {
            System.out.println("From button");
            click++;
            if (click >= range) {
                click = 0;
                button.setDefaultFrame(button.getDefaultFrame() + 1);
            }
        } else if(e.getSource().getClass().equals(JMenuItem.class)) {
            System.out.println(this.mask);
            if(e.getModifiers() == this.mask) {
                System.out.println("Likely hotkey");
                button.setVMax(button.getVMax() - 1);
            }
            else {
                System.out.println("From menuItem");
                /*click++;
                if (click >= range) {
                    click = 0;
                    button.setDefaultFrame(button.getDefaultFrame() + 1);
                }*/
            }
        }
    }

    public void setButton(HKButton button) {
        this.button = button;
        if (button.isUseAlt()) mask += ActionEvent.ALT_MASK;
        if (button.isUseCtrl()) mask += ActionEvent.CTRL_MASK;
        if (button.isUseMeta()) mask += ActionEvent.META_MASK;
        if (button.isUseSft()) mask += ActionEvent.SHIFT_MASK;
    }
}
