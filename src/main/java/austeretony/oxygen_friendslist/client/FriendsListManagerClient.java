package austeretony.oxygen_friendslist.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_friendslist.client.input.FriendsListKeyHandler;

public class FriendsListManagerClient {

    private static FriendsListManagerClient instance;

    private final PlayerDataContainerClient dataContainer = new PlayerDataContainerClient();

    private final PlayerDataManagerClient dataManager;

    private final FriendsListMenuManager menuManager = new FriendsListMenuManager();

    private final FriendsListKeyHandler keyHandler = new FriendsListKeyHandler();

    private FriendsListManagerClient() {
        this.dataManager = new PlayerDataManagerClient(this);
        CommonReference.registerEvent(this.keyHandler);

    }

    private void registerPersistentData() {
        OxygenHelperClient.registerPersistentData(this.dataContainer);
    }

    public static void create() {
        if (instance == null) {
            instance = new FriendsListManagerClient();
            instance.registerPersistentData();
        }
    }

    public static FriendsListManagerClient instance() {
        return instance;
    }

    public PlayerDataContainerClient getPlayerDataContainer() {
        return this.dataContainer;
    }

    public PlayerDataManagerClient getPlayerDataManager() {
        return this.dataManager;
    }

    public FriendsListMenuManager getFriendsListMenuManager() {
        return this.menuManager;
    }

    public FriendsListKeyHandler getKeyHandler() {
        return this.keyHandler;
    }

    public void worldLoaded() {
        OxygenHelperClient.loadPersistentDataAsync(this.dataContainer);
    }
}
