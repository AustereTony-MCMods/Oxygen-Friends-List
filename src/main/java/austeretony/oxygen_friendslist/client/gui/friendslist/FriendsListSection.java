package austeretony.oxygen_friendslist.client.gui.friendslist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Keyboard;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.api.PrivilegesProviderClient;
import austeretony.oxygen_core.client.gui.elements.OxygenActivityStatusSwitcher;
import austeretony.oxygen_core.client.gui.elements.OxygenButton;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_core.client.gui.elements.OxygenScrollablePanel;
import austeretony.oxygen_core.client.gui.elements.OxygenSectionSwitcher;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter.EnumSorting;
import austeretony.oxygen_core.client.gui.elements.OxygenTextField;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.common.EnumActivityStatus;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.FriendListBackgroundFiller;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.AddFriendCallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.EditNoteCallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.RemoveFriendCallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.context.EditNoteContextAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.context.RemoveFriendContextAction;
import austeretony.oxygen_friendslist.common.ListEntry;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListPrivilege;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class FriendsListSection extends AbstractGUISection {

    private final FriendsListScreen screen;

    private OxygenButton addFriendButton;

    private OxygenTextLabel friendsAmountTextLabel;

    private OxygenScrollablePanel friendsPanel;

    private OxygenActivityStatusSwitcher activityStatusSwitcher;

    private OxygenSorter statusSorter, usernameSorter;

    private OxygenTextField searchField;

    private AbstractGUICallback addFriendCallback, removeCallback, editNoteCallback;

    //cache

    private PlayerPanelEntry currentEntryButton;

    public FriendsListSection(FriendsListScreen screen) {
        super(screen);
        this.screen = screen;
        this.setDisplayText(ClientReference.localize("oxygen_friendslist.gui.friendslist.title"));
    }

    @Override
    public void init() {
        this.addElement(new FriendListBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_friendslist.gui.friendslist.title"), EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.friendsAmountTextLabel = new OxygenTextLabel(0, 23, "", EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.statusSorter = new OxygenSorter(13, 27, EnumSorting.DOWN, ClientReference.localize("oxygen_core.gui.status")));   

        this.statusSorter.setClickListener((sorting)->{
            this.usernameSorter.reset();
            if (sorting == EnumSorting.DOWN)
                this.sortPlayers(0);
            else
                this.sortPlayers(1);
        });

        this.addElement(this.usernameSorter = new OxygenSorter(19, 27, EnumSorting.INACTIVE, ClientReference.localize("oxygen_core.gui.username")));  

        this.usernameSorter.setClickListener((sorting)->{
            this.statusSorter.reset();
            if (sorting == EnumSorting.DOWN)
                this.sortPlayers(2);
            else
                this.sortPlayers(3);
        });

        this.addElement(this.friendsPanel = new OxygenScrollablePanel(this.screen, 6, 32, this.getWidth() - 15, 10, 1, 
                PrivilegesProviderClient.getAsInt(EnumFriendsListPrivilege.MAX_FRIENDS_AMOUNT.id(), FriendsListConfig.MAX_FRIENDS_AMOUNT.asInt()), 14, EnumBaseGUISetting.TEXT_PANEL_SCALE.get().asFloat(), true));
        this.addElement(this.searchField = new OxygenTextField(90, 16, 60, 24, ""));
        this.friendsPanel.initSearchField(this.searchField);

        this.friendsPanel.<PlayerPanelEntry>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->this.currentEntryButton = clicked);

        List<OxygenContextMenuAction> actions = new ArrayList<>(OxygenManagerClient.instance().getGUIManager().getContextActions(FriendsListMain.FRIENDS_LIST_MENU_SCREEN_ID));
        actions.add(new RemoveFriendContextAction(this));
        actions.add(new EditNoteContextAction(this));
        OxygenContextMenuAction[] array = new OxygenContextMenuAction[actions.size()];
        actions.toArray(array);
        this.friendsPanel.initContextMenu(new OxygenContextMenu(array));

        this.addElement(new OxygenSectionSwitcher(this.getWidth() - 4, 5, this, this.screen.getIgnoreListSection()));

        this.addElement(this.activityStatusSwitcher = new OxygenActivityStatusSwitcher(7, 16));

        this.addElement(this.addFriendButton = new OxygenButton(6, this.getHeight() - 11, 40, 10, ClientReference.localize("oxygen_core.gui.add")).disable());     
        this.addFriendButton.setKeyPressListener(Keyboard.KEY_F, 
                ()->{
                    if (!this.searchField.isDragged())
                        this.addFriendCallback.open();
                });

        this.addFriendCallback = new AddFriendCallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.removeCallback = new RemoveFriendCallback(this.screen, this, 140, 38).enableDefaultBackground();
        this.editNoteCallback = new EditNoteCallback(this.screen, this, 140, 48).enableDefaultBackground();       
    }

    private void sortPlayers(int mode) {  
        List<ListEntry> friends = FriendsListManagerClient.instance().getPlayerDataContainer().getFriends();

        if (mode == 0)
            Collections.sort(friends, (e1, e2)->getActivityStatus(e1).ordinal() - getActivityStatus(e2).ordinal());
        else if (mode == 1)
            Collections.sort(friends, (e1, e2)->getActivityStatus(e2).ordinal() - getActivityStatus(e1).ordinal());
        else if (mode == 2)
            Collections.sort(friends, (e1, e2)->getUsername(e1).compareTo(getUsername(e2)));
        else if (mode == 3)
            Collections.sort(friends, (e1, e2)->getUsername(e2).compareTo(getUsername(e1)));

        this.friendsPanel.reset();
        for (ListEntry entry : friends)  
            this.friendsPanel.addEntry(new PlayerPanelEntry(entry));

        this.searchField.reset();

        int maxAmount = PrivilegesProviderClient.getAsInt(EnumFriendsListPrivilege.MAX_FRIENDS_AMOUNT.id(), FriendsListConfig.MAX_FRIENDS_AMOUNT.asInt());
        this.friendsAmountTextLabel.setDisplayText(String.valueOf(friends.size()) + "/" + String.valueOf(maxAmount));
        this.friendsAmountTextLabel.setX(this.getWidth() - 6 - this.textWidth(this.friendsAmountTextLabel.getDisplayText(), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.05F));

        this.friendsPanel.getScroller().reset();
        this.friendsPanel.getScroller().updateRowsAmount(MathUtils.clamp(friends.size(), 14, maxAmount));
    }

    public static EnumActivityStatus getActivityStatus(ListEntry entry) {
        return OxygenHelperClient.getPlayerActivityStatus(OxygenHelperClient.getPlayerSharedData(entry.getPlayerUUID()));
    }

    public static String getUsername(ListEntry entry) {
        return OxygenHelperClient.getPlayerSharedData(entry.getPlayerUUID()).getUsername();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0)
            if (element == this.addFriendButton)
                this.addFriendCallback.open();
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {   
        if (!this.searchField.isDragged() && !this.hasCurrentCallback())
            if (OxygenGUIHelper.isOxygenMenuEnabled()) {
                if (keyCode == FriendsListScreen.FRIENDS_LIST_MENU_ENTRY.getKeyCode())
                    this.screen.close();
            } else if (FriendsListConfig.ENABLE_FRIENDSLIST_KEY.asBoolean() 
                    && keyCode == FriendsListManagerClient.instance().getKeyHandler().getFriendsListKeybinding().getKeyCode())
                this.screen.close();
        return super.keyTyped(typedChar, keyCode); 
    }

    public void sharedDataSynchronized() {
        this.activityStatusSwitcher.updateActivityStatus();
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
