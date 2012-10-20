package program.interfaces;

import program.data.MulticastData;

public interface MulticastSenderInterface{
	/**
	 * im constructor bekommst du nen MultiCastData objekt
	 * zus�tzlich gibts einen Loggerhaendler
	 */

	/**
	 * An und Abmeldung wegen Multicast wird hier stattfinden
	 * Man bedenke die Zeitverz�gerung beim aktivieren.
	 */
	public void setActive(boolean b);

	/**
	 *
	 * @return die MulticastData-Bean des Senders
	 */
	public MulticastData getMultiCastData();
}

// keine �nderung m�glich, es wird ein neuer Sender erzeugt!