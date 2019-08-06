package austeretony.oxygen_friendslist.client;

import java.util.Set;

import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.sync.gui.api.IAdvancedGUIHandlerClient;
import austeretony.oxygen.common.api.network.OxygenNetwork;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUIScreen;
import austeretony.oxygen_friendslist.common.main.FriendListEntry;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import net.minecraft.network.PacketBuffer;

public class FriendsListMenuHandlerClient implements IAdvancedGUIHandlerClient<FriendListEntry> {

    @Override
    public void open() {
        ClientReference.displayGuiScreen(new FriendsListGUIScreen());
    }

    @Override
    public OxygenNetwork getNetwork() {
        return FriendsListMain.network();
    }

    @Override
    public Set<Long> getIdentifiers() {
        return FriendsListManagerClient.instance().getClientPlayerData().getFriendListEntriesIds();
    }

    @Override
    public FriendListEntry getEntry(long entryId) {
        return FriendsListManagerClient.instance().getClientPlayerData().getFriendListEntry(entryId);
    }

    @Override
    public void clearData() {
        FriendsListManagerClient.instance().getClientPlayerData().clearFriendListEntries();
    }

    @Override
    public void addValidEntry(FriendListEntry entry) {
        FriendsListManagerClient.instance().getClientPlayerData().addFriendListEntry(entry);
    }

    @Override
    public void readEntries(PacketBuffer buffer, int amount) {
        for (int i = 0; i < amount; i++)
            FriendsListManagerClient.instance().getClientPlayerData().addFriendListEntry(FriendListEntry.read(buffer));
    }
}
