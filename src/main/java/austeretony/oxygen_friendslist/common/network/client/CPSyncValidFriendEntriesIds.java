package austeretony.oxygen_friendslist.common.network.client;

import java.util.Set;
import java.util.UUID;

import austeretony.oxygen.common.api.OxygenGUIHelper;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.common.FriendsListManagerServer;
import austeretony.oxygen_friendslist.common.main.FriendListEntry;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import austeretony.oxygen_friendslist.common.network.server.SPSendAbsentFriendListEntriesIds;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPSyncValidFriendEntriesIds extends ProxyPacket {

    private UUID playerUUID;

    public CPSyncValidFriendEntriesIds() {}

    public CPSyncValidFriendEntriesIds(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        Set<Long> entriesIds = FriendsListManagerServer.instance().getPlayerData(this.playerUUID).getFriendListEntriesIds();
        buffer.writeShort(entriesIds.size());
        for (long entryId : entriesIds)
            buffer.writeLong(entryId);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        long[] syncedIds = new long[buffer.readShort()];
        int 
        i = 0, 
        j = 0;
        for (; i < syncedIds.length; i++)
            syncedIds[i] = buffer.readLong();
        long[] needSync = new long[syncedIds.length];
        Set<Long> friendsIds = FriendsListManagerClient.instance().getClientPlayerData().getFriendListEntriesIds();
        FriendListEntry[] validEntries = new FriendListEntry[syncedIds.length];
        i = 0;
        for (long entryId : syncedIds)
            if (!friendsIds.contains(entryId))
                needSync[i++] = entryId;    
            else
                validEntries[j++] = FriendsListManagerClient.instance().getClientPlayerData().getFriendListEntry(entryId);
        FriendsListManagerClient.instance().getClientPlayerData().clearFriendListEntries();
        for (FriendListEntry validEntry : validEntries) {
            if (validEntry == null) break;
            FriendsListManagerClient.instance().getClientPlayerData().addFriendListEntry(validEntry);
        }
        if (i > 0)
            OxygenGUIHelper.needSync(FriendsListMain.FRIEND_LIST_SCREEN_ID);
        FriendsListMain.network().sendToServer(new SPSendAbsentFriendListEntriesIds(needSync, i));
    }
}
