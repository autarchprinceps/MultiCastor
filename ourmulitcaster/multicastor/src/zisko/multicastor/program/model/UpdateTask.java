package zisko.multicastor.program.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.interfaces.MulticastThreadSuper;
import zisko.multicastor.program.lang.LanguageManager;

/**
 * UpdateTask ist ein Timer, der die Oberflaeche/Tabelle aktuallisiert
 */
public class UpdateTask extends TimerTask {
	private final LanguageManager lang = LanguageManager.getInstance();
	private final Logger logger;
	private final Map<MulticastData, MulticastThreadSuper> mc_receiver_l2;
	private final Map<MulticastData, MulticastThreadSuper> mc_receiver_l3;
	private final Map<MulticastData, MulticastThreadSuper> mc_sender_l2;
	private final Map<MulticastData, MulticastThreadSuper> mc_sender_l3;
	// V1.5 [FH] edded that memory warning is only appearing once
	private boolean memoryWarned = false;

	private final ViewController viewController;

	/**
	 * Konstruktor
	 * 
	 * @param logger
	 *            Logger der zum loggen benutzt wird
	 * @param mcSenderL3
	 *            Multicast Sender L3 Daten
	 * @param mcReceiverL3
	 *            Multicast Empfaenger L3 Daten
	 * @param mcSenderL2
	 *            Multicast Sender L2 Daten
	 * @param mcReceiverL2
	 *            Multicast Empfaenger L2 Daten
	 * @param viewController
	 *            viewController fuer die Oberflaeche
	 */
	public UpdateTask(final Logger logger,
			final Map<MulticastData, MulticastThreadSuper> mcSenderL3,
			final Map<MulticastData, MulticastThreadSuper> mcReceiverL3,
			final Map<MulticastData, MulticastThreadSuper> mcSenderL2,
			final Map<MulticastData, MulticastThreadSuper> mcReceiverL2,
			final ViewController viewController) {
		super();
		this.logger = logger;
		mc_sender_l3 = mcSenderL3;
		mc_receiver_l3 = mcReceiverL3;
		mc_sender_l2 = mcSenderL2;
		mc_receiver_l2 = mcReceiverL2;
		this.viewController = viewController;
	}

	@Override
	public void run() {
		MulticastThreadSuper value = null;
		final long time1 = System.nanoTime();
		Map<MulticastData, MulticastThreadSuper> v = null;
		boolean memoryWarnedForLog = false;
		final Runtime rt = Runtime.getRuntime();

		// V1.5 [FH] Pruefung des Memories. Ob noch mehr als 10% frei sind
		if(!memoryWarned
				&& ((rt.freeMemory() + (rt.maxMemory() - rt.totalMemory())) < (rt
						.maxMemory() * 0.1))) {
			logger.warning(lang.getProperty("warning.memory.title")
					+ lang.getProperty("warning.memory.freeMemory") + ": "
					+ (rt.freeMemory() / (1024 * 1024)) + "\n"
					+ lang.getProperty("warning.memory.allocMemory") + ": "
					+ (rt.totalMemory() / (1024 * 1024)) + "\n"
					+ lang.getProperty("warning.memory.maxMemory") + ":  "
					+ (rt.maxMemory() / (1024 * 1024)));
			memoryWarnedForLog = true;
			memoryWarned = true;
		}

		for(int i = 0; i < 4; i++) {
			switch(i) {
				case 0:
					v = mc_sender_l3;
					break;
				case 1:
					v = mc_receiver_l3;
					break;
				case 2:
					v = mc_sender_l2;
					break;
				case 3:
					v = mc_receiver_l2;
					break;
			}
			for(final Entry<MulticastData, MulticastThreadSuper> m : v
					.entrySet()) {
				value = m.getValue();
				if(value.getMultiCastData().isActive()) {
					value.update();
				}
			}
		}

		if(viewController != null) {
			if(viewController.isInitFinished()) {
				viewController.viewUpdate();
			}
		}

		// V1.5 [FH] added !MemoryWarning, because if we have a memory warning
		// it is always taking longer
		if(!memoryWarnedForLog
				&& (((System.nanoTime() - time1) / 1000000) > 200)) {
			logger.log(Level.INFO, lang.getProperty("info.longUpdateTime")
					+ ": " + ((System.nanoTime() - time1) / 1000000)
					+ " ms !!!!!!!!!!!!");
			if(((System.nanoTime() - time1) / 1000000) > 300) {
				if(viewController != null) {
					logger.log(Level.WARNING,
							lang.getProperty("warning.longUpdateTime"));
				}
			}
		}

	}

}
