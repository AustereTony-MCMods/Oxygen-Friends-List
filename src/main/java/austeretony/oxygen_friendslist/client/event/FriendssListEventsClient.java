package austeretony.oxygen_friendslist.client.event;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.api.event.OxygenChatMessageEvent;
import austeretony.oxygen.client.api.event.OxygenClientInitEvent;
import austeretony.oxygen.client.api.event.OxygenNotificationRecievedEvent;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListChatMessages;
import austeretony.oxygen_friendslist.common.main.FriendListEntry;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FriendssListEventsClient {

    @SubscribeEvent     
    public void onClientInit(OxygenClientInitEvent event) {
        FriendsListManagerClient.instance().init();
    }

    @SubscribeEvent
    public void onChatMessage(OxygenChatMessageEvent event) {
        if (event.modIndex == FriendsListMain.FRIENDS_LIST_MOD_INDEX)
            EnumFriendsListChatMessages.values()[event.messageIndex].show(event.args);
    }

    @SubscribeEvent
    public void onNotificationRecieved(OxygenNotificationRecievedEvent event) {
        if (event.notification.getIndex() == FriendsListMain.FRIEND_REQUEST_ID 
                && OxygenHelperClient.getClientSettingBoolean(FriendsListMain.FRIEND_REQUESTS_AUTO_ACCEPT_SETTING))
            event.notification.accepted(null);
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (event.getType() == ChatType.CHAT) {
            String msg = event.getMessage().getUnformattedText();
            try {
                for (FriendListEntry entry : FriendsListManagerClient.instance().getClientPlayerData().getFriendListEntries())
                    if (entry.ignored && msg.indexOf(OxygenHelperClient.getSharedPlayerData(entry.playerUUID).getUsername()) != - 1)
                        event.setCanceled(true);
            } catch (Exception exception) {
                OxygenMain.OXYGEN_LOGGER.error("Chat message processing failure! Message: {}.", msg);
            }
        }
    }
}
