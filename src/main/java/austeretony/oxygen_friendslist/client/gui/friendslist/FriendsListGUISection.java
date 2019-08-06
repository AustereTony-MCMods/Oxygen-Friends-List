package austeretony.oxygen_friendslist.client.gui.friendslist;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import austeretony.alternateui.screen.browsing.GUIScroller;
import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.button.GUISlider;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.contextmenu.GUIContextMenu;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.image.GUIImageLabel;
import austeretony.alternateui.screen.list.GUIDropDownList;
import austeretony.alternateui.screen.panel.GUIButtonPanel;
import austeretony.alternateui.screen.text.GUITextField;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.alternateui.util.EnumGUIOrientation;
import austeretony.oxygen.client.OxygenManagerClient;
import austeretony.oxygen.client.api.OxygenGUIHelper;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.IndexedGUIDropDownElement;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenPlayerData.EnumActivityStatus;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen.util.MathUtils;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.FriendListGUIFiller;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.AddFriendGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.EditNoteGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.IgnoreGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.RemoveFriendGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.SettingsGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.context.EditNoteContextAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.context.IgnoreContextAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.context.RemoveFriendContextAction;
import austeretony.oxygen_friendslist.client.input.FriendsListKeyHandler;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.main.FriendListEntry;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class FriendsListGUISection extends AbstractGUISection {

    private final FriendsListGUIScreen screen;

    private GUIButton ignoredPageButton, settingsButton, searchButton, refreshButton, addFriendButton, 
    sortDownStatusButton, sortUpStatusButton, sortDownUsernameButton, sortUpUsernameButton;

    private FriendListEntryGUIButton currentEntry;

    private GUITextLabel friendsOnlineTextLabel, friendsAmountTextLabel, playerNameTextLabel, autoAcceptTextLabel;

    private GUITextField searchField;

    private GUIButtonPanel friendsPanel;

    private GUIDropDownList statusDropDownList;

    private EnumActivityStatus clientStatus;

    private GUIImageLabel statusImageLabel;

    private AbstractGUICallback settingsCallback, addFriendCallback, removeCallback, ignoreCallback, editNoteCallback;

    public FriendsListGUISection(FriendsListGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new FriendListGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        String title = ClientReference.localize("oxygen_friendslist.gui.friends.title");
        this.addElement(new GUITextLabel(2, 4).setDisplayText(title, false, GUISettings.instance().getTitleScale()));

        this.addElement(new GUIButton(this.getWidth() - 32, 0, 12, 12).setTexture(OxygenGUITextures.FRIENDS_ICONS, 12, 12).initSimpleTooltip(ClientReference.localize("oxygen_friendslist.gui.friends.title"), GUISettings.instance().getTooltipScale()).toggle()); 
        this.addElement(this.ignoredPageButton = new GUIButton(this.getWidth() - 18, 0, 12, 12).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.IGNORED_ICONS, 12, 12).initSimpleTooltip(ClientReference.localize("oxygen_friendslist.gui.ignored.title"), GUISettings.instance().getTooltipScale())); 

        this.addElement(this.settingsButton = new GUIButton(this.getWidth() - 5, 0, 5, 5).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.TRIANGLE_TOP_RIGHT_CORNER_ICONS, 5, 5).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.settings"), GUISettings.instance().getTooltipScale()));

        this.addElement(this.searchButton = new GUIButton(4, 15, 7, 7).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SEARCH_ICONS, 7, 7).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.search"), GUISettings.instance().getTooltipScale()));         
        this.addElement(this.refreshButton = new GUIButton(79, 14, 10, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.REFRESH_ICONS, 9, 9).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.refresh"), GUISettings.instance().getTooltipScale()));         
        this.addElement(this.playerNameTextLabel = new GUITextLabel(91, 15).setDisplayText(OxygenHelperClient.getSharedClientPlayerData().getUsername(), false, GUISettings.instance().getSubTextScale()));
        this.addElement(this.friendsOnlineTextLabel = new GUITextLabel(0, 15).setTextScale(GUISettings.instance().getSubTextScale()).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.online"), GUISettings.instance().getTooltipScale())); 

        this.addElement(this.sortDownStatusButton = new GUIButton(7, 29, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_DOWN_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortUpStatusButton = new GUIButton(7, 25, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_UP_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortDownUsernameButton = new GUIButton(19, 29, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_DOWN_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortUpUsernameButton = new GUIButton(19, 25, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_UP_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(new GUITextLabel(24, 25).setDisplayText(ClientReference.localize("oxygen.gui.username")).setTextScale(GUISettings.instance().getSubTextScale())); 
        this.addElement(new GUITextLabel(100, 25).setDisplayText(ClientReference.localize("oxygen.gui.dimension")).setTextScale(GUISettings.instance().getSubTextScale())); 

        this.friendsPanel = new GUIButtonPanel(EnumGUIOrientation.VERTICAL, 0, 35, this.getWidth() - 3, 10).setButtonsOffset(1).setTextScale(GUISettings.instance().getPanelTextScale());
        this.addElement(this.friendsPanel);
        this.addElement(this.searchField = new GUITextField(0, 14, 76, 9, 20).setTextScale(GUISettings.instance().getSubTextScale())
                .enableDynamicBackground(GUISettings.instance().getEnabledTextFieldColor(), GUISettings.instance().getDisabledTextFieldColor(), GUISettings.instance().getHoveredTextFieldColor())
                .setDisplayText("...").setLineOffset(3).disableFull().cancelDraggedElementLogic());
        this.friendsPanel.initSearchField(this.searchField);
        GUIScroller scroller = new GUIScroller(MathUtils.clamp(FriendsListConfig.MAX_FRIENDS.getIntValue(), 14, 100), 14);
        this.friendsPanel.initScroller(scroller);
        GUISlider slider = new GUISlider(this.getWidth() - 2, 35, 2, this.getHeight() - 49);
        slider.setDynamicBackgroundColor(GUISettings.instance().getEnabledSliderColor(), GUISettings.instance().getDisabledSliderColor(), GUISettings.instance().getHoveredSliderColor());
        scroller.initSlider(slider);    

        GUIContextMenu menu = new GUIContextMenu(GUISettings.instance().getContextMenuWidth(), 10).setScale(GUISettings.instance().getContextMenuScale()).setTextScale(GUISettings.instance().getTextScale()).setTextAlignment(EnumGUIAlignment.LEFT, 2);
        menu.setOpenSound(OxygenSoundEffects.CONTEXT_OPEN.soundEvent);
        menu.setCloseSound(OxygenSoundEffects.CONTEXT_CLOSE.soundEvent);
        this.friendsPanel.initContextMenu(menu);
        menu.enableDynamicBackground(GUISettings.instance().getEnabledContextActionColor(), GUISettings.instance().getDisabledContextActionColor(), GUISettings.instance().getHoveredContextActionColor());
        menu.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
        menu.addElement(new RemoveFriendContextAction(this));
        menu.addElement(new IgnoreContextAction(this));
        menu.addElement(new EditNoteContextAction(this));

        //Support
        for (AbstractContextAction action : OxygenGUIHelper.getContextActions(FriendsListMain.FRIENDS_LIST_MENU_SCREEN_ID))
            menu.addElement(action);

        this.addElement(this.addFriendButton = new GUIButton(4, this.getHeight() - 11, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(ClientReference.localize("oxygen.gui.addButton"), true, GUISettings.instance().getButtonTextScale()));     
        this.lockAddButton();
        this.addElement(this.friendsAmountTextLabel = new GUITextLabel(0, this.getHeight() - 10).setTextScale(GUISettings.instance().getSubTextScale())); 

        this.clientStatus = OxygenHelperClient.getClientPlayerStatus();
        int statusOffset = this.playerNameTextLabel.getX() + this.textWidth(this.playerNameTextLabel.getDisplayText(), GUISettings.instance().getSubTextScale());
        this.addElement(this.statusImageLabel = new GUIImageLabel(statusOffset + 4, 17).setTexture(OxygenGUITextures.STATUS_ICONS, 3, 3, this.clientStatus.ordinal() * 3, 0, 12, 3));   
        this.statusDropDownList = new GUIDropDownList(statusOffset + 10, 16, GUISettings.instance().getDropDownListWidth(), 10).setScale(GUISettings.instance().getDropDownListScale()).setDisplayText(this.clientStatus.localizedName()).setTextScale(GUISettings.instance().getTextScale()).setTextAlignment(EnumGUIAlignment.LEFT, 1);
        this.statusDropDownList.setOpenSound(OxygenSoundEffects.DROP_DOWN_LIST_OPEN.soundEvent);
        this.statusDropDownList.setCloseSound(OxygenSoundEffects.CONTEXT_CLOSE.soundEvent);
        IndexedGUIDropDownElement<EnumActivityStatus> profileElement;
        for (EnumActivityStatus status : EnumActivityStatus.values()) {
            profileElement = new IndexedGUIDropDownElement(status);
            profileElement.setDisplayText(status.localizedName());
            profileElement.enableDynamicBackground(GUISettings.instance().getEnabledContextActionColor(), GUISettings.instance().getDisabledContextActionColor(), GUISettings.instance().getHoveredContextActionColor());
            profileElement.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
            this.statusDropDownList.addElement(profileElement);
        }
        this.addElement(this.statusDropDownList);   

        this.settingsCallback = new SettingsGUICallback(this.screen, this, 140, 38).enableDefaultBackground();

        this.addFriendCallback = new AddFriendGUICallback(this.screen, this, 140, 68).enableDefaultBackground();
        this.removeCallback = new RemoveFriendGUICallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.ignoreCallback = new IgnoreGUICallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.editNoteCallback = new EditNoteGUICallback(this.screen, this, 140, 50).enableDefaultBackground();          
    }

    public void sortPlayers(int mode) {
        List<FriendListEntry> friends = FriendsListManagerClient.instance().getClientPlayerData().getFriendListEntries()
                .stream()
                .filter(e->!e.ignored)
                .collect(Collectors.toList());

        if (mode == 0)
            Collections.sort(friends, (f1, f2)->FriendsListManagerClient.getActivityStatus(f1).ordinal() - FriendsListManagerClient.getActivityStatus(f2).ordinal());
        else if (mode == 1)
            Collections.sort(friends, (f1, f2)->FriendsListManagerClient.getActivityStatus(f2).ordinal() - FriendsListManagerClient.getActivityStatus(f1).ordinal());
        else if (mode == 2)
            Collections.sort(friends, (f1, f2)->FriendsListManagerClient.getUsername(f1).compareTo(FriendsListManagerClient.getUsername(f2)));
        else if (mode == 3)
            Collections.sort(friends, (f1, f2)->FriendsListManagerClient.getUsername(f2).compareTo(FriendsListManagerClient.getUsername(f1)));

        this.friendsPanel.reset();
        FriendListEntryGUIButton button;
        int onlinePlayers = 0;
        EnumActivityStatus status;
        for (FriendListEntry entry : friends) {     
            status = EnumActivityStatus.OFFLINE;
            if (OxygenHelperClient.isOnline(entry.playerUUID)) {
                status = OxygenHelperClient.getPlayerStatus(entry.playerUUID);
                if (status != EnumActivityStatus.OFFLINE)
                    onlinePlayers++;
            }
            button = new FriendListEntryGUIButton(entry, status);
            button.enableDynamicBackground(GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getDisabledElementColor(), GUISettings.instance().getHoveredElementColor());
            button.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
            this.friendsPanel.addButton(button);
        }

        this.friendsPanel.getScroller().resetPosition();
        this.friendsPanel.getScroller().getSlider().reset();

        this.searchField.reset();

        this.friendsOnlineTextLabel.setDisplayText(onlinePlayers + " / " + friends.size());
        this.friendsOnlineTextLabel.setX(this.getWidth() - 4 - this.textWidth(this.friendsOnlineTextLabel.getDisplayText(), GUISettings.instance().getSubTextScale()));
        this.friendsAmountTextLabel.setDisplayText(friends.size() + " / " + FriendsListConfig.MAX_FRIENDS.getIntValue());
        this.friendsAmountTextLabel.setX(this.getWidth() - 4 - this.textWidth(this.friendsAmountTextLabel.getDisplayText(), GUISettings.instance().getSubTextScale()));

        this.sortUpStatusButton.toggle();
        this.sortDownStatusButton.setToggled(false);
        this.sortDownUsernameButton.setToggled(false);
        this.sortUpUsernameButton.setToggled(false);
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.ignoredPageButton)
                this.screen.getIgnoreListSection().open();
            else if (element == this.settingsButton)
                this.settingsCallback.open();
            else if (element == this.searchButton) {
                this.searchField.enableFull();
                this.searchButton.setHovered(false);
                this.searchButton.disableFull();
            } else if (element == this.refreshButton)
                this.sortPlayers(0);
            else if (element == this.sortDownStatusButton) {
                if (!this.sortDownStatusButton.isToggled()) {
                    this.sortPlayers(1);
                    this.sortUpStatusButton.setToggled(false);
                    this.sortDownStatusButton.toggle(); 

                    this.sortDownUsernameButton.setToggled(false);
                    this.sortUpUsernameButton.setToggled(false);
                }
            } else if (element == this.sortUpStatusButton) {
                if (!this.sortUpStatusButton.isToggled()) {
                    this.sortPlayers(0);
                    this.sortDownStatusButton.setToggled(false);
                    this.sortUpStatusButton.toggle();

                    this.sortDownUsernameButton.setToggled(false);
                    this.sortUpUsernameButton.setToggled(false);
                }
            } else if (element == this.sortDownUsernameButton) {
                if (!this.sortDownUsernameButton.isToggled()) {
                    this.sortPlayers(3);
                    this.sortUpUsernameButton.setToggled(false);
                    this.sortDownUsernameButton.toggle(); 

                    this.sortDownStatusButton.setToggled(false);
                    this.sortUpStatusButton.setToggled(false);
                }
            } else if (element == this.sortUpUsernameButton) {
                if (!this.sortUpUsernameButton.isToggled()) {
                    this.sortPlayers(2);
                    this.sortDownUsernameButton.setToggled(false);
                    this.sortUpUsernameButton.toggle();

                    this.sortDownStatusButton.setToggled(false);
                    this.sortUpStatusButton.setToggled(false);
                }
            } else if (element == this.addFriendButton)
                this.addFriendCallback.open();
            else if (element instanceof IndexedGUIDropDownElement) {
                IndexedGUIDropDownElement<EnumActivityStatus> profileButton = (IndexedGUIDropDownElement) element;
                if (profileButton.index != this.clientStatus) {
                    OxygenManagerClient.instance().changeActivityStatusSynced(profileButton.index);
                    this.clientStatus = profileButton.index;
                    this.statusImageLabel.setTextureUV(this.clientStatus.ordinal() * 3, 0);
                }
            }
        }
        if (element instanceof FriendListEntryGUIButton) {
            FriendListEntryGUIButton entry = (FriendListEntryGUIButton) element;
            if (entry != this.currentEntry)
                this.currentEntry = entry;
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.searchField.isEnabled() && !this.searchField.isHovered()) {
            this.searchButton.enableFull();
            this.searchField.disableFull();
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);              
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {   
        if (keyCode == FriendsListKeyHandler.FRIENDS_LIST.getKeyCode() && !this.searchField.isDragged() && !this.hasCurrentCallback())
            this.screen.close();
        return super.keyTyped(typedChar, keyCode); 
    }

    public FriendListEntryGUIButton getCurrentEntry() {
        return this.currentEntry;
    }

    public void openRemoveCallback() {
        this.removeCallback.open();
    }

    public void openIgnoreFriendCallback() {
        this.ignoreCallback.open();
    }

    public void openEditNoteCallback() {
        this.editNoteCallback.open();
    }

    public void lockAddButton() {
        if (FriendsListManagerClient.instance().getClientPlayerData().getFriendsAmount() >= FriendsListConfig.MAX_FRIENDS.getIntValue())
            this.addFriendButton.disable();
    }

    public void unlockAddButton() {
        if (FriendsListManagerClient.instance().getClientPlayerData().getFriendsAmount() < FriendsListConfig.MAX_FRIENDS.getIntValue())
            this.addFriendButton.enable();
    }
}
