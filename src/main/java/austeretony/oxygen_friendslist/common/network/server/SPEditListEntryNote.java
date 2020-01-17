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

public class SPEditListEntryNote extends Packet {

    private UUID playerUUID;

    private String note;

    public SPEditListEntryNote() {}

    public SPEditListEntryNote(UUID playerUUID, String note) {
        this.playerUUID = playerUUID;
        this.note = note;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        ByteBufUtils.writeUUID(this.playerUUID, buffer);
        ByteBufUtils.writeString(this.note, buffer);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (OxygenHelperServer.isNetworkRequestAvailable(CommonReference.getPersistentUUID(playerMP), FriendsListMain.LIST_MANAGEMENT_REQUEST_ID)) {
            final UUID playerUUID = ByteBufUtils.readUUID(buffer);
            final String note = ByteBufUtils.readString(buffer);
            OxygenHelperServer.addRoutineTask(()->FriendsListManagerServer.instance().getPlayerDataManager().editListEntryNote(playerMP, playerUUID, note));
        }
    }
}
