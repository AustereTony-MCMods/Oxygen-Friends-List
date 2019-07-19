package austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.context;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen_friendslist.client.gui.friendslist.IgnoreListGUISection;

public class EditNoteContextAction extends AbstractContextAction {

    private IgnoreListGUISection section;

    public EditNoteContextAction(IgnoreListGUISection section) {
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
