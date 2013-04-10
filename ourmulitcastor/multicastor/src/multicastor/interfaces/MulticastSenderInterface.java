package multicastor.interfaces;

import multicastor.data.MulticastData;

public interface MulticastSenderInterface {
	/**
	 * im constructor bekommst du nen MultiCastData objekt zusaetzlich gibts
	 * einen Loggerhaendler
	 */

	/**
	 * @return die MulticastData-Bean des Senders
	 */
	public MulticastData getMultiCastData();

	/**
	 * An und Abmeldung wegen Multicast wird hier stattfinden Man bedenke die
	 * Zeitverzoegerung beim aktivieren.
	 */
	public void setActive(boolean b);
}