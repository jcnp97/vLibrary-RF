package asia.virtualmc.vLibrary.integrations.realistic_seasons;

import asia.virtualmc.vLibrary.VLibrary;
import me.casperge.realisticseasons.api.SeasonsAPI;
import me.casperge.realisticseasons.season.Season;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class SeasonsUtils {
    private final VLibrary vlib;
    private static SeasonsAPI seasonsAPI;

    public SeasonsUtils(@NotNull VLibrary vlib) {
        this.vlib = vlib;
        initialize();
    }

    private void initialize() {
        if (vlib.getServer().getPluginManager().getPlugin("RealisticSeasons") == null) {
            vlib.getLogger().severe("RealisticSeasons not found, vLibrary will use its built-in seasons system.");
        }

        seasonsAPI = SeasonsAPI.getInstance();
        vlib.getLogger().info("RealisticSeasons found. Applying integration..");
    }

    public static void setCurrentSeason(World worldName, Season seasonName) {
        seasonsAPI.setSeason(worldName, seasonName);
    }

    public static int getSeasonID(World worldName) {
        switch (seasonsAPI.getSeason(worldName)) {
            default -> { return 0; }
            case Season.SPRING -> { return 1; }
            case Season.SUMMER -> { return 2; }
            case Season.FALL -> { return 3; }
            case Season.WINTER -> { return 4; }
        }
    }

    public static SeasonsAPI getInstance() {
        return seasonsAPI;
    }
}
