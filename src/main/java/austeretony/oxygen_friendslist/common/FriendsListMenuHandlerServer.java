package austeretony.oxygen_friendslist.common;

import java.util.Set;
import java.util.UUID;

import austeretony.oxygen.common.api.network.OxygenNetwork;
import austeretony.oxygen.common.sync.gui.api.IAdvancedGUIHandlerServer;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import austeretony.oxygen_friendslist.common.main.FriendsListPlayerData;
import net.minecraft.network.PacketBuffer;

public class FriendsListMenuHandlerServer implements IAdvancedGUIHandlerServer {

    @Override
    public OxygenNetwork getNetwork() {
        return FriendsListMain.network();
    }

    @Override
    public Set<Long> getValidIdentifiers(UUID playerUUID) {
        return FriendsListManagerServer.instance().getPlayerData(playerUUID).getFriendListEntriesIds();
    }

    @Override
    public void writeEntries(UUID playerUUID, PacketBuffer buffer, long[] entriesIds) {
        FriendsListPlayerData playerData = FriendsListManagerServer.instance().getPlayerData(playerUUID);
        for (long entryId : entriesIds)
            playerData.getFriendListEntry(entryId).write(buffer);
    }
}
