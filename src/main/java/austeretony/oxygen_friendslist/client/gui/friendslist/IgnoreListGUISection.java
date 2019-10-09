package austeretony.oxygen_friendslist.client.gui.friendslist;

import java.util.Collections;
import java.util.List;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.PrivilegeProviderClient;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButtonPanel;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIContextMenu;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIText;
import austeretony.oxygen_core.client.gui.elements.OxygenGUITextField;
import austeretony.oxygen_core.client.gui.elements.OxygenSorterGUIElement;
import austeretony.oxygen_core.client.gui.elements.OxygenSorterGUIElement.EnumSorting;
import austeretony.oxygen_core.client.gui.elements.SectionsGUIDDList;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.FriendListGUIFiller;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.callback.AddIgnoredGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.callback.EditNoteGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.callback.RemoveIgnoredGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.context.EditNoteContextAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.context.RemoveIgnoredContextAction;
import austeretony.oxygen_friendslist.client.input.FriendsListKeyHandler;
import austeretony.oxygen_friendslist.common.ListEntry;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListPrivilege;

public class IgnoreListGUISection extends AbstractGUISection {

    private final FriendsListGUIScreen screen;

    private OxygenGUIButton addIgnoredButton;

    private OxygenGUIText ignoredAmountTextLabel;

    private OxygenGUIButtonPanel ignoredPanel;

    private AbstractGUICallback addIgnoredCallback, removeCallback, editNoteCallback;

    private OxygenSorterGUIElement statusSorter, usernameSorter;

    private OxygenGUITextField searchField;

    //cache

    private FriendListEntryGUIButton currentEntryButton;

    public IgnoreListGUISection(FriendsListGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() { 
        this.addElement(new FriendListGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, ClientReference.localize("oxygen_friendslist.gui.friendslist.title"), GUISettings.get().getTitleScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(this.ignoredAmountTextLabel = new OxygenGUIText(0, this.getHeight() - 9, "", GUISettings.get().getSubTextScale() - 0.05F, GUISettings.get().getEnabledTextColor()));

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

        this.addElement(this.ignoredPanel = new OxygenGUIButtonPanel(this.screen, 6, 32, this.getWidth() - 15, 10, 1, 
                PrivilegeProviderClient.getValue(EnumFriendsListPrivilege.MAX_IGNORED_AMOUNT.toString(), FriendsListConfig.MAX_IGNORED_AMOUNT.getIntValue()), 12, GUISettings.get().getPanelTextScale(), true));
        this.addElement(this.searchField = new OxygenGUITextField(90, 16, 60, 8, 24, "...", 3, false, - 1L));
        this.ignoredPanel.initSearchField(this.searchField);

        this.ignoredPanel.<FriendListEntryGUIButton>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->this.currentEntryButton = clicked);

        this.ignoredPanel.initContextMenu(new OxygenGUIContextMenu(GUISettings.get().getContextMenuWidth(), 9, 
                new RemoveIgnoredContextAction(this),
                new EditNoteContextAction(this)));

        this.addElement(new SectionsGUIDDList(this.getWidth() - 4, 5, this, this.screen.getFriendListSection()));

        this.addElement(this.addIgnoredButton = new OxygenGUIButton(4, 167, 40, 10, ClientReference.localize("oxygen.gui.addButton")).disable());     

        this.addIgnoredCallback = new AddIgnoredGUICallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.removeCallback = new RemoveIgnoredGUICallback(this.screen, this, 140, 38).enableDefaultBackground();
        this.editNoteCallback = new EditNoteGUICallback(this.screen, this, 140, 48).enableDefaultBackground();
    }

    private void sortPlayers(int mode) {
        List<ListEntry> ignored = FriendsListManagerClient.instance().getPlayerDataContainer().getIgnored();

        if (mode == 0)
            Collections.sort(ignored, (f1, f2)->FriendsListGUISection.getActivityStatus(f1).ordinal() - FriendsListGUISection.getActivityStatus(f2).ordinal());
        else if (mode == 1)
            Collections.sort(ignored, (f1, f2)->FriendsListGUISection.getActivityStatus(f2).ordinal() - FriendsListGUISection.getActivityStatus(f1).ordinal());
        else if (mode == 2)
            Collections.sort(ignored, (f1, f2)->FriendsListGUISection.getUsername(f1).compareTo(FriendsListGUISection.getUsername(f2)));
        else if (mode == 3)
            Collections.sort(ignored, (f1, f2)->FriendsListGUISection.getUsername(f2).compareTo(FriendsListGUISection.getUsername(f1)));

        this.ignoredPanel.reset();
        for (ListEntry entry : ignored)  
            this.ignoredPanel.addButton(new FriendListEntryGUIButton(entry));

        this.searchField.reset();

        this.ignoredPanel.getScroller().resetPosition();
        this.ignoredPanel.getScroller().getSlider().reset();

        this.ignoredAmountTextLabel.setDisplayText(ignored.size() + "/" + PrivilegeProviderClient.getValue(EnumFriendsListPrivilege.MAX_IGNORED_AMOUNT.toString(), FriendsListConfig.MAX_IGNORED_AMOUNT.getIntValue()));
        this.ignoredAmountTextLabel.setX(this.getWidth() - 4 - this.textWidth(this.ignoredAmountTextLabel.getDisplayText(), GUISettings.get().getSubTextScale() - 0.05F));
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0)
            if (element == this.addIgnoredButton)
                this.addIgnoredCallback.open();
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

    public void sharedDataSynchronized() {}

    public void listSynchronized() {
        if (FriendsListManagerClient.instance().getPlayerDataContainer().canAddIgnored())
            this.addIgnoredButton.enable();
        this.statusSorter.setSorting(EnumSorting.DOWN);
        this.usernameSorter.reset();
        this.sortPlayers(0);    }

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
