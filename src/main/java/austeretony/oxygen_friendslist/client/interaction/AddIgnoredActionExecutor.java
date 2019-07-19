package austeretony.oxygen_friendslist.client.interaction;

import java.util.UUID;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.interaction.IInteractionMenuExecutor;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;
import net.minecraft.util.ResourceLocation;

public class AddIgnoredActionExecutor implements IInteractionMenuExecutor {

    @Override
    public String getName() {
        return "oxygen_friendslist.interaction.ignore";
    }

    @Override
    public ResourceLocation getIcon() {
        return OxygenGUITextures.IGNORED_ICONS;
    }

    @Override
    public boolean isValid(UUID playerUUID) {
        return !OxygenHelperClient.isOfflineStatus(playerUUID) && !FriendsListManagerClient.instance().getClientPlayerData().haveFriendListEntryForUUID(playerUUID);
    }

    @Override
    public void execute(UUID playerUUID) {
        FriendsListManagerClient.instance().addToIgnoredSynced(playerUUID);
    }
}
