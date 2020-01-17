package austeretony.oxygen_friendslist.server;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen_core.server.api.OxygenHelperServer;

public class FriendsListPlayerDataContainer {

    private final Map<UUID, FriendsListPlayerData> playersData = new ConcurrentHashMap<>();

    public Collection<FriendsListPlayerData> getPlayersData() {
        return this.playersData.values();
    }

    public FriendsListPlayerData createPlayerData(UUID playerUUID) {
        FriendsListPlayerData data = new FriendsListPlayerData(
                playerUUID, 
                OxygenHelperServer.getDataFolder() + "/server/players/" + playerUUID + "/friendslist/player_data.dat");
        this.playersData.put(playerUUID, data);
        return data;
    }

    public void removePlayerData(UUID playerUUID) {
        this.playersData.remove(playerUUID);
    }

    public FriendsListPlayerData getPlayerData(UUID playerUUID) {
        return this.playersData.get(playerUUID);
    }

    public void save() {
        OxygenHelperServer.addRoutineTask(()->{
            for (FriendsListPlayerData data : this.playersData.values()) {
                if (data.isChanged()) {
                    data.setChanged(false);
                    OxygenHelperServer.savePersistentDataAsync(data);
                }
            }   
        });
    }

    public void onPlayerLoaded(UUID playerUUID) {
        if (this.getPlayerData(playerUUID) == null) {
            FriendsListPlayerData data = this.createPlayerData(playerUUID);
            OxygenHelperServer.loadPersistentDataAsync(data);
        }
    }

    /*public void onPlayerUnloaded(UUID playerUUID) {
        if (this.playerDataExist(playerUUID))
            this.removePlayerData(playerUUID);
    }*/
}
