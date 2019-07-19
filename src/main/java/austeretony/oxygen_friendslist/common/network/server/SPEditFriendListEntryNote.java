package austeretony.oxygen_friendslist.common.network.server;

import java.util.UUID;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen.util.PacketBufferUtils;
import austeretony.oxygen_friendslist.common.FriendsListManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPEditFriendListEntryNote extends ProxyPacket {

    private UUID playerUUID;

    private String note;

    public SPEditFriendListEntryNote() {}

    public SPEditFriendListEntryNote(UUID playerUUID, String note) {
        this.playerUUID = playerUUID;
        this.note = note;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        PacketBufferUtils.writeUUID(this.playerUUID, buffer);
        PacketBufferUtils.writeString(this.note, buffer);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        FriendsListManagerServer.instance().editFriendListEntryNote(getEntityPlayerMP(netHandler), PacketBufferUtils.readUUID(buffer), PacketBufferUtils.readString(buffer));
    }
}
