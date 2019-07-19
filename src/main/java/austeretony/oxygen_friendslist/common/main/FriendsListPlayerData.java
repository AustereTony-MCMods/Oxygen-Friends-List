package austeretony.oxygen_friendslist.common.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen.common.api.IPersistentData;
import austeretony.oxygen.util.StreamUtils;

public class FriendsListPlayerData implements IPersistentData {

    private UUID playerUUID;

    private final Map<Long, FriendListEntry> friendList = new ConcurrentHashMap<Long, FriendListEntry>(5);

    //cached in memory just for fast access
    private final Map<UUID, Long> friendListAccess = new ConcurrentHashMap<UUID, Long>(5);

    private int friendsAmount, ignoredAmount;

    public final String dataPath;

    public FriendsListPlayerData(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.dataPath = "players/" + this.playerUUID + "/friendslist/player_data.dat";
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public Set<Long> getFriendListEntriesIds() {
        return this.friendList.keySet();
    }

    public Collection<FriendListEntry> getFriendListEntries() {
        return this.friendList.values();
    }

    public void clearFriendListEntries() {
        this.friendList.clear();
        this.friendListAccess.clear();
        this.friendsAmount = this.ignoredAmount = 0;
    }

    public int getFriendsAmount() {
        return this.friendsAmount;
    }

    public int getIgnoredAmount() {
        return this.ignoredAmount;
    }

    public boolean haveFriendListEntryForUUID(UUID playerUUID) {
        return this.friendListAccess.containsKey(playerUUID);
    }

    public long getFriendListEntryIdByUUID(UUID playerUUID) {
        return this.friendListAccess.get(playerUUID);
    }

    public FriendListEntry getFriendListEntryByUUID(UUID playerUUID) {
        return this.friendList.get(this.friendListAccess.get(playerUUID));
    }

    public FriendListEntry getFriendListEntry(long id) {
        return this.friendList.get(id);
    }

    public void addFriendListEntry(FriendListEntry friend) {
        if (friend.ignored)
            this.ignoredAmount++;
        else
            this.friendsAmount++;
        this.friendList.put(friend.getId(), friend);
        this.friendListAccess.put(friend.playerUUID, friend.getId());
    }

    public void removeFriendListEntry(long id) {
        if (this.friendList.containsKey(id)) {
            FriendListEntry entry = this.friendList.remove(id);
            if (entry.ignored)
                this.ignoredAmount--;
            else
                this.friendsAmount--;
            this.friendListAccess.remove(entry.playerUUID);
        }
    } 

    public void removeFriendListEntry(UUID playerUUID) {
        if (this.friendListAccess.containsKey(playerUUID)) {
            FriendListEntry entry = this.friendList.remove(this.friendListAccess.remove(playerUUID));
            if (entry.ignored)
                this.ignoredAmount--;
            else
                this.friendsAmount--;
        }
    } 

    @Override
    public String getName() {
        return "player_data";
    }

    @Override
    public String getModId() {
        return FriendsListMain.MODID;
    }

    @Override
    public String getPath() {
        return this.dataPath;
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write((short) this.friendList.size(), bos);
        for (FriendListEntry listEntry : this.friendList.values()) 
            listEntry.write(bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        int amount = StreamUtils.readShort(bis);
        for (int i = 0; i < amount; i++)
            this.addFriendListEntry(FriendListEntry.read(bis));
    }

    public void reset() {
        this.clearFriendListEntries();
    }
}
