package austeretony.oxygen_friendslist.common.network.server;

import java.util.UUID;

import austeretony.oxygen.common.OxygenManagerServer;
import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import austeretony.oxygen_friendslist.common.network.client.CPSyncValidFriendEntriesIds;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPFriendsListRequest extends ProxyPacket {

    private EnumRequest request;

    public SPFriendsListRequest() {}

    public SPFriendsListRequest(EnumRequest request) {
        this.request = request;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeByte(this.request.ordinal());
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        this.request = EnumRequest.values()[buffer.readByte()];
        switch (this.request) {
        case OPEN_FRIENDS_LIST:
            if (!OxygenHelperServer.isSyncing(playerUUID)) {
                OxygenHelperServer.setSyncing(playerUUID, true);
                OxygenManagerServer.instance().syncSharedPlayersData(playerMP, OxygenHelperServer.getSharedDataIdentifiersForScreen(FriendsListMain.FRIEND_LIST_SCREEN_ID));
                FriendsListMain.network().sendTo(new CPSyncValidFriendEntriesIds(playerUUID), playerMP);
            }
            break;
        }
    }

    public enum EnumRequest {

        OPEN_FRIENDS_LIST
    }
}
