package program.interfaces;

// timestamp wird geupdated sobald ein paket mit getPacket() angefordert wird. PacketCount wird
// auch automatisch erh�ht (oder soll der �bergeben werden?)

public interface PacketBuilderInterface {
	public byte[] getPacket();
}
