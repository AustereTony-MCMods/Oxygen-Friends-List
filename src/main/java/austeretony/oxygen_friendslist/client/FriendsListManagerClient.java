package austeretony.oxygen_friendslist.client;

import java.util.UUID;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUIScreen;
import austeretony.oxygen_friendslist.common.main.FriendListEntry;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import austeretony.oxygen_friendslist.common.main.FriendsListPlayerData;
import austeretony.oxygen_friendslist.common.network.server.SPEditFriendListEntryNote;
import austeretony.oxygen_friendslist.common.network.server.SPFriendsListRequest;
import austeretony.oxygen_friendslist.common.network.server.SPManageFriendList;

public class FriendsListManagerClient {

    private static FriendsListManagerClient instance;

    private FriendsListPlayerData playerData;

    public static void create() {
        if (instance == null)
            instance = new FriendsListManagerClient();
    }

    public static FriendsListManagerClient instance() {
        return instance;
    }

    public void init() {
        this.reset();
        this.playerData = new FriendsListPlayerData(OxygenHelperClient.getPlayerUUID());
        OxygenHelperClient.loadPersistentDataDelegated(this.playerData);
    }

    public FriendsListPlayerData getClientPlayerData() {
        return this.playerData;
    }

    public void openFriendsListSynced() {
        FriendsListMain.network().sendToServer(new SPFriendsListRequest(SPFriendsListRequest.EnumRequest.OPEN_FRIENDS_LIST));
    }

    public void openFriendsListDelegated() {
        ClientReference.getMinecraft().addScheduledTask(new Runnable() {

            @Override
            public void run() {
                openFriendsList();
            }
        });
    }

    private void openFriendsList() {
        ClientReference.displayGuiScreen(new FriendsListGUIScreen());
    }

    public void downloadFriendsListDataSynced() {
        this.getClientPlayerData().clearFriendListEntries();
        this.openFriendsListSynced();
    }

    public void sendFriendRequestSynced(UUID playerUUID) {
        FriendsListMain.network().sendToServer(new SPManageFriendList(SPManageFriendList.EnumAction.ADD_FRIEND, playerUUID));
    }

    public void removeFriendSynced(UUID playerUUID) {
        this.getClientPlayerData().removeFriendListEntry(playerUUID);
        FriendsListMain.network().sendToServer(new SPManageFriendList(SPManageFriendList.EnumAction.REMOVE_FRIEND, playerUUID));
        OxygenHelperClient.savePersistentDataDelegated(this.getClientPlayerData());
    }

    public void editFriendListEntryNoteSynced(UUID playerUUID, String note) {
        this.getClientPlayerData().getFriendListEntryByUUID(playerUUID).setNote(note);
        FriendsListMain.network().sendToServer(new SPEditFriendListEntryNote(playerUUID, note));
        OxygenHelperClient.savePersistentDataDelegated(this.getClientPlayerData());
    }

    public void addToIgnoredSynced(UUID playerUUID) {
        this.getClientPlayerData().addFriendListEntry(new FriendListEntry(playerUUID, true).createId());
        FriendsListMain.network().sendToServer(new SPManageFriendList(SPManageFriendList.EnumAction.ADD_IGNORED, playerUUID));
        OxygenHelperClient.savePersistentDataDelegated(this.getClientPlayerData());
    }

    public void removeIgnoredSynced(UUID playerUUID) {
        this.getClientPlayerData().removeFriendListEntry(playerUUID);
        FriendsListMain.network().sendToServer(new SPManageFriendList(SPManageFriendList.EnumAction.REMOVE_IGNORED, playerUUID));
        OxygenHelperClient.savePersistentDataDelegated(this.getClientPlayerData());
    }

    public void reset() {
        if (this.playerData != null)
            this.playerData.reset();
    }
}
