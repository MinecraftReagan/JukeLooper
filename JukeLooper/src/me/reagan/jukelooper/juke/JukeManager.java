package me.reagan.jukelooper.juke;

import me.reagan.jukelooper.Main;
import me.reagan.jukelooper.data.DataManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Jukebox;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class JukeManager {
    private Main plugin;
    private DataManager data;
    private Map<Location, Integer> boxList = new HashMap<Location, Integer>();

    public JukeManager(Main plugin, DataManager data) {
        this.plugin = plugin;
        this.data = data;
    }

    public void secondTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Location, Integer> jukebox : boxList.entrySet()) {
                    Location location = jukebox.getKey();
                    Integer duration = jukebox.getValue() - 1;
                    boxList.put(location, duration);
                    if (duration <= 0) {
                        nextDisc(location);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public Map<Location, Integer> getJukeboxs() {
        return this.boxList;
    }

    public void enableAllJukeboxes() {
        FileConfiguration saved = data.getData();
        for (String world : saved.getKeys(false)) {
            for (String box : saved.getConfigurationSection(world).getKeys(false)) {
                String record = saved.getString(world + "." + box);
                if (!Material.getMaterial(record).isRecord()) {
                    continue;
                }
                Material material = Material.getMaterial(record);

                String[] cords = box.split("_");
                int x = Integer.valueOf(cords[0]);
                int y = Integer.valueOf(cords[1]);
                int z = Integer.valueOf(cords[2]);
                Location location = new Location(plugin.getServer().getWorld(world), x, y, z);

                startPlaying(location, material);
            }
        }
    }

    public void disableAllJukeboxes() {
        for (Map.Entry<Location, Integer> jukebox : boxList.entrySet()) {
            Location location = jukebox.getKey();
            if (location.getBlock().getType().equals(Material.JUKEBOX)) {
                Jukebox box = (Jukebox) location.getBlock().getState();
                box.setPlaying(null);
                box.update();
            }
        }
    }

    public void startPlaying(Location location, Material record) {
        if (!location.getBlock().getType().equals(Material.JUKEBOX)) {
            return;
        }

        Jukebox box = (Jukebox) location.getBlock().getState();

        box.setPlaying(record);
        box.update(true);
        onPlay(box.getLocation(), record);
        data.changeJukebox(box, record);
    }

    public void stopPlaying(Location location) {
        if (!location.getBlock().getType().equals(Material.JUKEBOX)) {
            return;
        }

        Jukebox box = (Jukebox) location.getBlock().getState();

        box.setPlaying(null);
        box.update();
        onEject(location);
    }

    public void nextDisc(Location location) {
        if (!location.getBlock().getType().equals(Material.JUKEBOX)) {
            return;
        }

        Jukebox box = (Jukebox) location.getBlock().getState();

        Block storage = findStorage(location);
        if (storage == null) {
            return;
        }

        ItemStack playedRecord = box.getRecord();
        putInStorage(playedRecord, storage.getLocation());

        Material record = takeFromStorage(storage.getLocation());
        if (record == null) {
            return;
        }

        box.setPlaying(record);
        box.update();
        data.changeJukebox(box, record);
        onPlay(box.getLocation(), record);
    }

    public Boolean putInStorage(ItemStack item, Location storage) {
        if (storage == null) {
            return false;
        }

        if (storage.getBlock().getType().equals(Material.CHEST)) {
            Chest chest = (Chest) storage.getBlock().getState();
            chest.getBlockInventory().addItem(item);
            return true;
        }
        return false;
    }

    public Material takeFromStorage(Location storage) {
        if (!storage.getBlock().getType().equals(Material.CHEST)) {
            return null;
        }

        List<Material> recordList = new ArrayList<Material>();
        Chest chest = (Chest) storage.getBlock().getState();
        for (ItemStack item : chest.getBlockInventory().getContents()) {
            if (item != null && item.getType().isRecord()) {
                recordList.add(item.getType());
            }
        }
        if (recordList.isEmpty()) {
            return null;
        }

        Collections.shuffle(recordList);
        Material record = recordList.get(0);
        chest.getBlockInventory().removeItem(new ItemStack(record, 1));
        return record;
    }

    public Block findStorage(Location location) {
        Block block = location.getBlock();
        Block top = block.getLocation().clone().add(0, 1, 0).getBlock();
        if (top.getType().equals(Material.CHEST)) {
            return top;
        }
        return null;
    }

    public static BlockFace[] blockFaces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH,
            BlockFace.SOUTH, BlockFace.DOWN, BlockFace.UP};

    public void onPlay(Location location, Material record) {
        Integer duration = data.getDuration(record);
        boxList.put(location, duration);
    }

    public void onEject(Location location) {
        boxList.remove(location);
    }
}
