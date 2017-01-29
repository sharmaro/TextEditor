package textdocument;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import fileChooser.SingleTypeFileChooser;
import frame.OfficeFrame;

public class TextDocumentManager extends JTabbedPane {
	private static final long serialVersionUID = -4649936834531540925L;

	/* Menu for the document manager */
	private JMenuBar mMenuBar;
	private final JMenu mFileMenu;
	private final JMenuItem mNewItem;
	private final JMenuItem mOpenItem;
	private final JMenuItem mSaveItem;
	private final JMenuItem mCloseItem;
	private final JMenuItem mCloseWindowItem;
	private File fontFile;
	private Font font;
	private ImageIcon imgIcon;
	public int tabCount = 0;
	private OfficeFrame ofcFrame;

	{	
		// Makes the selected tabbed pane an "orange" color
		UIManager.put("TabbedPane.selected", new Color(243, 97, 0));
		SwingUtilities.updateComponentTreeUI(this);
		
		initializeFont();
		
		// Setting text color on tabs
		setForeground(Color.BLACK);

		mFileMenu = new JMenu("File");
		mFileMenu.setMnemonic('F');
		mFileMenu.setFont(font.deriveFont(Font.BOLD, 12));
		mNewItem = new JMenuItem("New");
		mNewItem.setMnemonic('N');
		mNewItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mNewItem.setFont(font.deriveFont(Font.BOLD, 12));
		imgIcon = new ImageIcon("resources/img/menuitems/new.png");
		mNewItem.setIcon(imgIcon);
		mNewItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDocumentPanel toAdd = new TextDocumentPanel();
				TextDocumentManager.this.addTab("New", toAdd);
				TextDocumentManager.this.setSelectedIndex(TextDocumentManager.this.getTabCount() - 1);
				colorTabs();
				tabCount++;
				ofcFrame.addRemoveInitBackground();
			}
		});
		
		mOpenItem = new JMenuItem("Open");
		mOpenItem.setMnemonic('O');
		mOpenItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mOpenItem.setFont(font.deriveFont(Font.BOLD, 12));
		imgIcon = new ImageIcon("resources/img/menuitems/open.png");
		mOpenItem.setIcon(imgIcon);
		mOpenItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SingleTypeFileChooser txtChooser = new SingleTypeFileChooser("text files", "txt");
				txtChooser.setDialogTitle("Open File...");
				int returnValue = txtChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = txtChooser.getSelectedFile();
					if (!isFileOpen(file)) {
						try {
							TextDocumentPanel toAdd = new TextDocumentPanel(file);
							TextDocumentManager.this.addTab(file.getName(), toAdd);
							TextDocumentManager.this.setSelectedIndex(TextDocumentManager.this.getTabCount() - 1);
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null, file.getName() + " failed to be read.", "File Error",
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						selectTabByFile(file);
						JOptionPane.showMessageDialog(null, file.getName() + "is already open.");
					}
				}
				colorTabs();
				tabCount++;
				ofcFrame.addRemoveInitBackground();
			}
		});

		mSaveItem = new JMenuItem("Save");
		mSaveItem.setMnemonic('S');
		mSaveItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mSaveItem.setFont(font.deriveFont(Font.BOLD, 12));
		imgIcon = new ImageIcon("resources/img/menuitems/save.png");
		mSaveItem.setIcon(imgIcon);
		mSaveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDocumentPanel toSave = getActiveDocumentPanel();
				if (toSave == null)
					return;
				SingleTypeFileChooser txtChooser = new SingleTypeFileChooser("text files", "txt");
				txtChooser.setDialogTitle("Save File...");
				int returnValue = txtChooser.showSaveDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = txtChooser.getSelectedFile();
					boolean shouldSave = true;
					if (file.exists()) {
						int n = JOptionPane.showConfirmDialog(TextDocumentManager.this,
								file.getName() + " already exists\nDo you want to replace it?", "Confirm Save As",
								JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						if (n != 0)
							shouldSave = false;
					}
					if (shouldSave) {
						if (!file.getName().endsWith(".txt")) {
							JOptionPane.showMessageDialog(null, "The file must be .txt!", "Saving Error",
									JOptionPane.ERROR_MESSAGE);
						} else {
							try {
								getActiveDocumentPanel().save(file);
								setActiveTabText(file.getName());
							} catch (IOException e) {
								JOptionPane.showMessageDialog(null, file.getName() + " failed to be saved.",
										"Saving Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		});

		mCloseItem = new JMenuItem("Close");
		mCloseItem.setMnemonic('C');
		mCloseItem.setFont(font.deriveFont(Font.BOLD, 12));
		imgIcon = new ImageIcon("resources/img/menuitems/close.png");
		mCloseItem.setIcon(imgIcon);
		mCloseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selected = TextDocumentManager.this.getSelectedIndex();
				if (selected == -1)
					return;
				TextDocumentManager.this.remove(selected);
				colorTabs();
				tabCount--;
				ofcFrame.addRemoveInitBackground();
			}
		});

		addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				refreshMenuBar();
			}
		});

		mCloseWindowItem = new JMenuItem("Close Window");
		mCloseWindowItem.setMnemonic('C');
		mCloseWindowItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mCloseWindowItem.setFont(font.deriveFont(Font.BOLD, 12));
		mCloseWindowItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		});
	
		mFileMenu.add(mNewItem);
		mFileMenu.add(mOpenItem);
		mFileMenu.add(mSaveItem);
		mFileMenu.add(mCloseItem);
		mFileMenu.add(mCloseWindowItem);
	}
	
	public void initializeFont(){
		fontFile = new File("resources/fonts/kenvector_future_thin.ttf");
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			setFont(font.deriveFont(Font.BOLD, 12));
		} catch (FontFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	// Changes the color of tab that isn't selected to a light gray
	public void colorTabs(){
		if(getTabCount() > 0){
			int currTabIndex = getSelectedIndex();
			// Background color for tabs WITHOUT focus
			setBackgroundAt(currTabIndex, Color.lightGray);
			revalidate();
		}
	}

	// The manager takes a JMenubar from the parent frame so the user can
	// interact with it
	public TextDocumentManager(JMenuBar inMenuBar, OfficeFrame officeFrame) {
		mMenuBar = inMenuBar;
		refreshMenuBar();
		
		ofcFrame = officeFrame;
	}

	// Returns the DocumentPanel that is currently being worked on
	public TextDocumentPanel getActiveDocumentPanel() {
		int selected = getSelectedIndex();
		if (selected == -1)
			return null;
		Component toReturn = getComponentAt(selected);
		if (toReturn instanceof TextDocumentPanel)
			return (TextDocumentPanel) toReturn;
		else
			return null;
	}

	// Focuses on tab that is dealing with the file
	private void selectTabByFile(File file) {
		int index = -1;
		for (int i = 0; i < getComponentCount(); ++i) {
			Component atIndex = getComponentAt(i);
			if (atIndex instanceof TextDocumentPanel) {
				if (file.equals(((TextDocumentPanel) atIndex).getFile())) {
					index = i;
					break;
				}
			}
		}
		if (index != -1)
			setSelectedIndex(index);
	}

	// Changes the name of the tab
	private void setActiveTabText(String title) {
		int selected = getSelectedIndex();
		if (selected == -1)
			return;
		this.setTitleAt(selected, title);
	}

	// Checks to see if the document is already opened
	public boolean isFileOpen(File file) {
		for (Component component : TextDocumentManager.this.getComponents()) {
			if (component instanceof TextDocumentPanel) {
				if (file.equals(((TextDocumentPanel) component).getFile())) {
					return true;
				}
			}
		}
		return false;
	}

	// Called when documents are switched, sets menu bar items
	private void refreshMenuBar() {
		mMenuBar.removeAll();
		mMenuBar.add(mFileMenu);
		TextDocumentPanel activeDocument = getActiveDocumentPanel();
		if (activeDocument != null) {
			mMenuBar.add(activeDocument.getEditMenu());
			mMenuBar.add(activeDocument.getSpellCheckMenu());
		}
	}
	
	public int getNumTabs(){
		return tabCount;
	}
}