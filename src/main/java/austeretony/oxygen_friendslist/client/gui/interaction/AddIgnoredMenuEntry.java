package austeretony.oxygen_friendslist.client.gui.interaction;

import java.util.UUID;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.interaction.PlayerInteractionMenuEntry;
import austeretony.oxygen_friendslist.client.FriendsListManagerClient;

public class AddIgnoredMenuEntry implements PlayerInteractionMenuEntry {

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_friendslist.interaction.ignore");
    }

    @Override
    public boolean isValid(UUID playerUUID) {
        return OxygenHelperClient.isPlayerAvailable(playerUUID) && !FriendsListManagerClient.instance().getPlayerDataContainer().haveEntryForUUID(playerUUID);
    }

    @Override
    public void execute(UUID playerUUID) {
        FriendsListManagerClient.instance().getPlayerDataManager().addToIgnoredSynced(playerUUID);
    }
}
