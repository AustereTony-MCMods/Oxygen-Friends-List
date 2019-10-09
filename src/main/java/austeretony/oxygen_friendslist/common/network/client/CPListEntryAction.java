package austeretony.oxygen_friendslist.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.common.ListEntry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPListEntryAction extends Packet {

    private int ordinal;

    private ListEntry entry;

    public CPListEntryAction() {}

    public CPListEntryAction(EnumAction action, ListEntry entry) {
        this.ordinal = action.ordinal();
        this.entry = entry;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeByte(this.ordinal);
        this.entry.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final int ordinal = buffer.readByte();
        final ListEntry entry = new ListEntry();
        entry.read(buffer);
        switch (EnumAction.values()[ordinal]) {
        case ADDED:
            OxygenHelperClient.addRoutineTask(()->FriendsListManagerClient.instance().getPlayerDataManager().entryAdded(entry));
            break;
        case REMOVED:
            OxygenHelperClient.addRoutineTask(()->FriendsListManagerClient.instance().getPlayerDataManager().entryRemoved(entry));
            break;
        }
    }

    public enum EnumAction {

        ADDED,
        REMOVED;
    }
}
