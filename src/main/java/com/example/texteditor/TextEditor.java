package com.example.texteditor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class TextEditor extends JFrame {
  
  private static final String ICONS_BASE_PATH = "pics/icons/";

  private String fileName;
  private final List<SearchResult> searchResults = new ArrayList<>();
  private SearchResult selectedSearchResult = new SearchResult();
  
  public TextEditor() {
    
    System.out.println("JFrame thread name: " + Thread.currentThread().getName()
        + (SwingUtilities.isEventDispatchThread() ? ". This is event dispatch thread " : ""));
    
    //JFrame initialization
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Dimension displaySize = Toolkit.getDefaultToolkit().getScreenSize();
    setSize(displaySize.width / 2, displaySize.height / 2);
    this.setLocationRelativeTo(null);
    setTitle("Text editor");
    
    //Menu
    JMenuBar menuBar = new JMenuBar();//Menu bar
    JMenu fileMenu = new JMenu("File");//Menu File
    JMenuItem openMenuItem = new JMenuItem("Open");//Menu item Open
    JMenuItem saveMenuItem = new JMenuItem("Save");//Menu item Save
    JMenuItem exitMenuItem = new JMenuItem("Exit");//Menu item Exit
    JMenu searchMenu = new JMenu("Search");//Menu Search
    JMenuItem startSearchMenuItem = new JMenuItem("Start search");//Menu item Start search
    JMenuItem previousMatchMenuItem = new JMenuItem("Previous match");//Menu item Previous match
    JMenuItem nextMatchMenuItem = new JMenuItem("Next match");//Menu item Next match
    JMenuItem regexMenuItem = new JMenuItem("Use regular expressions");//Menu item Next match
    
    //Toolbar
    JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    
    //button icons
    Icon openButtonIcon;
    Icon saveButtonIcon;
    Icon searchButtonIcon;
    Icon prevButtonIcon;
    Icon nextButtonIcon;
    int TOOLBAR_BUTTON_SIZE = 24;
    openButtonIcon = loadToolbarIcon(ICONS_BASE_PATH + "openfile.png", TOOLBAR_BUTTON_SIZE);
    saveButtonIcon = loadToolbarIcon(ICONS_BASE_PATH + "savefile.png", TOOLBAR_BUTTON_SIZE);
    searchButtonIcon = loadToolbarIcon(ICONS_BASE_PATH + "search.png", TOOLBAR_BUTTON_SIZE);
    prevButtonIcon = loadToolbarIcon(ICONS_BASE_PATH + "prevmatch.png", TOOLBAR_BUTTON_SIZE);
    nextButtonIcon = loadToolbarIcon(ICONS_BASE_PATH + "nextmatch.png", TOOLBAR_BUTTON_SIZE);
    
    //buttons
    JButton openButton = new JButton(openButtonIcon);//open button
    JButton saveButton = new JButton(saveButtonIcon);//save button
    JLabel searchFieldLabel = new JLabel("   Search:");//search field label
    JTextField searchPatternField = new JTextField(12);//search field
    JButton startSearchButton = new JButton(searchButtonIcon);//run the search button
    JButton prevSearchButton = new JButton(prevButtonIcon);//previous search result
    JButton nextSearchButton = new JButton(nextButtonIcon);//next search result
    JCheckBox isRegexCheckBox = new JCheckBox("Use regex");//regex checkbox
    
    //File chooser
    final JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    
    //Text area and scroll pane
    JTextArea textArea = new JTextArea();//Text area
    JScrollPane scrollPane = new JScrollPane(//Scroll pane
        textArea,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
    );
    
    //Menu mnemonics
    fileMenu.setMnemonic(KeyEvent.VK_F);
    openMenuItem.setMnemonic(KeyEvent.VK_O);
    saveMenuItem.setMnemonic(KeyEvent.VK_S);
    exitMenuItem.setMnemonic(KeyEvent.VK_X);
    searchMenu.setMnemonic(KeyEvent.VK_S);
    startSearchMenuItem.setMnemonic(KeyEvent.VK_S);
    previousMatchMenuItem.setMnemonic(KeyEvent.VK_P);
    nextMatchMenuItem.setMnemonic(KeyEvent.VK_N);
    regexMenuItem.setMnemonic(KeyEvent.VK_R);
    
    //Menu names
    fileMenu.setName("MenuFile");
    openMenuItem.setName("MenuOpen");
    saveMenuItem.setName("MenuSave");
    exitMenuItem.setName("MenuExit");
    searchMenu.setName("MenuSearch");
    startSearchMenuItem.setName("MenuStartSearch");
    previousMatchMenuItem.setName("MenuPreviousMatch");
    nextMatchMenuItem.setName("MenuNextMatch");
    regexMenuItem.setName("MenuUseRegExp");
    
    //Toolbar names
    openButton.setName("OpenButton");
    saveButton.setName("SaveButton");
    searchPatternField.setName("SearchField");
    startSearchButton.setName("StartSearchButton");
    prevSearchButton.setName("PreviousMatchButton");
    nextSearchButton.setName("NextMatchButton");
    isRegexCheckBox.setName("UseRegExCheckbox");
    
    //Toolbar buttons size
    for (JButton button : List.of(openButton, saveButton, startSearchButton, prevSearchButton, nextSearchButton)) {
      button.setSize(TOOLBAR_BUTTON_SIZE, TOOLBAR_BUTTON_SIZE);
      button.setMargin(new Insets(0, 0, 0, 0));
    }
    //search field size
    int SEARCH_FIELD_WIDTH = 60;
    int TEXT_FIELD_PADDING = 4;
    int SEARCH_FIELD_HEIGHT = TOOLBAR_BUTTON_SIZE + TEXT_FIELD_PADDING * 2;
    searchPatternField.setPreferredSize(new Dimension(SEARCH_FIELD_WIDTH, SEARCH_FIELD_HEIGHT));
    
    //Chooser name
    fileChooser.setName("FileChooser");
    
    //Text area name and settings
    textArea.setName("TextArea");
    textArea.setCaretPosition(0);
    scrollPane.setName("ScrollPane");
    
    //Action listeners
    ActionListener onOpenAction = _ -> {//On "Open file" command
      int returnVal = fileChooser.showOpenDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        try {
          fileName = fileChooser.getSelectedFile().getCanonicalPath();
          if (new File(fileName).exists()) {
            textArea.setText(new String(Files.readAllBytes(Paths.get(fileName))));
          } else {
            textArea.setText("");
          }
          textArea.grabFocus();
        } catch (IOException ignored) {
        }
      }
    };
    
    ActionListener onSaveAction = _ -> {//On the "Save file" command
      int returnVal = fileChooser.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        try {
          fileName = fileChooser.getSelectedFile().getCanonicalPath();
        } catch (IOException ignored) {
        }
        try (FileWriter writer = new FileWriter(fileName, false)) {
          writer.write(textArea.getText());
        } catch (IOException ignored) {
        }
      }
    };
    
    ActionListener onExitAction = _ -> dispose(); //On the "Exit app" command
    
    ActionListener onUseRegExMenuCommand = _ -> { //On the "Switch regex" mode command
        isRegexCheckBox.setSelected(!isRegexCheckBox.isSelected());
    };
    
    ActionListener onSearchStart = _ -> //On "Start Search" command
        //create SwingWorker task and run background search in the SwingWorker thread
        (new TextSearchTask(textArea, searchPatternField.getText(), textArea.getCaretPosition(),
            searchResults, selectedSearchResult, isRegexCheckBox.isSelected())).execute();
    
    ActionListener onPreviousMatch = event -> {//On the "Move to the previous search result" command,
      //select the previous match from the searchResults list in the textArea (in the current thread)
      if (!searchResults.isEmpty()) {
        selectedSearchResult = searchResults.get(
            //Cyclic previous index: if the current is the first, go to the last.
            (selectedSearchResult.getIndexInSearchResults() + searchResults.size() - 1) % searchResults.size()
        );
        TextSearchTask.showSearchResult(
            textArea,
            selectedSearchResult.getCaretPosition(),
            selectedSearchResult.getSize()
        );
      } else {
        onSearchStart.actionPerformed(event);
      }
    };
    
    ActionListener onNextMatch = event -> {//On the "Move to the next search result" command,
      //select the next match from the searchResults list in the textArea (in the current thread)
      if (!searchResults.isEmpty()) {
        selectedSearchResult = searchResults.get(
            //Cyclic next index: if the current is the last, go to the first.
            (selectedSearchResult.getIndexInSearchResults() + 1) % searchResults.size()
        );
        TextSearchTask.showSearchResult(
            textArea,
            selectedSearchResult.getCaretPosition(),
            selectedSearchResult.getSize()
        );
      } else {
        onSearchStart.actionPerformed(event);
      }
    };
    
    DocumentListener onSearchPatternChange = new DocumentListener() {//clear search results on any content change
      @Override
      public void insertUpdate(DocumentEvent documentEvent) {
        searchResults.clear();
      }
      
      @Override
      public void removeUpdate(DocumentEvent documentEvent) {
        searchResults.clear();
      }
      
      @Override
      public void changedUpdate(DocumentEvent documentEvent) {
        searchResults.clear();
      }
    };
    
    //add menu actions listeners
    openMenuItem.addActionListener(onOpenAction);
    saveMenuItem.addActionListener(onSaveAction);
    exitMenuItem.addActionListener(onExitAction);
    startSearchMenuItem.addActionListener(onSearchStart);
    previousMatchMenuItem.addActionListener(onPreviousMatch);
    nextMatchMenuItem.addActionListener(onNextMatch);
    regexMenuItem.addActionListener(onUseRegExMenuCommand);
    //add toolbar buttons actions listeners
    openButton.addActionListener(onOpenAction);
    saveButton.addActionListener(onSaveAction);
    searchPatternField.addActionListener(onSearchStart);
    startSearchButton.addActionListener(onSearchStart);
    prevSearchButton.addActionListener(onPreviousMatch);
    nextSearchButton.addActionListener(onNextMatch);
    //add  listeners on text fields content change
    searchPatternField.getDocument().addDocumentListener(onSearchPatternChange);
    textArea.getDocument().addDocumentListener(onSearchPatternChange);
    
    //add file menu items
    fileMenu.add(openMenuItem);
    fileMenu.add(saveMenuItem);
    fileMenu.addSeparator();
    fileMenu.add(exitMenuItem);
    
    //add search menu items
    searchMenu.add(startSearchMenuItem);
    searchMenu.add(previousMatchMenuItem);
    searchMenu.add(nextMatchMenuItem);
    searchMenu.add(regexMenuItem);
    
    //add menus to the menu bar
    menuBar.add(fileMenu);
    menuBar.add(searchMenu);
    
    //add controls to the toolbar panel
    toolbarPanel.add(openButton);
    toolbarPanel.add(saveButton);
    toolbarPanel.add(searchFieldLabel);
    toolbarPanel.add(searchPatternField);
    toolbarPanel.add(startSearchButton);
    toolbarPanel.add(prevSearchButton);
    toolbarPanel.add(nextSearchButton);
    toolbarPanel.add(isRegexCheckBox);
    
    //set menu bar to JFrame
    setJMenuBar(menuBar);
    
    //add toolbar to the north of the window
    add(toolbarPanel, BorderLayout.NORTH);
    
    //add file chooser and text area with a scroll panel
    add(fileChooser);
    add(scrollPane, BorderLayout.CENTER);
    
    //Show window
    setVisible(true);
    
  }
  
  private static Icon loadToolbarIcon(String resourcePath, int size) {
    var resource = TextEditor.class.getClassLoader().getResource(resourcePath);
    if (resource == null) {
      return null;
    }
    try {
      return new ImageIcon(ImageIO.read(resource)
          .getScaledInstance(size, size, Image.SCALE_SMOOTH));
    } catch (IOException ignored) {
      return null;
    }
  }
  
}
