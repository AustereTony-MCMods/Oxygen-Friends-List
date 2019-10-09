package austeretony.oxygen_friendslist.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;

public class FriendsListManagerClient {

    private static FriendsListManagerClient instance;

    private final PlayerDataContainerClient dataContainer = new PlayerDataContainerClient();

    private final PlayerDataManagerClient dataManager;

    private final FriendsListMenuManager menuManager = new FriendsListMenuManager();

    private FriendsListManagerClient() {
        this.dataManager = new PlayerDataManagerClient(this);
        OxygenHelperClient.registerPersistentData(this.dataContainer);
    }

    public static void create() {
        if (instance == null)
            instance = new FriendsListManagerClient();
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

    public void init() {
        OxygenHelperClient.loadPersistentDataAsync(this.dataContainer);
    }
}
