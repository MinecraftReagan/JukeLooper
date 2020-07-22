package me.reagan.jukelooper;

import me.reagan.jukelooper.commands.JukeLooper;
import me.reagan.jukelooper.commands.JukeLooperReload;
import me.reagan.jukelooper.data.DataManager;
import me.reagan.jukelooper.events.Events;
import me.reagan.jukelooper.juke.JukeManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private DataManager data;
    private JukeManager juke;

    @Override
    public void onEnable() {
        data = new DataManager(this);
        data.createDataFile();
        saveDefaultConfig();
        juke = new JukeManager(this, data);
        juke.enableAllJukeboxes();
        juke.secondTimer();

        this.getCommand("jukelooper").setExecutor(new JukeLooper(this));
        this.getCommand("jukelooperreload").setExecutor(new JukeLooperReload(this));
        this.getServer().getPluginManager().registerEvents(new Events(juke, data), this);
    }

    @Override
    public void onDisable() {
        juke.disableAllJukeboxes();
    }
}
