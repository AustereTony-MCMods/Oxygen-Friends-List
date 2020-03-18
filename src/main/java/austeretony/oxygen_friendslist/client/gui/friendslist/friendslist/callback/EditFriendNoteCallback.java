package austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback;

import org.lwjgl.input.Keyboard;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.elements.OxygenCallbackBackgroundFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenKeyButton;
import austeretony.oxygen_core.client.gui.elements.OxygenTextField;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListScreen;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListSection;
import austeretony.oxygen_friendslist.common.ListEntry;

public class EditFriendNoteCallback extends AbstractGUICallback {

    private final FriendsListScreen screen;

    private final FriendsListSection section;

    private OxygenTextField noteField;

    private OxygenKeyButton confirmButton, cancelButton;

    public EditFriendNoteCallback(FriendsListScreen screen, FriendsListSection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section; 
    }

    @Override
    public void init() {
        this.enableDefaultBackground(EnumBaseGUISetting.FILL_CALLBACK_COLOR.get().asInt());
        this.addElement(new OxygenCallbackBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_friendslist.gui.friendslist.callback.editNote"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));
        this.addElement(new OxygenTextLabel(6, 23, ClientReference.localize("oxygen_core.gui.note"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.noteField = new OxygenTextField(6, 25, this.getWidth() - 12, ListEntry.MAX_NOTE_LENGTH, ""));

        this.addElement(this.confirmButton = new OxygenKeyButton(15, this.getHeight() - 10, ClientReference.localize("oxygen_core.gui.confirm"), Keyboard.KEY_R, ()->this.confirm(false)));
        this.addElement(this.cancelButton = new OxygenKeyButton(this.getWidth() - 55, this.getHeight() - 10, ClientReference.localize("oxygen_core.gui.cancel"), Keyboard.KEY_X, ()->this.close(false)));
    }

    @Override
    protected void onOpen() {
        this.noteField.setText(this.section.getCurrentListEntry().getNote());
    }

    private void confirm(boolean mouseClick) {
        if (mouseClick || !this.noteField.isDragged()) {
            FriendsListManagerClient.instance().getPlayerDataManager().editListEntryNoteSynced(
                    this.section.getCurrentListEntry().getPlayerUUID(), this.noteField.getTypedText());
            this.close();
        }       
    }

    public void close(boolean mouseClick) {
        if (mouseClick || !this.noteField.isDragged())
            this.close();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) { 
            if (element == this.cancelButton)
                this.close(true);
            else if (element == this.confirmButton)
                this.confirm(true);
        }
    }
}
