package austeretony.oxygen_friendslist.client;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUIScreen;

public class FriendsListMenuManager {

    public void sharedDataSynchronized() {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((FriendsListGUIScreen) ClientReference.getCurrentScreen()).sharedDataSynchronized();
        });

    }

    public void listSynchronized() {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((FriendsListGUIScreen) ClientReference.getCurrentScreen()).listSynchronized();;
        });
    }

    public void entryAdded() {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((FriendsListGUIScreen) ClientReference.getCurrentScreen()).entryAdded();;
        });    
    }

    public void entryRemoved() {
        ClientReference.delegateToClientThread(()->{
            if (isMenuOpened())
                ((FriendsListGUIScreen) ClientReference.getCurrentScreen()).entryRemoved();;
        }); 
    }

    public static boolean isMenuOpened() {
        return ClientReference.hasActiveGUI() && ClientReference.getCurrentScreen() instanceof FriendsListGUIScreen;
    }
}
