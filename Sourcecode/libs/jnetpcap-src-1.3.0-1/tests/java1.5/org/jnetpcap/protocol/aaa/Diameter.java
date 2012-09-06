/*
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Sly Technologies, Inc.
 *
 * This file is part of jNetPcap.
 *
 * jNetPcap is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jnetpcap.protocol.aaa;

import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.JHeaderMap;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.RegistryHeaderErrors;
import org.jnetpcap.packet.annotate.Bind;
import org.jnetpcap.packet.annotate.Field;
import org.jnetpcap.packet.annotate.Header;
import org.jnetpcap.packet.annotate.HeaderLength;
import org.jnetpcap.protocol.tcpip.Tcp;

// TODO: Auto-generated Javadoc
/**
 * The Class Diameter.
 */
@Header
public class Diameter
    extends JHeaderMap<Diameter> {
	
	/** The ID. */
	public static int ID;

	static {
		try {
			ID = JRegistry.register(Diameter.class);
		} catch (final RegistryHeaderErrors e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Bind to tcp.
	 * 
	 * @param packet
	 *          the packet
	 * @param tcp
	 *          the tcp
	 * @return true, if successful
	 */
	@Bind(to=Tcp.class)
	public static boolean bindToTcp(JPacket packet, Tcp tcp) {
		return tcp.destination() == 3868 || tcp.source() == 3868;
	}
	
	/**
	 * Header length.
	 * 
	 * @param buffer
	 *          the buffer
	 * @param offset
	 *          the offset
	 * @return the int
	 */
	@HeaderLength
	public static int headerLength(JBuffer buffer, int offset) {
	    return (int) buffer.getUInt(offset) & 0x00FFFFFF;	
	}
	
	// Diameter header accessors
	/**
	 * Gets the version.
	 * 
	 * @return the version
	 */
	@Field(offset = 0, length = 8, format = "%x")
	public int getVersion() {
		return super.getUByte(0);
	}
	
	/**
	 * Gets the message length.
	 * 
	 * @return the message length
	 */
	@Field(offset = 8, length = 24, format = "%x")
	public int getMessageLength() {
		return (int) super.getUInt(0) & 0x00FFFFFF;
	}
	
	/**
	 * Gets the command flags.
	 * 
	 * @return the command flags
	 */
	@Field(offset = 0, length = 8, format = "%x")
	public int getCommandFlags() {
		return super.getUByte(4);
	}
	
	/**
	 * Gets the command code.
	 * 
	 * @return the command code
	 */
	@Field(offset = 0, length = 24, format = "%x")
	public int getCommandCode() {
		return (int) super.getUInt(4) & 0x00FFFFFF;
	}
}