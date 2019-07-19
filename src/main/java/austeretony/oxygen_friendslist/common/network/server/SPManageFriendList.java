package austeretony.oxygen_friendslist.common.network.server;

import java.util.UUID;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen.util.PacketBufferUtils;
import austeretony.oxygen_friendslist.common.FriendsListManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPManageFriendList extends ProxyPacket {

    private EnumAction action;

    private UUID playerUUID;

    public SPManageFriendList() {}

    public SPManageFriendList(EnumAction action, UUID playerUUID) {
        this.action = action;
        this.playerUUID = playerUUID;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeByte(this.action.ordinal());
        PacketBufferUtils.writeUUID(this.playerUUID, buffer);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        this.action = EnumAction.values()[buffer.readByte()];
        switch (this.action) {
        case ADD_FRIEND:
            FriendsListManagerServer.instance().sendFriendRequest(getEntityPlayerMP(netHandler), PacketBufferUtils.readUUID(buffer));
            break;
        case REMOVE_FRIEND:
            FriendsListManagerServer.instance().removeFriend(getEntityPlayerMP(netHandler), PacketBufferUtils.readUUID(buffer));
            break;
        case ADD_IGNORED:
            FriendsListManagerServer.instance().addToIgnored(getEntityPlayerMP(netHandler), PacketBufferUtils.readUUID(buffer));
            break;
        case REMOVE_IGNORED:
            FriendsListManagerServer.instance().removeIgnored(getEntityPlayerMP(netHandler), PacketBufferUtils.readUUID(buffer));
            break;
        }
    }

    public enum EnumAction {

        ADD_FRIEND,
        REMOVE_FRIEND,
        ADD_IGNORED,
        REMOVE_IGNORED
    }
}
