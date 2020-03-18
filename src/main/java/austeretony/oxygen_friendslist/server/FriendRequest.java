package austeretony.oxygen_friendslist.server;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.notification.AbstractNotification;
import austeretony.oxygen_core.common.notification.EnumNotification;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListStatusMessage;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class FriendRequest extends AbstractNotification {

    public final UUID senderUUID;

    public final String senderUsername;

    public FriendRequest(UUID senderUUID, String senderUsername) {
        this.senderUUID = senderUUID;
        this.senderUsername = senderUsername;
    }

    @Override
    public EnumNotification getType() {
        return EnumNotification.REQUEST;
    }

    @Override
    public String getDescription() {
        return "oxygen_friendslist.request.friendRequest";
    }

    @Override
    public String[] getArguments() {
        return new String[] {this.senderUsername};
    }

    @Override
    public int getIndex() {
        return FriendsListMain.FRIEND_REQUEST_ID;
    }

    @Override
    public int getExpireTimeSeconds() {
        return FriendsListConfig.FRIEND_REQUEST_EXPIRE_TIME_SECONDS.asInt();
    }

    @Override
    public void process() {}

    @Override
    public void accepted(EntityPlayer player) {
        FriendsListManagerServer.instance().getPlayerDataManager().addToFriendsList((EntityPlayerMP) player, this.senderUUID);
    }

    @Override
    public void rejected(EntityPlayer player) {
        if (OxygenHelperServer.isPlayerOnline(this.senderUUID))
            OxygenHelperServer.sendStatusMessage(CommonReference.playerByUUID(this.senderUUID), FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListStatusMessage.FRIEND_REQUEST_REJECTED_SENDER.ordinal());
        OxygenHelperServer.sendStatusMessage((EntityPlayerMP) player, FriendsListMain.FRIENDS_LIST_MOD_INDEX, EnumFriendsListStatusMessage.FRIEND_REQUEST_REJECTED_TARGET.ordinal());
    }

    @Override
    public void expired() {}
}
