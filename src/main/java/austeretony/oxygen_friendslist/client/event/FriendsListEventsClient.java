package austeretony.oxygen_friendslist.client.event;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.api.event.OxygenClientInitEvent;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.common.ListEntry;
import austeretony.oxygen_friendslist.common.ListEntry.EnumEntryType;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FriendsListEventsClient {

    @SubscribeEvent     
    public void onClientInit(OxygenClientInitEvent event) {
        FriendsListManagerClient.instance().worldLoaded();
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (event.getType() == ChatType.CHAT) {
            String msg = event.getMessage().getUnformattedText();
            try {
                for (ListEntry entry : FriendsListManagerClient.instance().getPlayerDataContainer().getListEntries())
                    if (entry.getType() == EnumEntryType.IGNORED && msg.indexOf(OxygenHelperClient.getPlayerSharedData(entry.getPlayerUUID()).getUsername()) != - 1)
                        event.setCanceled(true);
            } catch (Exception exception) {
                OxygenMain.LOGGER.error("[Friends List] Chat message processing failure! Message: {}.", msg);
            }
        }
    }
}
