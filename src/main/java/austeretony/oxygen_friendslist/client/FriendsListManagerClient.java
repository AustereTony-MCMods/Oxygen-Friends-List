package austeretony.oxygen_friendslist.client;

import java.util.UUID;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.privilege.api.PrivilegeProviderClient;
import austeretony.oxygen.common.main.EnumOxygenPrivilege;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen.common.main.OxygenPlayerData.EnumActivityStatus;
import austeretony.oxygen.common.main.SharedPlayerData;
import austeretony.oxygen_friendslist.common.main.FriendListEntry;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import austeretony.oxygen_friendslist.common.main.FriendsListPlayerData;
import austeretony.oxygen_friendslist.common.network.server.SPEditFriendListEntryNote;
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

    public static EnumActivityStatus getActivityStatus(FriendListEntry entry) {
        EnumActivityStatus activityStatus = EnumActivityStatus.OFFLINE;
        if (OxygenHelperClient.isOnline(entry.playerUUID))
            activityStatus = OxygenHelperClient.getPlayerStatus(entry.playerUUID);
        return activityStatus;
    }

    public static String getUsername(FriendListEntry entry) {
        return OxygenHelperClient.getObservedSharedData(entry.playerUUID).getUsername();
    }

    public static boolean isPlayerAvailable(String username) {
        if (username.equals(OxygenHelperClient.getSharedClientPlayerData().getUsername()))
            return false;
        SharedPlayerData sharedData = OxygenHelperClient.getSharedPlayerData(username);
        if (sharedData != null) {
            if (!FriendsListManagerClient.instance().getClientPlayerData().haveFriendListEntryForUUID(sharedData.getPlayerUUID())
                    && (OxygenHelperClient.getPlayerStatus(sharedData) != OxygenPlayerData.EnumActivityStatus.OFFLINE || PrivilegeProviderClient.getPrivilegeValue(EnumOxygenPrivilege.EXPOSE_PLAYERS_OFFLINE.toString(), false)))
                return true;
        }
        return false;
    }

    public void reset() {
        if (this.playerData != null)
            this.playerData.reset();
    }
}
