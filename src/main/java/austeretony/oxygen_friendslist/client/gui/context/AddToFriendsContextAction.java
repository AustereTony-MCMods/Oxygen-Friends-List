package austeretony.oxygen_friendslist.client.gui.context;

import java.util.UUID;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_core.client.gui.elements.OxygenWrapperPanelEntry;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;

public class AddToFriendsContextAction implements OxygenContextMenuAction {

    @Override
    public String getLocalizedName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_friendslist.gui.action.addToFriends");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        UUID targetUUID = ((OxygenWrapperPanelEntry<UUID>) currElement).getWrapped();
        return OxygenHelperClient.isPlayerAvailable(targetUUID) && !FriendsListManagerClient.instance().getPlayerDataContainer().haveEntryForUUID(targetUUID);

    }

    @Override
    public void execute(GUIBaseElement currElement) {
        UUID targetUUID = ((OxygenWrapperPanelEntry<UUID>) currElement).getWrapped();
        FriendsListManagerClient.instance().getPlayerDataManager().sendFriendRequestSynced(targetUUID);
    }
}
