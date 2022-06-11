package tech.kuraudo.client.view.renderers;

import tech.kuraudo.client.FileInfo;
import tech.kuraudo.client.FilesTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeRenderer extends KuraudoTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof LocalDateTime ldt) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatDateTime = ldt.format(formatter);
            label.setText(formatDateTime + " "); // пробел, чтобы дата не липла к границе
            label.setHorizontalAlignment(JLabel.RIGHT);
            return label;
        }

        return label;
    }
}
