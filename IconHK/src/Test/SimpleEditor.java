package Test;

import IconHK.*;
import IconHK.util.Image;
import IconHK.util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.Vector;

public class SimpleEditor extends JFrame implements ActionListener, KeyEventDispatcher {

    private static Dimension dim = new Dimension(50,50);

    // Tool bar
    private JToolBar toolbar;

    private static Timer timer;

    //Actions
    private Action cutAction, pasteAction, copyAction, saveAction, newAction, expandAction, rgbAction;
    private Action findAction, moveAction, pencilAction, increaseAction, mushroomAction, marioAction;

    // Boolean testing if buttons pressed
    private static boolean metaPressed = false;
    private static boolean altPressed = false;
    private static boolean ctrlPressed = false;
    private static boolean shftPressed = false;

    private JButton aa;

    private Vector<IconAnimation> animations;
    private Vector<HKButton> iconHKButtons;

    public SimpleEditor (){
        super("Swing Editor");

        animations = new Vector<>();
        iconHKButtons = new Vector<>();
        timer = new Timer(50, this);
        timer.start();

        JTextArea textComp = new JTextArea();
        textComp.setLineWrap(true);
        createActions();

        // Start adding buttons to toolbar
        this.toolbar = new JToolBar();
        addAllButtonsToToolBar();

        Container content = getContentPane();
        content.add(textComp, BorderLayout.CENTER);
        content.add(this.toolbar, BorderLayout.NORTH);
        this.setJMenuBar(createMenuBar());
        this.pack();
        this.setSize(this.getWidth(), this.getHeight()*4);
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
        addButtonToToolBar(new HKButton(newAction,dim,Image.DEFAULT));
        addButtonToToolBar(new HKButton(saveAction,dim,Image.DEFAULT));
        toolbar.addSeparator();
        addButtonToToolBar(new HKButton(copyAction,dim,Image.DEFAULT));
        addButtonToToolBar(new HKButton(cutAction,dim,Image.DEFAULT));
        addButtonToToolBar(new HKButton(pasteAction,dim,Image.DEFAULT));
        toolbar.addSeparator();
        addButtonToToolBar(new HKButton(expandAction,dim,Image.DEFAULT));
        addButtonToToolBar(new HKButton(rgbAction,dim,Image.DEFAULT));
        addButtonToToolBar(new HKButton(findAction,dim,Image.DEFAULT));
        addButtonToToolBar(new HKButton(moveAction,dim,Image.DEFAULT));
        addButtonToToolBar(new HKButton(pencilAction,dim,Image.DEFAULT));
        addButtonToToolBar(new HKButton(increaseAction,dim,Image.DEFAULT));
        toolbar.addSeparator();
        addButtonToToolBar(new HKButton(mushroomAction,dim,Image.QUADRATIC));
        addButtonToToolBar(new HKButton(marioAction,dim,Image.CUBIC));

        JPanel panel = new JPanel(new SpringLayout());
        aa = new JButton("Animate all");
        aa.addActionListener(e -> {
            animateall();
            animateToolbar();
        });
        JButton settings = new JButton("Settings");
        settings.addActionListener(e -> new IconHKSettingWindow(this.iconHKButtons));
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

        JMenuItem newfile = new JMenuItem(newAction);
        newfile.addActionListener(this);
        //	newfile.setAccelerator(KeyStroke.getKeyStroke(
        //  KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        file.add(newfile);

        JMenuItem save = new JMenuItem(saveAction);
        save.addActionListener(this);
        //save.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_S, ActionEvent.CTRL_MASK|ActionEvent.ALT_MASK));
        file.add(save);

        JMenuItem copy = new JMenuItem(copyAction);
        copy.addActionListener(this);
        //	copy.setAccelerator(KeyStroke.getKeyStroke(
        //   KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        edit.add(copy);

        JMenuItem cut = new JMenuItem(cutAction);
        cut.addActionListener(this);
        //	cut.setAccelerator(KeyStroke.getKeyStroke(
        //  KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        edit.add(cut);

        JMenuItem paste = new JMenuItem(pasteAction);
        paste.addActionListener(this);
        //paste.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        edit.add(paste);

        JMenuItem rgb = new JMenuItem(rgbAction);
        rgb.addActionListener(this);
        other.add(rgb);

        JMenuItem expand = new JMenuItem(expandAction);
        expand.addActionListener(this);
        other.add(expand);

        JMenuItem find = new JMenuItem(findAction);
        find.addActionListener(this);
        other.add(find);

        JMenuItem move = new JMenuItem(moveAction);
        move.addActionListener(this);
        other.add(move);


        JMenuItem pencil = new JMenuItem(pencilAction);
        pencil.addActionListener(this);
        other.add(pencil);

        JMenuItem increase = new JMenuItem(increaseAction);
        increase.addActionListener(this);
        other.add(increase);

        JMenuItem mushroom = new JMenuItem(mushroomAction);
        mushroom.addActionListener(this);
        other.add(mushroom);

        JMenuItem mario = new JMenuItem(marioAction);
        mario.addActionListener(this);
        other.add(mario);


        return menubar;
    }


    protected void createActions() {
        cutAction = new HKAction("Cut");
        cutAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_X, ActionEvent.CTRL_MASK));

        pasteAction = new HKAction("Paste");
        pasteAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_V, ActionEvent.ALT_MASK|ActionEvent.SHIFT_MASK));

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
                KeyEvent.VK_Q, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));

        moveAction = new HKAction("Move");
        moveAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.CTRL_MASK| ActionEvent.ALT_MASK));

        pencilAction = new HKAction("Pencil");
        pencilAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_W, ActionEvent.CTRL_MASK|ActionEvent.ALT_MASK|ActionEvent.SHIFT_MASK));

        increaseAction = new HKAction("Increase");
        increaseAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_H, ActionEvent.CTRL_MASK));

        mushroomAction = new HKAction("Mushroom");
        mushroomAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.ALT_MASK));

        marioAction = new HKAction("Mario");
        marioAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_M, ActionEvent.ALT_MASK));
    }

    private void animateToolbar() {
        if (animations.size() < iconHKButtons.size()) return;
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

    private void animateall(){
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

    private void desanimateHotkeyPressed(){
        if (HKButton.lockHotkey){
            animations.clear();
            for (HKButton button : iconHKButtons)
                animations.add(new IconAnimation(button,new int[]{IconAnimation.DEFAULT_STEP},40));
            for (IconAnimation animation : animations) {
                animation.getButton().decreaseCurrentFrame();
                animation.getButton().repaint();
            }
        }
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
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            if (ctrlPressed || altPressed || metaPressed || shftPressed)
                animateHotkeyPressed();
            else{
                desanimateHotkeyPressed();
                animateToolbar();
            }
            repaint();
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

    public static void setDim (Dimension d){
        SimpleEditor.dim = d;
    }

    public static void setTimer(int val){
        timer.setDelay(val);
        timer.restart();
    }

    public static int getTimer (){
        return timer.getDelay();
    }
}
