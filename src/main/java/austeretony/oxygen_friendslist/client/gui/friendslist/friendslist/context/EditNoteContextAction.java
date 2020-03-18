package austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.context;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListSection;

public class EditNoteContextAction implements OxygenContextMenuAction {

    private FriendsListSection section;

    public EditNoteContextAction(FriendsListSection section) {
        this.section = section;
    }

    @Override
    public String getLocalizedName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_friendslist.gui.context.editNote");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        return true;
    }

    @Override
    public void execute(GUIBaseElement currElement) {
        this.section.openEditFriendNoteCallback();
    }
}
