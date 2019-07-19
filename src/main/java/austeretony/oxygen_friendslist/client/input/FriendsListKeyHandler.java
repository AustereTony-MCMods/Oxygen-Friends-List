package austeretony.oxygen_friendslist.client.input;

import org.lwjgl.input.Keyboard;

import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class FriendsListKeyHandler {

    public static final KeyBinding FRIENDS_LIST_MENU = new KeyBinding("key.oxygen_friendslist.openFriendsList", Keyboard.KEY_O, OxygenMain.NAME);

    public FriendsListKeyHandler() {
        ClientReference.registerKeyBinding(FRIENDS_LIST_MENU);
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {        
        if (FRIENDS_LIST_MENU.isPressed())
            FriendsListManagerClient.instance().openFriendsListSynced();
    }
}
