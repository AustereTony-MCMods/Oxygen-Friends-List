package austeretony.oxygen_friendslist.client.gui.friendslist;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import net.minecraft.util.ResourceLocation;

public class FriendsListGUIScreen extends AbstractGUIScreen {

    public static final ResourceLocation NOTE_ICONS = new ResourceLocation(FriendsListMain.MODID, "textures/gui/note_icons.png");

    public static final OxygenMenuEntry FRIENDS_LIST_MENU_ENTRY = new FriendsListMenuEntry();

    private FriendsListGUISection friendListSection;

    private IgnoreListGUISection ignoreListSection;

    public FriendsListGUIScreen() {
        OxygenHelperClient.syncSharedData(FriendsListMain.FRIENDS_LIST_MENU_SCREEN_ID);
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 195, 179).setAlignment(EnumGUIAlignment.RIGHT, - 10, 0);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.friendListSection = (FriendsListGUISection) new FriendsListGUISection(this)
                .setDisplayText(ClientReference.localize("oxygen_friendslist.gui.friendslist.title")).enable());
        this.getWorkspace().initSection(this.ignoreListSection = (IgnoreListGUISection) new IgnoreListGUISection(this)
                .setDisplayText(ClientReference.localize("oxygen_friendslist.gui.ignorelist.title")).enable()); 
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

    public FriendsListGUISection getFriendListSection() {
        return this.friendListSection;
    }

    public IgnoreListGUISection getIgnoreListSection() {
        return this.ignoreListSection;
    }
}
