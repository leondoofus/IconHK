package Test;

import IconHK.*;
import IconHK.util.Image;
import IconHK.util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.Vector;

public class SimpleEditor extends JFrame{

    private static Dimension dim = new Dimension(50,50);

    // Tool bar
    private JToolBar toolbar;

    //Actions
    private Action cutAction, pasteAction, copyAction, saveAction, newAction, expandAction, rgbAction;
    private Action findAction, moveAction, pencilAction, increaseAction, mushroomAction, marioAction;

    private JButton aa;

    private Vector<HKButton> iconHKButtons;

    private HKKeyListener keyListener;

    public SimpleEditor (){
        super("Swing Editor");
        iconHKButtons = new Vector<>();

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

        keyListener = new HKKeyListener(iconHKButtons,toolbar);
        // Add mouse and keyboard events
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventDispatcher(keyListener);

        //animateall();
        keyListener.animateToolbar();
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
            if (HKButton.lockHotkey){
                Object[] options = {"Yes", "No"};
                int n = JOptionPane.showOptionDialog(this,
                        "By clicking this button you will unlock all hotkeys.\n"
                                + "Would you like to continue?",
                        "Warning !",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);
                if (n == 0){
                    HKButton.lockHotkey = false;
                    IconHKSettingWindow.deactiveCb();
                }
            }
            if (!HKButton.lockHotkey){
                keyListener.animateall();
                keyListener.animateToolbar();
            }
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
        button.addActionListener(keyListener);
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
        newfile.addActionListener(keyListener);
        //	newfile.setAccelerator(KeyStroke.getKeyStroke(
        //  KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        file.add(newfile);

        JMenuItem save = new JMenuItem(saveAction);
        save.addActionListener(keyListener);
        //save.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_S, ActionEvent.CTRL_MASK|ActionEvent.ALT_MASK));
        file.add(save);

        JMenuItem copy = new JMenuItem(copyAction);
        copy.addActionListener(keyListener);
        //	copy.setAccelerator(KeyStroke.getKeyStroke(
        //   KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        edit.add(copy);

        JMenuItem cut = new JMenuItem(cutAction);
        cut.addActionListener(keyListener);
        //	cut.setAccelerator(KeyStroke.getKeyStroke(
        //  KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        edit.add(cut);

        JMenuItem paste = new JMenuItem(pasteAction);
        paste.addActionListener(keyListener);
        //paste.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        edit.add(paste);

        JMenuItem rgb = new JMenuItem(rgbAction);
        rgb.addActionListener(keyListener);
        other.add(rgb);

        JMenuItem expand = new JMenuItem(expandAction);
        expand.addActionListener(keyListener);
        other.add(expand);

        JMenuItem find = new JMenuItem(findAction);
        find.addActionListener(keyListener);
        other.add(find);

        JMenuItem move = new JMenuItem(moveAction);
        move.addActionListener(keyListener);
        other.add(move);


        JMenuItem pencil = new JMenuItem(pencilAction);
        pencil.addActionListener(keyListener);
        other.add(pencil);

        JMenuItem increase = new JMenuItem(increaseAction);
        increase.addActionListener(keyListener);
        other.add(increase);

        JMenuItem mushroom = new JMenuItem(mushroomAction);
        mushroom.addActionListener(keyListener);
        other.add(mushroom);

        JMenuItem mario = new JMenuItem(marioAction);
        mario.addActionListener(keyListener);
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



    public static void setDim (Dimension d){
        SimpleEditor.dim = d;
    }
}
