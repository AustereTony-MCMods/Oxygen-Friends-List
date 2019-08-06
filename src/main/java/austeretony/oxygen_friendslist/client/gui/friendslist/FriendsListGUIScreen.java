package austeretony.oxygen_friendslist.client.gui.friendslist;

import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen.client.gui.SynchronizedGUIScreen;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import net.minecraft.util.ResourceLocation;

public class FriendsListGUIScreen extends SynchronizedGUIScreen {

    public static final ResourceLocation 
    FRIENDS_LIST_MENU_BACKGROUND = new ResourceLocation(FriendsListMain.MODID, "textures/gui/friendslist/friendslist_menu.png"),
    SETTINGS_CALLBACK_BACKGROUND = new ResourceLocation(FriendsListMain.MODID, "textures/gui/friendslist/settings_callback.png"),

    ADD_FRIEND_CALLBACK_BACKGROUND = new ResourceLocation(FriendsListMain.MODID, "textures/gui/friendslist/add_friend_callback.png"),
    EDIT_NOTE_CALLBACK_BACKGROUND = new ResourceLocation(FriendsListMain.MODID, "textures/gui/friendslist/edit_note_callback.png"),
    IGNORE_CALLBACK_BACKGROUND = new ResourceLocation(FriendsListMain.MODID, "textures/gui/friendslist/ignore_callback.png"),
    REMOVE_FRIEND_CALLBACK_BACKGROUND = new ResourceLocation(FriendsListMain.MODID, "textures/gui/friendslist/remove_friend_callback.png");

    private FriendsListGUISection friendListSection;

    private IgnoreListGUISection ignoreListSection;

    public FriendsListGUIScreen() {
        super(FriendsListMain.FRIENDS_LIST_MENU_SCREEN_ID);
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 195, 202).setAlignment(EnumGUIAlignment.RIGHT, - 10, 0);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.friendListSection = new FriendsListGUISection(this));     
        this.getWorkspace().initSection(this.ignoreListSection = new IgnoreListGUISection(this));     
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

    @Override
    public void loadData() {
        this.friendListSection.sortPlayers(0);
        this.ignoreListSection.sortPlayers(0);
    }

    public FriendsListGUISection getFriendListSection() {
        return this.friendListSection;
    }

    public IgnoreListGUISection getIgnoreListSection() {
        return this.ignoreListSection;
    }
}