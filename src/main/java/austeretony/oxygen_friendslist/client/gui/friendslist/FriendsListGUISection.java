package austeretony.oxygen_friendslist.client.gui.friendslist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.api.PrivilegeProviderClient;
import austeretony.oxygen_core.client.gui.OxygenGUITextures;
import austeretony.oxygen_core.client.gui.elements.ActivityStatusGUIDDList;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButtonPanel;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIContextMenu;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIContextMenuElement.ContextMenuAction;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIText;
import austeretony.oxygen_core.client.gui.elements.OxygenGUITextField;
import austeretony.oxygen_core.client.gui.elements.OxygenSorterGUIElement;
import austeretony.oxygen_core.client.gui.elements.OxygenSorterGUIElement.EnumSorting;
import austeretony.oxygen_core.client.gui.elements.OxygenTexturedGUIButton;
import austeretony.oxygen_core.client.gui.elements.SectionsGUIDDList;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.OxygenPlayerData.EnumActivityStatus;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.FriendListGUIFiller;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.AddFriendGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.EditNoteGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.RemoveFriendGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.SettingsGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.context.EditNoteContextAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.context.RemoveFriendContextAction;
import austeretony.oxygen_friendslist.client.input.FriendsListKeyHandler;
import austeretony.oxygen_friendslist.common.ListEntry;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListPrivilege;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class FriendsListGUISection extends AbstractGUISection {

    private final FriendsListGUIScreen screen;

    private OxygenGUIButton addFriendButton;

    private OxygenTexturedGUIButton settingsButton;

    private OxygenGUIText friendsAmountTextLabel;

    private OxygenGUIButtonPanel friendsPanel;

    private AbstractGUICallback settingsCallback, addFriendCallback, removeCallback, editNoteCallback;

    private ActivityStatusGUIDDList activityStatusDDList;

    private OxygenSorterGUIElement statusSorter, usernameSorter;

    private OxygenGUITextField searchField;

    //cache

    private FriendListEntryGUIButton currentEntryButton;

    public FriendsListGUISection(FriendsListGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new FriendListGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, ClientReference.localize("oxygen_friendslist.gui.friendslist.title"), GUISettings.get().getTitleScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(this.friendsAmountTextLabel = new OxygenGUIText(0, this.getHeight() - 9, "", GUISettings.get().getSubTextScale() - 0.05F, GUISettings.get().getEnabledTextColor()));

        this.addElement(this.statusSorter = new OxygenSorterGUIElement(13, 27, EnumSorting.DOWN, ClientReference.localize("oxygen.sorting.status")));   

        this.statusSorter.setClickListener((sorting)->{
            this.usernameSorter.reset();
            if (sorting == EnumSorting.DOWN)
                this.sortPlayers(0);
            else
                this.sortPlayers(1);
        });

        this.addElement(this.usernameSorter = new OxygenSorterGUIElement(19, 27, EnumSorting.INACTIVE, ClientReference.localize("oxygen.sorting.username")));  

        this.usernameSorter.setClickListener((sorting)->{
            this.statusSorter.reset();
            if (sorting == EnumSorting.DOWN)
                this.sortPlayers(2);
            else
                this.sortPlayers(3);
        });

        this.addElement(this.friendsPanel = new OxygenGUIButtonPanel(this.screen, 6, 32, this.getWidth() - 15, 10, 1, 
                PrivilegeProviderClient.getValue(EnumFriendsListPrivilege.MAX_FRIENDS_AMOUNT.toString(), FriendsListConfig.MAX_FRIENDS_AMOUNT.getIntValue()), 12, GUISettings.get().getPanelTextScale(), true));
        this.addElement(this.searchField = new OxygenGUITextField(90, 16, 60, 8, 24, "...", 3, false, - 1L));
        this.friendsPanel.initSearchField(this.searchField);

        this.friendsPanel.<FriendListEntryGUIButton>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->this.currentEntryButton = clicked);

        List<ContextMenuAction> actions = new ArrayList<>(OxygenManagerClient.instance().getGUIManager().getContextActions(FriendsListMain.FRIENDS_LIST_MENU_SCREEN_ID));
        actions.add(new RemoveFriendContextAction(this));
        actions.add(new EditNoteContextAction(this));
        ContextMenuAction[] array = new ContextMenuAction[actions.size()];
        actions.toArray(array);
        this.friendsPanel.initContextMenu(new OxygenGUIContextMenu(GUISettings.get().getContextMenuWidth(), 9, array));

        this.addElement(new SectionsGUIDDList(this.getWidth() - 4, 5, this, this.screen.getIgnoreListSection()));

        this.addElement(this.activityStatusDDList = new ActivityStatusGUIDDList(7, 16));

        this.activityStatusDDList.setActivityStatusChangeListener((status)->{
            this.statusSorter.setSorting(EnumSorting.DOWN);
            this.usernameSorter.reset();
            this.sortPlayers(0);
        });

        this.addElement(this.addFriendButton = new OxygenGUIButton(4, 167, 40, 10, ClientReference.localize("oxygen.gui.addButton")).disable());     
        this.addElement(this.settingsButton = new OxygenTexturedGUIButton(this.getWidth() - 5, 0, 5, 5, OxygenGUITextures.TRIANGLE_TOP_RIGHT_CORNER_ICONS, 5, 5, ClientReference.localize("oxygen.tooltip.settings")));

        this.settingsCallback = new SettingsGUICallback(this.screen, this, 140, 42).enableDefaultBackground();

        this.addFriendCallback = new AddFriendGUICallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.removeCallback = new RemoveFriendGUICallback(this.screen, this, 140, 38).enableDefaultBackground();
        this.editNoteCallback = new EditNoteGUICallback(this.screen, this, 140, 48).enableDefaultBackground();       
    }

    private void sortPlayers(int mode) {
        List<ListEntry> friends = FriendsListManagerClient.instance().getPlayerDataContainer().getFriends();

        if (mode == 0)
            Collections.sort(friends, (f1, f2)->getActivityStatus(f1).ordinal() - getActivityStatus(f2).ordinal());
        else if (mode == 1)
            Collections.sort(friends, (f1, f2)->getActivityStatus(f2).ordinal() - getActivityStatus(f1).ordinal());
        else if (mode == 2)
            Collections.sort(friends, (f1, f2)->getUsername(f1).compareTo(getUsername(f2)));
        else if (mode == 3)
            Collections.sort(friends, (f1, f2)->getUsername(f2).compareTo(getUsername(f1)));

        this.friendsPanel.reset();
        for (ListEntry entry : friends)  
            this.friendsPanel.addButton(new FriendListEntryGUIButton(entry));

        this.searchField.reset();

        this.friendsPanel.getScroller().resetPosition();
        this.friendsPanel.getScroller().getSlider().reset();

        this.friendsAmountTextLabel.setDisplayText(friends.size() + "/" + PrivilegeProviderClient.getValue(EnumFriendsListPrivilege.MAX_FRIENDS_AMOUNT.toString(), FriendsListConfig.MAX_FRIENDS_AMOUNT.getIntValue()));
        this.friendsAmountTextLabel.setX(this.getWidth() - 4 - this.textWidth(this.friendsAmountTextLabel.getDisplayText(), GUISettings.get().getSubTextScale() - 0.05F));
    }

    public static EnumActivityStatus getActivityStatus(ListEntry entry) {
        return EnumActivityStatus.values()[OxygenHelperClient.getPlayerSharedData(entry.getPlayerUUID()).getByte(OxygenMain.ACTIVITY_STATUS_SHARED_DATA_ID)];
    }

    public static String getUsername(ListEntry entry) {
        return OxygenHelperClient.getPlayerSharedData(entry.getPlayerUUID()).getUsername();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.settingsButton)
                this.settingsCallback.open();
            else if (element == this.addFriendButton)
                this.addFriendCallback.open();
        }
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {   
        if (!this.searchField.isDragged() && !this.hasCurrentCallback())
            if (OxygenGUIHelper.isOxygenMenuEnabled()) {
                if (keyCode == FriendsListGUIScreen.FRIENDS_LIST_MENU_ENTRY.getIndex() + 2)
                    this.screen.close();
            } else if (keyCode == FriendsListKeyHandler.FRIENDS_LIST.getKeyCode())
                this.screen.close();
        return super.keyTyped(typedChar, keyCode); 
    }

    public void sharedDataSynchronized() {
        this.activityStatusDDList.updateActivityStatus();
    }

    public void listSynchronized() {
        if (FriendsListManagerClient.instance().getPlayerDataContainer().canAddFriend())
            this.addFriendButton.enable();
        this.statusSorter.setSorting(EnumSorting.DOWN);
        this.usernameSorter.reset();
        this.sortPlayers(0);
    }

    public void entryAdded() {
        this.statusSorter.setSorting(EnumSorting.DOWN);
        this.usernameSorter.reset();
        this.sortPlayers(0);
    }

    public void entryRemoved() {
        this.statusSorter.setSorting(EnumSorting.DOWN);
        this.usernameSorter.reset();
        this.sortPlayers(0);
    }

    public ListEntry getCurrentListEntry() {
        return FriendsListManagerClient.instance().getPlayerDataContainer().getListEntryByUUID(this.currentEntryButton.index);
    }

    public void openRemoveCallback() {
        this.removeCallback.open();
    }

    public void openEditNoteCallback() {
        this.editNoteCallback.open();
    }
}
