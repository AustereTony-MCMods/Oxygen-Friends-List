package austeretony.oxygen_friendslist.client;

import java.util.Set;

import austeretony.oxygen_core.client.sync.DataSyncHandlerClient;
import austeretony.oxygen_core.client.sync.DataSyncListener;
import austeretony.oxygen_friendslist.common.ListEntry;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class ListSyncHandlerClient implements DataSyncHandlerClient<ListEntry> {

    @Override
    public int getDataId() {
        return FriendsListMain.LIST_DATA_ID;
    }

    @Override
    public Class<ListEntry> getDataContainerClass() {
        return ListEntry.class;
    }

    @Override
    public Set<Long> getIds() {
        return FriendsListManagerClient.instance().getPlayerDataContainer().getListEntriesIds();
    }

    @Override
    public void clearData() {
        FriendsListManagerClient.instance().getPlayerDataContainer().reset();
    }

    @Override
    public ListEntry getEntry(long entryId) {
        return FriendsListManagerClient.instance().getPlayerDataContainer().getListEntry(entryId);
    }

    @Override
    public void addEntry(ListEntry entry) {
        FriendsListManagerClient.instance().getPlayerDataContainer().addListEntry(entry);
    }

    @Override
    public void save() {
        FriendsListManagerClient.instance().getPlayerDataContainer().setChanged(true);
    }

    @Override
    public DataSyncListener getSyncListener() {
        return (updated)->FriendsListManagerClient.instance().getFriendsListMenuManager().listSynchronized();
    }
}
