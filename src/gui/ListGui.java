package gui;

import listItemStorage.ListEntry;

import javax.swing.*;
import java.awt.*;

public class ListGui {

    //Parent container of the gui
    private final JFrame PARENT;

    //GridBagLayout Constraints
    private final GridBagConstraints gbc;
    private final GridBagConstraints CONSTRAINTS_ENTRY = new GridBagConstraints();
    private final GridBagConstraints CONSTRAINTS_L2 = new GridBagConstraints();

    private final GridBagLayout layout;

    //manages the data stored in all the lists
    private final ListManager MANAGER;

    //observer to track when list selection changes
    private final ListSelectionObserver SELECTION_OBSERVER = new ListSelectionObserver();

    //components of the gui
    private JPanel completed;
    private EntryViewPanel viewPanel;

    //default colors
    public static final Color BACKGROUND = new Color(24, 32, 54);
    public static final Color TEXT = new Color(255, 255, 255);

    //fonts
    public static final Font TITLE = new Font("arial", Font.PLAIN, 24);

    //create the gui for the list app
    public ListGui() {
        PARENT = new JFrame("List App");
        PARENT.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        PARENT.setSize(new Dimension(800, 500));
        layout = new GridBagLayout();
        gbc = new GridBagConstraints();

        PARENT.setLayout(layout);
        PARENT.getContentPane().setBackground(BACKGROUND);

        initViewPanel();
        initListConstraints();

        MANAGER = new ListManager();

        addLists();
        addButtons();

        PARENT.setVisible(true);
    }

    private void addLists() {
        //list component constraints
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.BOTH;

        //to do list
        ScrollTaskList todo = createScroll();
        todo.setTitle("To-Do");
        MANAGER.registerList(todo.LIST, "todo");


        JPanel todoPanel = newPanel(todo, new BorderLayout(), BorderLayout.CENTER);
        todoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));


        addTo(todoPanel, 0, 0);

        //completed list

        ScrollTaskList completedTasks = createScroll();
        completedTasks.setTitle("Completed");
        MANAGER.registerList(completedTasks.LIST, "completed");

        JPanel completedTaskPanel = newPanel(completedTasks, new BorderLayout(), BorderLayout.CENTER);
        completedTaskPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        completed = completedTaskPanel;

        //update constraints for list 2
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        addTo(completedTaskPanel, 1, 0);

        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        loadLists();

    }

    public void loadLists() {
        MANAGER.initAll();
    }

    private void addButtons() {
        EntryControlBar bar = new EntryControlBar(MANAGER);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth= GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.ipady = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        addTo(bar, 0, 1);
    }

    //creates the EntryViewPanel and sets its constraints in the layout
    private void initViewPanel() {
        viewPanel = new EntryViewPanel(new ListEntry("null"));
        viewPanel.addObserver(new ViewPanelObserver());

        CONSTRAINTS_ENTRY.gridwidth = GridBagConstraints.REMAINDER;
        CONSTRAINTS_ENTRY.fill = GridBagConstraints.BOTH;
        CONSTRAINTS_ENTRY.gridx = 3;
        CONSTRAINTS_ENTRY.gridy = 0;
        CONSTRAINTS_ENTRY.weightx = 0.25;
        CONSTRAINTS_ENTRY.weighty = 0.5;
    }
    
    //initialize the default layout constraints for the second list
    private void initListConstraints() {
        CONSTRAINTS_L2.gridwidth = 1;
        CONSTRAINTS_L2.weightx = 0.5;
        CONSTRAINTS_L2.weighty = 1.0;
        CONSTRAINTS_L2.gridheight = GridBagConstraints.RELATIVE;
        CONSTRAINTS_L2.fill = GridBagConstraints.BOTH;
    }

    //displays the Access Panel for the selected entry
    private void displayEntry(ListEntry entry) {

        //change layout constraints for completed list
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        layout.setConstraints(completed, gbc);

        PARENT.add(viewPanel, CONSTRAINTS_ENTRY);
        viewPanel.setEntry(entry);

        PARENT.paintAll(PARENT.getGraphics());
    }

    //adds component to grid at index x and y
    //resets grid x and grid y back to original values
    //does NOT reset GridBagLayout constraints
    private void addTo(Component c, int x, int y) {
        int origx = gbc.gridx;
        int origy = gbc.gridy;
        gbc.gridx = x;
        gbc.gridy = y;
        PARENT.add(c, gbc);
        gbc.gridx = origx;
        gbc.gridy = origy;
    }

    //creates and returns a new Panel with the specified component added
    //uses specified LayoutManager and adds with specified constraints
    //if null, adds with default settings
    private JPanel newPanel(Component c, LayoutManager layout, Object layoutRules) {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND);
        panel.setForeground(TEXT);
        if (layout != null) {
            panel.setLayout(layout);
        }
        if (layoutRules != null) {
            panel.add(c, layoutRules);
        } else {
            panel.add(c);
        }
        return panel;
    }



    //helper to create new label with correct colors
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setBackground(BACKGROUND);
        label.setForeground(TEXT);
        return label;
    }

    //helper to create Scroll task list with correct colors
    private ScrollTaskList createScroll() {
        ScrollTaskList scroll = new ScrollTaskList(SELECTION_OBSERVER);
        scroll.setBg(BACKGROUND);
        scroll.setFg(TEXT);
        return scroll;
    }


    //observer that notifies gui when list entry is selected from list
    public class ListSelectionObserver {

        //called when a ListEntry is selected
        //notifies gui to display and update access panel
        public void notifySelection(ListEntry entry) {
            displayEntry(entry);
        }

    }

    //observer that handles events with the EntryViewPanel
    public class ViewPanelObserver {

        //closes the entryViewPanel
        public void notifyClose() {
            PARENT.remove(viewPanel);
            PARENT.paintAll(PARENT.getGraphics());
            layout.setConstraints(completed, CONSTRAINTS_L2);
        }

    }

}
