package austeretony.oxygen_friendslist.client.gui.friendslist.friendlist.callback;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.image.GUIImageLabel;
import austeretony.alternateui.screen.text.GUITextField;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUIScreen;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUISection;
import austeretony.oxygen_friendslist.common.main.FriendListEntry;

public class EditNoteGUICallback extends AbstractGUICallback {

    private final FriendsListGUIScreen screen;

    private final FriendsListGUISection section;

    private GUITextField noteField;

    private GUIButton confirmButton, cancelButton;

    public EditNoteGUICallback(FriendsListGUIScreen screen, FriendsListGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section; 
    }

    @Override
    public void init() {
        this.addElement(new GUIImageLabel(- 1, - 1, this.getWidth() + 2, this.getHeight() + 2).enableStaticBackground(GUISettings.instance().getBaseGUIBackgroundColor()));//main background 1st layer
        this.addElement(new GUIImageLabel(0, 0, this.getWidth(), 11).enableStaticBackground(GUISettings.instance().getAdditionalGUIBackgroundColor()));//main background 2nd layer
        this.addElement(new GUIImageLabel(0, 12, this.getWidth(), this.getHeight() - 12).enableStaticBackground(GUISettings.instance().getAdditionalGUIBackgroundColor()));//main background 2nd layer
        this.addElement(new GUITextLabel(2, 2).setDisplayText(ClientReference.localize("oxygen.gui.editNoteCallback"), true, GUISettings.instance().getTitleScale()));   
        this.addElement(new GUITextLabel(2, 16).setDisplayText(ClientReference.localize("oxygen.gui.note"), false, GUISettings.instance().getSubTextScale()));  
        this.addElement(this.noteField = new GUITextField(2, 25, 187, FriendListEntry.MAX_NOTE_LENGTH).setScale(0.7F).enableDynamicBackground().cancelDraggedElementLogic());       

        this.addElement(this.confirmButton = new GUIButton(15, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.confirmButton"), true, GUISettings.instance().getButtonTextScale()));
        this.addElement(this.cancelButton = new GUIButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.cancelButton"), true, GUISettings.instance().getButtonTextScale()));

        this.confirmButton.disable();
    }

    @Override
    protected void onOpen() {
        this.noteField.setText(this.section.getCurrentEntry().listEntry.getNote());
    }

    @Override
    protected void onClose() {
        this.noteField.reset();
        this.confirmButton.disable();
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        boolean flag = super.keyTyped(typedChar, keyCode);   
        if (this.noteField.isDragged()) {
            if (!this.noteField.getTypedText().isEmpty())
                this.confirmButton.enable();
            else
                this.confirmButton.disable();
        }
        return flag;   
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (element == this.cancelButton)
            this.close();
        else if (element == this.confirmButton) {
            FriendsListManagerClient.instance().editFriendListEntryNoteSynced(this.section.getCurrentEntry().playerUUID, this.noteField.getTypedText());
            this.section.sortPlayers(0);
            this.close();
        }
    }
}
