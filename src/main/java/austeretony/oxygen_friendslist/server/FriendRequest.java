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
    public int getExpireTimeSeconds() {
        return FriendsListConfig.FRIEND_REQUEST_EXPIRE_TIME_SECONDS.getIntValue();
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
