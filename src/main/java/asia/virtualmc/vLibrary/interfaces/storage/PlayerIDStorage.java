package asia.virtualmc.vLibrary.interfaces.storage;

import java.util.UUID;

public interface PlayerIDStorage {
    Integer getPlayerID(UUID uuid);
    boolean replaceUUID(int playerID, UUID newUUID);
}