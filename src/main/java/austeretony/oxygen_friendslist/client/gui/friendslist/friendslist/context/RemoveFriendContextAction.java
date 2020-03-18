package austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.context;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListSection;

public class RemoveFriendContextAction implements OxygenContextMenuAction {

    private FriendsListSection section;

    public RemoveFriendContextAction(FriendsListSection section) {
        this.section = section;
    }

    @Override
    public String getLocalizedName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_core.gui.remove");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        return true;
    }

    @Override
    public void execute(GUIBaseElement currElement) {
        this.section.openRemoveFriendCallback();
    }
}
