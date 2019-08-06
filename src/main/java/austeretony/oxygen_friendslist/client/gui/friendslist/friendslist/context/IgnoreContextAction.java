package austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.context;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUISection;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;

public class IgnoreContextAction extends AbstractContextAction {

    private FriendsListGUISection section;

    public IgnoreContextAction(FriendsListGUISection section) {
        this.section = section;
    }

    @Override
    protected String getName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen.gui.action.ignore");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        return FriendsListManagerClient.instance().getClientPlayerData().getIgnoredAmount() < FriendsListConfig.MAX_IGNORED.getIntValue();
    }

    @Override
    public void execute(GUIBaseElement currElement) {
        this.section.openIgnoreFriendCallback();
    }
}
