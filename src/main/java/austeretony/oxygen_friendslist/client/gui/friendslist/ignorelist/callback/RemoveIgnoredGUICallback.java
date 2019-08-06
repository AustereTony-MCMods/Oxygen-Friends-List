package austeretony.oxygen_friendslist.client.gui.friendslist.ignorelist.callback;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.text.GUITextBoxLabel;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUIScreen;
import austeretony.oxygen_friendslist.client.gui.friendslist.IgnoreListGUISection;
import austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback.RemoveFriendCallbackGUIFiller;

public class RemoveIgnoredGUICallback extends AbstractGUICallback {

    private final FriendsListGUIScreen screen;

    private final IgnoreListGUISection section;

    private GUITextBoxLabel requestLabel;

    private GUIButton confirmButton, cancelButton;

    public RemoveIgnoredGUICallback(FriendsListGUIScreen screen, IgnoreListGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section; 
    }

    @Override
    public void init() {
        this.addElement(new RemoveFriendCallbackGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new GUITextLabel(2, 2).setDisplayText(ClientReference.localize("oxygen_friendslist.gui.ignored.removeIgnoredCallback"), true, GUISettings.instance().getTitleScale()));
        this.addElement(this.requestLabel = new GUITextBoxLabel(2, 16, 160, 20));     

        this.addElement(this.confirmButton = new GUIButton(15, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.confirmButton"), true, GUISettings.instance().getButtonTextScale()));
        this.addElement(this.cancelButton = new GUIButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.cancelButton"), true, GUISettings.instance().getButtonTextScale()));
    }

    @Override
    protected void onOpen() {
        this.requestLabel.setDisplayText(ClientReference.localize("oxygen_friendslist.gui.ignored.removeIgnoredCallback.request", this.section.getCurrentEntry().getDisplayText()), false, GUISettings.instance().getTextScale());
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (element == this.cancelButton)
            this.close();
        else if (element == this.confirmButton) {
            FriendsListManagerClient.instance().removeIgnoredSynced(this.section.getCurrentEntry().listEntry.playerUUID);
            this.section.sortPlayers(0);
            this.section.unlockAddButton();
            this.close();            
        }
    }
}
