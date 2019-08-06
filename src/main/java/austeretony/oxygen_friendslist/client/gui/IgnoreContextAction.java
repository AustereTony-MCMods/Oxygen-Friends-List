package austeretony.oxygen_friendslist.client.gui;

import java.util.UUID;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.IndexedGUIButton;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;

public class IgnoreContextAction extends AbstractContextAction {

    @Override
    protected String getName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_friendslist.gui.action.addToIgnored");
    }

    @Override
    protected boolean isValid(GUIBaseElement currElement) {
        UUID targetUUID = ((IndexedGUIButton<UUID>) currElement).index;
        return !targetUUID.equals(OxygenHelperClient.getPlayerUUID()) 
                && OxygenHelperClient.isOnline(targetUUID)
                && !FriendsListManagerClient.instance().getClientPlayerData().haveFriendListEntryForUUID(targetUUID);
    }

    @Override
    protected void execute(GUIBaseElement currElement) {
        UUID targetUUID = ((IndexedGUIButton<UUID>) currElement).index;
        FriendsListManagerClient.instance().addToIgnoredSynced(targetUUID);
    }
}
