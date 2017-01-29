package frame;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import textdocument.TextDocumentManager;

/*
 * The Office Frame simply holds the TextDocumentManager
 * It could have probably been the TextDocumentManager itself,
 * but I like to keep this small and separated.
 * */

public class OfficeFrame extends JFrame {
	private static final long serialVersionUID = 9183816558021947333L;
	private JLabel labelOnStartUp;
	private File fontFile;
	private Font font;
	private JPanel defaultPanel;
	private TextDocumentManager textDocMang;
	private int numTabs = 0;
	
	public OfficeFrame(){
		
		setDocImage();
		setFont();
        settingUpAccelerators();
        		
		setTitle("Trojan Office");
		setSize(640, 480);
		setJMenuBar(new JMenuBar(){
			public void paintComponent(Graphics g) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("resources/img/menu/red_button11.png"),
																 0, 0, getWidth(), getHeight(), this);
			}
		});
		textDocMang = new TextDocumentManager(getJMenuBar(), this);
  	
		getContentPane().add(textDocMang);
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		defaultPanel = new JPanel();
		defaultPanel.setLayout(new GridLayout(1, 1));
		
		labelOnStartUp = new JLabel("TROJAN OFFICE!", SwingConstants.CENTER);
		labelOnStartUp.setFont(font.deriveFont(Font.BOLD, 48f));
		labelOnStartUp.setForeground(Color.ORANGE);
		labelOnStartUp.setBackground(Color.GRAY);
		labelOnStartUp.setOpaque(true);
		defaultPanel.add(labelOnStartUp);
		addRemoveInitBackground();
		
		addCursorImage();
	}
	
	// Initializing font
	private void setFont(){
		fontFile = new File("resources/fonts/kenvector_future.ttf");
        try {
			font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Changing accelerator font type, text color, and background color
	private void settingUpAccelerators(){
		 UIManager.getLookAndFeelDefaults().put("MenuItem.acceleratorForeground", new Color(243, 97, 0));
	     UIManager.put("MenuItem.background", Color.lightGray);
	     UIManager.getLookAndFeelDefaults().put("MenuItem.acceleratorFont", Font.SANS_SERIF);
	}
	
	// Making dock image the image from /resources 
	private void setDocImage(){
		setIconImage(Toolkit.getDefaultToolkit().getImage("resources/img/icon/office.png"));
		Class<?> applicationClass = null;
		try {
			applicationClass = Class.forName("com.apple.eawt.Application");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Method getApplicationMethod = null;
		try {
			getApplicationMethod = applicationClass.getMethod("getApplication");
		} catch (NoSuchMethodException | SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Method setDockIconMethod = null;
		try {
			setDockIconMethod = applicationClass.getMethod("setDockIconImage", java.awt.Image.class);
		} catch (NoSuchMethodException | SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Object macOSXApplication = null;
		try {
			macOSXApplication = getApplicationMethod.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			setDockIconMethod.invoke(macOSXApplication, Toolkit.getDefaultToolkit().getImage("resources/img/icon/office.png"));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	// Adding or removing initial background depending on whether or not any tabs are open
	public void addRemoveInitBackground(){
		numTabs = textDocMang.getNumTabs();
		if(numTabs == 0){
			getContentPane().add(defaultPanel);
		} else {
			getContentPane().remove(defaultPanel);
			getContentPane().add(textDocMang);
		}
		repaint();
		revalidate();
	}
	
	// Making the custom cursor image
	private void addCursorImage(){
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage("resources/img/icon/cursor.png");
		Cursor c = toolkit.createCustomCursor(image , new Point(0, 0), "img");
		this.setCursor (c);
	}

	public static void main(String[] args) {
		try {// Set the UI to cross platform for better portability
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Warning! Cross-platform L&F not used!");
		} finally {
			/* Not necessary for Assignment 2 - but this is good practice */
			SwingUtilities.invokeLater(() -> {
				new OfficeFrame().setVisible(true);
			});
		}
	}
}
