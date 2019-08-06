package austeretony.oxygen_friendslist.common;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.main.EnumOxygenChatMessage;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen.common.privilege.api.PrivilegeProviderServer;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListChatMessage;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListPrivilege;
import austeretony.oxygen_friendslist.common.main.FriendListEntry;
import austeretony.oxygen_friendslist.common.main.FriendRequest;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import austeretony.oxygen_friendslist.common.main.FriendsListPlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class FriendsListManagerServer {

    private static FriendsListManagerServer instance;

    private final Map<UUID, FriendsListPlayerData> playersData = new ConcurrentHashMap<UUID, FriendsListPlayerData>();

    public static void create() {
        if (instance == null)
            instance = new FriendsListManagerServer();
    }

    public static FriendsListManagerServer instance() {
        return instance;
    }

    public Collection<FriendsListPlayerData> getPlayersData() {
        return this.playersData.values();
    }

    public void createPlayerData(UUID playerUUID) {
        this.playersData.put(playerUUID, new FriendsListPlayerData(playerUUID));
    }

    public void removePlayerData(UUID playerUUID) {
        this.playersData.remove(playerUUID);
    }

    public boolean playerDataExist(UUID playerUUID) {
        return this.playersData.containsKey(playerUUID);
    }

    public FriendsListPlayerData getPlayerData(UUID playerUUID) {
        return this.playersData.get(playerUUID);
    }

    public void sendFriendRequest(EntityPlayerMP sender, UUID targetUUID) {
        UUID senderUUID = CommonReference.getPersistentUUID(sender);
        if (!senderUUID.equals(targetUUID)
                && OxygenHelperServer.isOnline(targetUUID)) {
            FriendsListPlayerData senderData = this.getPlayerData(senderUUID);
            if (senderData.getFriendsAmount() < FriendsListConfig.MAX_FRIENDS.getIntValue()
                    && !senderData.haveFriendListEntryForUUID(targetUUID)) {
                OxygenHelperServer.sendRequest(sender, CommonReference.playerByUUID(targetUUID), 
                        new FriendRequest(FriendsListMain.FRIEND_REQUEST_ID, senderUUID, CommonReference.getName(sender)), true);
            } else
                OxygenHelperServer.sendMessage(sender, OxygenMain.OXYGEN_MOD_INDEX, EnumOxygenChatMessage.REQUEST_RESET.ordinal());
        }
    }

    public void removeFriend(EntityPlayerMP playerMP, UUID targetUUID) {
        UUID senderUUID = CommonReference.getPersistentUUID(playerMP);
        if (!senderUUID.equals(targetUUID)) {
            FriendsListPlayerData 
            senderData = this.getPlayerData(senderUUID), 
            targetData;  
            if (!this.playerDataExist(targetUUID)) {
                this.createPlayerData(targetUUID);
                targetData = this.getPlayerData(targetUUID);
                OxygenHelperServer.loadPersistentData(targetData);
            } else
                targetData = this.getPlayerData(targetUUID);   
            if (senderData.haveFriendListEntryForUUID(targetUUID)) {
                senderData.removeFriendListEntry(targetUUID);
                targetData.removeFriendListEntry(senderUUID);
                OxygenHelperServer.removeObservedPlayer(senderUUID, targetUUID, false);
                OxygenHelperServer.removeObservedPlayer(targetUUID, senderUUID, true);
                OxygenHelperServer.savePersistentDataDelegated(senderData);
                OxygenHelperServer.savePersistentDataDelegated(targetData); 
                OxygenHelperServer.sendMessage(playerMP, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListChatMessage.FRIEND_REMOVED.ordinal());
            }
        }
    }

    public void editFriendListEntryNote(EntityPlayerMP playerMP, UUID targetUUID, String note) {
        UUID senderUUID = CommonReference.getPersistentUUID(playerMP);
        if (!senderUUID.equals(targetUUID)) {
            if (note.length() > FriendListEntry.MAX_NOTE_LENGTH)//maximum note length is 40 symbols
                note = note.substring(0, FriendListEntry.MAX_NOTE_LENGTH);
            FriendsListPlayerData playerData = this.getPlayerData(senderUUID);
            playerData.getFriendListEntryByUUID(targetUUID).setNote(note);
            OxygenHelperServer.savePersistentDataDelegated(playerData);
            OxygenHelperServer.sendMessage(playerMP, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListChatMessage.NOTE_EDITED.ordinal());
        }
    }

    public void addToIgnored(EntityPlayerMP playerMP, UUID targetUUID) {
        UUID senderUUID = CommonReference.getPersistentUUID(playerMP);
        if (!senderUUID.equals(targetUUID)
                && !PrivilegeProviderServer.getPrivilegeValue(targetUUID, EnumFriendsListPrivilege.PREVENT_IGNORE.toString(), false)
                && OxygenHelperServer.isOnline(targetUUID)) {
            FriendsListPlayerData senderData = this.getPlayerData(senderUUID);
            if (senderData.getIgnoredAmount() < FriendsListConfig.MAX_IGNORED.getIntValue() 
                    && !senderData.haveFriendListEntryForUUID(targetUUID)) {
                senderData.addFriendListEntry(new FriendListEntry(targetUUID, true).createId());
                OxygenHelperServer.addObservedPlayer(senderUUID, targetUUID, true);
                OxygenHelperServer.savePersistentDataDelegated(senderData);
                OxygenHelperServer.sendMessage(playerMP, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListChatMessage.ADDED_TO_IGNORED.ordinal());
            } else
                OxygenHelperServer.sendMessage(playerMP, OxygenMain.OXYGEN_MOD_INDEX, EnumOxygenChatMessage.REQUEST_RESET.ordinal());
        } else
            OxygenHelperServer.sendMessage(playerMP, OxygenMain.OXYGEN_MOD_INDEX, EnumOxygenChatMessage.REQUEST_RESET.ordinal());
    }

    public void removeIgnored(EntityPlayerMP playerMP, UUID targetUUID) {
        UUID senderUUID = CommonReference.getPersistentUUID(playerMP);
        if (!senderUUID.equals(targetUUID)) {
            FriendsListPlayerData senderData = this.getPlayerData(senderUUID);
            if (senderData.haveFriendListEntryForUUID(targetUUID)) {
                senderData.removeFriendListEntry(targetUUID);
                OxygenHelperServer.removeObservedPlayer(senderUUID, targetUUID, true);
                OxygenHelperServer.savePersistentDataDelegated(senderData);
                OxygenHelperServer.sendMessage(playerMP, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListChatMessage.IGNORED_REMOVED.ordinal());
            }
        }
    }

    //TODO onPlayerLoaded()
    public void onPlayerLoaded(EntityPlayer player) {
        UUID playerUUID = CommonReference.getPersistentUUID(player);
        if (!this.playerDataExist(playerUUID))
            this.createPlayerData(playerUUID);
        OxygenHelperServer.loadPersistentDataDelegated(this.getPlayerData(playerUUID));
    }

    //TODO onPlayerUnloaded()
    public void onPlayerUnloaded(EntityPlayer player) {
        UUID playerUUID = CommonReference.getPersistentUUID(player);
        if (this.playerDataExist(playerUUID))
            this.removePlayerData(playerUUID);
    }

    public void reset() {
        this.playersData.clear();
    }
}
