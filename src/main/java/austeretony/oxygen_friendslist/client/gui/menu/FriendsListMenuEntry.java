package austeretony.oxygen_friendslist.client.gui.menu;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_friendslist.client.FriendsListMenuManager;
import austeretony.oxygen_friendslist.client.settings.EnumFriendsListClientSetting;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class FriendsListMenuEntry implements OxygenMenuEntry {

    @Override
    public int getId() {
        return FriendsListMain.FRIENDS_LIST_MENU_SCREEN_ID;
    }

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_friendslist.gui.friendslist.title");
    }

    @Override
    public int getKeyCode() {
        return FriendsListConfig.FRIENDSLIST_KEY.asInt();
    }

    @Override
    public boolean isValid() {
        return EnumFriendsListClientSetting.ADD_FRIENDS_LIST.get().asBoolean();
    }

    @Override
    public void open() {
        FriendsListMenuManager.openFriendsListMenu();
    }
}
