package austeretony.oxygen_friendslist.client.gui.friendslist.friendslist.callback;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.gui.elements.OxygenCallbackGUIFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenCheckBoxGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIText;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUISection;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class SettingsGUICallback extends AbstractGUICallback {

    private final AbstractGUIScreen screen;

    private final FriendsListGUISection section;

    private OxygenCheckBoxGUIButton autoAcceptButton;

    private OxygenGUIButton closeButton;

    public SettingsGUICallback(AbstractGUIScreen screen, FriendsListGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.addElement(new OxygenCallbackGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, ClientReference.localize("oxygen.gui.callback.settings"), GUISettings.get().getTextScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(this.autoAcceptButton = new OxygenCheckBoxGUIButton(6, 18));
        this.addElement(new OxygenGUIText(16, 19, ClientReference.localize("oxygen_friendslist.gui.friends.autoAccept"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColorDark()));

        this.addElement(this.closeButton = new OxygenGUIButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen.gui.closeButton"))); 
    }

    @Override
    public void onOpen() {
        this.autoAcceptButton.setToggled(OxygenHelperClient.getClientSettingBoolean(FriendsListMain.FRIEND_REQUESTS_AUTO_ACCEPT_SETTING_ID));
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.closeButton)
                this.close();
            if (element == this.autoAcceptButton)
                OxygenHelperClient.setClientSetting(FriendsListMain.FRIEND_REQUESTS_AUTO_ACCEPT_SETTING_ID, this.autoAcceptButton.isToggled());
        }
    }
}
