package me.reagan.jukelooper.data;

import me.reagan.jukelooper.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DataManager {
    private Main plugin;
    private FileConfiguration data;
    private File dataFile;

    public DataManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadData() throws FileNotFoundException, IOException, InvalidConfigurationException {
        data.load(dataFile);
    }

    public FileConfiguration getData() {
        return this.data;
    }

    public void changeJukebox(Jukebox jukebox, Material material) {
        Location loc = jukebox.getLocation();
        String world = loc.getWorld().getName();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        if (jukebox.isPlaying() && !jukebox.getPlaying().equals(material)) {
            material = jukebox.getPlaying();
        }
        data.set(world + "." + x + "_" + y + "_" + z, material.toString());
        saveData();
    }

    public boolean checkJukebox(Jukebox jukebox) {
        String world = jukebox.getLocation().getWorld().getName();
        int x = jukebox.getLocation().getBlockX();
        int y = jukebox.getLocation().getBlockY();
        int z = jukebox.getLocation().getBlockZ();
        String box = data.getString(world + "." + x + "_" + y + "_" + z);
        return box != null;
    }

    public void removeJukebox(Jukebox jukebox) {
        Location loc = jukebox.getLocation();
        String world = loc.getWorld().getName();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        data.set(world + "." + x + "_" + y + "_" + z, null);
        saveData();
    }

    public void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDataFile() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        data = new YamlConfiguration();
        try {
            data.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Integer getDuration(Material record) {
        Integer duration = plugin.getConfig().getInt("recordDurations." + record.toString());
        return duration;
    }
}
