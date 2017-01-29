package spellcheck;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import wordhelper.SpellChecker;

/*
The Spell Check Helper holds a SpellChecker object that is shared
between a single configuration panel and spell check panel
The helper can return its panels in order to be displayed
*/
public class SpellCheckHelper {
	
	private final SpellChecker mSpellChecker;
	private final SpellCheckPanel mSpellCheckPanel;
	private final ConfigurationPanel mConfigurationPanel;
	
	{
		mSpellChecker = new SpellChecker();
		mSpellCheckPanel = new SpellCheckPanel(mSpellChecker);
		mConfigurationPanel = new ConfigurationPanel(mSpellChecker);
	}
	
	public JPanel getConfigurePanel() {
		return mConfigurationPanel;
	}
	
	public JPanel getSpellCheckPanel() {
		if(!mSpellChecker.hasWordList() || !mSpellChecker.hasKeyboard()) {
			JOptionPane.showMessageDialog(null, "Please configure the spellchecker.");
			return mConfigurationPanel;
		}
		return mSpellCheckPanel;
	}
	
}
