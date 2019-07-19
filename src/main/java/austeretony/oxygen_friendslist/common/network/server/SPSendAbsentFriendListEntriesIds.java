package austeretony.oxygen_friendslist.common.network.server;

import java.util.UUID;

import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_friendslist.common.main.FriendsListMain;
import austeretony.oxygen_friendslist.common.network.client.CPFriendsListCommand;
import austeretony.oxygen_friendslist.common.network.client.CPSyncFriendListEntries;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPSendAbsentFriendListEntriesIds extends ProxyPacket {

    private long[] entriesIds;

    private int amount;

    public SPSendAbsentFriendListEntriesIds() {}

    public SPSendAbsentFriendListEntriesIds(long[] entriesIds, int amount) {
        this.entriesIds = entriesIds;
        this.amount = amount;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeShort(this.amount);
        for (long entryId : this.entriesIds) {
            if (entryId == 0L) break;
            buffer.writeLong(entryId);
        }
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        int amount = buffer.readShort();
        if (amount > 0) {
            long[] needSync = new long[amount];
            int index = 0;
            for (int i = 0; i < amount; i++)
                needSync[index++] = buffer.readLong();
            FriendsListMain.network().sendTo(new CPSyncFriendListEntries(playerUUID, needSync), playerMP);
        }
        FriendsListMain.network().sendTo(new CPFriendsListCommand(CPFriendsListCommand.EnumCommand.OPEN_FRIENDS_LIST), playerMP);
        OxygenHelperServer.setSyncing(playerUUID, false);
    }
}
