package Test;

import IconHK.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class SimpleEditor extends JFrame implements ActionListener, KeyEventDispatcher {

    private static final Dimension dim = new Dimension(50,50);
    private static final int persistence = 20;

    // Text Editor
    private JTextArea textComp;

    // Tool bar
    private JToolBar toolbar;

    private Timer timer;

    //Actions
    private Action cutAction, pasteAction, copyAction, saveAction, newAction, expandAction, rgbAction;
    private Action findAction, moveAction, pencilAction, increaseAction;


    private Vector<IconAnimation> animations;
    private Vector<HKButton> iconHKButtons;

    public SimpleEditor (){
        super("Swing Editor");
        animations = new Vector<IconAnimation>();
        iconHKButtons = new Vector<HKButton>();
        timer = new Timer(50, this);
        timer.start();

        textComp = new JTextArea();
        textComp.setLineWrap(true);
        createActions();

        // Start adding buttons to toolbar
        this.toolbar = new JToolBar();
        addButtonToToolBar(new HKButton(newAction,dim));
        addButtonToToolBar(new HKButton(saveAction,dim));

        Container content = getContentPane();
        content.add(textComp, BorderLayout.CENTER);
        content.add(this.toolbar, BorderLayout.NORTH);
        //this.setJMenuBar(new JMenuBar());
        //this.setJMenuBar(createMenuBar());
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add mouse and keyboard events
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventDispatcher(this);

        //animateall();
    }

    private void addButtonToToolBar(JButton button) {
        button.addActionListener(this);
        toolbar.add(Box.createRigidArea(new Dimension(2,0)));
        toolbar.add(button);
        toolbar.add(Box.createRigidArea(new Dimension(2,0)));
        if(button.getClass() == HKButton.class){
            iconHKButtons.add((HKButton)button);
        }
    }

    /*
    private void animateall(){
        for (Component c : toolbar.getComponents()) {
            if (c.getClass() == HKButton.class) {
                HKButton but = (HKButton) c;
                int[] sequence = { IconAnimation.HOTKEY_STEP, IconAnimation.DEFAULT_STEP };
                animations.add(new IconAnimation(but, sequence, 40));
            }
        }
    }*/

    protected void createActions() {
        cutAction = new HKAction("Cut");
        cutAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_X, ActionEvent.CTRL_MASK));

        pasteAction = new HKAction("Paste");
        pasteAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_V, ActionEvent.CTRL_MASK));

        copyAction = new HKAction("Copy");
        copyAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.CTRL_MASK));

        saveAction = new HKAction("Save");
        saveAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        newAction = new HKAction("New");
        newAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.CTRL_MASK));

        expandAction = new HKAction("Expand");
        expandAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.CTRL_MASK));

        rgbAction = new HKAction("RGB");
        rgbAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.CTRL_MASK|ActionEvent.ALT_MASK));

        findAction = new HKAction("Find");
        findAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

        moveAction = new HKAction("Move");
        moveAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        pencilAction = new HKAction("Pencil");
        pencilAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_W, ActionEvent.CTRL_MASK));

        increaseAction = new HKAction("Increase");
        increaseAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_H, ActionEvent.CTRL_MASK));

    }

    /*private void animateToolbar() {

        if (animations.size() > 0) {
            Iterator<IconAnimation> i = animations.iterator();
            while (i.hasNext()) {
                IconAnimation animation = i.next();
                int objective = animation.getCurrentObjective();
                boolean over = false;
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
    }*/

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            switch(e.getKeyCode()){
                case KeyEvent.VK_META:
                    for (HKButton b : iconHKButtons)
                        b.setMetaPressed(true);
                    break;
                case KeyEvent.VK_CONTROL:
                    for (HKButton b : iconHKButtons)
                        b.setCtrlPressed(true);
                    break;
                case KeyEvent.VK_ALT:
                    for (HKButton b : iconHKButtons)
                        b.setAltPressed(true);
                    break;
                case KeyEvent.VK_SHIFT:
                    for (HKButton b : iconHKButtons)
                        b.setShftPressed(true);
                    break;
            }
        }
        else if (e.getID() == KeyEvent.KEY_RELEASED) {
            switch(e.getKeyCode()){
                case KeyEvent.VK_META:
                    for (HKButton b : iconHKButtons)
                        b.setMetaPressed(false);
                    break;
                case KeyEvent.VK_CONTROL:
                    for (HKButton b : iconHKButtons)
                        b.setCtrlPressed(false);
                    break;
                case KeyEvent.VK_ALT:
                    for (HKButton b : iconHKButtons)
                        b.setAltPressed(false);
                    break;
                case KeyEvent.VK_SHIFT:
                    for (HKButton b : iconHKButtons)
                        b.setShftPressed(false);
                    break;
            }
        }
        repaint();
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*if (e.getSource() == timer) {
            //animateToolbar();
            repaint();
        } else {

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
