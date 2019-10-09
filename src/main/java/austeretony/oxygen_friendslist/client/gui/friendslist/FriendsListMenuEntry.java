package austeretony.oxygen_friendslist.client.gui.friendslist;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.menu.AbstractMenuEntry;

public class FriendsListMenuEntry extends AbstractMenuEntry  {

    @Override
    public String getName() {
        return "oxygen_friendslist.gui.friendslist.title";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void open() {
        ClientReference.displayGuiScreen(new FriendsListGUIScreen());
    }
}