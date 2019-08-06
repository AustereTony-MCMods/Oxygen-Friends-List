package austeretony.oxygen_friendslist.common.main;

import austeretony.oxygen.common.privilege.api.PrivilegeProviderServer;

public enum EnumFriendsListPrivilege {

    PREVENT_IGNORE(":preventIgnore");

    private final String name;

    EnumFriendsListPrivilege(String name) {
        this.name = name;
        PrivilegeProviderServer.registerPrivilege(FriendsListMain.MODID + name, FriendsListMain.NAME);
    }

    @Override
    public String toString() {
        return FriendsListMain.MODID + this.name;
    }
}
