package austeretony.oxygen_friendslist.client.gui.friendslist;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_friendslist.client.gui.menu.FriendsListMenuEntry;
import austeretony.oxygen_friendslist.client.settings.gui.EnumFriendsListGUISetting;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class FriendsListScreen extends AbstractGUIScreen {

    public static final OxygenMenuEntry FRIENDS_LIST_MENU_ENTRY = new FriendsListMenuEntry();

    private FriendsListSection friendListSection;

    private IgnoreListSection ignoreListSection;

    public FriendsListScreen() {
        OxygenHelperClient.syncSharedData(FriendsListMain.FRIENDS_LIST_MENU_SCREEN_ID);
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        EnumGUIAlignment alignment = EnumGUIAlignment.CENTER;
        switch (EnumFriendsListGUISetting.FRIENDS_LIST_ALIGNMENT.get().asInt()) {
        case - 1: 
            alignment = EnumGUIAlignment.LEFT;
            break;
        case 0:
            alignment = EnumGUIAlignment.CENTER;
            break;
        case 1:
            alignment = EnumGUIAlignment.RIGHT;
            break;    
        default:
            alignment = EnumGUIAlignment.CENTER;
            break;
        }
        return new GUIWorkspace(this, 195, 188).setAlignment(alignment, 0, 0);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.friendListSection = (FriendsListSection) new FriendsListSection(this).enable());
        this.getWorkspace().initSection(this.ignoreListSection = (IgnoreListSection) new IgnoreListSection(this).enable()); 
    }

    @Override
    protected AbstractGUISection getDefaultSection() {
        return this.friendListSection;
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element) {}

    @Override
    protected boolean doesGUIPauseGame() {
        return false;
    }

    public void sharedDataSynchronized() {
        OxygenHelperClient.syncData(FriendsListMain.LIST_DATA_ID);

        this.friendListSection.sharedDataSynchronized();
        this.ignoreListSection.sharedDataSynchronized();
    }

    public void listSynchronized() {
        this.friendListSection.listSynchronized();
        this.ignoreListSection.listSynchronized();
    }

    public void entryAdded() {
        this.friendListSection.entryAdded();
        this.ignoreListSection.entryAdded();
    }

    public void entryRemoved() {
        this.friendListSection.entryRemoved();
        this.ignoreListSection.entryRemoved();
    }

    public FriendsListSection getFriendListSection() {
        return this.friendListSection;
    }

    public IgnoreListSection getIgnoreListSection() {
        return this.ignoreListSection;
    }
}
