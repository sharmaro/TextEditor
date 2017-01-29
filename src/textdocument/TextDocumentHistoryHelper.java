package textdocument;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class TextDocumentHistoryHelper {
	//The document that is being tracked
	private Document mDocument;
	
	//Allows control of undo and redo
	private final UndoManager mUndoManager;
	private final JMenuItem mUndoItem;
	private final JMenuItem mRedoItem;

	{
		mUndoManager = new UndoManager();
		
		mUndoItem = new JMenuItem("Undo");
		mUndoItem.setMnemonic('U');
		mUndoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mUndoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mUndoManager.undo();
				} catch(CannotUndoException ue) {
					JOptionPane.showMessageDialog(
							null,
						    "Error occured while trying to undo!",
						    "Undo Error",
						    JOptionPane.ERROR_MESSAGE);
				}
				updateHistoryActions();
			}
		});
		
		mRedoItem = new JMenuItem("Redo");
		mRedoItem.setMnemonic('R');
		mRedoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mRedoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mUndoManager.redo();
				} catch(CannotRedoException re) {
					JOptionPane.showMessageDialog(
							null,
						    "Error occured while trying to redo!",
						    "Redo Error",
						    JOptionPane.ERROR_MESSAGE);
				}
				updateHistoryActions();
			}
		});
		updateHistoryActions();
	}
	
	//Manager takes in a document that it will help to control
	public TextDocumentHistoryHelper(Document inDocument) {
		setDocument(inDocument);
	}
	
	//The document can be overwritten
	public void setDocument(Document inDocument) {
		mDocument = inDocument;
		mDocument.addUndoableEditListener( new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				mUndoManager.addEdit(e.getEdit());
				updateHistoryActions();
			}
		});
	}
	
	//Updates the enabled/disabled states of the buttons
	private void updateHistoryActions() {
		mUndoItem.setEnabled(mUndoManager.canUndo());
	    mRedoItem.setEnabled(mUndoManager.canRedo());
	}
	
	//Gets the menu item to display
	public JMenuItem getUndoMenuItem() {
		return mUndoItem;
	}
	
	//Gets the menu item to display
	public JMenuItem getRedoMenuItem() {
		return mRedoItem;
	}
	
}
