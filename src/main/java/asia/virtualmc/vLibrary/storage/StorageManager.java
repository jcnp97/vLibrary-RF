package asia.virtualmc.vLibrary.storage;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.storage.database.MySQLConnection;
import asia.virtualmc.vLibrary.storage.database.PlayerIDUtils;
import org.jetbrains.annotations.NotNull;

public class StorageManager {

    public StorageManager(@NotNull VLibrary vlib) {
        MySQLConnection.initialize(vlib);
        PlayerIDUtils.createTable();
    }
}
