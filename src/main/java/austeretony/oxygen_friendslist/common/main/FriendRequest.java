package austeretony.oxygen_friendslist.common.main;

import java.util.UUID;

import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.api.notification.AbstractNotification;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.notification.EnumNotification;
import austeretony.oxygen_friendslist.common.FriendsListManagerServer;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import net.minecraft.entity.player.EntityPlayer;

public class FriendRequest extends AbstractNotification {

    public final int index;

    public final UUID senderUUID;

    public final String senderUsername;

    public FriendRequest(int index, UUID senderUUID, String senderUsername) {
        this.index = index;
        this.senderUUID = senderUUID;
        this.senderUsername = senderUsername;
    }

    @Override
    public EnumNotification getType() {
        return EnumNotification.REQUEST;
    }

    @Override
    public String getDescription() {
        return "oxygen.request.friendRequest";
    }

    @Override
    public String[] getArguments() {
        return new String[] {this.senderUsername};
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public int getExpireTime() {
        return FriendsListConfig.FRIEND_REQUEST_EXPIRE_TIME.getIntValue();
    }

    @Override
    public void accepted(EntityPlayer player) {
        UUID targetUUID = CommonReference.getPersistentUUID(player);
        FriendsListPlayerData 
        senderData = FriendsListManagerServer.instance().getPlayerData(this.senderUUID),
        targetData = FriendsListManagerServer.instance().getPlayerData(targetUUID);

        targetData.addFriendListEntry(new FriendListEntry(this.senderUUID, false).createId()); 
        senderData.addFriendListEntry(new FriendListEntry(targetUUID, false).createId());

        OxygenHelperServer.addObservedPlayer(this.senderUUID, targetUUID, false);
        OxygenHelperServer.addObservedPlayer(targetUUID, this.senderUUID, true);

        OxygenHelperServer.savePersistentDataDelegated(senderData);
        OxygenHelperServer.savePersistentDataDelegated(targetData);

        if (OxygenHelperServer.isOnline(this.senderUUID))
            OxygenHelperServer.sendMessage(CommonReference.playerByUUID(this.senderUUID), FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListChatMessages.FRIEND_REQUEST_ACCEPTED_SENDER.ordinal());

        OxygenHelperServer.sendMessage(player, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListChatMessages.FRIEND_REQUEST_ACCEPTED_TARGET.ordinal());

        OxygenHelperServer.setRequesting(this.senderUUID, false);
    }

    @Override
    public void rejected(EntityPlayer player) {
        if (OxygenHelperServer.isOnline(this.senderUUID))
            OxygenHelperServer.sendMessage(CommonReference.playerByUUID(this.senderUUID), FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListChatMessages.FRIEND_REQUEST_REJECTED_SENDER.ordinal());
        OxygenHelperServer.sendMessage(player, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListChatMessages.FRIEND_REQUEST_REJECTED_TARGET.ordinal());

        OxygenHelperServer.setRequesting(this.senderUUID, false);
    }

    @Override
    public void expired() {
        OxygenHelperServer.setRequesting(this.senderUUID, false);
    }
}
