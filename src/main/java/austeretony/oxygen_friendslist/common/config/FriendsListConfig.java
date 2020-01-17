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

    FRIEND_REQUEST_EXPIRE_TIME_SECONDS = ConfigValueUtils.getValue("server", "friend_request_expire_time_seconds", 20),   
    MAX_FRIENDS_AMOUNT = ConfigValueUtils.getValue("server", "max_friends_amount", 20, true),
    MAX_IGNORED_AMOUNT = ConfigValueUtils.getValue("server", "max_ignored_amount", 20, true);

    @Override
    public String getDomain() {
        return FriendsListMain.MODID;
    }

    @Override
    public String getExternalPath() {
        return CommonReference.getGameFolder() + "/config/oxygen/friends-list.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(ENABLE_FRIENDSLIST_KEY);

        values.add(FRIEND_REQUEST_EXPIRE_TIME_SECONDS);
        values.add(MAX_FRIENDS_AMOUNT);
        values.add(MAX_IGNORED_AMOUNT);
    }
}
