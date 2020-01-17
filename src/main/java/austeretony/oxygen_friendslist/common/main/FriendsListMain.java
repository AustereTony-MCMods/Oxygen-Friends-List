package austeretony.oxygen_friendslist.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.api.PlayerInteractionMenuHelper;
import austeretony.oxygen_core.client.command.CommandOxygenClient;
import austeretony.oxygen_core.client.gui.settings.SettingsScreen;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.privilege.PrivilegeUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.PrivilegesProviderServer;
import austeretony.oxygen_core.server.network.NetworkRequestsRegistryServer;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.FriendsListStatusMessagesHandler;
import austeretony.oxygen_friendslist.client.ListSyncHandlerClient;
import austeretony.oxygen_friendslist.client.command.FriendsListArgumentClient;
import austeretony.oxygen_friendslist.client.event.FriendsListEventsClient;
import austeretony.oxygen_friendslist.client.gui.context.AddToFriendsContextAction;
import austeretony.oxygen_friendslist.client.gui.context.IgnoreContextAction;
import austeretony.oxygen_friendslist.client.gui.friendslist.FriendsListScreen;
import austeretony.oxygen_friendslist.client.gui.interaction.AddFriendMenuEntry;
import austeretony.oxygen_friendslist.client.gui.interaction.AddIgnoredMenuEntry;
import austeretony.oxygen_friendslist.client.gui.settings.FriendsListSettingsContainer;
import austeretony.oxygen_friendslist.client.settings.EnumFriendsListClientSetting;
import austeretony.oxygen_friendslist.client.settings.gui.EnumFriendsListGUISetting;
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
        dependencies = "required-after:oxygen_core@[0.10.0,);",
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = FriendsListMain.VERSIONS_FORGE_URL)
public class FriendsListMain {

    public static final String 
    MODID = "oxygen_friendslist", 
    NAME = "Oxygen: Friends List", 
    VERSION = "0.10.0", 
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Friends-List/info/mod_versions_forge.json";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final int 
    FRIENDS_LIST_MOD_INDEX = 6,

    FRIEND_REQUEST_ID = 60,

    FRIENDS_LIST_MENU_SCREEN_ID = 60,

    LIST_DATA_ID = 60,

    LIST_MANAGEMENT_REQUEST_ID = 66,

    FRIEND_REQUESTS_AUTO_ACCEPT_SETTING_ID = 60;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperCommon.registerConfig(new FriendsListConfig());
        if (event.getSide() == Side.CLIENT)
            CommandOxygenClient.registerArgument(new FriendsListArgumentClient());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        FriendsListManagerServer.create();
        CommonReference.registerEvent(new FriendsListEventsServer());
        OxygenHelperServer.registerRequestValidator(new FriendsListRequestValidator());
        OxygenHelperServer.registerDataSyncHandler(new ListSyncHandlerServer());
        NetworkRequestsRegistryServer.registerRequest(LIST_MANAGEMENT_REQUEST_ID, 1000);
        EnumFriendsListPrivilege.register();
        if (event.getSide() == Side.CLIENT) {     
            FriendsListManagerClient.create();
            CommonReference.registerEvent(new FriendsListEventsClient());
            OxygenGUIHelper.registerContextAction(20, new AddToFriendsContextAction());//20 - group menu id
            OxygenGUIHelper.registerContextAction(20, new IgnoreContextAction());
            OxygenGUIHelper.registerContextAction(50, new AddToFriendsContextAction());//50 - players list menu id
            OxygenGUIHelper.registerContextAction(50, new IgnoreContextAction());
            OxygenGUIHelper.registerScreenId(FRIENDS_LIST_MENU_SCREEN_ID);
            OxygenHelperClient.registerStatusMessagesHandler(new FriendsListStatusMessagesHandler());
            OxygenHelperClient.registerSharedDataSyncListener(FRIENDS_LIST_MENU_SCREEN_ID, 
                    ()->FriendsListManagerClient.instance().getFriendsListMenuManager().sharedDataSynchronized());
            PlayerInteractionMenuHelper.registerInteractionMenuEntry(new AddFriendMenuEntry());
            PlayerInteractionMenuHelper.registerInteractionMenuEntry(new AddIgnoredMenuEntry());
            OxygenGUIHelper.registerOxygenMenuEntry(FriendsListScreen.FRIENDS_LIST_MENU_ENTRY);
            OxygenHelperClient.registerDataSyncHandler(new ListSyncHandlerClient());
            EnumFriendsListClientSetting.register();
            EnumFriendsListGUISetting.register();
            SettingsScreen.registerSettingsContainer(new FriendsListSettingsContainer());
        }
    }

    public static void addDefaultPrivileges() {
        if (PrivilegesProviderServer.getRole(OxygenMain.OPERATOR_ROLE_ID).getPrivilege(EnumFriendsListPrivilege.PREVENT_IGNORE.id()) == null) {
            PrivilegesProviderServer.getRole(OxygenMain.OPERATOR_ROLE_ID).addPrivilege(PrivilegeUtils.getPrivilege(EnumFriendsListPrivilege.PREVENT_IGNORE.id(), true));
            LOGGER.info("Default Operator role privileges added.");
        }
    }

    private void initNetwork() {
        OxygenMain.network().registerPacket(SPManageList.class);
        OxygenMain.network().registerPacket(SPEditListEntryNote.class);

        OxygenMain.network().registerPacket(CPListEntryAction.class);
    }
}
