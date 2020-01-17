package austeretony.oxygen_friendslist.common.main;

import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.privilege.PrivilegeRegistry;

public enum EnumFriendsListPrivilege {

    PREVENT_IGNORE("friendslist:preventIgnore", 600, EnumValueType.BOOLEAN),

    MAX_FRIENDS_AMOUNT("friendslist:maxFriendsAmount", 601, EnumValueType.INT),
    MAX_IGNORED_AMOUNT("friendslist:maxIgnoredAmount", 602, EnumValueType.INT);

    private final String name;

    private final int id;

    private final EnumValueType type;

    EnumFriendsListPrivilege(String name, int id, EnumValueType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public int id() {
        return id;
    }

    public static void register() {
        for (EnumFriendsListPrivilege privilege : EnumFriendsListPrivilege.values())
            PrivilegeRegistry.registerPrivilege(privilege.name, privilege.id, privilege.type);
    }
}
