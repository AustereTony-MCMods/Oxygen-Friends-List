package austeretony.oxygen_friendslist.client.input;

import org.lwjgl.input.Keyboard;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUIScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class FriendsListKeyHandler {

    public static final KeyBinding FRIENDS_LIST = new KeyBinding("key.oxygen_friendslist.openFriendsList", Keyboard.KEY_O, "Oxygen");

    public FriendsListKeyHandler() {
        ClientReference.registerKeyBinding(FRIENDS_LIST);
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {        
        if (FRIENDS_LIST.isPressed())
            ClientReference.displayGuiScreen(new FriendsListGUIScreen());
    }
}
