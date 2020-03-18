package austeretony.oxygen_friendslist.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_friendslist.common.main.EnumFriendsListStatusMessage;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import net.minecraft.entity.player.EntityPlayerMP;

public class FriendsListManagerServer {

    private static FriendsListManagerServer instance;

    private final FriendsListPlayerDataContainerServer dataContainer = new FriendsListPlayerDataContainerServer();

    private final FriendsListPlayerDataManagerServer dataManager;

    private FriendsListManagerServer() {
        this.dataManager = new FriendsListPlayerDataManagerServer(this);
    }

    private void registerPersistentData() {
        OxygenHelperServer.registerPersistentData(this.dataContainer::save);
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

    public FriendsListPlayerDataContainerServer getPlayerDataContainer() {
        return this.dataContainer;
    }

    public FriendsListPlayerDataManagerServer getPlayerDataManager() {
        return this.dataManager;
    }

    public void playerLoaded(EntityPlayerMP playerMP) {
        this.dataContainer.playerLoaded(CommonReference.getPersistentUUID(playerMP));
    }

    public void sendStatusMessage(EntityPlayerMP playerMP, EnumFriendsListStatusMessage message) {
        OxygenHelperServer.sendStatusMessage(playerMP, FriendsListMain.FRIENDS_LIST_MOD_INDEX, message.ordinal());
    }
}
