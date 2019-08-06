package austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.button.GUICheckBoxButton;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUISection;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class SettingsGUICallback extends AbstractGUICallback {

    private final AbstractGUIScreen screen;

    private final FriendsListGUISection section;

    private GUICheckBoxButton hideOverlayButton;

    private GUIButton closeButton;

    public SettingsGUICallback(AbstractGUIScreen screen, FriendsListGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.addElement(new SettingsCallbackGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new GUITextLabel(2, 2).setDisplayText(ClientReference.localize("oxygen.gui.callback.settings"), true, GUISettings.instance().getTitleScale()));

        this.addElement(this.hideOverlayButton = new GUICheckBoxButton(2, 16, 6).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor()));
        this.addElement(new GUITextLabel(10, 15).setDisplayText(ClientReference.localize("oxygen_friendslist.gui.friends.autoAccept"), false, GUISettings.instance().getSubTextScale()));

        this.addElement(this.closeButton = new GUIButton(this.getWidth() - 42, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.closeButton"), true, GUISettings.instance().getButtonTextScale()));
    }

    @Override
    public void onOpen() {
        this.hideOverlayButton.setToggled(OxygenHelperClient.getClientSettingBoolean(FriendsListMain.FRIEND_REQUESTS_AUTO_ACCEPT_SETTING_ID));
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.closeButton)
                this.close();
            if (element == this.hideOverlayButton) {
                if (this.hideOverlayButton.isToggled())
                    OxygenHelperClient.setClientSetting(FriendsListMain.FRIEND_REQUESTS_AUTO_ACCEPT_SETTING_ID, true);
                else
                    OxygenHelperClient.setClientSetting(FriendsListMain.FRIEND_REQUESTS_AUTO_ACCEPT_SETTING_ID, false);
                OxygenHelperClient.saveClientSettings();
            }
        }
    }
}
