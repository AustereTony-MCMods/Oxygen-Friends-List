package austeretony.oxygen_friendslist.server.event;

import austeretony.oxygen_core.server.api.event.OxygenPlayerLoadedEvent;
import austeretony.oxygen_core.server.api.event.OxygenPrivilegesLoadedEvent;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import austeretony.oxygen_friendslist.server.FriendsListManagerServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FriendsListEventsServer {

    @SubscribeEvent
    public void onPrivilegesLoaded(OxygenPrivilegesLoadedEvent event) {        
        FriendsListMain.addDefaultPrivileges();
    }

    @SubscribeEvent
    public void onPlayerLoaded(OxygenPlayerLoadedEvent event) {        
        FriendsListManagerServer.instance().playerLoaded(event.playerMP);
    }
}
