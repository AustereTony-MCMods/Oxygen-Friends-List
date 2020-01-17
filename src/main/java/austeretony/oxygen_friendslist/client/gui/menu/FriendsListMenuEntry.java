package austeretony.oxygen_friendslist.client.gui.menu;

import org.lwjgl.input.Keyboard;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListScreen;
import austeretony.oxygen_friendslist.client.settings.EnumFriendsListClientSetting;

public class FriendsListMenuEntry implements OxygenMenuEntry {

    @Override
    public int getId() {
        return 60;
    }

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_friendslist.gui.friendslist.title");
    }

    @Override
    public int getKeyCode() {
        return Keyboard.KEY_F;
    }

    @Override
    public boolean isValid() {
        return EnumFriendsListClientSetting.ADD_FRIENDS_LIST.get().asBoolean();
    }

    @Override
    public void open() {
        ClientReference.displayGuiScreen(new FriendsListScreen());
    }
}
