import listItemStorage.ListEntry;

import javax.swing.*;
import java.awt.*;

public class EntryCellRenderer extends JLabel implements ListCellRenderer<ListEntry> {


    public EntryCellRenderer() {
        setPreferredSize(new Dimension(60, 20));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ListEntry> list, ListEntry value, int index, boolean isSelected, boolean cellHasFocus) {
        int maxWidth = list.getWidth();
        String entry = value.toString();
        entry = entry + "               Due: " + value.getStringDueDate();
        setText(entry);
        setFont(list.getFont());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setOpaque(true);
        return this;
    }
}
