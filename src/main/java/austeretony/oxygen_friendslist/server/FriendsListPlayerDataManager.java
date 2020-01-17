package austeretony.oxygen_friendslist.server;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.EnumOxygenStatusMessage;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.PrivilegesProviderServer;
import austeretony.oxygen_friendslist.common.ListEntry;
import austeretony.oxygen_friendslist.common.ListEntry.EnumEntryType;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListPrivilege;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListStatusMessage;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import austeretony.oxygen_friendslist.common.network.client.CPListEntryAction;
import net.minecraft.entity.player.EntityPlayerMP;

public class FriendsListPlayerDataManager {

    private final FriendsListManagerServer manager;

    public FriendsListPlayerDataManager(FriendsListManagerServer manager) {
        this.manager = manager;
    }

    public void onPlayerLoaded(EntityPlayerMP playerMP) {
        this.manager.getPlayerDataContainer().onPlayerLoaded(CommonReference.getPersistentUUID(playerMP));
    }

    public void onPlayerUnloaded(EntityPlayerMP playerMP) {
        //TODO 0.10 - May cause unsaved data loss
        /*UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        this.manager.getPlayerDataContainer().onPlayerUnloaded(playerUUID);*/
    }

    public void sendFriendRequest(EntityPlayerMP sender, UUID targetUUID) {
        UUID senderUUID = CommonReference.getPersistentUUID(sender);
        if (!senderUUID.equals(targetUUID)
                && OxygenHelperServer.isPlayerOnline(targetUUID)) {
            FriendsListPlayerData senderData = this.manager.getPlayerDataContainer().getPlayerData(senderUUID);
            if (senderData.canAddFriend() && !senderData.haveEntryForUUID(targetUUID))
                OxygenHelperServer.sendRequest(sender, CommonReference.playerByUUID(targetUUID), new FriendRequest(FriendsListMain.FRIEND_REQUEST_ID, senderUUID, CommonReference.getName(sender)));
            else
                OxygenHelperServer.sendStatusMessage(sender, OxygenMain.OXYGEN_CORE_MOD_INDEX, EnumOxygenStatusMessage.REQUEST_RESET.ordinal());
        }
    }

    public void addToFriendsList(EntityPlayerMP playerMP, UUID senderUUID) {
        if (OxygenHelperServer.isPlayerOnline(senderUUID)) {
            UUID targetUUID = CommonReference.getPersistentUUID(playerMP);
            EntityPlayerMP senderMP = CommonReference.playerByUUID(senderUUID);
            FriendsListPlayerData 
            senderData = this.manager.getPlayerDataContainer().getPlayerData(senderUUID),
            targetData = this.manager.getPlayerDataContainer().getPlayerData(targetUUID);

            ListEntry 
            entry = new ListEntry(EnumEntryType.FRIEND, senderUUID);
            entry.setId(System.currentTimeMillis());
            targetData.addListEntry(entry); 
            OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.ADDED, entry), playerMP);


            entry = new ListEntry(EnumEntryType.FRIEND, targetUUID);
            entry.setId(System.currentTimeMillis());
            senderData.addListEntry(entry);
            OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.ADDED, entry), senderMP);

            targetData.setChanged(true);
            senderData.setChanged(true);
            OxygenHelperServer.addObservedPlayer(senderUUID, targetUUID);
            OxygenHelperServer.addObservedPlayer(targetUUID, senderUUID);     

            OxygenHelperServer.sendStatusMessage(senderMP, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListStatusMessage.FRIEND_REQUEST_ACCEPTED_SENDER.ordinal());
            OxygenHelperServer.sendStatusMessage(playerMP, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListStatusMessage.FRIEND_REQUEST_ACCEPTED_TARGET.ordinal());
        }
    }

    public void removeFriend(EntityPlayerMP playerMP, UUID targetUUID) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (!playerUUID.equals(targetUUID)) {
            FriendsListPlayerData 
            playerData = this.manager.getPlayerDataContainer().getPlayerData(playerUUID), 
            targetData = this.manager.getPlayerDataContainer().getPlayerData(targetUUID);  
            if (targetData == null) {
                targetData = this.manager.getPlayerDataContainer().createPlayerData(targetUUID);
                OxygenHelperServer.loadPersistentData(targetData);
            }  
            ListEntry entry = playerData.getListEntryByUUID(targetUUID);
            if (entry != null) {
                playerData.removeListEntry(entry.getId());
                OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.REMOVED, entry), playerMP);
                targetData.removeListEntry(playerUUID);
                playerData.setChanged(true);
                targetData.setChanged(true);
                OxygenHelperServer.removeObservedPlayer(playerUUID, targetUUID);
                OxygenHelperServer.removeObservedPlayer(targetUUID, playerUUID);
                OxygenHelperServer.sendStatusMessage(playerMP, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListStatusMessage.FRIEND_REMOVED.ordinal());
            }
        }
    }

    public void editListEntryNote(EntityPlayerMP playerMP, UUID targetUUID, String note) {
        UUID senderUUID = CommonReference.getPersistentUUID(playerMP);
        if (!senderUUID.equals(targetUUID)) {
            FriendsListPlayerData playerData = this.manager.getPlayerDataContainer().getPlayerData(senderUUID);
            ListEntry entry = playerData.getListEntryByUUID(targetUUID);
            if (entry != null) {
                note = note.trim();
                if (note.length() > ListEntry.MAX_NOTE_LENGTH)
                    note = note.substring(0, ListEntry.MAX_NOTE_LENGTH);
                playerData.removeListEntry(entry.getId());
                OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.REMOVED, entry), playerMP);
                ListEntry newEntry = new ListEntry(entry.getType(), targetUUID);
                entry.setId(playerData.getNewEntryId(entry.getId()));
                newEntry.setNote(note);
                playerData.addListEntry(newEntry);
                OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.ADDED, newEntry), playerMP);
                playerData.setChanged(true);
                OxygenHelperServer.sendStatusMessage(playerMP, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListStatusMessage.NOTE_EDITED.ordinal());
            }
        }
    }

    public void addToIgnored(EntityPlayerMP playerMP, UUID targetUUID) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (!playerUUID.equals(targetUUID)
                && !PrivilegesProviderServer.getAsBoolean(targetUUID, EnumFriendsListPrivilege.PREVENT_IGNORE.id(), false)
                && OxygenHelperServer.isPlayerOnline(targetUUID)) {
            FriendsListPlayerData playerData = this.manager.getPlayerDataContainer().getPlayerData(playerUUID);
            if (playerData.canAddIgnored() && !playerData.haveEntryForUUID(targetUUID)) {
                ListEntry entry = new ListEntry(EnumEntryType.IGNORED, targetUUID);
                entry.setId(System.currentTimeMillis());
                playerData.addListEntry(entry); 
                OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.ADDED, entry), playerMP);
                playerData.setChanged(true);
                OxygenHelperServer.addObservedPlayer(playerUUID, targetUUID);
                OxygenHelperServer.sendStatusMessage(playerMP, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListStatusMessage.ADDED_TO_IGNORED.ordinal());
            } else
                OxygenHelperServer.sendStatusMessage(playerMP, OxygenMain.OXYGEN_CORE_MOD_INDEX, EnumOxygenStatusMessage.REQUEST_RESET.ordinal());
        } else
            OxygenHelperServer.sendStatusMessage(playerMP, OxygenMain.OXYGEN_CORE_MOD_INDEX, EnumOxygenStatusMessage.REQUEST_RESET.ordinal());
    }

    public void removeIgnored(EntityPlayerMP playerMP, UUID targetUUID) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (!playerUUID.equals(targetUUID)) {
            FriendsListPlayerData playerData = this.manager.getPlayerDataContainer().getPlayerData(playerUUID);
            ListEntry entry = playerData.getListEntryByUUID(targetUUID);
            if (entry != null) {
                playerData.removeListEntry(entry.getId());
                OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.REMOVED, entry), playerMP);
                playerData.setChanged(true);
                OxygenHelperServer.removeObservedPlayer(playerUUID, targetUUID);
                OxygenHelperServer.sendStatusMessage(playerMP, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListStatusMessage.IGNORED_REMOVED.ordinal());
            }
        }
    }
}
