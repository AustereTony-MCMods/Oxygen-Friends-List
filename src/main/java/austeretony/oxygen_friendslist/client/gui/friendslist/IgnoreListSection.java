package austeretony.oxygen_friendslist.client.gui.friendslist;

import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Keyboard;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.PrivilegesProviderClient;
import austeretony.oxygen_core.client.gui.elements.OxygenButton;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu;
import austeretony.oxygen_core.client.gui.elements.OxygenScrollablePanel;
import austeretony.oxygen_core.client.gui.elements.OxygenSectionSwitcher;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter.EnumSorting;
import austeretony.oxygen_core.client.gui.elements.OxygenTextField;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.FriendListBackgroundFiller;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.callback.AddIgnoredCallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.callback.EditNoteCallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.callback.RemoveIgnoredCallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.context.EditNoteContextAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.context.RemoveIgnoredContextAction;
import austeretony.oxygen_friendslist.common.ListEntry;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListPrivilege;

public class IgnoreListSection extends AbstractGUISection {

    private final FriendsListScreen screen;

    private OxygenButton addIgnoredButton;

    private OxygenTextLabel ignoredAmountTextLabel;

    private OxygenScrollablePanel ignoredPanel;

    private OxygenSorter statusSorter, usernameSorter;

    private OxygenTextField searchField;

    private AbstractGUICallback addIgnoredCallback, removeCallback, editNoteCallback;

    //cache

    private PlayerPanelEntry currentEntryButton;

    public IgnoreListSection(FriendsListScreen screen) {
        super(screen);
        this.screen = screen;
        this.setDisplayText(ClientReference.localize("oxygen_friendslist.gui.ignorelist.title"));
    }

    @Override
    public void init() { 
        this.addElement(new FriendListBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_friendslist.gui.friendslist.title"), EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.ignoredAmountTextLabel = new OxygenTextLabel(0, 23, "", EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

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

        this.addElement(this.ignoredPanel = new OxygenScrollablePanel(this.screen, 6, 32, this.getWidth() - 15, 10, 1, 
                PrivilegesProviderClient.getAsInt(EnumFriendsListPrivilege.MAX_IGNORED_AMOUNT.id(), FriendsListConfig.MAX_IGNORED_AMOUNT.asInt()), 14, EnumBaseGUISetting.TEXT_PANEL_SCALE.get().asFloat(), true));
        this.addElement(this.searchField = new OxygenTextField(90, 16, 60, 24, ""));
        this.ignoredPanel.initSearchField(this.searchField);

        this.ignoredPanel.<PlayerPanelEntry>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->this.currentEntryButton = clicked);

        this.ignoredPanel.initContextMenu(new OxygenContextMenu(
                new RemoveIgnoredContextAction(this),
                new EditNoteContextAction(this)));

        this.addElement(new OxygenSectionSwitcher(this.getWidth() - 4, 5, this, this.screen.getFriendListSection()));

        this.addElement(this.addIgnoredButton = new OxygenButton(6, this.getHeight() - 11, 40, 10, ClientReference.localize("oxygen_core.gui.add")).disable());     
        this.addIgnoredButton.setKeyPressListener(Keyboard.KEY_F, 
                ()->{
                    if (!this.searchField.isDragged())
                        this.addIgnoredCallback.open();
                });

        this.addIgnoredCallback = new AddIgnoredCallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.removeCallback = new RemoveIgnoredCallback(this.screen, this, 140, 38).enableDefaultBackground();
        this.editNoteCallback = new EditNoteCallback(this.screen, this, 140, 48).enableDefaultBackground();
    }

    private void sortPlayers(int mode) {
        List<ListEntry> ignored = FriendsListManagerClient.instance().getPlayerDataContainer().getIgnored();

        if (mode == 0)
            Collections.sort(ignored, (e1, e2)->FriendsListSection.getActivityStatus(e1).ordinal() - FriendsListSection.getActivityStatus(e2).ordinal());
        else if (mode == 1)
            Collections.sort(ignored, (e1, e2)->FriendsListSection.getActivityStatus(e2).ordinal() - FriendsListSection.getActivityStatus(e1).ordinal());
        else if (mode == 2)
            Collections.sort(ignored, (e1, e2)->FriendsListSection.getUsername(e1).compareTo(FriendsListSection.getUsername(e2)));
        else if (mode == 3)
            Collections.sort(ignored, (e1, e2)->FriendsListSection.getUsername(e2).compareTo(FriendsListSection.getUsername(e1)));

        this.ignoredPanel.reset();
        for (ListEntry entry : ignored)  
            this.ignoredPanel.addEntry(new PlayerPanelEntry(entry));

        this.searchField.reset();

        int maxAmount = PrivilegesProviderClient.getAsInt(EnumFriendsListPrivilege.MAX_IGNORED_AMOUNT.id(), FriendsListConfig.MAX_IGNORED_AMOUNT.asInt());
        this.ignoredAmountTextLabel.setDisplayText(String.valueOf(ignored.size()) + "/" + String.valueOf(maxAmount));
        this.ignoredAmountTextLabel.setX(this.getWidth() - 6 - this.textWidth(this.ignoredAmountTextLabel.getDisplayText(), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.05F));

        this.ignoredPanel.getScroller().reset();
        this.ignoredPanel.getScroller().updateRowsAmount(MathUtils.clamp(ignored.size(), 14, maxAmount));
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
                if (keyCode == FriendsListScreen.FRIENDS_LIST_MENU_ENTRY.getKeyCode())
                    this.screen.close();
            } else if (FriendsListConfig.ENABLE_FRIENDSLIST_KEY.asBoolean() 
                    && keyCode == FriendsListManagerClient.instance().getKeyHandler().getFriendsListKeybinding().getKeyCode())
                this.screen.close();
        return super.keyTyped(typedChar, keyCode); 
    }

    public void sharedDataSynchronized() {}

    public void listSynchronized() {
        if (FriendsListManagerClient.instance().getPlayerDataContainer().canAddIgnored())
            this.addIgnoredButton.enable();
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
