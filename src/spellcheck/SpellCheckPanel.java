package spellcheck;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.MatchResult;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.JTextComponent;

import wordhelper.SpellChecker;

/*
 * The spell check panel runs the actual spell check
 * It takes in a text component in which it scans through
 * it offers suggestions based on its SpellChecker
 * and updates the text component based on user choice
 * */

public class SpellCheckPanel extends JPanel {
	private static final long serialVersionUID = 1203713931331563215L;

	private final SpellChecker mSpellChecker;
	private Scanner mScanner;
	private MatchResult mMatchResult;
	private JTextComponent mTextComponent;

	private final JLabel mSpellingLabel;
	private final JButton mIgnoreButton;
	private final JButton mAddButton;
	private final JComboBox<String> mChangeOptions;
	private final JButton mChangeButton;
	private final JButton mCloseButton;

	private int offset;

	private File fontFile;
	private Font font;
	private TitledBorder titleBorder;
	private ImageIcon buttonUnselectedIcon, buttonSelectedIcon;

	{
		initializeFont();
		
		buttonSelectedIcon = new ImageIcon("resources/img/menu/red_button11_selected.png");
		buttonUnselectedIcon = new ImageIcon("resources/img/menu/red_button11.png");

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		titleBorder = new TitledBorder("Spell Check");
		titleBorder.setTitleFont(font.deriveFont(Font.BOLD, 12));
		setBorder(titleBorder);

		mSpellingLabel = new JLabel("N/A") {
			private static final long serialVersionUID = -438929501552222784L;

			{
				setFont(getFont().deriveFont(16.0f));
			}

			@Override
			public void setText(String text) {
				super.setText("Spelling: " + text);
			}
		};
		mSpellingLabel.setFont(font.deriveFont(Font.BOLD, 12));
		mSpellingLabel.setBackground(Color.GRAY);

		mIgnoreButton = new JButton("Ignore");
		mIgnoreButton.setFont(font.deriveFont(Font.BOLD, 12));
		mIgnoreButton.setIcon(new ImageIcon("resources/img/menu/red_button11.png"));
		mIgnoreButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mIgnoreButton.setPreferredSize(new Dimension(88, 24));
		mIgnoreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				next();
			}
		});
		changeWhenMouseOver(mIgnoreButton, 88, 24);

		mAddButton = new JButton("Add");
		mAddButton.setFont(font.deriveFont(Font.BOLD, 12));
		mAddButton.setIcon(new ImageIcon("resources/img/menu/red_button11.png"));
		mAddButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mAddButton.setPreferredSize(new Dimension(88, 24));
		mAddButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mSpellChecker.addWordToDictionary(mTextComponent.getText().substring(mMatchResult.start() + offset,
							mMatchResult.end() + offset));
					next();
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(null, "Word failed to save!\n Please check configurations.",
							"File Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		changeWhenMouseOver(mAddButton, 88, 24);

		mChangeOptions = new JComboBox<String>();
		mChangeOptions.setFont(font.deriveFont(Font.BOLD, 12));
		mChangeOptions.setUI(MyComboBoxArrow.createUI(mChangeOptions));
		mChangeButton = new JButton("Change");
		mChangeButton.setFont(font.deriveFont(Font.BOLD, 12));
		mChangeButton.setIcon(new ImageIcon("resources/img/menu/red_button11.png"));
		mChangeButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mChangeButton.setPreferredSize(new Dimension(100, 24));
		mChangeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				focusSpellingError();
				String choice = mChangeOptions.getSelectedItem().toString();
				offset += choice.length() - (mMatchResult.end() - mMatchResult.start());
				mTextComponent.setEditable(true);
				mTextComponent.replaceSelection(choice);
				mTextComponent.setEditable(false);
				next();
			}
		});
		changeWhenMouseOver(mChangeButton, 100, 24);

		mCloseButton = new JButton("Close");
		mCloseButton.setFont(font.deriveFont(Font.BOLD, 12));
		mCloseButton.setIcon(new ImageIcon("resources/img/menu/red_button11.png"));
		mCloseButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mCloseButton.setPreferredSize(new Dimension(180, 24));
		mCloseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		changeWhenMouseOver(mCloseButton, 180, 24);

		JPanel titlePanel = new JPanel();
		titlePanel.add(mSpellingLabel);
		titlePanel.setBackground(Color.GRAY);

		JPanel addIgnorePanel = new JPanel();
		addIgnorePanel.add(mIgnoreButton);
		addIgnorePanel.add(mAddButton);
		addIgnorePanel.setBackground(Color.GRAY);

		JPanel changePanel = new JPanel();
		changePanel.add(mChangeOptions);
		changePanel.add(mChangeButton);
		changePanel.setBackground(Color.GRAY);

		JPanel optionsPanel = new JPanel(new BorderLayout());
		optionsPanel.add(addIgnorePanel, "North");
		optionsPanel.add(changePanel, "Center");
		optionsPanel.setBackground(Color.GRAY);

		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(new BorderLayout());
		footerPanel.add(mCloseButton, "South");
		footerPanel.setBackground(Color.GRAY);

		add(titlePanel);
		add(optionsPanel);
		add(Box.createVerticalGlue());
		add(footerPanel);

		offset = 0;
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

	// Changing button image when mouse rolls over
	public void changeWhenMouseOver(JButton thisButton, int width, int height) {
		thisButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				thisButton.setIcon(buttonSelectedIcon);
				thisButton.setHorizontalTextPosition(SwingConstants.CENTER);
				thisButton.setPreferredSize(new Dimension(width, height));
			}

			public void mouseExited(MouseEvent evt) {
				thisButton.setIcon(buttonUnselectedIcon);
				thisButton.setHorizontalTextPosition(SwingConstants.CENTER);
				thisButton.setPreferredSize(new Dimension(width, height));
			}
		});
	}

	SpellCheckPanel(SpellChecker inSpellChecker) {
		mSpellChecker = inSpellChecker;
	}

	// Starts the spell check sequence
	public void runSpellCheck(JTextComponent inTextComponent) {
		mTextComponent = inTextComponent;
		mTextComponent.setEditable(false);
		mScanner = new Scanner(mTextComponent.getText());
		mScanner.useDelimiter("([^A-Za-z])");
		offset = 0;
		next();
	}

	// Moves to the next word, exits if done
	private void next() {
		String word = null;
		while (mScanner.hasNext() && !mSpellChecker.isSpellingError(word = mScanner.next()))
			word = null;
		if (word == null) {
			close();
			JOptionPane.showMessageDialog(null, "The SpellCheck is Complete.");
			return;
		}
		word = word.toLowerCase();
		mMatchResult = mScanner.match();
		mSpellingLabel.setText(word);
		mChangeOptions.removeAllItems();
		for (String suggestion : mSpellChecker.getSpellingSuggestions(word, 10))
			mChangeOptions.addItem(suggestion);
		if (mChangeOptions.getSelectedIndex() == -1)
			mChangeButton.setEnabled(false);
		else
			mChangeButton.setEnabled(true);
		focusSpellingError();
	}

	// Selects the spelling error in the text component
	private void focusSpellingError() {
		mTextComponent.requestFocus();
		mTextComponent.setCaretPosition(mMatchResult.start() + offset);
		mTextComponent.setSelectionStart(mMatchResult.start() + offset);
		mTextComponent.setSelectionEnd(mMatchResult.end() + offset);
	}

	// Exits the spell checker
	private void close() {
		offset = 0;
		mTextComponent.setEditable(true);
		Container parent = getParent();
		if (parent != null) {
			parent.remove(this);
			parent.revalidate();
			parent.repaint();
		}
	}
}

// Custom ComboBox class that extends BasicComboBox UI
// Used for changing arrow button image on default combo boxes
class MyComboBoxArrow extends BasicComboBoxUI {
	public static ComboBoxUI createUI(JComponent c) {
		return new MyComboBoxArrow();
	}

	@Override
	protected JButton createArrowButton() {
		ImageIcon newButton = new ImageIcon("resources/img/scrollbar/red_sliderDown.png");
		JButton newArrow = new JButton(newButton);
		return newArrow;
	}

}
