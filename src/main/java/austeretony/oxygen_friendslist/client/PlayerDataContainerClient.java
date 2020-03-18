package austeretony.oxygen_friendslist.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.api.PrivilegesProviderClient;
import austeretony.oxygen_core.common.persistent.AbstractPersistentData;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_friendslist.common.ListEntry;
import austeretony.oxygen_friendslist.common.ListEntry.EnumEntryType;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListPrivilege;

public class PlayerDataContainerClient extends AbstractPersistentData {

    private final Map<Long, ListEntry> entries = new ConcurrentHashMap<>(10);

    public Set<Long> getListEntriesIds() {
        return this.entries.keySet();
    }

    public Collection<ListEntry> getListEntries() {
        return this.entries.values();
    }

    public int getFriendsAmount() {
        int amount = 0;
        for (ListEntry entry : this.entries.values())
            if (entry.getType() == EnumEntryType.FRIEND)
                amount++;
        return amount;
    }

    public List<ListEntry> getFriends() {
        return this.entries.values()
                .stream()
                .filter((entry)->entry.getType() == EnumEntryType.FRIEND)
                .collect(Collectors.toList());
    }

    public int getIgnoredAmount() {
        int amount = 0;
        for (ListEntry entry : this.entries.values())
            if (entry.getType() == EnumEntryType.IGNORED)
                amount++;
        return amount;
    }

    public List<ListEntry> getIgnored() {
        return this.entries.values()
                .stream()
                .filter((entry)->entry.getType() == EnumEntryType.IGNORED)
                .collect(Collectors.toList());
    }

    public boolean canAddFriend() {
        return this.getFriendsAmount() < PrivilegesProviderClient.getAsInt(EnumFriendsListPrivilege.MAX_FRIENDS_AMOUNT.id(), FriendsListConfig.MAX_FRIENDS_AMOUNT.asInt());
    }

    public boolean canAddIgnored() {
        return this.getIgnoredAmount() < PrivilegesProviderClient.getAsInt(EnumFriendsListPrivilege.MAX_IGNORED_AMOUNT.id(), FriendsListConfig.MAX_IGNORED_AMOUNT.asInt());
    }

    public boolean haveEntryForUUID(UUID playerUUID) {
        for (ListEntry entry : this.entries.values())
            if (entry.getPlayerUUID().equals(playerUUID))
                return true;
        return false;
    }

    @Nullable
    public ListEntry getListEntry(long entryId) {
        return this.entries.get(entryId);
    }

    @Nullable
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
        return "friendslist:player_data_client";
    }

    @Override
    public String getPath() {
        return OxygenHelperClient.getDataFolder() + "/client/players/" + OxygenHelperClient.getPlayerUUID() + "/friendslist/player_data.dat";
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
