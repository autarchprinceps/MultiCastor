   
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
 
 package dhbw.multicastor.program.interfaces;

import java.net.InetAddress;

import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.data.MulticastData.Source;
import dhbw.multicastor.program.data.MulticastData.senderState;


public interface IgmpMldInterface {
	
	public void resetValues();
	
	public InetAddress getGroupIp();

	public void setGroupIp(InetAddress groupIp);
	
	public InetAddress getSourceIp();

	public void setSourceIp(InetAddress sourceIp);

	public int getUdpPort();

	public void setUdpPort(int udpPort);

	public int getTtl();

	public void setTtl(int ttl);
	
	@Override
	public String toString();
	
	public String toStringConsole();
	
	public String identify();
	
	public void setActive(boolean active);
	
	public int getPacketLength();
	
	public void setPacketLength(int packetLength);
	
	public int getPacketRateDesired();
	
	public void setPacketRateDesired(int packetRateDesired);
	
	public int getPacketRateMeasured();
	
	public void setPacketRateMeasured(int packetRateMeasured);
	
	public ProtocolType getProtocolType();
	
	public void setProtocolType(ProtocolType typ);
	
	public int getThreadID();
	
	public void setThreadID(int threadID);
	
	public String getHostID();
	
	public void setHostID(String hostID);
	
	public boolean getActive();
	
	public int getNumberOfInterruptions() ;
	
	public void setNumberOfInterruptions(int numberOfInterruptions);
	
	public int getAverageInterruptionTime();
	
	public void setAverageInterruptionTime(int averageInterruptionTime);
	
	public int getPacketLossPerSecond();
	
	public void setPacketLossPerSecond(int packetLossPerSecond);
	
	public int getJitter();
	
	public void setJitter(int jitter);
	
	public String getColor();
	
	public void setColor(String color);
	
	public long getPacketRateAvg();
	
	public void setPacketRateAvg(long packetRateAvg);
	
	public long getPacketLossPerSecondAvg();
	
	public void setPacketLossPerSecondAvg(long packetLossPerSecondAvg);
	
	public long getJitterAvg();
	
	public void setJitterAvg(long jitterAvg);
	
	public int getTraffic();
	
	public void setTraffic(int traffic);
	
	public long getTrafficAvg();
	
	public void setTrafficAvg(long trafficAvg);
	
	public long getPacketCount();
	
	public void setPacketCount(long packetCount) ;
	
	/**
	 * Returns a unique SenderID consisting of the hostID and the threadID
	 * @return
	 */
	public String getSenderID();
	
	public Source getPacketSource();
	
	public void setPacketSource(Source packetSource);
	
	public senderState getSenders();
	
	public void setSenders(senderState senders);
	
	public void setMaxInterruptionTime(int maxInterruptionTime);
	
	public int getMaxInterruptionTime();
}
