package tech.kuraudo.client.view.renderers;

import tech.kuraudo.client.FileInfo;
import tech.kuraudo.client.FilesTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class KuraudoTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // @todo вытащить значение цвета из FlatLaf схемы
        Color basicLabelColor = table.getBackground();

        FileInfo.FileType type = (FileInfo.FileType) table.getModel().getValueAt(row, FilesTableModel.TYPE);
        if (type == FileInfo.FileType.DIR) {
            label.setForeground(new Color(217, 163, 67));
        } else {
            label.setForeground(Color.LIGHT_GRAY);
        }

        return label;
    }

}
