package austeretony.oxygen_friendslist.common.config;

import java.util.List;

import austeretony.oxygen.common.api.config.AbstractConfigHolder;
import austeretony.oxygen.common.api.config.ConfigValue;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class FriendsListConfig extends AbstractConfigHolder {

    public static final ConfigValue
    FRIEND_REQUEST_EXPIRE_TIME = new ConfigValue(ConfigValue.EnumValueType.INT, "main", "friend_request_expire_time_seconds"),
    MAX_FRIENDS = new ConfigValue(ConfigValue.EnumValueType.INT, "main", "max_friends"),
    MAX_IGNORED = new ConfigValue(ConfigValue.EnumValueType.INT, "main", "max_ignored");

    @Override
    public String getModId() {
        return FriendsListMain.MODID;
    }

    @Override
    public String getVersion() {
        return FriendsListMain.VERSION_CUSTOM;
    }

    @Override
    public String getExternalPath() {
        return CommonReference.getGameFolder() + "/config/oxygen/friends_list.json";
    }

    @Override
    public String getInternalPath() {
        return "assets/oxygen_friendslist/friends_list.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(FRIEND_REQUEST_EXPIRE_TIME);
        values.add(MAX_FRIENDS);
        values.add(MAX_IGNORED);
    }

    @Override
    public boolean sync() {
        return true;
    }
}