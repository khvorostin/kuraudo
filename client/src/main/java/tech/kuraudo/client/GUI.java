package tech.kuraudo.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;

public class GUI extends JFrame {

    private JMenuBar menuBar;
    private JToolBar toolBar;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        new GUI();
    }

    public GUI() throws HeadlessException {

        initComponents();

        setSize(840, 420);
        setTitle("Kuraudo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setIconImages(FlatSVGUtils.createWindowIconImages("/tech/kuraudo/client/Kuraudo.svg"));

        setJMenuBar(menuBar);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(toolBar, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(new EmptyBorder(new Insets(3, 3, 3, 3)));

        FileSystem fs = FileSystems.getDefault();
        Path path = fs.getPath("C:\\Users\\denis\\Videos");

        FilesTableModel tableModel1 = new FilesTableModel();
        tableModel1.updateList(path);

        FilesTableModel tableModel2 = new FilesTableModel();
        tableModel2.updateList(path);

        JPanel leftPane = buildNavigationPane(tableModel1);
        JPanel rightPane = buildNavigationPane(tableModel2);

        splitPane.setLeftComponent(leftPane);
        splitPane.setRightComponent(rightPane);

        contentPane.add(splitPane);

        setVisible(true);
    }

    private void initComponents() {
        initMenuBar();
        initToolBar();
    }

    private Box.Filler getVerticalGap() {
        Box.Filler verticalGap = (Box.Filler) Box.createGlue();
        verticalGap.changeShape(
                new Dimension(5, Short.MAX_VALUE),
                new Dimension(5, Short.MAX_VALUE),
                new Dimension(5, Short.MAX_VALUE)
        );

        return verticalGap;
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu();
        fileMenu.setText("File");
        fileMenu.setMnemonic('F');

        JMenuItem copyMenuItem = new JMenuItem();
        copyMenuItem.setText("Copy");
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        copyMenuItem.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/menu-copy.svg"));
        copyMenuItem.setMnemonic('C');
        fileMenu.add(copyMenuItem);

        JMenuItem cutMenuItem = new JMenuItem();
        cutMenuItem.setText("Cut");
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        cutMenuItem.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/menu-cut.svg"));
        cutMenuItem.setMnemonic('U');
        fileMenu.add(cutMenuItem);

        JMenuItem pasteMenuItem = new JMenuItem();
        pasteMenuItem.setText("Paste");
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        pasteMenuItem.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/menu-paste.svg"));
        pasteMenuItem.setMnemonic('P');
        fileMenu.add(pasteMenuItem);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.setText("Exit");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exitMenuItem.setMnemonic('X');
        fileMenu.add(exitMenuItem);

        JMenu cloudMenu = new JMenu();
        cloudMenu.setText("Cloud");
        cloudMenu.setMnemonic('L');

        JCheckBoxMenuItem connectMenuItem = new JCheckBoxMenuItem();
        connectMenuItem.setText("Connect");
        connectMenuItem.setMnemonic('T');
        cloudMenu.add(connectMenuItem);

        JCheckBoxMenuItem disconnectMenuItem = new JCheckBoxMenuItem();
        disconnectMenuItem.setText("Disconnect");
        disconnectMenuItem.setMnemonic('D');
        cloudMenu.add(disconnectMenuItem);

        JMenuItem settingsMenuItem = new JMenuItem();
        settingsMenuItem.setText("Settings");
        settingsMenuItem.setMnemonic('S');
        cloudMenu.add(settingsMenuItem);

        JMenu helpMenu = new JMenu();
        helpMenu.setText("Help");
        helpMenu.setMnemonic('H');

        JMenuItem aboutMenuItem = new JMenuItem();
        aboutMenuItem.setText("About");
        aboutMenuItem.setMnemonic('A');
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(cloudMenu);
        menuBar.add(helpMenu);
    }

    private void initToolBar() {
        toolBar = new JToolBar();
        toolBar.setMargin(new Insets(3, 3, 3, 3));

        JLabel label = new JLabel("Hello world");
        toolBar.add(label);
    }

    private JPanel buildNavigationPane(FilesTableModel tableModel) {
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());

        JPanel handlePane = new JPanel();
        handlePane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        JLabel handleLabel = new JLabel("Top");
        handlePane.add(handleLabel);

        JTable table = new JTable(tableModel);
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

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        table.setFillsViewportHeight(true);
        scrollPane.setViewportView(table);

        pane.add(handlePane, BorderLayout.NORTH);
        pane.add(scrollPane, BorderLayout.CENTER);

        return pane;
    }
}
