package zisko.multicastor.program.interfaces;

import zisko.multicastor.program.data.MulticastData;

public interface MulticastSenderInterface{
	/**
	 * im constructor bekommst du nen MultiCastData objekt
	 * zusaetzlich gibts einen Loggerhaendler
	 */
	
	/**
	 * An und Abmeldung wegen Multicast wird hier stattfinden
	 * Man bedenke die Zeitverzoegerung beim aktivieren.
	 */
	public void setActive(boolean b);
	
	/**
	 * 
	 * @return die MulticastData-Bean des Senders
	 */
	public MulticastData getMultiCastData();
}