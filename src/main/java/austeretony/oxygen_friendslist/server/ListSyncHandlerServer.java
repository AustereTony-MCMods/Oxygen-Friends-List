package austeretony.oxygen_friendslist.server;

import java.util.Set;
import java.util.UUID;

import austeretony.oxygen_core.server.sync.DataSyncHandlerServer;
import austeretony.oxygen_friendslist.common.ListEntry;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class ListSyncHandlerServer implements DataSyncHandlerServer<ListEntry> {

    @Override
    public int getDataId() {
        return FriendsListMain.LIST_DATA_ID;
    }

    @Override
    public boolean allowSync(UUID playerUUID) {
        return true;
    }

    @Override
    public Set<Long> getIds(UUID playerUUID) {
        return FriendsListManagerServer.instance().getPlayerDataContainer().getPlayerData(playerUUID).getListEntriesIds();
    }

    @Override
    public ListEntry getEntry(UUID playerUUID, long entryId) {
        return FriendsListManagerServer.instance().getPlayerDataContainer().getPlayerData(playerUUID).getListEntry(entryId);
    }
}
