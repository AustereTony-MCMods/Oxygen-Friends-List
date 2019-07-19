package austeretony.oxygen_friendslist.common.main;

import java.util.UUID;

import austeretony.oxygen.common.request.IRequestValidator;
import austeretony.oxygen_friendslist.common.FriendsListManagerServer;

public class IgnoreRequestValidator implements IRequestValidator {

    @Override
    public boolean isValid(UUID senderUUID, UUID requestedUUID) {
        FriendsListPlayerData 
        senderData = FriendsListManagerServer.instance().getPlayerData(senderUUID),
        requestedData = FriendsListManagerServer.instance().getPlayerData(requestedUUID);
        if ((!senderData.haveFriendListEntryForUUID(requestedUUID) || (senderData.haveFriendListEntryForUUID(requestedUUID) && !senderData.getFriendListEntryByUUID(requestedUUID).ignored))
                && (!requestedData.haveFriendListEntryForUUID(senderUUID) || (requestedData.haveFriendListEntryForUUID(senderUUID) && !requestedData.getFriendListEntryByUUID(senderUUID).ignored)))
            return true;
        return false;
    }
}
