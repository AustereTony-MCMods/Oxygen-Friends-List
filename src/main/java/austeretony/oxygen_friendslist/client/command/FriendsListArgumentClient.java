package austeretony.oxygen_friendslist.client.command;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import austeretony.oxygen_friendslist.client.FriendsListMenuManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class FriendsListArgumentClient implements ArgumentExecutor {

    @Override
    public String getName() {
        return "friendslist";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1)
            OxygenHelperClient.scheduleTask(FriendsListMenuManager::openFriendsListMenuDelegated, 100L, TimeUnit.MILLISECONDS);
        else if (args.length == 2) {
            if (args[1].equals("-reset-data")) {
                FriendsListManagerClient.instance().getPlayerDataContainer().reset();
                ClientReference.showChatMessage("oxygen_friendslist.command.dataReset");
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, "-reset-data");
        return Collections.<String>emptyList();
    }
}
