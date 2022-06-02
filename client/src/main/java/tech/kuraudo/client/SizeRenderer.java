package tech.kuraudo.client;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SizeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Long) {
            Long size = (Long) value;
            String sizeAsString = convertSizeToString(size);
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setText(sizeAsString);
            label.setHorizontalAlignment(JLabel.RIGHT);
            return label;
        }

        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    /**
     * Приведение размеров файлов к человекочитаемому виду.
     *
     * @param size Размер файла в байтах.
     * @return Размер файла в виде строки.
     */
    private String convertSizeToString(Long size) {

        // у директорий размер не выводим
        if (size == -1) {
            return "";
        }

        double sz = size.doubleValue();
        double kb = 1024;
        double mb = 1024 * 1024;
        double gb = 1024 * 1024 * 1024;

        if (sz < kb) {
            return sz + " B";
        }

        if (sz < mb) {
            sz /= kb;
            return String.format("%.2f KB", sz);
        }

        if (sz < gb) {
            sz /= mb;
            return String.format("%.2f MB", sz);
        }

        sz /= gb;
        return String.format("%.2f GB", sz);
    }
}

