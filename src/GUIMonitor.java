import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The GUI for assignment 4
 */
public class GUIMonitor {
    /**
     * These are the components you need to handle.
     * You have to add listeners and/or code
     */
    private JFrame frame;                // The Main window
    private JMenu fileMenu;                // The menu
    private JMenuItem openItem;            // File - open
    private JMenuItem saveItem;            // File - save as
    private JMenuItem exitItem;            // File - exit
    private JTextField txtFind;            // Input string to find
    private JTextField txtReplace;        // Input string to replace
    private JCheckBox chkNotify;        // User notification choise
    private JLabel lblInfo;                // Hidden after file selected
    private JButton btnCreate;            // Start copying
    private JButton btnClear;            // Removes dest. file and removes marks
    private JLabel lblChanges;            // Label telling number of replacements

    private JTextPane txtPaneSource;
    private JTextPane txtPaneDest;

    private ButtonListener listener = new ButtonListener();
    private String find = "";
    private String replace = "";
    private boolean notify = false;
    private int matches = 0;

    private File selectedFile;
    private Controller controller;

    JFileChooser fileChooser = new JFileChooser();

    /**
     * Constructor
     */
    public GUIMonitor() {
    }

    /**
     * Starts the application
     */
    public void Start() {
        frame = new JFrame();
        frame.setBounds(0, 0, 714, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setTitle("Text File Copier - with Find and Replace");
        InitializeGUI();                    // Fill in components
        frame.setVisible(true);
        frame.setResizable(false);            // Prevent user from change size
        frame.setLocationRelativeTo(null);    // Start middle screen
    }

    /**
     * Sets up the GUI with components
     */
    private void InitializeGUI() {
        fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open Source File");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        saveItem = new JMenuItem("Save Destination File As");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveItem.setEnabled(false);
        exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        JMenuBar bar = new JMenuBar();
        frame.setJMenuBar(bar);
        bar.add(fileMenu);

        JPanel pnlFind = new JPanel();
        pnlFind.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Find and Replace"));
        pnlFind.setBounds(12, 32, 436, 122);
        pnlFind.setLayout(null);
        frame.add(pnlFind);
        JLabel lab1 = new JLabel("Find:");
        lab1.setBounds(7, 30, 80, 13);
        pnlFind.add(lab1);
        JLabel lab2 = new JLabel("Replace with:");
        lab2.setBounds(7, 63, 80, 13);
        pnlFind.add(lab2);

        txtFind = new JTextField();
        txtFind.setBounds(88, 23, 327, 20);
        txtFind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        pnlFind.add(txtFind);
        txtReplace = new JTextField();
        txtReplace.setBounds(88, 60, 327, 20);
        pnlFind.add(txtReplace);
        chkNotify = new JCheckBox("Notify user on every match");
        chkNotify.setBounds(88, 87, 180, 17);
        pnlFind.add(chkNotify);

        lblInfo = new JLabel("Select Source File..");
        lblInfo.setBounds(485, 42, 120, 13);
        frame.add(lblInfo);

        btnCreate = new JButton("Copy to Destination");
        btnCreate.setEnabled(false);
        btnCreate.setBounds(465, 119, 230, 23);
        frame.add(btnCreate);
        btnClear = new JButton("Clear dest. and remove marks");
        btnClear.setEnabled(false);
        btnClear.setBounds(465, 151, 230, 23);
        frame.add(btnClear);

        lblChanges = new JLabel("No. of Replacements:");
        lblChanges.setBounds(279, 161, 200, 13);
        frame.add(lblChanges);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(12, 170, 653, 359);
        frame.add(tabbedPane);
        txtPaneSource = new JTextPane();
        JScrollPane scrollSource = new JScrollPane(txtPaneSource);
        tabbedPane.addTab("Source", null, scrollSource, null);
        txtPaneDest = new JTextPane();
        JScrollPane scrollDest = new JScrollPane(txtPaneDest);
        tabbedPane.addTab("Destination", null, scrollDest, null);
        addListeners(); //adding listeners to th buttons
    }

    /**
     * Method for adding listeners to all the buttons
     */
    private void addListeners() {
        saveItem.addActionListener(listener);
        openItem.addActionListener(listener);
        exitItem.addActionListener(listener);
        btnCreate.addActionListener(listener);
        btnClear.addActionListener(listener);
    }

    /**
     * Method for adding text to the source panel for a text file
     *
     * @param path the path to the text file
     */
    public void appendSource(String path) {
        txtPaneSource.setText("");
        try {
            File file = new File(path);
            FileReader fr = new FileReader(file);
            while (fr.read() != -1) {
                txtPaneSource.read(fr, null);
            }
            fr.close();
        } catch (Exception e) {
        }
    }

    /**
     * Method for clearing the destination text panel
     */
    public void clearDest() {
        txtPaneDest.setText("");
    }

    /**
     * Updates label that shows how many hits
     *
     * @param matches the number of hits
     */
    public void changes(int matches) {
        System.out.println("HIT KOM JAG NU");
        this.matches = matches;
        lblChanges.setText(lblChanges.getText() + " " + matches);
    }

    /**
     * Class that handles the Listeners for the buttons
     */
    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == openItem) {
                chooseFile();
                if (selectedFile != null) {
                    lblInfo.setText("");
                    appendSource(selectedFile.getAbsolutePath());
                }
            } else if (e.getSource() == saveItem) {
                saveFile();
            } else if (e.getSource() == exitItem) {
                System.exit(0);
            } else if (e.getSource() == btnCreate) {
                btnClear.setEnabled(true);
                btnCreate.setEnabled(false);
                notify = chkNotify.isSelected();
                find = txtFind.getText();
                replace = txtReplace.getText();
                init();
            } else if (e.getSource() == btnClear) {
                clear();
                btnCreate.setEnabled(true);
                btnClear.setEnabled(false);
            }
        }
    }

    /**
     * Method for initiating the controller that starts the threads
     */
    private void init() {
        controller = new Controller(this, txtPaneSource, txtPaneDest, notify, find, replace);
    }

    /**
     * Method for saving the new text as a file. Saves the files in the resources directory
     * with ascending filenames
     */
    public void saveFile() {
        File file = new File("./resources/temp.txt");
        int num = 0;
        while (file.exists()) {
            num++;
            file = new File("./resources/temp" + num + ".txt");
        }
        try (FileWriter fw = new FileWriter(file)) {  //new File("./resources/temp.txt")
            fw.write(txtPaneDest.getText());
        } catch (Exception e) {

        }
    }

    /**
     * Method for clearing destination panel and markings.
     */
    private void clear() {
        clearDest();
        matches = 0;
        lblChanges.setText("No. of Replacements:");
        txtFind.setText("");
        txtReplace.setText("");
        controller.removeHighlite();
    }

    /**
     * Method for displaying a filechooser so you can choose a text file.
     */
    public void chooseFile() {
        fileChooser.setCurrentDirectory(new File("./resources/")); //System.getProperty("user.dir")
        FileFilter filter = new FileNameExtensionFilter("Text file", new String[]{"txt"});
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            saveItem.setEnabled(true);
            btnCreate.setEnabled(true);
            System.out.println(selectedFile.getName());
        }
    }
}


