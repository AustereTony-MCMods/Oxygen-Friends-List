package austeretony.oxygen_friendslist.common.network.client;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPFriendsListCommand extends ProxyPacket {

    private EnumCommand command;

    public CPFriendsListCommand() {}

    public CPFriendsListCommand(EnumCommand command) {
        this.command = command;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeByte(this.command.ordinal());
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        this.command = EnumCommand.values()[buffer.readByte()];
        switch (this.command) {
        case OPEN_FRIENDS_LIST:
            FriendsListManagerClient.instance().openFriendsListDelegated();
            break;
        }
    }

    public enum EnumCommand {

        OPEN_FRIENDS_LIST
    }
}
