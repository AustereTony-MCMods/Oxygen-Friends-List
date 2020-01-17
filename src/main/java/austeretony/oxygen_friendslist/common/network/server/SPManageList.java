package austeretony.oxygen_friendslist.common.network.server;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import austeretony.oxygen_friendslist.server.FriendsListManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPManageList extends Packet {

    private int ordinal;

    private UUID playerUUID;

    public SPManageList() {}

    public SPManageList(EnumRequest request, UUID playerUUID) {
        this.ordinal = request.ordinal();
        this.playerUUID = playerUUID;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        ByteBufUtils.writeUUID(this.playerUUID, buffer);
        buffer.writeByte(this.ordinal);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (OxygenHelperServer.isNetworkRequestAvailable(CommonReference.getPersistentUUID(playerMP), FriendsListMain.LIST_MANAGEMENT_REQUEST_ID)) {
            final UUID playerUUID = ByteBufUtils.readUUID(buffer);
            switch (EnumRequest.values()[buffer.readByte()]) {
            case ADD_FRIEND:
                OxygenHelperServer.addRoutineTask(()->FriendsListManagerServer.instance().getPlayerDataManager().sendFriendRequest(playerMP, playerUUID));
                break;
            case REMOVE_FRIEND:
                OxygenHelperServer.addRoutineTask(()->FriendsListManagerServer.instance().getPlayerDataManager().removeFriend(playerMP, playerUUID));
                break;
            case ADD_IGNORED:
                OxygenHelperServer.addRoutineTask(()->FriendsListManagerServer.instance().getPlayerDataManager().addToIgnored(playerMP, playerUUID));
                break;
            case REMOVE_IGNORED:
                OxygenHelperServer.addRoutineTask(()->FriendsListManagerServer.instance().getPlayerDataManager().removeIgnored(playerMP, playerUUID));
                break;
            }
        }
    }

    public enum EnumRequest {

        ADD_FRIEND,
        REMOVE_FRIEND,
        ADD_IGNORED,
        REMOVE_IGNORED
    }
}
