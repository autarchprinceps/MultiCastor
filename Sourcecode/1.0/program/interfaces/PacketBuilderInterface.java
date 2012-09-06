package zisko.multicastor.program.interfaces;

import zisko.multicastor.program.data.MulticastData;

// timestamp wird geupdated sobald ein paket mit getPacket() angefordert wird. PacketCount wird
// auch automatisch erhöht (oder soll der übergeben werden?)

public interface PacketBuilderInterface {
	public byte[] getPacket();
}
