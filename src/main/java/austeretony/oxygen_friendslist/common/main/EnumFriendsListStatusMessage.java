package austeretony.oxygen_friendslist.common.main;

import austeretony.oxygen_core.client.api.ClientReference;

public enum EnumFriendsListStatusMessage {

    FRIEND_REQUEST_ACCEPTED_SENDER("senderFriendRequestAccepted"),
    FRIEND_REQUEST_ACCEPTED_TARGET("targetFriendRequestAccepted"),
    FRIEND_REQUEST_REJECTED_SENDER("senderFriendRequestRejected"),
    FRIEND_REQUEST_REJECTED_TARGET("targetFriendRequestRejected"),
    FRIEND_REMOVED("friendRemoved"),
    ADDED_TO_IGNORED("addedToIgnored"),
    NOTE_EDITED("noteEdited"),
    IGNORED_REMOVED("ignoredRemoved");

    private final String status;

    EnumFriendsListStatusMessage(String status) {
        this.status = "oxygen_friendslist.status." + status;
    }

    public String localized() {
        return ClientReference.localize(this.status);
    }
}
