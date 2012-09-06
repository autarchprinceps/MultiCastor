   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenströmen. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation veröffentlicht, 
 *	weitergeben und/oder modifizieren, gemäß Version 3 der Lizenz.
 *
 *  Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 *  Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 ****************************************************************************************************************************************************************
 *  MultiCastor is a Tool for sending and receiving of Multicast-Data Streams. This project was created for the subject "Software Engineering" at 
 *	Dualen Hochschule Stuttgart under the direction of Markus Rentschler and Andreas Stuckert.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; 
 *  either version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
 
 package dhbw.multicastor.program.model;


import java.text.SimpleDateFormat;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.interfaces.MulticastSenderInterface;
import dhbw.multicastor.program.interfaces.MulticastThreadSuper;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;


/**
 * Die MultiCastSender-Klasse kï¿½mmert sich um das tatsï¿½chliche Senden der Multicast-
 * Objekte ï¿½ber das Netzwerk.
 * Sie extended {@link MulticastThreadSuper}, ist also ein Runnable.
 * Ein MultiCastSender hat eine Grundkonfiguration, die nicht mehr abgeï¿½ndert werden kann,
 * wie zum Beispiel die gesetzten IPs. Soll diese Grundkonfiguration geï¿½ndert werden, muss
 * eine neue Instanz der Klasse gebildet werden. Das Erleichtert die nï¿½chtrï¿½gliche Analyse,
 * Da das Objekt eindeutig einem "Test" zuordnungsbar ist.
 * @author jannik
 *
 */
public abstract class MulticastSender extends MulticastThreadSuper implements MulticastSenderInterface{
	protected PacketBuilder 	myPacketBuilder;
	
	/**
	 * Variablen fï¿½r die verschiedenen Sendemethoden
	 */
	public static enum		sendingMethod 	{	/**
												 * Senden unter Volllast, ohne auf die angegebene
												 * Paketrate zu achten
												 */
												NO_SLEEP,
												
												/**
												 * Alle Pakete werden am Anfang der Sekunde gesendet,
												 * danach wird der Thread fï¿½r den Rest der Sekunde in den
												 * sleep geschickt
												 */
												PEAK,
												
												/**
												 * Nach jedem Paket wird eine errechnete Zeitspanne
												 * der thread auf sleep gesetzt
												 * (lï¿½sst zur Zeit keine hohen Paketraten zu)
												 */
												SLEEP_MULTIPLE
											};
	protected sendingMethod	usedMethod		 			=	sendingMethod.PEAK;
	protected int 			packetRateDes;
							

	
	//Variablen fï¿½r das Senden
	protected boolean 		isSending 		 			= false;
	protected long			pausePeriodMs;
	protected int				pausePeriodNs;
	protected long totalPacketCount			= 0;
	
	//Variablen fï¿½r Logging
	protected Logger 	messages;
	SimpleDateFormat 		sdf 						= new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
	protected int						resetablePcktCnt 			= 0;
	protected int cumulatedResetablePcktCnt 	= 0;
	
	
	/**
	 * Einziger Konstruktor der Klasse (Sieht man vom Konstruktor der Superklasse ab).
	 * Im Konstruktor wird die hostID gesetzt (entspricht dem hostnamen des Gerï¿½ts),
	 * der MultiCastSocket initialisiert, das Network-Interface
	 * ï¿½ber das die Multicasts gesendet werden sollen gesetzt und das Datenpaket
	 * mit dem {@link PacketBuilder} erstellt.
	 * @param mcBean Das {@link MulticastData}-Object, dass alle fï¿½r den Betrieb nï¿½tigen Daten enthï¿½lt.
	 * @param _messages Eine {@link Queue}, ï¿½ber den der Sender seine Ausgaben an den Controller weitergibt.
	 */
	public MulticastSender(MulticastData mcBean, Logger _logger){
		super(mcBean);
		messages = _logger;
	}

	/**
	 * Wird der Methode true ï¿½bergeben, meldet diese sich bei der Multicastgruppe an
	 * und startet zu senden.
	 * Wird der Methode false ï¿½bergeben, stoppt sie das senden der Multicasts und
	 * veranlasst damit auch das Abmelden von der Multicastgruppe.
	 * @param active boolean
	 */
	@Override
	public void setActive(boolean active) {
		if(active) {
			//Setzen der ThreadID, da diese evtl.
			//im Controller noch einmal geï¿½ndert wird
			myPacketBuilder.alterThreadID(mcData.getThreadID());
			setSending(true);
			mcData.setActive(true);
			setStillRunning(true);
			proclaim(2, Event.ACTIVATED, "");
		}
		else{
			setSending(false);
			mcData.setActive(false);
		}
	}
	
	/**
	 * Siehe public void setActive(boolean active).
	 * Es lï¿½sst sich zusï¿½tzlich die Art und Weise angeben, mit der gesendet wird (diese
	 * Mï¿½glichkeit wird in der aktuellen Version nur auf Modulebene unterstï¿½tzt
	 * und zielt auf spï¿½tere Versionen des ByteTools und Testzwecke ab)
	 * @param active true=senden, false=nicht senden
	 * @param _method Art und Weise des Sendens
	 */
	public void setActive(boolean active, sendingMethod _method){
		usedMethod = _method;
		setActive(active);
	}
	
	/**
	 * hier geschieht das eigentliche Senden. Beim Starten des Threads wird
	 * probiert, der Multicast-Gruppe beizutreten. Gelingt dies nicht, wird ein Fehler
	 * ausgegeben und das Senden wird garnicht erst gestartet.
	 * Gelingt das Beitreten, wird so lange gesendet, bis setActive(false) aufgerufen wird.
	 * Auf Modulebene kann bei setActive() festgelegt werden, auf welche Art und Weise
	 * gesendet wird. Im Tool wird in dieser Version ausschlieï¿½lich PEAK verwendet.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Aktualisiert das MultiCastData-Objekt
	 * und resetet den internen Paket-Counter
	 */
	public void update(){
		mcData.setPacketRateMeasured(resetablePcktCnt);
		// TODO
		mcData.setPacketCount(totalPacketCount);
		mcData.setTraffic(resetablePcktCnt*mcData.getPacketLength());
		cumulatedResetablePcktCnt =+ resetablePcktCnt;
		resetablePcktCnt = 0;
	}
	
	/**
	 * Aktualisiert das MultiCastData-Objekt.
	 * Dieser Counter summiert die gemessene Paketrate,
	 * die mit der update()-Funktion ins MultiCastData-Objekt geschrieben wird.
	 * Diese Summe wird bei jedem Aufruf resetet.
	 */
	public void updateMin(){
		mcData.setPacketRateAvg(cumulatedResetablePcktCnt);
		cumulatedResetablePcktCnt = 0;
	}
	
	/**
	 * Methode ï¿½ber die die Kommunikation zum MultiCastController realisiert wird.
	 * @param level unterscheidet zwischen Fehlern und Status-Meldungen
	 * @param mssg Die zu sendende Nachricht (String)
	 */
	protected void proclaim(int level, Event event, String mssg){
		Level l;
		if(event == Event.ACTIVATED || event == Event.DEACTIVATED || event == Event.DELETED || event == Event.ADDED){
			mssg = mcData.identify() + " " + mssg;
		}
		
		switch(level){	
			case 1:		l = Level.WARNING;
						break;
			case 2:		l = Level.INFO;
						break;
			default:	l = Level.SEVERE;
		}
		messages.log(l,mssg,event);
	}	
	
	public void reset(){
		totalPacketCount = 0;
	}

	public void setSending(boolean isSending) {
		this.isSending = isSending;
	}

	public boolean isSending() {
		return isSending;
	}
}