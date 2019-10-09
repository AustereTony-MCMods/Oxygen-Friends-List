package austeretony.oxygen_friendslist.client;

import java.util.UUID;

import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_friendslist.common.ListEntry;
import austeretony.oxygen_friendslist.common.network.server.SPEditListEntryNote;
import austeretony.oxygen_friendslist.common.network.server.SPManageList;

public class PlayerDataManagerClient {

    private final FriendsListManagerClient manager;

    protected PlayerDataManagerClient(FriendsListManagerClient manager) {
        this.manager = manager;
    }

    public void sendFriendRequestSynced(UUID playerUUID) {
        OxygenMain.network().sendToServer(new SPManageList(SPManageList.EnumRequest.ADD_FRIEND, playerUUID));
    }

    public void removeFriendSynced(UUID playerUUID) {
        OxygenMain.network().sendToServer(new SPManageList(SPManageList.EnumRequest.REMOVE_FRIEND, playerUUID));
    }

    public void editListEntryNoteSynced(UUID playerUUID, String note) {
        OxygenMain.network().sendToServer(new SPEditListEntryNote(playerUUID, note));
    }

    public void addToIgnoredSynced(UUID playerUUID) {
        OxygenMain.network().sendToServer(new SPManageList(SPManageList.EnumRequest.ADD_IGNORED, playerUUID));
    }

    public void removeIgnoredSynced(UUID playerUUID) {
        OxygenMain.network().sendToServer(new SPManageList(SPManageList.EnumRequest.REMOVE_IGNORED, playerUUID));
    }

    public void entryAdded(ListEntry entry) {
        this.manager.getPlayerDataContainer().addListEntry(entry);
        this.manager.getFriendsListMenuManager().entryAdded();
    }

    public void entryRemoved(ListEntry entry) {
        this.manager.getPlayerDataContainer().removeListEntry(entry.getId());
        this.manager.getFriendsListMenuManager().entryRemoved();
    }
}
