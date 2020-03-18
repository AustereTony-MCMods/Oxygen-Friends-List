package austeretony.oxygen_friendslist.common.config;

import java.util.List;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.config.AbstractConfig;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_core.common.config.ConfigValueUtils;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class FriendsListConfig extends AbstractConfig {

    public static final ConfigValue
    ENABLE_FRIENDSLIST_KEY = ConfigValueUtils.getValue("client", "enable_friendslist_key", true),
    FRIENDSLIST_KEY = ConfigValueUtils.getValue("client", "friendslist_key", 24),

    DISABLE_PVP_FOR_FRIENDS = ConfigValueUtils.getValue("server", "disable_pvp_for_friends", false),
    FRIEND_REQUEST_EXPIRE_TIME_SECONDS = ConfigValueUtils.getValue("server", "friend_request_expire_time_seconds", 20),   
    MAX_FRIENDS_AMOUNT = ConfigValueUtils.getValue("server", "max_friends_amount", 20, true),
    MAX_IGNORED_AMOUNT = ConfigValueUtils.getValue("server", "max_ignored_amount", 20, true),
    ADVANCED_LOGGING = ConfigValueUtils.getValue("server", "advanced_logging", false);

    @Override
    public String getDomain() {
        return FriendsListMain.MODID;
    }

    @Override
    public String getVersion() {
        return FriendsListMain.VERSION_CUSTOM;
    }

    @Override
    public String getExternalPath() {
        return CommonReference.getGameFolder() + "/config/oxygen/friends-list.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(ENABLE_FRIENDSLIST_KEY);
        values.add(FRIENDSLIST_KEY);

        values.add(DISABLE_PVP_FOR_FRIENDS);
        values.add(FRIEND_REQUEST_EXPIRE_TIME_SECONDS);
        values.add(MAX_FRIENDS_AMOUNT);
        values.add(MAX_IGNORED_AMOUNT);
        values.add(ADVANCED_LOGGING);
    }
}
