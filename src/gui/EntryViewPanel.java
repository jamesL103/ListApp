package gui;

import listItemStorage.ListEntry;
import util.Util;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionListener;

public class EntryViewPanel extends EntryAccessPanel {

    private ListGui.EntryPanelObserver observer;


    public EntryViewPanel(ListEntry entry) {
        super(entry);
    }



    @Override
    public void initialize() {
        addNameAccessor(makeName());
        addDescAccessor(makeDescription());
        addDateAccessor(makeDate());
        addButtons(makeButtons());

        updateFields();
    }

    private JLabel makeName() {
        JLabel name = Util.newLabel();
        name.setFont(ListGui.TITLE);
        name.setText("default");
        name.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        return name;
    }

    private JTextArea makeDescription() {
        JTextArea desc = new JTextArea();
        desc.setEditable(false);
        desc.setFocusable(false);
        desc.setLineWrap(true);
        desc.setText("default");

        return desc;
    }

    private JLabel makeDate() {
        JLabel date = new JLabel();
        date.setText("Due: " + toDisplay.getStringDate());

        return date;
    }

    private JPanel makeButtons() {
        JPanel panel = new JPanel();
        panel.setBackground(ListGui.BACKGROUND);

        JButton edit = Util.newButton("Edit");
        edit.addActionListener(makeEditListener());
        edit.setFont(smallFont);
        panel.add(edit);

        JButton delete = Util.newButton("Delete Entry");
        delete.addActionListener(makeDeleteListener());
        delete.setFont(smallFont);
        panel.add(delete);

        return panel;

    }

    @Override
    public ActionListener makeExitListener() {
        return (e) -> {
            observer.notifyClose();
        };
    }

    private ActionListener makeEditListener() {
        return (e) -> {
            observer.notifyEdit(toDisplay);
        };
    }

    private ActionListener makeDeleteListener() {
        return (e) -> {
            int delete = JOptionPane.showOptionDialog(null, "Delete this entry? This action cannot be undone!", "Delete Entry", JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE, null, null, null);
            if (delete == JOptionPane.OK_OPTION) {
                observer.notifyDelete(toDisplay);
            }
        };
    }

    /**Sets this panel's observer to the specified observer
     * @param observer the observer for this panel
     */
    public void addObserver(ListGui.EntryPanelObserver observer) {
        this.observer = observer;
    }

    @Override
    public void updateFields() {
        ((JLabel)nameDisplay).setText(toDisplay.getName());
        ((JTextComponent)descDisplay).setText(toDisplay.getDescription());
        ((JLabel)dateDisplay).setText("Due: " + toDisplay.getStringDate());
    }
}
