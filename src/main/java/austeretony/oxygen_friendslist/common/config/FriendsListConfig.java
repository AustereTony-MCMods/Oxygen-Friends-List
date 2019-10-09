package austeretony.oxygen_friendslist.common.config;

import java.util.List;

import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.config.AbstractConfigHolder;
import austeretony.oxygen_core.common.api.config.ConfigValueImpl;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;

public class FriendsListConfig extends AbstractConfigHolder {

    public static final ConfigValue
    LIST_SAVE_DELAY_MINUTES = new ConfigValueImpl(EnumValueType.INT, "setup", "list_save_delay_minutes"),

    FRIEND_REQUEST_EXPIRE_TIME_SECONDS = new ConfigValueImpl(EnumValueType.INT, "main", "friend_request_expire_time_seconds"),
    
    MAX_FRIENDS_AMOUNT = new ConfigValueImpl(EnumValueType.INT, "main", "max_friends_amount"),
    MAX_IGNORED_AMOUNT = new ConfigValueImpl(EnumValueType.INT, "main", "max_ignored_amount");

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
        return CommonReference.getGameFolder() + "/config/oxygen/friends_list.json";
    }

    @Override
    public String getInternalPath() {
        return "assets/oxygen_friendslist/friends_list.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(LIST_SAVE_DELAY_MINUTES);

        values.add(FRIEND_REQUEST_EXPIRE_TIME_SECONDS);
        
        values.add(MAX_FRIENDS_AMOUNT);
        values.add(MAX_IGNORED_AMOUNT);
    }

    @Override
    public boolean sync() {
        return true;
    }
}
