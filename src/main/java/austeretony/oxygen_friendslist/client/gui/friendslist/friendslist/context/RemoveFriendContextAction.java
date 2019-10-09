package austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.context;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIContextMenuElement.ContextMenuAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUISection;

public class RemoveFriendContextAction implements ContextMenuAction {

    private FriendsListGUISection section;

    public RemoveFriendContextAction(FriendsListGUISection section) {
        this.section = section;
    }

    @Override
    public String getName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen.gui.action.remove");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        return true;
    }

    @Override
    public void execute(GUIBaseElement currElement) {
        this.section.openRemoveCallback();
    }
}
