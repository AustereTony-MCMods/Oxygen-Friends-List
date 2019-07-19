package austeretony.oxygen_friendslist.common.network.client;

import java.util.UUID;

import austeretony.oxygen.common.api.OxygenGUIHelper;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.common.FriendsListManagerServer;
import austeretony.oxygen_friendslist.common.main.FriendListEntry;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import austeretony.oxygen_friendslist.common.main.FriendsListPlayerData;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPSyncFriendListEntries extends ProxyPacket {

    private UUID playerUUID;

    private long[] entriesIds;

    public CPSyncFriendListEntries() {}

    public CPSyncFriendListEntries(UUID playerUUID, long[] entriesIds) {
        this.playerUUID = playerUUID;
        this.entriesIds = entriesIds;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        FriendsListPlayerData playerData = FriendsListManagerServer.instance().getPlayerData(this.playerUUID);
        buffer.writeShort(this.entriesIds.length);
        for (long entryId : this.entriesIds)
            playerData.getFriendListEntry(entryId).write(buffer);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        int amount = buffer.readShort();
        for (int i = 0; i < amount; i++)
            FriendsListManagerClient.instance().getClientPlayerData().addFriendListEntry(FriendListEntry.read(buffer));
        OxygenGUIHelper.dataRecieved(FriendsListMain.FRIEND_LIST_SCREEN_ID);
    }
}
