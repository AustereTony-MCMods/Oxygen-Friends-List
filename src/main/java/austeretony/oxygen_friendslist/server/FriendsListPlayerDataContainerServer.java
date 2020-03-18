package austeretony.oxygen_friendslist.server;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import austeretony.oxygen_core.server.api.OxygenHelperServer;

public class FriendsListPlayerDataContainerServer {

    private final Map<UUID, FriendsListPlayerDataServer> players = new ConcurrentHashMap<>();

    public FriendsListPlayerDataServer createPlayerData(UUID playerUUID) {        
        FriendsListPlayerDataServer playerData = new FriendsListPlayerDataServer(playerUUID);
        this.players.put(playerUUID, playerData);
        return playerData;
    }

    public void removePlayerData(UUID playerUUID) {
        this.players.remove(playerUUID);
    }

    @Nullable
    public FriendsListPlayerDataServer getPlayerData(UUID playerUUID) {
        return this.players.get(playerUUID);
    }

    void save() {
        OxygenHelperServer.addRoutineTask(()->{
            for (FriendsListPlayerDataServer playerData : this.players.values()) {
                if (playerData.isChanged()) {
                    playerData.setChanged(false);
                    OxygenHelperServer.savePersistentDataAsync(playerData);
                }
            }   
        });
    }

    public void playerLoaded(UUID playerUUID) {
        if (this.getPlayerData(playerUUID) == null) {
            FriendsListPlayerDataServer playerData = this.createPlayerData(playerUUID);
            OxygenHelperServer.loadPersistentDataAsync(playerData);
        }
    }
}
