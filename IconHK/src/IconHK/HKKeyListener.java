package IconHK;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Vector;

public class HKKeyListener  implements ActionListener, KeyEventDispatcher {
    JToolBar toolbar;
    Vector<HKButton> iconHKButtons;
    private static Timer timer;
    private Vector<IconAnimation> animations;

    // Boolean testing if iconHKButtons pressed
    private static boolean metaPressed = false;
    private static boolean altPressed = false;
    private static boolean ctrlPressed = false;
    private static boolean shftPressed = false;

    public HKKeyListener(Vector<HKButton> iconHKButtons, JToolBar toolbar) {
        timer = new Timer(50, this);
        timer.start();
        this.iconHKButtons = iconHKButtons;
        this.toolbar = toolbar;
        animations = new Vector<>();
    }

    public static void setTimer(int val){
        timer.setDelay(val);
        timer.restart();
    }

    public static int getTimer (){
        return timer.getDelay();
    }

    public void animateToolbar() {
        //if (animations.size() < iconHKButtons.size()) return;
        if (animations.size() > 0) {
            Iterator<IconAnimation> i = animations.iterator();
            while (i.hasNext()) {
                IconAnimation animation = i.next();
                int objective = animation.getCurrentObjective();
                boolean over;
                switch (objective) {
                    case IconAnimation.HOTKEY_STEP:
                        over = animation.getButton().animateToDestination(IconAnimation.HOTKEY_STEP);
                        animation.getButton().repaint();
                        if (over) {
                            if(animation.hasToPersist()){
                                animation.increaseCurrentPersistency();
                            }
                            else {
                                animation.nextAnimationStep();
                            }
                        }
                        break;
                    case IconAnimation.ICON_STEP:
                        over = animation.getButton().animateToDestination(IconAnimation.ICON_STEP);
                        animation.getButton().repaint();
                        if (over) {
                            animation.nextAnimationStep();
                        }
                        break;
                    case IconAnimation.DEFAULT_STEP:
                        over = animation.getButton().animateToDestination(IconAnimation.DEFAULT_STEP);
                        animation.getButton().repaint();
                        if (over) {
                            animation.nextAnimationStep();
                        }
                        break;
                    case IconAnimation.OVER_STEP:
                        i.remove();
                        break;
                }
            }
        }
    }

    public void animateall(){
        animations.removeAllElements();
        for (Component c : toolbar.getComponents()) {
            if (c.getClass() == HKButton.class) {
                HKButton button = (HKButton) c;
                int[] sequence = { IconAnimation.HOTKEY_STEP, IconAnimation.DEFAULT_STEP };
                animations.add(new IconAnimation(button, sequence, 40));
            }
        }
    }

    private void animateActivated(){
        animations.removeAllElements();
        for (Component c : toolbar.getComponents()) {
            if (c.getClass() == HKButton.class) {
                HKButton button = (HKButton) c;
                if (((HKButton) c).isActived()) {
                    int[] sequence = {IconAnimation.HOTKEY_STEP, IconAnimation.DEFAULT_STEP};
                    animations.add(new IconAnimation(button, sequence, 40));
                }
            }
        }
    }

    private void animateHotkeyPressed() {
        if (HKButton.lockHotkey){
            if (animations.size() > 0) {
                for (IconAnimation animation : animations) {
                    animation.getButton().increaseCurrentFrame();
                    animation.getButton().repaint();
                }
            }
        } else {
            if (animations.size() > 0) {
                for (IconAnimation animation : animations) {
                    animation.getButton().changeCurrentFrame();
                    animation.getButton().repaint();
                }
            }
        }
    }

    private void desanimateAll(){
        if (HKButton.lockHotkey){
            animations.clear();
            for (HKButton button : iconHKButtons)
                if (!button.getMousePressed())
                    animations.add(new IconAnimation(button,new int[]{IconAnimation.DEFAULT_STEP},40));
            for (IconAnimation animation : animations) {
                animation.getButton().decreaseCurrentFrame();
                animation.getButton().repaint();
            }
        }
    }


    private void setAllModifiers (HKButton b){
        if (ctrlPressed)
            if (!b.isUseCtrl())
                deactivateAllModifiers(b);
        if (altPressed)
            if (!b.isUseAlt())
                deactivateAllModifiers(b);
        if (shftPressed)
            if (!b.isUseSft())
                deactivateAllModifiers(b);
        if (metaPressed)
            if (!b.isUseMeta())
                deactivateAllModifiers(b);
    }

    private void updateModifiers (HKButton b){
        b.setHotkeyUsed(true);
        b.setCtrlPressed(ctrlPressed);
        b.setAltPressed(altPressed);
        b.setShftPressed(shftPressed);
        b.setMetaPressed(metaPressed);
    }

    private void deactivateAllModifiers(HKButton b){
        b.setHotkeyUsed(false);
        b.setCtrlPressed(false);
        b.setAltPressed(false);
        b.setShftPressed(false);
        b.setMetaPressed(false);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_META:
                    for (HKButton b : iconHKButtons) {
                        metaPressed = true;
                        updateModifiers(b);
                        setAllModifiers(b);
                    }
                    break;
                case KeyEvent.VK_CONTROL:
                    for (HKButton b : iconHKButtons) {
                        ctrlPressed = true;
                        updateModifiers(b);
                        setAllModifiers(b);
                    }
                    break;
                case KeyEvent.VK_ALT:
                    for (HKButton b : iconHKButtons) {
                        altPressed = true;
                        updateModifiers(b);
                        setAllModifiers(b);
                    }
                    break;
                case KeyEvent.VK_SHIFT:
                    for (HKButton b : iconHKButtons) {
                        shftPressed = true;
                        updateModifiers(b);
                        setAllModifiers(b);
                    }
                    break;
            }
            animateActivated();
        } else if (e.getID() == KeyEvent.KEY_RELEASED) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_META:
                    for (HKButton b : iconHKButtons) {
                        metaPressed = false;
                        updateModifiers(b);
                        setAllModifiers(b);
                    }
                    break;
                case KeyEvent.VK_CONTROL:
                    for (HKButton b : iconHKButtons) {
                        ctrlPressed = false;
                        updateModifiers(b);
                        setAllModifiers(b);
                    }
                    break;
                case KeyEvent.VK_ALT:
                    for (HKButton b : iconHKButtons) {
                        altPressed = false;
                        updateModifiers(b);
                        setAllModifiers(b);
                    }
                    break;
                case KeyEvent.VK_SHIFT:
                    for (HKButton b : iconHKButtons) {
                        shftPressed = false;
                        updateModifiers(b);
                        setAllModifiers(b);
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            if (ctrlPressed || altPressed || metaPressed || shftPressed)
                animateHotkeyPressed();
            else{
                if (HKButton.lockHotkey) desanimateAll();
                else animateToolbar();
            }
            toolbar.getParent().repaint();
            //repaint();
        }/* else {

            String text = null;
            int modality=0;
            if (e.getSource().getClass() == HKButton.class) {
                HKButton but = (HKButton) e.getSource();
                text = but.getName();

                int[] sequence = { IconAnimation.HOTKEY_STEP, IconAnimation.DEFAULT_STEP };
                animations.add(new IconAnimation(but, sequence, persistence));
                //modality = CommandSelection.TOOLBAR_BUTTON;
            } else if (e.getSource().getClass() == JMenuItem.class) {
                JMenuItem item = (JMenuItem) e.getSource();
                text = item.getText();
                //modality = CommandSelection.MENU_ITEM;
                for (Component c : toolbar.getComponents()) {

                    if (c.getClass() == HKButton.class) {
                        HKButton but = (HKButton) c;
                        if (but.getName().equals(text)) {
                            int[] sequence = { IconAnimation.HOTKEY_STEP, IconAnimation.DEFAULT_STEP };
                            animations.add(new IconAnimation(but, sequence, persistence));
                        }
                    }
                }
            }

            if(e.getModifiers() == InputEvent.CTRL_MASK(e)){
                modality = CommandSelection.HOTKEY;
            }
            if((text!=null)&&(modality!=0)){
                history.addCommandSelection(new CommandSelection(text,modality));
                updateWidgetForCommand(text,modality);
            }
        }*/
    }
}
