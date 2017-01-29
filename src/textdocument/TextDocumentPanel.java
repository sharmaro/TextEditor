package textdocument;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicScrollBarUI;

import spellcheck.SpellCheckHelper;
import spellcheck.SpellCheckPanel;

/*
This Panel is responsible for displaying the TextComponent
It contains a menu that aids in editing the text component it owns
It also has a SpellChekerHelper that it can use to check the spelling of the text
*/
public class TextDocumentPanel extends JPanel {

	private static final long serialVersionUID = -3927634294406617454L;

	private final JScrollPane mScrollPane;
	private JScrollBar mScrollBar;
	private final JTextArea mTextPane;
	private File mFile;

	private final TextDocumentHistoryHelper mTextDocumentHistoryHelper;
	private final JMenu mEditMenu;
	private final JMenuItem mUndoItem;
	private final JMenuItem mRedoItem;
	private final JMenuItem mCutItem;
	private final JMenuItem mCopyItem;
	private final JMenuItem mPasteItem;
	private final JMenuItem mSelectAllItem;

	private final SpellCheckHelper mSpellCheckHelper;
	private final JMenu mSpellCheckMenu;
	private final JMenuItem mRunItem;
	private final JMenuItem mConfigureItem;
	private File fontFile;
	private Font font;
	private ImageIcon imgIcon;

	{
		// Changing the color of the selected text to orange
		UIManager.put("TextArea.selectionBackground", new javax.swing.plaf.ColorUIResource(new Color(242, 139, 71)));
		
		initializeFont();

		setLayout(new BorderLayout());
		mScrollPane = new JScrollPane();
		mScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		mScrollBar = mScrollPane.getVerticalScrollBar();
		// Making the scroll bar custom
		mScrollBar.setUI(new MyScrollbarUI());
		mScrollBar.setBackground(Color.GRAY);
		
		// Changed to JTextArea for line wrapping
		mTextPane = new JTextArea();
		mTextPane.setLineWrap(true);
		mTextPane.setWrapStyleWord(true);
		mTextPane.setFont(font.deriveFont(Font.BOLD, 12));
		mTextPane.setBackground(Color.GRAY);
		mTextPane.setForeground(Color.WHITE);
		mScrollPane.getViewport().add(mTextPane);
		add(mScrollPane, "Center");

		mTextDocumentHistoryHelper = new TextDocumentHistoryHelper(mTextPane.getDocument());

		mEditMenu = new JMenu("Edit");
		mEditMenu.setMnemonic('E');
		mEditMenu.setFont(font.deriveFont(Font.BOLD, 12));

		mUndoItem = mTextDocumentHistoryHelper.getUndoMenuItem();
		mUndoItem.setFont(font.deriveFont(Font.BOLD, 12));
		imgIcon = new ImageIcon("resources/img/menuitems/undo.png");
		mUndoItem.setIcon(imgIcon);

		mRedoItem = mTextDocumentHistoryHelper.getRedoMenuItem();
		mRedoItem.setFont(font.deriveFont(Font.BOLD, 12));
		imgIcon = new ImageIcon("resources/img/menuitems/redo.png");
		mRedoItem.setIcon(imgIcon);

		mCutItem = new JMenuItem("Cut");
		mCutItem.setMnemonic('C');
		mCutItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mCutItem.setFont(font.deriveFont(Font.BOLD, 12));
		imgIcon = new ImageIcon("resources/img/menuitems/cut.png");
		mCutItem.setIcon(imgIcon);
		mCutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.cut();
			}
		});

		mCopyItem = new JMenuItem("Copy");
		mCopyItem.setMnemonic('C');
		mCopyItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mCopyItem.setFont(font.deriveFont(Font.BOLD, 12));
		imgIcon = new ImageIcon("resources/img/menuitems/copy.png");
		mCopyItem.setIcon(imgIcon);
		mCopyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.copy();
			}
		});

		mPasteItem = new JMenuItem("Paste");
		mPasteItem.setMnemonic('P');
		mPasteItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mPasteItem.setFont(font.deriveFont(Font.BOLD, 12));
		imgIcon = new ImageIcon("resources/img/menuitems/paste.png");
		mPasteItem.setIcon(imgIcon);
		mPasteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.paste();
			}
		});

		mSelectAllItem = new JMenuItem("Select All");
		mSelectAllItem.setMnemonic('S');
		mSelectAllItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mSelectAllItem.setFont(font.deriveFont(Font.BOLD, 12));
		imgIcon = new ImageIcon("resources/img/menuitems/select.png");
		mSelectAllItem.setIcon(imgIcon);
		mSelectAllItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mTextPane.selectAll();
			}
		});

		mEditMenu.add(mUndoItem);
		mEditMenu.add(mRedoItem);
		mEditMenu.add(new JSeparator());
		mEditMenu.add(mCutItem);
		mEditMenu.add(mCopyItem);
		mEditMenu.add(mPasteItem);
		mEditMenu.add(new JSeparator());
		mEditMenu.add(mSelectAllItem);

		mSpellCheckHelper = new SpellCheckHelper();
		mSpellCheckMenu = new JMenu("SpellCheck");
		mSpellCheckMenu.setMnemonic('S');
		mSpellCheckMenu.setFont(font.deriveFont(Font.BOLD, 12));

		mRunItem = new JMenuItem("Run");
		mRunItem.setMnemonic('R');
		mRunItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
		mRunItem.setFont(font.deriveFont(Font.BOLD, 12));
		imgIcon = new ImageIcon("resources/img/menuitems/run.png");
		mRunItem.setIcon(imgIcon);
		mRunItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JPanel spellCheckPanel = mSpellCheckHelper.getSpellCheckPanel();
				if (TextDocumentPanel.this.getComponentCount() == 1) {
					TextDocumentPanel.this.add(spellCheckPanel, "East");
					if (spellCheckPanel instanceof SpellCheckPanel)
						((SpellCheckPanel) spellCheckPanel).runSpellCheck(mTextPane);
				}
				revalidate();
				repaint();
			}
		});

		mConfigureItem = new JMenuItem("Configure");
		mConfigureItem.setMnemonic('C');
		mConfigureItem.setFont(font.deriveFont(Font.BOLD, 12));
		imgIcon = new ImageIcon("resources/img/menuitems/configure.png");
		mConfigureItem.setIcon(imgIcon);
		mConfigureItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (TextDocumentPanel.this.getComponentCount() == 1)
					TextDocumentPanel.this.add(mSpellCheckHelper.getConfigurePanel(), "East");
				revalidate();
				repaint();
			}
		});

		mSpellCheckMenu.add(mRunItem);
		mSpellCheckMenu.add(mConfigureItem);

	}
	
	public void initializeFont(){
		fontFile = new File("resources/fonts/kenvector_future_thin.ttf");
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			this.setFont(font.deriveFont(Font.BOLD, 12));
		} catch (FontFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// Reads in a file to the text pane so user can edit
	public TextDocumentPanel() {
	}

	public TextDocumentPanel(File inFile) throws IOException {
		mFile = inFile;
		FileReader fr = new FileReader(inFile);
		mTextPane.read(fr, "");
		fr.close();
	}

	// Gets the edit menu bar for the document
	public JMenu getEditMenu() {
		return mEditMenu;
	}

	// Gets the spell check menu for the document
	public JMenu getSpellCheckMenu() {
		return mSpellCheckMenu;
	}

	// File currently being operated on
	public File getFile() {
		return mFile;
	}

	// Saves the file to disk
	public void save(File inFile) throws IOException {
		mFile = inFile;
		FileWriter fw = new FileWriter(mFile);
		fw.write(mTextPane.getText());
		fw.close();
	}
}

// Custom scrollbar class I made
class MyScrollbarUI extends BasicScrollBarUI {
	public Image imageThumb, imageTrack;
	public ImageIcon downArrow, upArrow;

	MyScrollbarUI() {
		try {
			imageThumb = ImageIO.read(new File("resources/img/scrollbar/red_button05.png"));
			imageTrack = ImageIO.read(new File("resources/img/scrollbar/red_button03.png"));
			downArrow = new ImageIcon("resources/img/scrollbar/red_sliderDown.png");
			upArrow = new ImageIcon("resources/img/scrollbar/red_sliderUp.png");
		} catch (IOException e) {
		}
	}

	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		((Graphics2D) g).drawImage(imageThumb, thumbBounds.x, 
								   thumbBounds.y, thumbBounds.width, thumbBounds.height, null);
	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle thumbBounds) {
		((Graphics2D) g).drawImage(imageTrack, thumbBounds.x, 
				   				   thumbBounds.y, thumbBounds.width, thumbBounds.height, null);
	}
	
	@Override
	protected JButton createDecreaseButton(int orientation) {
        JButton decreaseButton = new JButton(upArrow){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(22, 22);
            }
        };
        return decreaseButton;
    }
	
	@Override
	protected JButton createIncreaseButton(int orientation) {
        JButton decreaseButton = new JButton(downArrow){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(22, 22);
            }
        };
        return decreaseButton;
    }
}