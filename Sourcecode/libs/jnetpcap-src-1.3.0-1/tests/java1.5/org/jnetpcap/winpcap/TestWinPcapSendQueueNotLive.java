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
package org.jnetpcap.winpcap;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

import org.jnetpcap.packet.PeeringException;

/**
 * Test out the WinPcapSendQueue java class without transmitting or requirement
 * of having an open interface. Safe for build tests.
 * 
 * @author Sly Technologies, Inc.
 * 
 */
public class TestWinPcapSendQueueNotLive extends TestCase {

	public void testCreateQueue() {

		WinPcapSendQueue queue = WinPcap.sendQueueAlloc(512);

		assertEquals("queue->maxlen", 512, queue.getMaxLen());
		assertEquals("queue->len", 0, queue.getLen());
		assertEquals("queue->buffer", 512, queue.getBuffer().size());
	}

	public void testQueueConstructor1() {
		WinPcapSendQueue queue = new WinPcapSendQueue();

		assertEquals("queue->maxlen",
				WinPcapSendQueue.DEFAULT_QUEUE_SIZE,
				queue.getMaxLen());
		assertEquals("queue->len", 0, queue.getLen());
		assertEquals("queue->buffer", WinPcapSendQueue.DEFAULT_QUEUE_SIZE, queue
				.getBuffer().size());
	}

	public void testQueueConstructor2() {
		WinPcapSendQueue queue =
				new WinPcapSendQueue(new byte[WinPcapSendQueue.DEFAULT_QUEUE_SIZE]);

		assertEquals("queue->maxlen",
				WinPcapSendQueue.DEFAULT_QUEUE_SIZE,
				queue.getMaxLen());
		assertEquals("queue->len",
				WinPcapSendQueue.DEFAULT_QUEUE_SIZE,
				queue.getLen());
		assertEquals("queue->buffer", WinPcapSendQueue.DEFAULT_QUEUE_SIZE, queue
				.getBuffer().size());
	}

	public void testQueueConstructor3() throws PeeringException {
		WinPcapSendQueue queue =
				new WinPcapSendQueue(
						ByteBuffer.allocateDirect(WinPcapSendQueue.DEFAULT_QUEUE_SIZE));

		assertEquals("queue->maxlen",
				WinPcapSendQueue.DEFAULT_QUEUE_SIZE,
				queue.getMaxLen());
		assertEquals("queue->len",
				WinPcapSendQueue.DEFAULT_QUEUE_SIZE,
				queue.getLen());
		assertEquals("queue->buffer", WinPcapSendQueue.DEFAULT_QUEUE_SIZE, queue
				.getBuffer().size());
	}

	public void testQueueConstructor4() throws PeeringException {
		try {
			new WinPcapSendQueue(
					ByteBuffer.allocate(WinPcapSendQueue.DEFAULT_QUEUE_SIZE));
			fail("Did not generate the IllegalArgumentException as expected");
		} catch (IllegalArgumentException e) {
			// Success
		}
	}
}
