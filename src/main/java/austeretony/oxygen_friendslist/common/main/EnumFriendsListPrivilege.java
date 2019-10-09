package austeretony.oxygen_friendslist.common.main;

import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.privilege.PrivilegeRegistry;

public enum EnumFriendsListPrivilege {

    PREVENT_IGNORE(":preventIgnore", EnumValueType.BOOLEAN),

    MAX_FRIENDS_AMOUNT(":maxFriendsAmount", EnumValueType.INT),
    MAX_IGNORED_AMOUNT(":maxIgnoredAmount", EnumValueType.INT);

    private final String name;

    private final EnumValueType type;

    EnumFriendsListPrivilege(String name, EnumValueType type) {
        this.name = "friends_list:" + name;
        this.type = type;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static void register() {
        for (EnumFriendsListPrivilege privilege : EnumFriendsListPrivilege.values())
            PrivilegeRegistry.registerPrivilege(privilege.name, privilege.type);
    }
}
