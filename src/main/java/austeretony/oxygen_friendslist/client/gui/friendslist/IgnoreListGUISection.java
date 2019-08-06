package austeretony.oxygen_friendslist.client.gui.friendslist;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import austeretony.alternateui.screen.browsing.GUIScroller;
import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.button.GUISlider;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.contextmenu.GUIContextMenu;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.panel.GUIButtonPanel;
import austeretony.alternateui.screen.text.GUITextField;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.alternateui.util.EnumGUIOrientation;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen.util.MathUtils;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.FriendListGUIFiller;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.callback.AddIgnoredGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.callback.EditNoteGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.callback.RemoveIgnoredGUICallback;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.context.EditNoteContextAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.context.RemoveIgnoredContextAction;
import austeretony.oxygen_friendslist.client.input.FriendsListKeyHandler;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.main.FriendListEntry;

public class IgnoreListGUISection extends AbstractGUISection {

    private final FriendsListGUIScreen screen;

    private GUIButton friendsPageButton, searchButton, refreshButton, addIgnoredButton, 
    sortDownStatusButton, sortUpStatusButton, sortDownUsernameButton, sortUpUsernameButton;

    private FriendListEntryGUIButton currentEntry;

    private GUITextLabel ignoredAmountTextLabel;

    private GUITextField searchField;

    private GUIButtonPanel ignoredPanel;

    private AbstractGUICallback addIgnoredCallback, removeCallback, editNoteCallback;

    public IgnoreListGUISection(FriendsListGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() { 
        this.addElement(new FriendListGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new GUITextLabel(2, 4).setDisplayText(ClientReference.localize("oxygen_friendslist.gui.ignored.title"), false, GUISettings.instance().getTitleScale()));

        this.addElement(this.friendsPageButton = new GUIButton(this.getWidth() - 32, 0,  12, 12).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.FRIENDS_ICONS, 12, 12).initSimpleTooltip(ClientReference.localize("oxygen_friendslist.gui.friends.title"), GUISettings.instance().getTooltipScale())); 
        this.addElement(new GUIButton(this.getWidth() - 18, 0,  12, 12).setTexture(OxygenGUITextures.IGNORED_ICONS, 12, 12).initSimpleTooltip(ClientReference.localize("oxygen_friendslist.gui.ignored.title"), GUISettings.instance().getTooltipScale()).toggle()); 

        this.addElement(this.searchButton = new GUIButton(4, 15, 7, 7).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SEARCH_ICONS, 7, 7).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.search"), GUISettings.instance().getTooltipScale()));         
        this.addElement(this.refreshButton = new GUIButton(79, 14, 10, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.REFRESH_ICONS, 9, 9).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.refresh"), GUISettings.instance().getTooltipScale()));         

        this.addElement(this.sortDownStatusButton = new GUIButton(7, 29, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_DOWN_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortUpStatusButton = new GUIButton(7, 25, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_UP_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortDownUsernameButton = new GUIButton(19, 29, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_DOWN_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortUpUsernameButton = new GUIButton(19, 25, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_UP_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(new GUITextLabel(24, 25).setDisplayText(ClientReference.localize("oxygen.gui.username")).setTextScale(GUISettings.instance().getSubTextScale())); 
        this.addElement(new GUITextLabel(100, 25).setDisplayText(ClientReference.localize("oxygen.gui.dimension")).setTextScale(GUISettings.instance().getSubTextScale())); 

        this.ignoredPanel = new GUIButtonPanel(EnumGUIOrientation.VERTICAL, 0, 35, this.getWidth() - 3, 10).setButtonsOffset(1).setTextScale(GUISettings.instance().getPanelTextScale());
        this.addElement(this.ignoredPanel);
        this.addElement(this.searchField = new GUITextField(0, 14, 76, 9, 20).setTextScale(GUISettings.instance().getSubTextScale())
                .enableDynamicBackground(GUISettings.instance().getEnabledTextFieldColor(), GUISettings.instance().getDisabledTextFieldColor(), GUISettings.instance().getHoveredTextFieldColor())
                .setDisplayText("...").setLineOffset(3).disableFull().cancelDraggedElementLogic());
        this.ignoredPanel.initSearchField(this.searchField);
        GUIScroller scroller = new GUIScroller(MathUtils.clamp(FriendsListConfig.MAX_FRIENDS.getIntValue(), 14, 100), 14);
        this.ignoredPanel.initScroller(scroller);
        GUISlider slider = new GUISlider(this.getWidth() - 2, 35, 2, this.getHeight() - 49);
        slider.setDynamicBackgroundColor(GUISettings.instance().getEnabledSliderColor(), GUISettings.instance().getDisabledSliderColor(), GUISettings.instance().getHoveredSliderColor());
        scroller.initSlider(slider);    

        GUIContextMenu menu = new GUIContextMenu(GUISettings.instance().getContextMenuWidth(), 10).setScale(GUISettings.instance().getContextMenuScale()).setTextScale(GUISettings.instance().getTextScale()).setTextAlignment(EnumGUIAlignment.LEFT, 2);
        menu.setOpenSound(OxygenSoundEffects.CONTEXT_OPEN.soundEvent);
        menu.setCloseSound(OxygenSoundEffects.CONTEXT_CLOSE.soundEvent);
        this.ignoredPanel.initContextMenu(menu);
        menu.enableDynamicBackground(GUISettings.instance().getEnabledContextActionColor(), GUISettings.instance().getDisabledContextActionColor(), GUISettings.instance().getHoveredContextActionColor());
        menu.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
        menu.addElement(new RemoveIgnoredContextAction(this));
        menu.addElement(new EditNoteContextAction(this));

        this.addElement(this.addIgnoredButton = new GUIButton(4, this.getHeight() - 11,  40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(ClientReference.localize("oxygen.gui.addButton"), true, GUISettings.instance().getButtonTextScale()));     
        this.lockAddButton();
        this.addElement(this.ignoredAmountTextLabel = new GUITextLabel(0, this.getHeight() - 10).setTextScale(GUISettings.instance().getSubTextScale())); 

        this.addIgnoredCallback = new AddIgnoredGUICallback(this.screen, this, 140, 68).enableDefaultBackground();
        this.removeCallback = new RemoveIgnoredGUICallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.editNoteCallback = new EditNoteGUICallback(this.screen, this, 140, 50).enableDefaultBackground();
    }

    public void sortPlayers(int mode) {
        List<FriendListEntry> ignored = FriendsListManagerClient.instance().getClientPlayerData().getFriendListEntries()
                .stream()
                .filter(e->e.ignored)
                .collect(Collectors.toList());

        if (mode == 0)
            Collections.sort(ignored, (f1, f2)->FriendsListManagerClient.getActivityStatus(f1).ordinal() - FriendsListManagerClient.getActivityStatus(f2).ordinal());
        else if (mode == 1)
            Collections.sort(ignored, (f1, f2)->FriendsListManagerClient.getActivityStatus(f2).ordinal() - FriendsListManagerClient.getActivityStatus(f1).ordinal());
        else if (mode == 2)
            Collections.sort(ignored, (f1, f2)->FriendsListManagerClient.getUsername(f1).compareTo(FriendsListManagerClient.getUsername(f2)));
        else if (mode == 3)
            Collections.sort(ignored, (f1, f2)->FriendsListManagerClient.getUsername(f2).compareTo(FriendsListManagerClient.getUsername(f1)));

        this.ignoredPanel.reset();
        FriendListEntryGUIButton button;
        int onlinePlayers = 0;
        OxygenPlayerData.EnumActivityStatus status;
        for (FriendListEntry playerList : ignored) { 
            status = OxygenPlayerData.EnumActivityStatus.OFFLINE;
            if (OxygenHelperClient.isOnline(playerList.playerUUID)) {
                status = OxygenHelperClient.getPlayerStatus(playerList.playerUUID);
                if (status == OxygenPlayerData.EnumActivityStatus.ONLINE)
                    onlinePlayers++;
            }
            button = new FriendListEntryGUIButton(playerList, status);
            button.enableDynamicBackground(GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getDisabledElementColor(), GUISettings.instance().getHoveredElementColor());
            button.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
            this.ignoredPanel.addButton(button);
        }

        this.ignoredPanel.getScroller().resetPosition();
        this.ignoredPanel.getScroller().getSlider().reset();

        this.searchField.reset();

        this.ignoredAmountTextLabel.setDisplayText(ignored.size() + " / " + FriendsListConfig.MAX_FRIENDS.getIntValue());
        this.ignoredAmountTextLabel.setX(this.getWidth() - 4 - this.textWidth(this.ignoredAmountTextLabel.getDisplayText(), GUISettings.instance().getSubTextScale()));

        this.sortUpStatusButton.toggle();
        this.sortDownStatusButton.setToggled(false);
        this.sortDownUsernameButton.setToggled(false);
        this.sortUpUsernameButton.setToggled(false);
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.friendsPageButton)
                this.screen.getFriendListSection().open();
            else if (element == this.searchButton) {
                this.searchField.enableFull();
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
                    this.sortPlayers(2);
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
            } else if (element == this.addIgnoredButton)
                this.addIgnoredCallback.open();
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

    public void openEditNoteCallback() {
        this.editNoteCallback.open();
    }

    public void lockAddButton() {
        if (FriendsListManagerClient.instance().getClientPlayerData().getIgnoredAmount() >= FriendsListConfig.MAX_IGNORED.getIntValue())
            this.addIgnoredButton.disable();
    }

    public void unlockAddButton() {
        if (FriendsListManagerClient.instance().getClientPlayerData().getIgnoredAmount() < FriendsListConfig.MAX_IGNORED.getIntValue())
            this.addIgnoredButton.enable();
    }
}
