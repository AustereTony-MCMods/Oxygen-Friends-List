package austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.context;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUISection;

public class EditNoteContextAction extends AbstractContextAction {

    private FriendsListGUISection section;

    public EditNoteContextAction(FriendsListGUISection section) {
        this.section = section;
    }

    @Override
    protected String getName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen.gui.action.editNote");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        return true;
    }

    @Override
    public void execute(GUIBaseElement currElement) {
        this.section.openEditNoteCallback();
    }
}
