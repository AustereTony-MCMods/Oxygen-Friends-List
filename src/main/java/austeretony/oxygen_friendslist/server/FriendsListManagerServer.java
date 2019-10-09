package austeretony.oxygen_friendslist.server;

import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import net.minecraft.entity.player.EntityPlayerMP;

public class FriendsListManagerServer {

    private static FriendsListManagerServer instance;

    private final FriendsListPlayerDataContainer dataContainer = new FriendsListPlayerDataContainer();

    private final FriendsListPlayerDataManager dataManager;

    private FriendsListManagerServer() {
        this.dataManager = new FriendsListPlayerDataManager(this);
    }

    private void scheduleRepeatableProcesses() {
        OxygenManagerServer.instance().getExecutionManager().getExecutors().getSchedulerExecutorService().scheduleAtFixedRate(
                ()->this.dataContainer.saveData(), 
                FriendsListConfig.LIST_SAVE_DELAY_MINUTES.getIntValue(), 
                FriendsListConfig.LIST_SAVE_DELAY_MINUTES.getIntValue(), 
                TimeUnit.MINUTES);
    }

    public static void create() {
        if (instance == null) {
            instance = new FriendsListManagerServer();
            instance.scheduleRepeatableProcesses();
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
