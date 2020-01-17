package austeretony.oxygen_friendslist.client.input;

import org.lwjgl.input.Keyboard;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListScreen;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class FriendsListKeyHandler {

    private KeyBinding friendsListKeybinding;

    public FriendsListKeyHandler() {        
        if (FriendsListConfig.ENABLE_FRIENDSLIST_KEY.asBoolean() && !OxygenGUIHelper.isOxygenMenuEnabled())
            ClientReference.registerKeyBinding(this.friendsListKeybinding = new KeyBinding("key.oxygen_friendslist.friendsList", Keyboard.KEY_O, "Oxygen"));
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {        
        if (this.friendsListKeybinding != null && this.friendsListKeybinding.isPressed())
            ClientReference.displayGuiScreen(new FriendsListScreen());
    }

    public KeyBinding getFriendsListKeybinding() {
        return this.friendsListKeybinding;
    }
}
