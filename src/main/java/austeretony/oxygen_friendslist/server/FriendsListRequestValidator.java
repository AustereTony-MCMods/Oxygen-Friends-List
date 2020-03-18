package austeretony.oxygen_friendslist.server;

import java.util.UUID;

import austeretony.oxygen_core.server.request.RequestValidator;

public class FriendsListRequestValidator implements RequestValidator {

    @Override
    public boolean isValid(UUID senderUUID, UUID requestedUUID) {
        FriendsListPlayerDataServer 
        senderData = FriendsListManagerServer.instance().getPlayerDataContainer().getPlayerData(senderUUID),
        requestedData = FriendsListManagerServer.instance().getPlayerDataContainer().getPlayerData(requestedUUID);
        if (!(senderData.isIgnored(requestedUUID) || requestedData.isIgnored(senderUUID)))
            return true;
        return false;
    }
}
