package austeretony.oxygen_friendslist.client.gui.friendslist;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.common.api.OxygenGUIHelper;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import net.minecraft.util.ResourceLocation;

public class FriendsListGUIScreen extends AbstractGUIScreen {

    public static final ResourceLocation FRIEND_LIST_BACKGROUND_TEXTURE = new ResourceLocation(FriendsListMain.MODID, "textures/gui/friendslist/background.png");

    private FriendsListGUISection friendListSection;

    private IgnoreListGUISection ignoreListSection;

    private boolean initialized;

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 195, 202).setAlignment(EnumGUIAlignment.RIGHT, - 10, 0);
    }

    @Override
    protected void initSections() {
        this.friendListSection = new FriendsListGUISection(this);
        this.getWorkspace().initSection(this.friendListSection);     
        this.ignoreListSection = new IgnoreListGUISection(this);  
        this.getWorkspace().initSection(this.ignoreListSection);     
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
    public void updateScreen() {    
        super.updateScreen();
        if (!this.initialized//reduce map calls
                && OxygenGUIHelper.isNeedSync(FriendsListMain.FRIEND_LIST_SCREEN_ID)
                && OxygenGUIHelper.isScreenInitialized(FriendsListMain.FRIEND_LIST_SCREEN_ID)
                && OxygenGUIHelper.isDataRecieved(FriendsListMain.FRIEND_LIST_SCREEN_ID)) {
            this.initialized = true;
            OxygenGUIHelper.resetNeedSync(FriendsListMain.FRIEND_LIST_SCREEN_ID);
            this.friendListSection.sortPlayers(0);
            this.ignoreListSection.sortPlayers(0);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        OxygenGUIHelper.resetNeedSync(FriendsListMain.FRIEND_LIST_SCREEN_ID);
        OxygenGUIHelper.resetScreenInitialized(FriendsListMain.FRIEND_LIST_SCREEN_ID);
        OxygenGUIHelper.resetDataRecieved(FriendsListMain.FRIEND_LIST_SCREEN_ID);
        OxygenHelperClient.savePersistentDataDelegated(FriendsListManagerClient.instance().getClientPlayerData());
    }

    public FriendsListGUISection getFriendListSection() {
        return this.friendListSection;
    }

    public IgnoreListGUISection getIgnoreListSection() {
        return this.ignoreListSection;
    }
}