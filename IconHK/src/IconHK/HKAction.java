package IconHK;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class HKAction extends AbstractAction {
    public HKAction(String s){
        this.putValue(Action.NAME,s);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(this.getValue(Action.NAME));
    }

}
