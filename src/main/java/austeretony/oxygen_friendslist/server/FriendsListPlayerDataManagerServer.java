package austeretony.oxygen_friendslist.server;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.EnumOxygenStatusMessage;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.PrivilegesProviderServer;
import austeretony.oxygen_core.server.api.TimeHelperServer;
import austeretony.oxygen_friendslist.common.ListEntry;
import austeretony.oxygen_friendslist.common.ListEntry.EnumEntryType;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListPrivilege;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListStatusMessage;
import austeretony.oxygen_friendslist.common.network.client.CPListEntryAction;
import net.minecraft.entity.player.EntityPlayerMP;

public class FriendsListPlayerDataManagerServer {

    private final FriendsListManagerServer manager;

    public FriendsListPlayerDataManagerServer(FriendsListManagerServer manager) {
        this.manager = manager;
    }

    public void sendFriendRequest(EntityPlayerMP senderMP, UUID targetUUID) {
        UUID senderUUID = CommonReference.getPersistentUUID(senderMP);
        if (!senderUUID.equals(targetUUID)
                && OxygenHelperServer.isPlayerOnline(targetUUID)) {
            FriendsListPlayerDataServer senderData = this.manager.getPlayerDataContainer().getPlayerData(senderUUID);
            if (senderData.canAddFriend() && !senderData.haveEntryForUUID(targetUUID)) {
                EntityPlayerMP targetMP = CommonReference.playerByUUID(targetUUID);
                OxygenHelperServer.sendRequest(senderMP, targetMP, new FriendRequest(senderUUID, CommonReference.getName(senderMP)));

                if (FriendsListConfig.ADVANCED_LOGGING.asBoolean())
                    OxygenMain.LOGGER.info("[Friends List] Player {}/{} sent friend request to player <{}/{}>",
                            CommonReference.getName(senderMP),
                            CommonReference.getPersistentUUID(senderMP),
                            CommonReference.getName(targetMP),
                            CommonReference.getPersistentUUID(targetMP));
            } else
                OxygenManagerServer.instance().sendStatusMessage(senderMP, EnumOxygenStatusMessage.REQUEST_RESET);
        }
    }

    public void addToFriendsList(EntityPlayerMP targetMP, UUID senderUUID) {
        if (OxygenHelperServer.isPlayerOnline(senderUUID)) {
            UUID targetUUID = CommonReference.getPersistentUUID(targetMP);
            EntityPlayerMP senderMP = CommonReference.playerByUUID(senderUUID);
            FriendsListPlayerDataServer 
            senderData = this.manager.getPlayerDataContainer().getPlayerData(senderUUID),
            targetData = this.manager.getPlayerDataContainer().getPlayerData(targetUUID);

            ListEntry senderEntry = new ListEntry(
                    targetData.createId(TimeHelperServer.getCurrentMillis()),
                    EnumEntryType.FRIEND, 
                    senderUUID);
            targetData.addListEntry(senderEntry); 
            targetData.setChanged(true);
            OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.ADDED, senderEntry), targetMP);

            ListEntry targetEntry = new ListEntry(
                    senderData.createId(TimeHelperServer.getCurrentMillis()),
                    EnumEntryType.FRIEND, 
                    targetUUID);
            senderData.addListEntry(targetEntry);
            senderData.setChanged(true);
            OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.ADDED, targetEntry), senderMP);

            OxygenHelperServer.addObservedPlayer(senderUUID, targetUUID);
            OxygenHelperServer.addObservedPlayer(targetUUID, senderUUID);     

            this.manager.sendStatusMessage(senderMP, EnumFriendsListStatusMessage.FRIEND_REQUEST_ACCEPTED_SENDER);
            this.manager.sendStatusMessage(targetMP, EnumFriendsListStatusMessage.FRIEND_REQUEST_ACCEPTED_TARGET);

            if (FriendsListConfig.ADVANCED_LOGGING.asBoolean())
                OxygenMain.LOGGER.info("[Friends List] Player {}/{} accepted friend request from player <{}/{}>",
                        CommonReference.getName(targetMP),
                        targetUUID,
                        CommonReference.getName(senderMP),
                        senderUUID);
        }
    }

    public void removeFriend(EntityPlayerMP playerMP, UUID targetUUID) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (!playerUUID.equals(targetUUID)) {
            FriendsListPlayerDataServer 
            playerData = this.manager.getPlayerDataContainer().getPlayerData(playerUUID), 
            targetData = this.manager.getPlayerDataContainer().getPlayerData(targetUUID);  

            ListEntry targetEntry = playerData.getListEntryByUUID(targetUUID);
            if (targetEntry != null) {
                if (targetData == null) {
                    targetData = this.manager.getPlayerDataContainer().createPlayerData(targetUUID);
                    OxygenHelperServer.loadPersistentData(targetData);
                }  

                playerData.removeListEntry(targetEntry.getId());
                playerData.setChanged(true);
                OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.REMOVED, targetEntry), playerMP);

                targetData.removeListEntry(playerUUID);
                targetData.setChanged(true);

                OxygenHelperServer.removeObservedPlayer(playerUUID, targetUUID);
                OxygenHelperServer.removeObservedPlayer(targetUUID, playerUUID);

                this.manager.sendStatusMessage(playerMP, EnumFriendsListStatusMessage.FRIEND_REMOVED);

                if (FriendsListConfig.ADVANCED_LOGGING.asBoolean())
                    OxygenMain.LOGGER.info("[Friends List] Player {}/{} removed player <{}> from friend list.",
                            CommonReference.getName(playerMP),
                            playerUUID,
                            targetUUID);
            }
        }
    }

    public void editListEntryNote(EntityPlayerMP playerMP, UUID targetUUID, String note) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (!playerUUID.equals(targetUUID)) {
            FriendsListPlayerDataServer playerData = this.manager.getPlayerDataContainer().getPlayerData(playerUUID);
            ListEntry targetEntry = playerData.getListEntryByUUID(targetUUID);
            if (targetEntry != null) {
                note = note.trim();
                if (note.length() > ListEntry.MAX_NOTE_LENGTH)
                    note = note.substring(0, ListEntry.MAX_NOTE_LENGTH);
                if (note.isEmpty()) return;

                targetEntry.setNote(note);

                long oldId = targetEntry.getId();
                targetEntry.setId(playerData.createId(oldId));
                playerData.removeListEntry(oldId);
                playerData.addListEntry(targetEntry);
                playerData.setChanged(true);

                OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.REMOVED, new ListEntry(oldId, EnumEntryType.FRIEND, targetUUID)), playerMP);
                OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.ADDED, targetEntry), playerMP);

                this.manager.sendStatusMessage(playerMP, EnumFriendsListStatusMessage.NOTE_EDITED);

                if (FriendsListConfig.ADVANCED_LOGGING.asBoolean())
                    OxygenMain.LOGGER.info("[Friends List] Player {}/{} set note [{}] for player <{}>.",
                            CommonReference.getName(playerMP),
                            playerUUID,
                            note,
                            targetUUID);
            }
        }
    }

    public void addToIgnored(EntityPlayerMP playerMP, UUID targetUUID) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (!playerUUID.equals(targetUUID)
                && !PrivilegesProviderServer.getAsBoolean(targetUUID, EnumFriendsListPrivilege.PREVENT_IGNORE.id(), false)
                && OxygenHelperServer.isPlayerOnline(targetUUID)) {
            FriendsListPlayerDataServer playerData = this.manager.getPlayerDataContainer().getPlayerData(playerUUID);
            if (playerData.canAddIgnored() && !playerData.haveEntryForUUID(targetUUID)) {
                ListEntry targetEntry = new ListEntry(
                        playerData.createId(TimeHelperServer.getCurrentMillis()),
                        EnumEntryType.IGNORED, 
                        targetUUID);
                playerData.addListEntry(targetEntry); 
                playerData.setChanged(true);
                OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.ADDED, targetEntry), playerMP);

                OxygenHelperServer.addObservedPlayer(playerUUID, targetUUID);

                this.manager.sendStatusMessage(playerMP, EnumFriendsListStatusMessage.ADDED_TO_IGNORED);

                if (FriendsListConfig.ADVANCED_LOGGING.asBoolean())
                    OxygenMain.LOGGER.info("[Friends List] Player {}/{} added player <{}> to ignore list.",
                            CommonReference.getName(playerMP),
                            playerUUID,
                            targetUUID);
            } else
                OxygenManagerServer.instance().sendStatusMessage(playerMP, EnumOxygenStatusMessage.REQUEST_RESET);
        } else
            OxygenManagerServer.instance().sendStatusMessage(playerMP, EnumOxygenStatusMessage.REQUEST_RESET);
    }

    public void removeIgnored(EntityPlayerMP playerMP, UUID targetUUID) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        FriendsListPlayerDataServer playerData = this.manager.getPlayerDataContainer().getPlayerData(playerUUID);
        ListEntry targetEntry = playerData.getListEntryByUUID(targetUUID);
        if (targetEntry != null) {
            playerData.removeListEntry(targetEntry.getId());
            playerData.setChanged(true);
            OxygenMain.network().sendTo(new CPListEntryAction(CPListEntryAction.EnumAction.REMOVED, targetEntry), playerMP);

            OxygenHelperServer.removeObservedPlayer(playerUUID, targetUUID);

            this.manager.sendStatusMessage(playerMP, EnumFriendsListStatusMessage.IGNORED_REMOVED);

            if (FriendsListConfig.ADVANCED_LOGGING.asBoolean())
                OxygenMain.LOGGER.info("[Friends List] Player {}/{} removed player <{}> from ignore list.",
                        CommonReference.getName(playerMP),
                        playerUUID,
                        targetUUID);
        }
    }
}
