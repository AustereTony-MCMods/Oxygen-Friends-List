package austeretony.oxygen_friendslist.client.gui.context;

import java.util.UUID;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.IndexedGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIContextMenuElement.ContextMenuAction;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;

public class AddToFriendsContextAction implements ContextMenuAction {

    @Override
    public String getName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_friendslist.gui.action.addToFriends");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        UUID targetUUID = ((IndexedGUIButton<UUID>) currElement).index;
        return OxygenHelperClient.isPlayerAvailable(targetUUID) && !FriendsListManagerClient.instance().getPlayerDataContainer().haveEntryForUUID(targetUUID);

    }

    @Override
    public void execute(GUIBaseElement currElement) {
        UUID targetUUID = ((IndexedGUIButton<UUID>) currElement).index;
        FriendsListManagerClient.instance().getPlayerDataManager().sendFriendRequestSynced(targetUUID);
    }
}
