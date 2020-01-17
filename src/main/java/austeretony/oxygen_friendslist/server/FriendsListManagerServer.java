package austeretony.oxygen_friendslist.server;

import austeretony.oxygen_core.server.api.OxygenHelperServer;
import net.minecraft.entity.player.EntityPlayerMP;

public class FriendsListManagerServer {

    private static FriendsListManagerServer instance;

    private final FriendsListPlayerDataContainer dataContainer = new FriendsListPlayerDataContainer();

    private final FriendsListPlayerDataManager dataManager;

    private FriendsListManagerServer() {
        this.dataManager = new FriendsListPlayerDataManager(this);
    }

    private void registerPersistentData() {
        OxygenHelperServer.registerPersistentData(()->this.dataContainer.save());
    }

    public static void create() {
        if (instance == null) {
            instance = new FriendsListManagerServer();
            instance.registerPersistentData();
        }
    }

    public static FriendsListManagerServer instance() {
        return instance;
    }

    public FriendsListPlayerDataContainer getPlayerDataContainer() {
        return this.dataContainer;
    }

    public FriendsListPlayerDataManager getPlayerDataManager() {
        return this.dataManager;
    }

    public void onPlayerLoaded(EntityPlayerMP playerMP) {
        this.dataManager.onPlayerLoaded(playerMP);
    }

    public void onPlayerUnloaded(EntityPlayerMP playerMP) {
        this.dataManager.onPlayerUnloaded(playerMP);
    }
}
