package austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.context;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.IgnoreListSection;

public class RemoveIgnoredContextAction implements OxygenContextMenuAction {

    private IgnoreListSection section;

    public RemoveIgnoredContextAction(IgnoreListSection section) {
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
        this.section.openRemoveIgnoredCallback();
    }
}
