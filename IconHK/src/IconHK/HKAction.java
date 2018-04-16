package IconHK;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class HKAction extends AbstractAction {
    private int mask = 0;
    private HKButton button;
    private Runnable runnable = null;

    public HKAction(String s){
        this.putValue(Action.NAME,s);
    }

    public HKAction(String s, Runnable r){
        this.putValue(Action.NAME,s);
        this.runnable = r;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (runnable != null) runnable.run();
        //System.out.println(this.getValue(Action.NAME));
        if(e.getSource().getClass().equals(HKButton.class)) {
            button.increaseClick();
        } else if(e.getSource().getClass().equals(JMenuItem.class)) {
            if(e.getModifiers() == this.mask) {
                //System.out.println("Likely hotkey");
                button.setDefaultFrame(button.getDefaultFrame() - 1);
            } else {
                //System.out.println("From menuItem");
                button.increaseClick();
                button.setMousePressed(true);
                button.increaseCurrentFrame();
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
