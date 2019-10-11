package austeretony.oxygen_friendslist.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.command.CommandOxygenClient;
import austeretony.oxygen_core.client.interaction.InteractionHelperClient;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.privilege.PrivilegeImpl;
import austeretony.oxygen_core.common.privilege.PrivilegedGroupImpl;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.PrivilegeProviderServer;
import austeretony.oxygen_core.server.api.RequestsFilterHelper;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.FriendsListStatusMessagesHandler;
import austeretony.oxygen_friendslist.client.ListSyncHandlerClient;
import austeretony.oxygen_friendslist.client.command.FriendsListArgumentExecutorClient;
import austeretony.oxygen_friendslist.client.event.FriendsListEventsClient;
import austeretony.oxygen_friendslist.client.gui.context.AddToFriendsContextAction;
import austeretony.oxygen_friendslist.client.gui.context.IgnoreContextAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListGUIScreen;
import austeretony.oxygen_friendslist.client.gui.interaction.AddFriendInteractionExecutor;
import austeretony.oxygen_friendslist.client.gui.interaction.AddIgnoredActionExecutor;
import austeretony.oxygen_friendslist.client.input.FriendsListKeyHandler;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.network.client.CPListEntryAction;
import austeretony.oxygen_friendslist.common.network.server.SPEditListEntryNote;
import austeretony.oxygen_friendslist.common.network.server.SPManageList;
import austeretony.oxygen_friendslist.server.FriendsListManagerServer;
import austeretony.oxygen_friendslist.server.FriendsListRequestValidator;
import austeretony.oxygen_friendslist.server.ListSyncHandlerServer;
import austeretony.oxygen_friendslist.server.event.FriendsListEventsServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = FriendsListMain.MODID, 
        name = FriendsListMain.NAME, 
        version = FriendsListMain.VERSION,
        dependencies = "required-after:oxygen_core@[0.9.3,);",
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = FriendsListMain.VERSIONS_FORGE_URL)
public class FriendsListMain {

    public static final String 
    MODID = "oxygen_friendslist", 
    NAME = "Oxygen: Friends List", 
    VERSION = "0.9.1", 
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Friends-List/info/mod_versions_forge.json";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final int 
    FRIENDS_LIST_MOD_INDEX = 6,

    FRIEND_REQUEST_ID = 60,

    FRIENDS_LIST_MENU_SCREEN_ID = 60,

    LIST_DATA_ID = 60,

    EDIT_NOTE_REQUEST_ID = 65,
    LIST_MANAGEMENT_REQUEST_ID = 66,

    FRIEND_REQUESTS_AUTO_ACCEPT_SETTING_ID = 60;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperCommon.registerConfig(new FriendsListConfig());
        if (event.getSide() == Side.CLIENT)
            CommandOxygenClient.registerArgumentExecutor(new FriendsListArgumentExecutorClient("friendslist", true));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        FriendsListManagerServer.create();
        CommonReference.registerEvent(new FriendsListEventsServer());
        OxygenHelperServer.registerRequestValidator(new FriendsListRequestValidator());
        OxygenHelperServer.registerDataSyncHandler(new ListSyncHandlerServer());
        RequestsFilterHelper.registerNetworkRequest(EDIT_NOTE_REQUEST_ID, 1);
        RequestsFilterHelper.registerNetworkRequest(LIST_MANAGEMENT_REQUEST_ID, 1);
        if (event.getSide() == Side.CLIENT) {     
            FriendsListManagerClient.create();
            CommonReference.registerEvent(new FriendsListEventsClient());
            if (!OxygenGUIHelper.isOxygenMenuEnabled())
                CommonReference.registerEvent(new FriendsListKeyHandler());
            OxygenGUIHelper.registerContextAction(20, new AddToFriendsContextAction());//20 - group menu id
            OxygenGUIHelper.registerContextAction(20, new IgnoreContextAction());
            OxygenGUIHelper.registerContextAction(50, new AddToFriendsContextAction());//50 - players list menu id
            OxygenGUIHelper.registerContextAction(50, new IgnoreContextAction());
            OxygenGUIHelper.registerScreenId(FRIENDS_LIST_MENU_SCREEN_ID);
            OxygenHelperClient.registerStatusMessagesHandler(new FriendsListStatusMessagesHandler());
            OxygenHelperClient.registerSharedDataSyncListener(FRIENDS_LIST_MENU_SCREEN_ID, 
                    ()->FriendsListManagerClient.instance().getFriendsListMenuManager().sharedDataSynchronized());
            InteractionHelperClient.registerInteractionMenuEntry(new AddFriendInteractionExecutor());
            InteractionHelperClient.registerInteractionMenuEntry(new AddIgnoredActionExecutor());
            OxygenHelperClient.registerClientSetting(FRIEND_REQUESTS_AUTO_ACCEPT_SETTING_ID);
            OxygenGUIHelper.registerOxygenMenuEntry(FriendsListGUIScreen.FRIENDS_LIST_MENU_ENTRY);
            OxygenHelperClient.registerDataSyncHandler(new ListSyncHandlerClient());
        }
        EnumFriendsListPrivilege.register();
    }

    public static void addDefaultPrivileges() {
        if (!PrivilegeProviderServer.getGroup(PrivilegedGroupImpl.OPERATORS_GROUP.groupName).hasPrivilege(EnumFriendsListPrivilege.PREVENT_IGNORE.toString())) {
            PrivilegeProviderServer.addPrivilege(PrivilegedGroupImpl.OPERATORS_GROUP.groupName, new PrivilegeImpl(EnumFriendsListPrivilege.PREVENT_IGNORE.toString(), true), true);
            LOGGER.info("Default <{}> group privileges added.", PrivilegedGroupImpl.OPERATORS_GROUP.groupName);
        }
    }

    private void initNetwork() {
        OxygenMain.network().registerPacket(SPManageList.class);
        OxygenMain.network().registerPacket(SPEditListEntryNote.class);

        OxygenMain.network().registerPacket(CPListEntryAction.class);
    }
}
