package austeretony.oxygen_friendslist.common.main;

import austeretony.oxygen.client.core.api.ClientReference;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public enum EnumFriendsListChatMessage {

    SIMPLE_LINE,
    FRIEND_REQUEST_ACCEPTED_SENDER,
    FRIEND_REQUEST_ACCEPTED_TARGET,
    FRIEND_REQUEST_REJECTED_SENDER,
    FRIEND_REQUEST_REJECTED_TARGET,
    FRIEND_REMOVED,
    ADDED_TO_IGNORED,
    NOTE_EDITED,
    IGNORED_REMOVED;

    public void show(String... args) {
        switch (this) {
        case SIMPLE_LINE:
            ClientReference.showMessage(new TextComponentString(args[0]));
            break;
        case FRIEND_REQUEST_ACCEPTED_SENDER:
            ClientReference.showMessage(new TextComponentTranslation("oxygen_friendslist.message.senderFriendRequestAccepted"));
            break;
        case FRIEND_REQUEST_ACCEPTED_TARGET:
            ClientReference.showMessage(new TextComponentTranslation("oxygen_friendslist.message.targetFriendRequestAccepted"));
            break;
        case FRIEND_REQUEST_REJECTED_SENDER:
            ClientReference.showMessage(new TextComponentTranslation("oxygen_friendslist.message.senderFriendRequestRejected"));
            break;
        case FRIEND_REQUEST_REJECTED_TARGET:
            ClientReference.showMessage(new TextComponentTranslation("oxygen_friendslist.message.targetFriendRequestRejected"));
            break;
        case FRIEND_REMOVED:
            ClientReference.showMessage(new TextComponentTranslation("oxygen_friendslist.message.friendRemoved"));
            break;
        case ADDED_TO_IGNORED:
            ClientReference.showMessage(new TextComponentTranslation("oxygen_friendslist.message.addedToIgnored"));
            break;
        case NOTE_EDITED:
            ClientReference.showMessage(new TextComponentTranslation("oxygen_friendslist.message.noteEdited"));
            break;
        case IGNORED_REMOVED:
            ClientReference.showMessage(new TextComponentTranslation("oxygen_friendslist.message.ignoredRemoved"));
            break;
        }
    }
}
