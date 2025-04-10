package asia.virtualmc.vLibrary.storage;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.storage.database.MySQLConnection;
import asia.virtualmc.vLibrary.storage.database.PlayerIDManager;
import org.jetbrains.annotations.NotNull;

public class StorageManager {
    private final MySQLConnection mySQLConnection;
    private final PlayerIDManager playerIDManager;

    public StorageManager(@NotNull VLibrary vlib) {
        this.mySQLConnection = new MySQLConnection(vlib);
        this.playerIDManager = new PlayerIDManager(vlib);
    }
}
