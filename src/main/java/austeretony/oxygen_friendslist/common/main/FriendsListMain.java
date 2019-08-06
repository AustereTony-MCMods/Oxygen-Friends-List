package austeretony.oxygen_friendslist.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.oxygen.client.api.OxygenGUIHelper;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.command.CommandOxygenClient;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.interaction.InteractionHelperClient;
import austeretony.oxygen.client.sync.gui.api.AdvancedGUIHandlerClient;
import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.api.network.OxygenNetwork;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen.common.privilege.api.Privilege;
import austeretony.oxygen.common.privilege.api.PrivilegeProviderServer;
import austeretony.oxygen.common.privilege.api.PrivilegedGroup;
import austeretony.oxygen.common.sync.gui.api.AdvancedGUIHandlerServer;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.FriendsListMenuHandlerClient;
import austeretony.oxygen_friendslist.client.command.FriendsListArgumentExecutorClient;
import austeretony.oxygen_friendslist.client.event.FriendssListEventsClient;
import austeretony.oxygen_friendslist.client.gui.AddToFriendsContextAction;
import austeretony.oxygen_friendslist.client.gui.IgnoreContextAction;
import austeretony.oxygen_friendslist.client.input.FriendsListKeyHandler;
import austeretony.oxygen_friendslist.client.interaction.AddFriendInteractionExecutor;
import austeretony.oxygen_friendslist.client.interaction.AddIgnoredActionExecutor;
import austeretony.oxygen_friendslist.common.FriendsListManagerServer;
import austeretony.oxygen_friendslist.common.FriendsListMenuHandlerServer;
import austeretony.oxygen_friendslist.common.config.FriendsListConfig;
import austeretony.oxygen_friendslist.common.event.FriendsListEventsServer;
import austeretony.oxygen_friendslist.common.network.server.SPEditFriendListEntryNote;
import austeretony.oxygen_friendslist.common.network.server.SPManageFriendList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = FriendsListMain.MODID, 
        name = FriendsListMain.NAME, 
        version = FriendsListMain.VERSION,
        dependencies = "required-after:oxygen@[0.8.0,);",//TODO Always check required Oxygen version before build
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = FriendsListMain.VERSIONS_FORGE_URL)
public class FriendsListMain {

    public static final String 
    MODID = "oxygen_friendslist", 
    NAME = "Oxygen: Friends List", 
    VERSION = "0.8.0", 
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Friends-List/info/mod_versions_forge.json";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static OxygenNetwork network;

    public static final int 
    FRIENDS_LIST_MOD_INDEX = 6,//Teleportation - 1, Groups - 2, Exchange - 3, Merchants - 4, Players List - 5, Interaction - 7, Mail - 8, Chat - 9

    FRIEND_REQUEST_ID = 60,

    FRIENDS_LIST_MENU_SCREEN_ID = 60,

    FRIEND_REQUESTS_AUTO_ACCEPT_SETTING_ID = 60;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperServer.registerConfig(new FriendsListConfig());
        if (event.getSide() == Side.CLIENT)
            CommandOxygenClient.registerArgumentExecutor(new FriendsListArgumentExecutorClient("friendslist", true));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        FriendsListManagerServer.create();
        CommonReference.registerEvent(new FriendsListEventsServer());
        OxygenHelperServer.registerRequestValidator(new IgnoreRequestValidator());
        OxygenHelperServer.registerSharedDataIdentifierForScreen(FRIENDS_LIST_MENU_SCREEN_ID, OxygenMain.ACTIVITY_STATUS_SHARED_DATA_ID);
        OxygenHelperServer.registerSharedDataIdentifierForScreen(FRIENDS_LIST_MENU_SCREEN_ID, OxygenMain.DIMENSION_SHARED_DATA_ID);
        AdvancedGUIHandlerServer.registerScreen(FRIENDS_LIST_MENU_SCREEN_ID, new FriendsListMenuHandlerServer());
        if (event.getSide() == Side.CLIENT) {     
            FriendsListManagerClient.create();
            CommonReference.registerEvent(new FriendssListEventsClient());
            CommonReference.registerEvent(new FriendsListKeyHandler());
            OxygenGUIHelper.registerContextAction(20, new AddToFriendsContextAction());//20 - group menu id
            OxygenGUIHelper.registerContextAction(20, new IgnoreContextAction());
            OxygenGUIHelper.registerContextAction(50, new AddToFriendsContextAction());//50 - players list menu id
            OxygenGUIHelper.registerContextAction(50, new IgnoreContextAction());
            OxygenGUIHelper.registerScreenId(FRIENDS_LIST_MENU_SCREEN_ID);
            AdvancedGUIHandlerClient.registerScreen(FRIENDS_LIST_MENU_SCREEN_ID, new FriendsListMenuHandlerClient());
            InteractionHelperClient.registerInteractionMenuAction(new AddFriendInteractionExecutor());
            InteractionHelperClient.registerInteractionMenuAction(new AddIgnoredActionExecutor());
            OxygenHelperClient.registerNotificationIcon(FRIEND_REQUEST_ID, OxygenGUITextures.REQUEST_ICONS);
            OxygenHelperClient.registerClientSetting(FRIEND_REQUESTS_AUTO_ACCEPT_SETTING_ID);
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        FriendsListManagerServer.instance().reset();
    }

    public static void addDefaultPrivileges() {
        if (!PrivilegeProviderServer.getGroup(PrivilegedGroup.OPERATORS_GROUP.groupName).hasPrivilege(EnumFriendsListPrivilege.PREVENT_IGNORE.toString())) {
            PrivilegeProviderServer.addPrivilege(PrivilegedGroup.OPERATORS_GROUP.groupName, new Privilege(EnumFriendsListPrivilege.PREVENT_IGNORE.toString(), true), true);
            LOGGER.info("Default <{}> group privileges added.", PrivilegedGroup.OPERATORS_GROUP.groupName);
        }
    }

    public static OxygenNetwork network() {
        return network;
    }

    private void initNetwork() {
        network = OxygenHelperServer.createNetworkHandler(MODID);

        network.registerPacket(SPManageFriendList.class);
        network.registerPacket(SPEditFriendListEntryNote.class);
    }
}
