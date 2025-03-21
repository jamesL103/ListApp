package gui;

import gui.lists.ScrollListPanel;
import gui.panels.AbstractEntryPanel;
import gui.panels.EntryEditPanel;
import gui.panels.EntryViewPanel;
import listItemStorage.ListEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ListGui {

    //Parent container of the gui
    private final JFrame PARENT;

    //GridBagLayout Constraints
    private static final GridBagConstraints CONSTRAINTS_ENTRY = createEntryViewConstraints();
    private static final GridBagConstraints CONSTRAINTS_L1_DEFAULT = createList1DefConstraints();
    private static final GridBagConstraints CONSTRAINTS_L2_DEFAULT = createDefList2Constraints();
    private static final GridBagConstraints CONSTRAINTS_CONTROL_BAR = createControlBarConstraints();

    private final GridBagLayout layout;

    /**manages the data stored in all the lists
     * 
     */
    private final ListManager MANAGER;

    //components of the gui
    private final ScrollListPanel LIST_TODO = createScroll();
    private final ScrollListPanel LIST_COMPLETED = createScroll();

    //Control bar with buttons
    private final EntryControlBar CONTROL_BAR = new EntryControlBar(new ControlBarObserver());

    //the list with the current active selection
    private ScrollListPanel activeList;

    //panels for accessing and editing entry fields
    private AbstractEntryPanel currentView;
    private final EntryViewPanel VIEW_PANEL = new EntryViewPanel(ListEntry.DEFAULT_ENTRY);
    private final EntryEditPanel EDIT_PANEL = new EntryEditPanel(ListEntry.DEFAULT_ENTRY);

    //default colors
    public static final Color COLOR_BACKGROUND = new Color(24, 32, 54);
    public static final Color COLOR_BG_ACCENT = new Color(54, 60, 81);
    public static final Color COLOR_BORDER = new Color(101, 101, 101);
    public static final Color COLOR_TEXT = new Color(255, 255, 255);
    public static final Color COLOR_BUTTON = new Color(116, 160, 255);
    public static final Color COLOR_TEXT_ACCENT = new Color(172, 172, 172);

    //fonts
    public static final Font FONT_TITLE = new Font("arial", Font.PLAIN, 24);
    public static final Font FONT_SMALL_TEXT = new Font("arial", Font.PLAIN, 12);

    //create the gui for the list app
    public ListGui() {
        PARENT = new JFrame("Lister");
        PARENT.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        PARENT.setSize(new Dimension(800, 500));
        layout = new GridBagLayout();

        PARENT.setLayout(layout);
        PARENT.getContentPane().setBackground(COLOR_BACKGROUND);

        //set up key binds
        PARENT.addMouseListener(new MouseFocusListener());
        PARENT.setFocusable(true);
        PARENT.requestFocusInWindow();
        PARENT.addKeyListener(new KeyBindListener());

        initViewPanel();
        initEditPanel();
        currentView = VIEW_PANEL;

        MANAGER = new ListManager();

        addLists();
        addControlBar();

        PARENT.setVisible(true);
    }

    //initialize list fields and add them to the gui
    private void addLists() {

        //to do list
        LIST_TODO.setTitle("To-Do");
        MANAGER.registerList(LIST_TODO, "todo");


        activeList = LIST_TODO;

        PARENT.add(LIST_TODO, CONSTRAINTS_L1_DEFAULT);


        //completed list
        LIST_COMPLETED.setTitle("Completed");
        MANAGER.registerList(LIST_COMPLETED, "completed");

        PARENT.add(LIST_COMPLETED, CONSTRAINTS_L2_DEFAULT);

        loadLists();

        //set the counters of the lists
        LIST_TODO.updateCount();
        LIST_COMPLETED.updateCount();
    }

    //loads all lists in the manager from their files
    public void loadLists() {
        MANAGER.loadAllLists();
    }

    //create and add control bar
    private void addControlBar() {
        PARENT.add(CONTROL_BAR, CONSTRAINTS_CONTROL_BAR);
    }

    //creates the EntryViewPanel
    private void initViewPanel() {
        VIEW_PANEL.addObserver(new EntryPanelObserver());
    }

    //creates the EntryEditPanel
    private void initEditPanel() {
        EDIT_PANEL.setObserver(new EntryPanelObserver());
    }

    //create layout constraints for entry viewer
    private static GridBagConstraints createEntryViewConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 0;
        c.weightx = 0.2;
        c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridheight = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0,20, 0, 20);

        return c;
    }
    
    //create the layout constraints for list 1
    private static GridBagConstraints createList1DefConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridheight = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 10, 0, 10);
        return c;
    }

    //initialize the default layout constraints for the second list
    private static GridBagConstraints createDefList2Constraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridheight = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 10, 0, 10);
        return c;
    }


    //create the layout constraints for the control bar
    private static GridBagConstraints createControlBarConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        return c;
    }

    //displays the specified entry and its fields as a side panel of a specified type
    //replaces the current display panel if one is visible
    private void displayEntry(ListEntry entry, AbstractEntryPanel panel) {

        PARENT.remove(currentView);
        currentView = panel;

        PARENT.add(panel, CONSTRAINTS_ENTRY);
        currentView.setEntry(entry);

        PARENT.paintComponents(PARENT.getGraphics());
    }

    //close the panel currently displaying an entry
    private void closeEntryPanel() {
        PARENT.remove(currentView);
        PARENT.paintComponents(PARENT.getGraphics());
        layout.setConstraints(LIST_COMPLETED, CONSTRAINTS_L2_DEFAULT);
        layout.setConstraints(LIST_TODO, CONSTRAINTS_L1_DEFAULT);
        CONTROL_BAR.BUTTON_COMPLETE.setEnabled(false);
        LIST_TODO.clearSelection();
        LIST_COMPLETED.clearSelection();
    }

    //completes an entry, moving it from to-do to completed
    private void completeEntry(ListEntry entry) {
        CONTROL_BAR.BUTTON_COMPLETE.setEnabled(false);
        MANAGER.moveEntry(LIST_TODO, LIST_COMPLETED, entry);
    }

    //deletes an entry
    private void deleteEntry(ScrollListPanel list, ListEntry entry) {
        MANAGER.removeEntry(list, entry);
        closeEntryPanel();
    }

    //helper to create Scroll task list with correct colors
    private ScrollListPanel createScroll() {
        ScrollListPanel scroll = new ScrollListPanel(new ListSelectionObserver());
        scroll.setBg(COLOR_BACKGROUND);
        scroll.setFg(COLOR_TEXT);
        return scroll;
    }


    //observer for ScrollListPanel notifies gui when list entry is selected from list
    public class ListSelectionObserver {

        //called when list selection is updated
        public void notifySelection(ListEntry entry, ScrollListPanel caller) {
            if (entry != null) {
                CONTROL_BAR.BUTTON_COMPLETE.setEnabled(true);
                displayEntry(entry, VIEW_PANEL);
                activeList = caller;
            }
        }

    }

    //observer for EntryAccessPanels that handles events with any entry panel
    public class EntryPanelObserver {

        //closes the entryViewPanel
        public void notifyClose() {
            closeEntryPanel();
        }

        //opens editing of the currently viewed entry
        public void notifyEdit(ListEntry entry) {
            displayEntry(entry, EDIT_PANEL);
            EDIT_PANEL.setCreatingNewEntry(false);
        }

        //notifies that the edited entry is being saved
        public void notifySave(ListEntry entry, boolean newEntry, boolean dateEdited) {
            displayEntry(entry, VIEW_PANEL);
            if (newEntry) {
                MANAGER.addSorted(LIST_TODO, entry);
            } else if (dateEdited) {
                MANAGER.sortEntry(LIST_TODO, entry);
            }
            MANAGER.save(LIST_TODO);
        }

        //cancels editing of an entry
        public void notifyCancel(ListEntry entry) {
            displayEntry(entry, VIEW_PANEL);
        }

        public void notifyDelete(ListEntry entry) {
            deleteEntry(activeList, entry);
        }

    }

    //observer for EntryControlBar that tracks when buttons in the control bar are used
    public class ControlBarObserver {

        public void notifyAdd() {
            ListEntry entry = new ListEntry();
            displayEntry(entry, EDIT_PANEL);
            EDIT_PANEL.setCreatingNewEntry(true);
            PARENT.paintComponents(PARENT.getGraphics());
        }

        public void notifyComplete() {
            ListEntry selection = LIST_TODO.LIST.getSelectedValue();
            if (selection != null) {
                completeEntry(selection);
            }
        }

    }

    /** Listener for application key binds
     *
     */
    private class KeyBindListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (currentView != null) {
                    closeEntryPanel();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    private class MouseFocusListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            PARENT.requestFocusInWindow();
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

}
