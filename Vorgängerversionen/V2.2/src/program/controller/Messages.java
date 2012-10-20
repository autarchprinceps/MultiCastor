package program.controller;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class is automatically generated for loading localization files
 * @author Alexander Behm
 *
 */
public class Messages {
	private final static String BUNDLE_NAME = "messages"; //$NON-NLS-1$

	private static ResourceBundle RESOURCE_BUNDLE;

	private Messages() {
	}
	
	public static void setLanguage(String loc)
	{
		RESOURCE_BUNDLE  = ResourceBundle.getBundle(BUNDLE_NAME, new Locale(loc));
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
