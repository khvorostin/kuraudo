package tech.kuraudo.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Arrays;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.ui.FlatUIUtils;
import tech.kuraudo.client.view.renderers.KuraudoTableCellRenderer;
import tech.kuraudo.client.view.renderers.LocalDateTimeRenderer;
import tech.kuraudo.client.view.renderers.NavigationPane;
import tech.kuraudo.client.view.renderers.SizeRenderer;

public class GUI extends JFrame {

    private MessagePool messagePool;
    private JMenuBar menuBar;
    private JToolBar toolBar;

    private JDialog settingsPane;

    public GUI(MessagePool messagePool) throws HeadlessException {

        this.messagePool = messagePool;

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

        // корневые директории
        Iterable< Path > rootDirectories = fs.getRootDirectories();
        for (Path p : rootDirectories) {
            System.out.println(p);
        }

        // текущая директория
        Path userDir = Path.of(System.getProperty("user.dir"));
        System.out.println(userDir);

        // по умолчанию - текущая директория
        Path pathToDirInLeftPanel = Path.of(System.getProperty("user.dir"));
        Path pathToDirInRightPanel = Path.of(System.getProperty("user.dir"));

        FilesTableModel tableModel1 = new FilesTableModel();
        tableModel1.updateList(pathToDirInLeftPanel);

        FilesTableModel tableModel2 = new FilesTableModel();
        tableModel2.updateList(pathToDirInRightPanel);

        JPanel leftPane = NavigationPane.getInstance(tableModel1, pathToDirInLeftPanel);
        JPanel rightPane = NavigationPane.getInstance(tableModel2, pathToDirInRightPanel);

        splitPane.setLeftComponent(leftPane);
        splitPane.setRightComponent(rightPane);

        contentPane.add(splitPane);

        setVisible(true);
    }

    private void initComponents() {
        initMenuBar();
        initToolBar();
        initSettingsPane();
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
        copyMenuItem.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/content-copy.svg"));
        copyMenuItem.setMnemonic('C');
        copyMenuItem.setEnabled(false);
        fileMenu.add(copyMenuItem);

        JMenuItem cutMenuItem = new JMenuItem();
        cutMenuItem.setText("Cut");
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        cutMenuItem.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/content-cut.svg"));
        cutMenuItem.setMnemonic('U');
        cutMenuItem.setEnabled(false);
        fileMenu.add(cutMenuItem);

        JMenuItem pasteMenuItem = new JMenuItem();
        pasteMenuItem.setText("Paste");
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        pasteMenuItem.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/content-paste.svg"));
        pasteMenuItem.setMnemonic('P');
        pasteMenuItem.setEnabled(false);
        fileMenu.add(pasteMenuItem);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.setText("Exit");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exitMenuItem.setMnemonic('X');
        exitMenuItem.addActionListener(e -> exitActionPeformed());
        fileMenu.add(exitMenuItem);

        JMenu cloudMenu = new JMenu();
        cloudMenu.setText("Cloud");
        cloudMenu.setMnemonic('L');

        JCheckBoxMenuItem connectMenuItem = new JCheckBoxMenuItem();
        connectMenuItem.setText("Connect");
        connectMenuItem.setMnemonic('T');
        connectMenuItem.setEnabled(false);
        cloudMenu.add(connectMenuItem);

        JCheckBoxMenuItem disconnectMenuItem = new JCheckBoxMenuItem();
        disconnectMenuItem.setText("Disconnect");
        disconnectMenuItem.setMnemonic('D');
        disconnectMenuItem.setEnabled(false);
        cloudMenu.add(disconnectMenuItem);

        JMenuItem settingsMenuItem = new JMenuItem();
        settingsMenuItem.setText("Settings");
        settingsMenuItem.setMnemonic('S');
        settingsMenuItem.addActionListener(e -> settingsActionPerformed());
        cloudMenu.add(settingsMenuItem);

        JMenu helpMenu = new JMenu();
        helpMenu.setText("Help");
        helpMenu.setMnemonic('H');

        JMenuItem aboutMenuItem = new JMenuItem();
        aboutMenuItem.setText("About");
        aboutMenuItem.setMnemonic('A');
        aboutMenuItem.addActionListener(e -> aboutActionPerformed());
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(cloudMenu);
        menuBar.add(helpMenu);
    }

    private void initToolBar() {
        toolBar = new JToolBar();
        toolBar.setMargin(new Insets(3, 3, 3, 3));

        JButton contentCopyButton = new JButton();
        contentCopyButton.setToolTipText("Copy selected file(s)");
        contentCopyButton.setFocusPainted(false);
        contentCopyButton.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/content-copy.svg"));
        contentCopyButton.setEnabled(false);

        JButton contentCutButton = new JButton();
        contentCutButton.setToolTipText("Cut selected file(s)");
        contentCutButton.setFocusPainted(false);
        contentCutButton.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/content-cut.svg"));
        contentCutButton.setEnabled(false);

        JButton contentPasteButton = new JButton();
        contentPasteButton.setToolTipText("Paste files");
        contentPasteButton.setFocusPainted(false);
        contentPasteButton.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/content-paste.svg"));
        contentPasteButton.setEnabled(false);

        JButton deleteButton = new JButton();
        deleteButton.setToolTipText("Delete selected file(s)");
        deleteButton.setFocusPainted(false);
        deleteButton.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/delete.svg"));
        deleteButton.setEnabled(false);

        JButton cogButton = new JButton();
        cogButton.setToolTipText("Settings");
        cogButton.setFocusPainted(false);
        cogButton.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/cog.svg"));
        cogButton.addActionListener(e -> settingsActionPerformed());

        JButton connectionButton = new JButton();
        connectionButton.setToolTipText("Connection");
        connectionButton.setFocusPainted(false);
        connectionButton.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/connection.svg"));
        connectionButton.setEnabled(false);

        toolBar.add(contentCopyButton);
        toolBar.add(contentCutButton);
        toolBar.add(contentPasteButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        toolBar.add(cogButton);
        toolBar.add(connectionButton);
    }

    private void initSettingsPane() {

        JPanel logInPanel = new JPanel(new BorderLayout());
        {
            logInPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            JLabel logInLeadLabel = new JLabel("Use this form if you are already registered.");
            logInLeadLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            logInPanel.add(logInLeadLabel, BorderLayout.NORTH);

            JPanel signInFormPanel = new JPanel();
            GroupLayout logInPanelLayout = new GroupLayout(signInFormPanel);
            signInFormPanel.setLayout(logInPanelLayout);

            logInPanelLayout.setAutoCreateGaps(true);
            logInPanelLayout.setAutoCreateContainerGaps(true);

            JLabel logInLoginLabel = new JLabel("Login:");
            logInLoginLabel.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/account.svg"));
            logInLoginLabel.setMaximumSize(new Dimension(100, 5)); // остальные выровняются по этим размерам
            JTextField logInLoginField = new JTextField();
            logInLoginField.setMinimumSize(new Dimension(150, 5)); // остальные выровняются по этим размерам

            JLabel logInPasswordLabel = new JLabel("Password:");
            logInPasswordLabel.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/lock.svg"));

            JPasswordField logInPasswordField = new JPasswordField();

            GroupLayout.SequentialGroup signInLayoutHGroup = logInPanelLayout.createSequentialGroup();
            signInLayoutHGroup.addGroup(
                    logInPanelLayout.createParallelGroup()
                            .addComponent(logInLoginLabel)
                            .addComponent(logInPasswordLabel)
            );
            signInLayoutHGroup.addGroup(
                    logInPanelLayout.createParallelGroup()
                            .addComponent(logInLoginField)
                            .addComponent(logInPasswordField)
            );
            logInPanelLayout.setHorizontalGroup(signInLayoutHGroup);

            GroupLayout.SequentialGroup logInLayoutVGroup = logInPanelLayout.createSequentialGroup();
            logInLayoutVGroup.addGroup(
                    logInPanelLayout.createParallelGroup()
                            .addComponent(logInLoginLabel)
                            .addComponent(logInLoginField)
            );
            logInLayoutVGroup.addGroup(
                    logInPanelLayout.createParallelGroup()
                            .addComponent(logInPasswordLabel)
                            .addComponent(logInPasswordField)
            );
            logInPanelLayout.setVerticalGroup(logInLayoutVGroup);

            JPanel logInFormOuterPanel = new JPanel();
            logInFormOuterPanel.add(signInFormPanel);

            logInPanel.add(logInFormOuterPanel, BorderLayout.CENTER);

            JButton logInOkButton = new JButton("Log in and connect");
            logInOkButton.setBackground(new Color(75, 110, 175));
            logInOkButton.setForeground(Color.WHITE);
            JButton logInCancelButton = new JButton("Cancel");
            logInCancelButton.addActionListener(e -> {
                settingsPane.setVisible(false);
            });

            JPanel buttonsLogInPanel = new JPanel();
            buttonsLogInPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            buttonsLogInPanel.add(logInOkButton);
            buttonsLogInPanel.add(logInCancelButton);

            logInPanel.add(buttonsLogInPanel, BorderLayout.SOUTH);
        }

        JPanel signInPanel = new JPanel(new BorderLayout());
        {
            signInPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            JLabel signInLeadLabel = new JLabel("Register to be able to use cloud storage.");
            signInLeadLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            signInPanel.add(signInLeadLabel, BorderLayout.NORTH);

            JPanel signInFormPanel = new JPanel();
            GroupLayout singInPanelLayout = new GroupLayout(signInFormPanel);
            signInFormPanel.setLayout(singInPanelLayout);

            singInPanelLayout.setAutoCreateGaps(true);
            singInPanelLayout.setAutoCreateContainerGaps(true);

            JLabel signInLoginLabel = new JLabel("Login:");
            signInLoginLabel.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/account.svg"));
            JTextField signInLoginField = new JTextField();
            signInLoginField.setMinimumSize(new Dimension(150, 5)); // остальные выровняются по этим размерам

            JLabel signInPasswordLabel = new JLabel("Password:");
            signInPasswordLabel.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/lock.svg"));

            JPasswordField signInPasswordField = new JPasswordField();

            JLabel signInPasswordCheckLabel = new JLabel("Retype password:");
            signInPasswordCheckLabel.setIcon(new FlatSVGIcon("tech/kuraudo/client/icons/material/lock.svg"));

            JPasswordField signInPasswordCheckField = new JPasswordField();

            GroupLayout.SequentialGroup signInLayoutHGroup = singInPanelLayout.createSequentialGroup();
            signInLayoutHGroup.addGroup(
                    singInPanelLayout.createParallelGroup()
                            .addComponent(signInLoginLabel)
                            .addComponent(signInPasswordLabel)
                            .addComponent(signInPasswordCheckLabel)
            );
            signInLayoutHGroup.addGroup(
                    singInPanelLayout.createParallelGroup()
                            .addComponent(signInLoginField)
                            .addComponent(signInPasswordField)
                            .addComponent(signInPasswordCheckField)
            );
            singInPanelLayout.setHorizontalGroup(signInLayoutHGroup);

            GroupLayout.SequentialGroup signInLayoutVGroup = singInPanelLayout.createSequentialGroup();
            signInLayoutVGroup.addGroup(
                    singInPanelLayout.createParallelGroup()
                            .addComponent(signInLoginLabel)
                            .addComponent(signInLoginField)
            );
            signInLayoutVGroup.addGroup(
                    singInPanelLayout.createParallelGroup()
                            .addComponent(signInPasswordLabel)
                            .addComponent(signInPasswordField)
            );
            signInLayoutVGroup.addGroup(
                    singInPanelLayout.createParallelGroup()
                            .addComponent(signInPasswordCheckLabel)
                            .addComponent(signInPasswordCheckField)
            );
            singInPanelLayout.setVerticalGroup(signInLayoutVGroup);

            JPanel signInFormOuterPanel = new JPanel();
            signInFormOuterPanel.add(signInFormPanel);

            signInPanel.add(signInFormOuterPanel, BorderLayout.CENTER);

            JButton signInOkButton = new JButton("Sign in and connect");
            signInOkButton.setBackground(new Color(75, 110, 175));
            signInOkButton.setForeground(Color.WHITE);
            signInOkButton.addActionListener(e -> {
                String username = signInLoginField.getText();
                char[] password = signInPasswordField.getPassword();
                char[] passwordCheck = signInPasswordCheckField.getPassword();

                if ("".equals(username)) {
                    showErrorModalWindow(SwingUtilities.windowForComponent(this), "Username can't be empty.");
                } else if (password.length == 0) {
                    showErrorModalWindow(SwingUtilities.windowForComponent(this), "Password can't be empty.");
                } else {
                    boolean isCorrect;
                    if (password.length != passwordCheck.length) {
                        isCorrect = false;
                    } else {
                        isCorrect = Arrays.equals(password, passwordCheck);
                    }

                    if (!isCorrect) {
                        showErrorModalWindow(SwingUtilities.windowForComponent(this), "Passwords aren't equal.");
                        signInPasswordField.setText("");
                        signInPasswordCheckField.setText("");
                    } else {
                        // ..
                    }
                }

                System.out.println(signInLoginField.getText() + " " + String.valueOf(signInPasswordField.getPassword()) + " " + signInPasswordCheckField.getPassword());
            });
            JButton signInCancelButton = new JButton("Cancel");
            signInCancelButton.addActionListener(e -> {
                settingsPane.setVisible(false);
            });

            JPanel buttonsSignInPanel = new JPanel();
            buttonsSignInPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            buttonsSignInPanel.add(signInOkButton);
            buttonsSignInPanel.add(signInCancelButton);

            signInPanel.add(buttonsSignInPanel, BorderLayout.SOUTH);
        }

        // Создание диалогового окна
        settingsPane = new JDialog(this, "Cloud storage client settings");
        // Определение способа завершения работы диалогового окна
        settingsPane.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        settingsPane.setMinimumSize(new Dimension(320, 250));
        settingsPane.setMinimumSize(new Dimension(340, 260));
        settingsPane.setPreferredSize(new Dimension(340, 250));
        // Определение типа оформления диалогового окна
        settingsPane.getRootPane().setWindowDecorationStyle(JRootPane.INFORMATION_DIALOG);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        // tabbedPane.addChangeListener(e -> selectedTabChanged());
        tabbedPane.addTab("Log in", logInPanel);
        tabbedPane.addTab("Sign in", signInPanel);
        settingsPane.add(tabbedPane);

        settingsPane.setVisible(false);
    }


    private void settingsActionPerformed() {
        settingsPane.setVisible(true);
    }

    private void aboutActionPerformed() {
        JLabel titleLabel = new JLabel();
        titleLabel.setText("Kuraudo");
        titleLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");

        String link = "https://kuraudo.tech/storage/";
        JLabel linkLabel = new JLabel("<html><a href=\"#\">" + link + "</a></html>");
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(link));
                } catch (IOException | URISyntaxException ex) {
                    JOptionPane.showMessageDialog(linkLabel,
                            "Failed to open '" + link + "' in browser.",
                            "About", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        JOptionPane.showMessageDialog(this,
                new Object[]{
                        titleLabel,
                        "File manager & Cloud storage client",
                        " ",
                        "Copyright 2022 Denis Khvorostin",
                        linkLabel,
                },
                "About", JOptionPane.PLAIN_MESSAGE);
    }

    private void exitActionPeformed() {
        dispose();
    }

    private void showErrorModalWindow(Window parentComponent, String message) {
        JOptionPane.showOptionDialog(
                parentComponent,
                message,
                "Something went wrong",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE,
                new FlatSVGIcon("tech/kuraudo/client/icons/material/alert.svg"),
                null,
                null
        );
    }
}
