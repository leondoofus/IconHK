package Test;

import IconHK.HKAction;
import IconHK.HKButton;
import IconHK.IconAnimation;
import IconHK.IconHKSettingWindow;
import IconHK.util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Vector;

public class TestSuperposition extends JFrame implements ActionListener, KeyEventDispatcher {
    private static Dimension dim = new Dimension(50,50);

    // Text Editor
    private JTextArea textComp;

    // Tool bar
    private JToolBar toolbar;

    private Timer timer;

    //Actions
    private Action mushroomAction;

    // Boolean testing if buttons pressed
    private static boolean metaPressed = false;
    private static boolean altPressed = false;
    private static boolean ctrlPressed = false;
    private static boolean shftPressed = false;


    private Vector<IconAnimation> animations;
    private Vector<HKButton> iconHKButtons;

    public TestSuperposition (){
        super("Swing Editor");

        animations = new Vector<>();
        iconHKButtons = new Vector<>();
        timer = new Timer(50, this);
        timer.start();

        textComp = new JTextArea();
        textComp.setLineWrap(true);
        createActions();

        // Start adding buttons to toolbar
        this.toolbar = new JToolBar();
        addAllButtonsToToolBar();

        Container content = getContentPane();
        content.add(textComp, BorderLayout.CENTER);
        content.add(this.toolbar, BorderLayout.NORTH);
        this.setJMenuBar(createMenuBar());
        this.setSize(dim.width * 16, dim.height * 9);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add mouse and keyboard events
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventDispatcher(this);

        //animateall();
        animateToolbar();
    }

    private void addAllButtonsToToolBar (){
        addButtonToToolBar(new HKButton(mushroomAction,dim));

        JPanel panel = new JPanel(new SpringLayout());
        JButton aa = new JButton("Animate all");
        aa.addActionListener(e -> {
            animateall();
            animateToolbar();
        });
        JButton settings = new JButton("Settings");
        settings.addActionListener(e -> {
            new IconHKSettingWindow(this.iconHKButtons);
        });
        panel.add(aa);
        panel.add(settings);
        SpringUtilities.makeCompactGrid(panel, 2, 1, 3, 3, 0, 5);
        //toolbar.add(aa);
        //toolbar.add(settings);
        toolbar.add(panel);
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

    private JMenuBar createMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu  other = new JMenu("Other");
        menubar.add(file);
        menubar.add(edit);
        menubar.add(other);

        JMenuItem cut = new JMenuItem(mushroomAction);
        cut.addActionListener(this);
        //	cut.setAccelerator(KeyStroke.getKeyStroke(
        //  KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        edit.add(cut);

        return menubar;
    }


    private void animateall(){
        animations.removeAllElements();
        for (Component c : toolbar.getComponents()) {
            if (c.getClass() == HKButton.class) {
                HKButton button = (HKButton) c;
                int[] sequence = { IconAnimation.HOTKEY_STEP, IconAnimation.DEFAULT_STEP, IconAnimation.OVER_STEP };
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

    protected void createActions() {
        mushroomAction = new HKAction("Mushroom");
        mushroomAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_L, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));

    }

    private void animateToolbar() {
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

    private void animateHotkeyPressed() {
        if (animations.size() > 0) {
            Iterator<IconAnimation> i = animations.iterator();
            while (i.hasNext()) {
                IconAnimation animation = i.next();
                int objective = animation.getCurrentObjective();
                animation.getButton().changeCurrentFrame();
                animation.getButton().repaint();
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            switch(e.getKeyCode()){
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
        } else if (e.getID() == KeyEvent.KEY_RELEASED) {
            switch(e.getKeyCode()){
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
        animateActivated();
        animateHotkeyPressed();
        return false;
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
        b.setCtrlPressed(ctrlPressed);
        b.setAltPressed(altPressed);
        b.setShftPressed(shftPressed);
        b.setMetaPressed(metaPressed);
    }

    private void deactivateAllModifiers(HKButton b){
        b.setCtrlPressed(false);
        b.setAltPressed(false);
        b.setShftPressed(false);
        b.setMetaPressed(false);
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            if (ctrlPressed || altPressed || metaPressed || shftPressed)
                animateHotkeyPressed();
            else
                animateToolbar();
            repaint();
        }
    }
}
