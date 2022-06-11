package tech.kuraudo.client.view.renderers;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.w3c.dom.ls.LSOutput;
import tech.kuraudo.client.FileInfo;
import tech.kuraudo.client.FilesTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NavigationPane extends JPanel {

    public static NavigationPane getInstance(FilesTableModel tableModel, Path path) {

        final Path[] iPath = {path};
        final JTextField textField = new JTextField();
        List< String > files = new ArrayList<>();

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        NavigationPane pane = new NavigationPane();
        pane.setLayout(new BorderLayout());

        JPanel handlePane = new JPanel();
        handlePane.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        handlePane.setLayout(new BorderLayout());

        JButton buttonUp = new JButton();
        buttonUp.setFocusPainted(false);
        buttonUp.setToolTipText("Go to parent directory");
        buttonUp.setFocusPainted(false);
        buttonUp.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/arrow-up-left-bold.svg"));
        buttonUp.addActionListener(e -> {
            iPath[0] = iPath[0].getParent();
            textField.setText(iPath[0].toString()); // меняем путь к директории
            tableModel.updateList(iPath[0]); // меняем список файлов и директорий в таблице
            pane.repaint();
            scrollPane.repaint();
            // если директория корневая - то отключаем возможность идти выше
            if (isRootDirectory(iPath[0])) {
                buttonUp.setEnabled(false);
            }
        });

        if (isRootDirectory(iPath[0])) {
            buttonUp.setEnabled(false);
        }

        handlePane.add(buttonUp, BorderLayout.WEST);

        textField.setText(" " + iPath[0].toString()); // пробел для красоты
        textField.setEnabled(false);
        textField.setEditable(false);
        textField.setMargin(new Insets(3, 0, 3, 0));
        handlePane.add(textField, BorderLayout.CENTER);

        JTable table = new JTable(tableModel);
        table.setDefaultRenderer(FileInfo.FileType.class, new KuraudoTableCellRenderer());
        table.setDefaultRenderer(String.class, new KuraudoTableCellRenderer());
        table.setDefaultRenderer(LocalDateTime.class, new LocalDateTimeRenderer());
        table.setDefaultRenderer(Long.class, new SizeRenderer());

        TableColumnModel cm = table.getColumnModel();

        TableColumn typeColumn = cm.getColumn(FilesTableModel.TYPE);
        typeColumn.setMinWidth(20);
        typeColumn.setMaxWidth(40);

        TableColumn sizeColumn = cm.getColumn(FilesTableModel.SIZE);
        sizeColumn.setMinWidth(60);
        sizeColumn.setMaxWidth(120);

        TableColumn modifiedColumn = cm.getColumn(FilesTableModel.MODIFIED);
        modifiedColumn.setMinWidth(120);
        modifiedColumn.setMaxWidth(130);

        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem contextMenuItemCopy = new JMenuItem();
        contextMenuItemCopy.setText("Copy");
        contextMenuItemCopy.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/content-copy.svg"));
        contextMenu.add(contextMenuItemCopy);

        JMenuItem contextMenuItemCut = new JMenuItem();
        contextMenuItemCut.setText("Cut");
        contextMenuItemCut.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/content-cut.svg"));
        contextMenu.add(contextMenuItemCut);

        JMenuItem contextMenuItemPaste = new JMenuItem();
        contextMenuItemPaste.setText("Paste");
        contextMenuItemPaste.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/content-paste.svg"));
        contextMenu.add(contextMenuItemPaste);
        contextMenu.addSeparator();

        JMenuItem contextMenuItemDelete = new JMenuItem();
        contextMenuItemDelete.setText("Delete");
        contextMenuItemDelete.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/delete.svg"));
        contextMenu.add(contextMenuItemDelete);

//        table.addFocusListener(new FocusListener() {
//            @Override
//            public void focusGained(FocusEvent e) {
//                files.add(iPath[0].toString() + "\\" + tableModel.getValueAt(table.getSelectedRow(), FilesTableModel.NAME));
//                System.out.println(files);
//            }
//
//            @Override
//            public void focusLost(FocusEvent e) {
//
//            }
//        });

        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            files.clear();
            for (int i : lsm.getSelectedIndices()) {
                files.add((String) tableModel.getValueAt(i, FilesTableModel.NAME));
            }
        });

        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    int row = table.rowAtPoint(e.getPoint());
                    // двойной клик по директории - переход в нее
                    if (tableModel.getValueAt(row, FilesTableModel.TYPE).equals(FileInfo.FileType.DIR)) {
                        Path maybePath = Path.of(iPath[0] + "\\" + (String)tableModel.getValueAt(row, FilesTableModel.NAME));
                        File dir = new File(maybePath.toString());
                        // быстрая и простая проверка на читаемость директории.
                        if (null != dir.listFiles()) {
                            iPath[0] = maybePath;
                            textField.setText(iPath[0].toString()); // меняем путь к директории
                            tableModel.updateList(iPath[0]); // меняем список файлов и директорий в таблице
                            pane.repaint();
                            scrollPane.repaint();
                            buttonUp.setEnabled(true);
                        } else {
                            // todo
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        table.setComponentPopupMenu(contextMenu);
        table.setFillsViewportHeight(true);
        scrollPane.setViewportView(table);

        pane.add(handlePane, BorderLayout.NORTH);
        pane.add(scrollPane, BorderLayout.CENTER);

        return pane;
    }

    static boolean isRootDirectory(Path path) {
        FileSystem fs = FileSystems.getDefault();
        Iterable<Path> rootDirectories = fs.getRootDirectories();
        for (Path p : rootDirectories) {
            if (p.equals(path)) {
                return true;
            }
        }

        return false;
    }
}
