   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenstr�men. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie k�nnen es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation ver�ffentlicht, 
 *	weitergeben und/oder modifizieren, gem�� Version 3 der Lizenz.
 *
 *  Die Ver�ffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT F�R EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
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

import java.util.Arrays;

public class MacAddress {

	private byte[] macAddress;

	public MacAddress() {

	}

	public MacAddress(byte[] macAddress) {
		this.macAddress = macAddress;
	}

	/**
	 * gibt die Mac-Adresse im folgenden Format aus xx:xx:xx:xx:xx:xx
	 */
	@Override
	public String toString() {
		if (macAddress != null) {
			final StringBuilder buf = new StringBuilder();
			for (byte b : macAddress) {
				if (buf.length() != 0) {
					buf.append(':');
				}
				if (b >= 0 && b < 16) {
					buf.append('0');
				}
				buf.append(Integer.toHexString((b < 0) ? b + 256 : b)
						.toUpperCase());
			}

			return buf.toString();
		}
		return null;
	}

	public byte[] getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(byte[] macAddress) {
		this.macAddress = macAddress;
	}

	@Override
	public boolean equals(Object obj) {
		return Arrays.equals(this.getMacAddress(),((MacAddress) obj).getMacAddress());
	}
	
}
