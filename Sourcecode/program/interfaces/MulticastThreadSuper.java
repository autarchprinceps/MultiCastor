package zisko.multicastor.program.interfaces;

import zisko.multicastor.program.data.MulticastData;

/**
 * Der MulticastThreadSuper stellt eine gemeinsame Superklasse fuer MulticastReceiver und
 * MulticastSender dar. 
 * @author Bastian Wagener
 */
public abstract class MulticastThreadSuper implements Runnable {
	protected MulticastData mcData;
	private Boolean stillRunning = false;
	
	public MulticastThreadSuper(MulticastData multicastData){
		super();
		mcData = multicastData;
	}
	
	/**
	 * Gibt das zugeordnete MulticastData Objekt zurueck.
	 * @return MulticastData Objekt, welches zur Initialisierung genutzt wurde.
	 */
	public MulticastData getMultiCastData() {
		return mcData;
	}
	
	/**
	 * Setzt alle notwendigen Vorraussetzungen damit ein Thread mit diesem Objekt
	 * gestartet werden kann.
	 */
	public abstract void setActive(boolean active); // hier muss die setStillRunning(true) aufgerufen werden
	
	/**
	 * Aktualisiert die ermittelten Durchschnittswerte im MulticastData Objekt.
	 */
	public abstract void updateMin();
	
	/**
	 * Aktualisiert ermittelte Daten im MulticastData Objekt. Hierbei werden nicht
	 * die Durchschnittswerte aktualisiert.
	 */
	public abstract void update();
	
	/**
	 * Setzt den Zustand der stillRunning Variable.
	 * @param b Neuer Zustand.
	 */
	protected void setStillRunning(Boolean b){
		synchronized (stillRunning) {
			stillRunning = b;
		}
	}
	
	/**
	 * Gibt den genauen Zustand des Threads an.
	 * @return Wird <code>TRUE</code> zurueckgegeben laeuft der Thread.
	 */
	public boolean isStillRunning(){
		synchronized (stillRunning){
			return stillRunning;
		}
	}
}
