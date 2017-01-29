package spellcheck;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import fileChooser.SingleTypeFileChooser;
import wordhelper.SpellChecker;

/*
 * The Configuration Panel takes in a SpellChecker in which
 * it can modify the current keyboard and wordlist setup
 * */

public class ConfigurationPanel extends JPanel {
	private static final long serialVersionUID = 2796044961643727103L;

	private final SpellChecker mSpellChecker;
	private final JLabel mWlFileLabel;
	private final JButton mWlFileChooserButton;
	private final JLabel mKbFileLabel;
	private final JButton mKbFileChooserButton;
	private final JButton mCloseButton;
	private File fontFile;
	private Font font;
	private TitledBorder titledBorder;
	private ImageIcon buttonUnselectedIcon, buttonSelectedIcon;

	{
		setBackground(Color.GRAY);
		
		initializeFont();
		
		buttonSelectedIcon = new ImageIcon("resources/img/menu/red_button11_selected.png");
		buttonUnselectedIcon = new ImageIcon("resources/img/menu/red_button11.png");

		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		titledBorder = new TitledBorder("Configure");
		titledBorder.setTitleFont(font.deriveFont(Font.BOLD, 12));
		setBorder(titledBorder);

		mWlFileLabel = new JLabel("N/A") {
			private static final long serialVersionUID = 1L;

			@Override
			public void setText(String text) {
				super.setText(".wl: " + text);
			}
		};
		mWlFileLabel.setFont(font.deriveFont(Font.BOLD, 12));

		mWlFileChooserButton = new JButton("Select WordList...");
		mWlFileChooserButton.setFont(font.deriveFont(Font.BOLD, 12));
		mWlFileChooserButton.setIcon(new ImageIcon("resources/img/menu/red_button11.png"));
		mWlFileChooserButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mWlFileChooserButton.setPreferredSize(new Dimension(180, 24));
		mWlFileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SingleTypeFileChooser wlChooser = new SingleTypeFileChooser("Word List", ".wl");
				wlChooser.setDialogTitle("Select Wordlist...");
				int returnValue = wlChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = wlChooser.getSelectedFile();
					if (file.exists()) {
						mSpellChecker.loadWordList(file);
						updateFields();
					} else {
						JOptionPane.showMessageDialog(null, file.getName() + " was not found!", "File Not Found",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		changeWhenMouseOver(mWlFileChooserButton, 180, 24);

		mKbFileLabel = new JLabel("N/A") {
			private static final long serialVersionUID = 1L;

			@Override
			public void setText(String text) {
				super.setText(".kb: " + text);
			}
		};
		mKbFileLabel.setFont(font.deriveFont(Font.BOLD, 12));

		mKbFileChooserButton = new JButton("Select Keyboard...");
		mKbFileChooserButton.setFont(font.deriveFont(Font.BOLD, 12));
		mKbFileChooserButton.setIcon(new ImageIcon("resources/img/menu/red_button11.png"));
		mKbFileChooserButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mKbFileChooserButton.setPreferredSize(new Dimension(180, 24));
		mKbFileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SingleTypeFileChooser wlChooser = new SingleTypeFileChooser("Keyboard", ".kb");
				wlChooser.setDialogTitle("Select Keyboard...");
				int returnValue = wlChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = wlChooser.getSelectedFile();
					if (file.exists()) {
						mSpellChecker.loadKeyboard(file);
						updateFields();
					} else {
						JOptionPane.showMessageDialog(null, file.getName() + " was not found!", "File Not Found",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		changeWhenMouseOver(mKbFileChooserButton, 180, 24);

		mCloseButton = new JButton("Close");
		mCloseButton.setFont(font.deriveFont(Font.BOLD, 12));
		mCloseButton.setIcon(new ImageIcon("resources/img/menu/red_button11.png"));
		mCloseButton.setHorizontalTextPosition(SwingConstants.CENTER);
		mCloseButton.setPreferredSize(new Dimension(20, 24));
		mCloseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (ConfigurationPanel.this.getParent() != null)
					ConfigurationPanel.this.getParent().remove(ConfigurationPanel.this);
			}
		});
		changeWhenMouseOver(mCloseButton, 20, 24);

		add(mWlFileLabel);
		add(mWlFileChooserButton);
		add(Box.createVerticalStrut(20));
		add(mKbFileLabel);
		add(mKbFileChooserButton);
		add(Box.createVerticalGlue());
		add(mCloseButton);
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
	
	// Making the button change when mouse rolls over them
	public void changeWhenMouseOver(JButton thisButton, int width, int height) {
		thisButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				thisButton.setFont(font.deriveFont(Font.BOLD, 12));
				thisButton.setIcon(buttonSelectedIcon);
				thisButton.setHorizontalTextPosition(SwingConstants.CENTER);
				thisButton.setPreferredSize(new Dimension(width, height));
			}

			public void mouseExited(MouseEvent evt) {
				thisButton.setFont(font.deriveFont(Font.BOLD, 12));
				thisButton.setIcon(buttonUnselectedIcon);
				thisButton.setHorizontalTextPosition(SwingConstants.CENTER);
				thisButton.setPreferredSize(new Dimension(width, height));
			}
		});
	}

	public ConfigurationPanel(SpellChecker inSpellChecker) {
		mSpellChecker = inSpellChecker;
		/* Not necessary for assignment, but loads files in another thread */
		new Thread(() -> {
			mSpellChecker.loadWordList(new File("src/wordlist.wl"));
			mSpellChecker.loadKeyboard(new File("src/qwerty-us.kb"));
			updateFields();
		}).start();
	}

	// Updates the text labels with the currently selected file names
	private void updateFields() {
		mWlFileLabel.setText(mSpellChecker.getFileByType(SpellChecker.WORDLIST).getName());
		mKbFileLabel.setText(mSpellChecker.getFileByType(SpellChecker.KEYBOARD).getName());
	}

}
