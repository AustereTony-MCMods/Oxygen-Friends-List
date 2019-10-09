package austeretony.oxygen_friendslist.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen_core.common.persistent.AbstractPersistentData;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_core.server.api.PrivilegeProviderServer;
import austeretony.oxygen_friendslist.common.ListEntry;
import austeretony.oxygen_friendslist.common.ListEntry.EnumEntryType;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListPrivilege;

public class FriendsListPlayerData extends AbstractPersistentData {

    private UUID playerUUID;

    private final Map<Long, ListEntry> entries = new ConcurrentHashMap<>(10);

    public String dataPath;

    public FriendsListPlayerData(UUID playerUUID, String dataPath) {
        this.playerUUID = playerUUID;
        this.dataPath = dataPath;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public Set<Long> getListEntriesIds() {
        return this.entries.keySet();
    }

    public Collection<ListEntry> getListEntries() {
        return this.entries.values();
    }

    public int getFriendsAmount() {
        return (int) this.entries.values()
                .stream()
                .filter((e)->e.getType() == EnumEntryType.FRIEND)
                .count();
    }

    public int getIgnoredAmount() {
        return (int) this.entries.values()
                .stream()
                .filter((e)->e.getType() == EnumEntryType.IGNORED)
                .count();   
    }

    public boolean canAddFriend() {
        return this.getFriendsAmount() < PrivilegeProviderServer.getValue(this.playerUUID, EnumFriendsListPrivilege.MAX_FRIENDS_AMOUNT.toString(), FriendsListConfig.MAX_FRIENDS_AMOUNT.getIntValue());
    }

    public boolean canAddIgnored() {
        return this.getIgnoredAmount() < PrivilegeProviderServer.getValue(this.playerUUID, EnumFriendsListPrivilege.MAX_IGNORED_AMOUNT.toString(), FriendsListConfig.MAX_IGNORED_AMOUNT.getIntValue());
    }

    public boolean haveEntryForUUID(UUID playerUUID) {
        for (ListEntry entry : this.entries.values())
            if (entry.getPlayerUUID().equals(playerUUID))
                return true;
        return false;
    }

    public ListEntry getListEntry(long entryId) {
        return this.entries.get(entryId);
    }

    public ListEntry getListEntryByUUID(UUID playerUUID) {
        for (ListEntry entry : this.entries.values())
            if (entry.getPlayerUUID().equals(playerUUID))
                return entry;
        return null;
    }

    public void addListEntry(ListEntry entry) {
        this.entries.put(entry.getId(), entry);
    }

    public void removeListEntry(long id) {
        this.entries.remove(id);
    } 

    public void removeListEntry(UUID playerUUID) {
        Iterator<ListEntry> iterator = this.entries.values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getPlayerUUID().equals(playerUUID))
                iterator.remove();
        }
    } 

    public boolean isIgnored(UUID playerUUID) {
        ListEntry entry = this.getListEntryByUUID(playerUUID);
        if (entry != null)
            return entry.getType() == EnumEntryType.IGNORED;
        return false;
    }

    @Override
    public String getDisplayName() {
        return "friendslist_player_data";
    }

    @Override
    public String getPath() {
        return this.dataPath;
    }

    @Override
    public long getSaveDelayMinutes() {
        return FriendsListConfig.LIST_SAVE_DELAY_MINUTES.getIntValue();
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write((short) this.entries.size(), bos);
        for (ListEntry listEntry : this.entries.values()) 
            listEntry.write(bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        int amount = StreamUtils.readShort(bis);
        ListEntry entry;
        for (int i = 0; i < amount; i++) {
            entry = new ListEntry();
            entry.read(bis);
            this.addListEntry(entry);
        }
    }

    @Override
    public void reset() {
        this.entries.clear();
    }
}
