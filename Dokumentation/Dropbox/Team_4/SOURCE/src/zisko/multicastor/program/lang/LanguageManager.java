package zisko.multicastor.program.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * This class supports the LanguageFile management of the MultiCastor It is
 * created as a singleton. That means that there can be only one object of this
 * class at runtime
 * 
 * @see "http://en.wikipedia.org/wiki/Singleton_pattern"
 * @see "http://de.wikipedia.org/wiki/Singleton_(Entwurfsmuster)"
 */
@SuppressWarnings("serial")
public class LanguageManager extends Properties {

	/**
	 * Array stores all languages detected as language file in the language
	 * directory
	 */
	public final static String[] languages;

	/**
	 * stores the current selected language
	 */
	private static String currentLanguage;

	/**
	 * This class (Language Manager) is a singleton. That means that there can
	 * be only one Object with this type at runtime. The reference to this
	 * object is stored in this variable
	 * 
	 * @see http://en.wikipedia.org/wiki/Singleton_pattern
	 * @see http://de.wikipedia.org/wiki/Singleton_%28Entwurfsmuster%29
	 */
	private static LanguageManager instance;
	/**
	 * This array holds all the required keys used in the program. All this keys
	 * have to be included in every language file. Remember: If you want to use
	 * new keys in the program, you should register them here!
	 */
	private static String[] keys = { "mi.autoSave", "mi.changeWindowTitle",
			"mi.language", "mi.saveConfiguration", "mi.saveAllMc",
			"mi.saveSelectedMc", "mi.loadConfiguration", "mi.loadMc",
			"mi.loadAdditionalMc", "mi.errorFileNotFound", "mi.snake",
			"mi.help", "mi.exit", "mi.about", "mi.menu", "mi.options",
			"mi.views", "mi.layer2Receiver", "mi.layer2Sender",
			"mi.layer3Receiver", "mi.layer3Sender", "mi.info", "tab.l2s",
			"tab.l3s", "tab.l2r", "tab.l3r", "tab.graph", "tab.console",
			"plus.l2rDescription", "plus.l2sDescription",
			"plus.l3rDescription", "plus.l3sDescription",
			"plus.aboutDescription", "miscBorder.mcOverwiew",
			"miscBorder.mcControl", "miscBorder.mcConfig",
			"miscBorder.ipGroupAddress", "miscBorder.ipNetworkInterface",
			"miscBorder.udpPort", "miscBorder.packetRate",
			"miscBorder.packetLength", "miscBorder.timeToLive",
			"miscBorder.ipGroupAddress", "miscBorder.ipNetworkInterface",
			"miscBorder.MacGroupAddress", "miscBorder.NetworkInterface",
			"config.message.ipFirst", "config.message.ipFirstShort",
			"button.start", "button.stop", "button.delete",
			"button.deSelectAll", "button.new", "button.active",
			"button.inactive", "button.add", "button.change",
			"button.changeAll", "status.mcSelected", "status.mcTotal",
			"status.traffic", "status.in", "status.out", "graph.time",
			"graph.sec", "graph.y", "graph.jitter", "graph.lostPackets",
			"graph.measuredPacketRate", "graph.packetsPerSec", "graph.current",
			"error.memory.part1", "error.memory.part2", "error.memory.part3",
			"error.memory.title", "warning.memory.title",
			"warning.memory.freeMemory", "warning.memory.allocMemory",
			"warning.memory.maxMemory", "logger.info.startWithGui",
			"logger.info.startNoGui", "logger.info.startGuiFile",
			"error.config.notSpecified", "error.config.wrongFormat",
			"error.invalidParameter", "error.logfile.canNotWrite",
			"error.network.noHostName", "error.network.settingUDP",
			"error.network.noDefaultPort", "error.network.sourceIP",
			"error.mr.logger", "error.mc.logger", "error.mr.mcdata",
			"console.helptext", "info.longUpdateTime",
			"warning.longUpdateTime", "warning.invalidNetAdapter",
			"toolTip.closeThisTab", "toolTip.mcAddressRange",
			"toolTip.ipFirst", "toolTip.specifyUDPPort", "toolTip.specifyTTL",
			"toolTip.specifyPacketRate", "toolTip.specifyPacketLength",
			"toolTip.selectNetInterface", "toolTip.selectMacAddress",
			"message.setNewTitle", "message.noMcCreated",
			"message.noMcSelected", "message.canNotOpenHelpPart1",
			"message.canNotOpenHelpPart2", "message.useDefaultPort",
			"message.mcActivated", "message.mcDeaktivated", "message.mcAdded",
			"message.mcDeleted", "message.mcAdvisePart1",
			"message.mcAdvisePart2", "message.mcStartRunning",
			"message.mcStopNotRunning", "message.joinMcPart1",
			"message.joinMcPart2", "message.unableToJoin",
			"message.settingTTLErr", "message.resetPackets",
			"message.senderStoped", "message.whileSending",
			"message.sleepPeak", "message.senderWorkingAgain",
			"message.problemIp", "message.tryReconnect", "message.senderFail",
			"message.senderViaIp", "message.sendingWarning",
			"message.whileSending", "message.whileSendingSleepFail",
			"message.packetsSendTotal", "message.defaultPort4711",
			"message.receiverMcObj", "message.receiverStartFail",
			"message.mcgroupMcobj", "message.mcgroupLeaveFail",
			"message.packetReceiver", "message.getHostname",
			"message.mmrpSender", "message.mcSenderActivated",
			"message.mcSenderDeactivated", "message.problemSending",
			"message.registerMmrpSender", "message.receiverInterfaceFail",
			"message.jnetpcapNotInstalled", "message.registerReceiverPath",
			"message.deregisterReceiverPath", "message.unsatisfiedLinkError",
			"message.loadDeviceFail", "message.pcapDeviceFail",
			"message.savedGUI", "message.savedGUINot", "message.savedMCConfig",
			"message.savedMCConfigNot", "message.gui.Loaded",
			"message.gui.NotFoundWithout", "message.gui.NotFound",
			"message.gui.NotParsedWithout", "message.gui.NotParsed",
			"message.gui.NotLoadedWithout", "message.gui.NotLoaded",
			"message.gui.ErrorDefault", "message.gui.Error",
			"message.gui.unexcpectedError", "message.gui.usedPath",
			"message.mcc.Loaded", "message.mcc.NotFoundWithout",
			"message.mcc.NotFound", "message.mcc.NotParsedWithout",
			"message.mcc.NotParsed", "message.mcc.NotLoadedWithout",
			"message.mcc.NotLoaded", "message.mcc.ErrorDefault",
			"message.mcc.Error", "message.mcc.unexcpectedError",
			"message.mcc.usedPath", "message.mc.undefined", "about.license",
			"about.text1", "about.text2", "about.text3", "about.mc1",
			"about.mc2", "general.device" };
	/**
	 * this initialization block fills the languages array with all languages
	 * detected as language file in the language directory. remember: static
	 * initialization blocks are the first thing that run at the start of the
	 * application!
	 * 
	 * @see http://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
	 */
	static {
		final File dir = new File("Language");
		languages = dir.list(new FilenameFilter() {
			@Override
			public boolean accept(final File d, final String name) {
				return name.endsWith(".lang");
			}
		});
	}

	/**
	 * Method returns the name of the current selected language
	 * 
	 * @return name of the current selected language
	 */
	public static String getCurrentLanguage() {
		return currentLanguage;
	}

	/**
	 * Method returns the one and only LanguageManager object. If there isn't
	 * any object it will be created by calling the constructor without any
	 * parameters.
	 * 
	 * @return the one and only LanguageManager object
	 */
	public static LanguageManager getInstance() {
		if(instance == null) {
			return instance = new LanguageManager();
		} else {
			return instance;
		}
	}

	/**
	 * Set the current language of the program. The given language name must be
	 * the same name as the language file name. The file have to be placed at
	 * 'Language/GIVENLANGUAGE.lang'.
	 * 
	 * @param currentLanguage
	 *            name of the language that you want to load
	 */
	public static void setCurrentLanguage(final String currentLanguage) {
		if(instance == null) {
			new LanguageManager("Language/" + currentLanguage + ".lang");
			// Following Line not needed: current Language is already set by the
			// constructor
			// LanguageManager.currentLanguage=currentLanguage;
		} else {
			try {
				getInstance().loadLanguage(
						"Language/" + currentLanguage + ".lang");
			} catch(final FileNotFoundException e) {
				JOptionPane.showMessageDialog(null,
						"Can not find the language file at\nLanguage/"
								+ currentLanguage
								+ ".lang\nCan not change language!");
			} catch(final IOException e) {
				JOptionPane.showMessageDialog(null,
						"Can not read the language file at\nLanguage/"
								+ currentLanguage
								+ ".lang\nCan not change language!");
			} catch(final InvalidLanguageFileException e) {
				JOptionPane
						.showMessageDialog(
								null,
								"The language file at\nLanguage/"
										+ currentLanguage
										+ ".lang\nis invalid. Can not change language.\n Missing value "
										+ e.getErrorKey() + " at index "
										+ e.getErrorIndex() + ".");
			}
		}
		LanguageManager.currentLanguage = currentLanguage;
	}

	/**
	 * this constructor starts the MultiCastor with the standard language file.
	 * For this is no parameter required. The method will call the constructor
	 * with a String parameter. The parameter will be set to the standard
	 * language file path. All constructors of this class are private. It is a
	 * singleton!
	 */
	private LanguageManager() {
		this("Language/english.lang");
	}

	/**
	 * this constructor loads the language file at the given path. All
	 * constructors of this class are private. It is a singleton!
	 * 
	 * @param currentLang
	 */
	private LanguageManager(final String currentLang) {
		final File languageFile = new File(currentLang); // [FF] neded to output
		// absolut path. better for
		// debugging and nice error
		// messages
		currentLanguage = languageFile.getName().replaceAll(".lang", "");
		try {
			loadLanguage(currentLang);
		} catch(final FileNotFoundException e) {
			JOptionPane.showMessageDialog(
					null,
					"Can not find the language file at\n"
							+ languageFile.getAbsolutePath()
							+ "\nCan not start Multicastor.");
			System.exit(1);
		} catch(final IOException e) {
			JOptionPane.showMessageDialog(
					null,
					"Can not read the language file at\n"
							+ languageFile.getAbsolutePath()
							+ "\nCan not start Multicastor.");
			System.exit(1);
		} catch(final InvalidLanguageFileException e) {
			JOptionPane
					.showMessageDialog(
							null,
							"The language file at\n"
									+ languageFile.getAbsolutePath()
									+ "\nis invalid. Can not start Multicastor.\n Missing value "
									+ e.getErrorKey() + " at index "
									+ e.getErrorIndex() + ".");
			System.exit(1);
		}
	}

	/**
	 * Method should be called for switching the language at runtime
	 * 
	 * @param currentLangFile
	 *            the full path the language file to load
	 * @throws FileNotFoundException
	 *             If there is no file at the given path
	 * @throws IOException
	 *             If the file at the given path is not readable
	 * @throws InvalidLanguageFileException
	 *             If there are not all needed keys entered in the file at the
	 *             given path Remember: If there is an one file with all keys
	 *             loaded before it wont cause an exception if the now loaded
	 *             file does not contain all keys! Unavailable Keys wont be
	 *             resetet to the new Language if they are not available
	 */
	private void loadLanguage(final String currentLangFile)
			throws FileNotFoundException, IOException,
			InvalidLanguageFileException {
		final FileInputStream fis = new FileInputStream(currentLangFile);
		this.load(new InputStreamReader(fis, "UTF8"));
		fis.close();
		// Check the Language File
		// Remember: If there is an one file with all keys loaded before
		// it wont cause an exception if the now loaded file does not contain
		// all keys!
		// Unavailable Keys wont be resetet to the new Language if they are not
		// available
		for(int i = 0; i < keys.length; i++) {
			if(!containsKey(keys[i])) {
				throw new InvalidLanguageFileException(i, keys[i], keys);
			}
		}
	}
}
