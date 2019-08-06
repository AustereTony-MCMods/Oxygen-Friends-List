package austeretony.oxygen_friendslist.common.event;

import austeretony.oxygen.common.api.event.OxygenPlayerLoadedEvent;
import austeretony.oxygen.common.api.event.OxygenPlayerUnloadedEvent;
import austeretony.oxygen.common.api.event.OxygenPrivilegesLoadedEvent;
import austeretony.oxygen_friendslist.common.FriendsListManagerServer;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FriendsListEventsServer {

    @SubscribeEvent
    public void onPrivilegesLoaded(OxygenPrivilegesLoadedEvent event) {        
        FriendsListMain.addDefaultPrivileges();
    }

    @SubscribeEvent
    public void onPlayerLoaded(OxygenPlayerLoadedEvent event) {        
        FriendsListManagerServer.instance().onPlayerLoaded(event.player);
    }

    @SubscribeEvent
    public void onPlayerUnloaded(OxygenPlayerUnloadedEvent event) {        
        FriendsListManagerServer.instance().onPlayerUnloaded(event.player);
    }
}
