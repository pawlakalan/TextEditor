package editor;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextEditor extends JFrame {
    boolean isRegex = false;

    public TextEditor() {
        super("Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1000);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        //creating menubar and menu option
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        //adding menu items
        JMenuItem loadMenuItem = new JMenuItem("Load");
        loadMenuItem.setName("MenuLoad");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");

        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);

        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        //add jpanel for save and load buttons
        JPanel panel = new JPanel();
        panel.setName("Panel");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        add(panel, BorderLayout.NORTH);

        JLabel searchLabel = new JLabel("Search: ");
        JTextField searchField = new JTextField();
        searchField.setName("SearchField");
        searchField.setPreferredSize(new Dimension(200, 25));

        JButton saveButton = new JButton("Save");
        saveButton.setName("SaveButton");

        JButton loadButton = new JButton("Load");
        loadButton.setName("LoadButton");

        JButton startSearch = new JButton("Start search");
        loadButton.setName("StartSearchButton");
        JButton previousMatch = new JButton("Prev");
        loadButton.setName("PreviousMatchButton");

        JButton nextMatch = new JButton("Next");
        loadButton.setName("NextMatchButton");

        JLabel regexLabel = new JLabel("Use regex: ");
        JCheckBox regexCheckbox = new JCheckBox();

        panel.add(saveButton);
        panel.add(loadButton);
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(startSearch);
        panel.add(previousMatch);
        panel.add(nextMatch);
        panel.add(regexLabel);
        panel.add(regexCheckbox);


        //main area to write
        JTextArea textArea = new JTextArea();
        textArea.setName("TextArea");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setName("ScrollPane");
        add(scrollPane, BorderLayout.CENTER);

        //action listeners for clicking buttons and menu options
        loadButton.addActionListener(event -> {
            loadManager(textArea);
        });
        loadMenuItem.addActionListener(event -> {
            loadManager(textArea);
        });

        saveButton.addActionListener(event -> {
            saveManager(textArea);
        });
        saveMenuItem.addActionListener(event -> {
            saveManager(textArea);
        });
        exitMenuItem.addActionListener(event -> System.exit(0));

        regexCheckbox.addActionListener(event -> {
            if (isRegex == false) {
                isRegex = true;
            } else {
                isRegex = false;
            }
        });
        //TODO searching
        startSearch.addActionListener(event -> {
            int searchIndex = 0;
            List<String> foundStrings = search(textArea, searchField.getText(),isRegex);
            //System.out.println(foundStrings);
            try {
                highlightText(textArea, foundStrings.get(searchIndex));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

        });
    }

    private byte[] readFile(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            System.out.println("Can't read file!");
            return null;
        }
    }

    private void saveFile(String path, String content) {
        Path filePath = Paths.get(path);
        try (
                final BufferedWriter writer = Files.newBufferedWriter(filePath);
        ) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Cant save file!" + e);
        }
    }
    private void loadManager(JTextArea textArea) {
        JFileChooser FileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = FileChooser.showOpenDialog(null);
        String filePath = "";

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = FileChooser.getSelectedFile();
            filePath = selectedFile.getAbsolutePath();
        }
        if (filePath != null && filePath.trim().length() > 0) {
            byte[] bytes = readFile(filePath);
            if (bytes == null) {
                textArea.setText("");
            } else {
                textArea.setText(new String(bytes));
            }
        }
    }
    private void saveManager(JTextArea textArea) {
        JFileChooser FileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        FileChooser.setDialogTitle("Choose a directory to save your file: ");
        FileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        String filePath = "";
        int returnValue = FileChooser.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            filePath = FileChooser.getSelectedFile().toString();

        }
        if (filePath != null && filePath.trim().length() > 0) {
            saveFile(filePath, textArea.getText());
        }
    }

    //TODO searching and highlighting text
    private List<String> search(JTextArea textArea, String pattern, boolean isRegex) {
        List<String> text = new ArrayList<>(Arrays.asList(textArea.getText().split(" ")));
        List<String> foundText = new ArrayList<>();
        for (String word : text) {
            if (word.equals(pattern)) {
                foundText.add(word);
            }
        }
        return foundText;
    }
    private void highlightText(JTextArea textArea, String word) throws BadLocationException {
        Highlighter highlighter = textArea.getHighlighter();
        Highlighter.HighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
        highlighter.addHighlight(0, word.length(), highlightPainter);
    }
 }
