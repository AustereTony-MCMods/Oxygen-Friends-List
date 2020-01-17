package austeretony.oxygen_friendslist.client;

import austeretony.oxygen_core.common.chat.ChatMessagesHandler;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListStatusMessage;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class FriendsListStatusMessagesHandler implements ChatMessagesHandler {

    @Override
    public int getModIndex() {
        return FriendsListMain.FRIENDS_LIST_MOD_INDEX;
    }

    @Override
    public String getMessage(int messageIndex) {
        return EnumFriendsListStatusMessage.values()[messageIndex].localized();
    }
}
