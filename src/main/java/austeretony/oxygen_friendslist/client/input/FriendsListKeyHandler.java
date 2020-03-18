package austeretony.oxygen_friendslist.client.input;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_friendslist.client.FriendsListMenuManager;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class FriendsListKeyHandler {

    private KeyBinding friendsListKeybinding;

    public FriendsListKeyHandler() {        
        if (FriendsListConfig.ENABLE_FRIENDSLIST_KEY.asBoolean() && !OxygenGUIHelper.isOxygenMenuEnabled())
            ClientReference.registerKeyBinding(this.friendsListKeybinding = new KeyBinding("key.oxygen_friendslist.friendsList", FriendsListConfig.FRIENDSLIST_KEY.asInt(), "Oxygen"));
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {        
        if (this.friendsListKeybinding != null && this.friendsListKeybinding.isPressed())
            FriendsListMenuManager.openFriendsListMenu();
    }

    public KeyBinding getFriendsListKeybinding() {
        return this.friendsListKeybinding;
    }
}
