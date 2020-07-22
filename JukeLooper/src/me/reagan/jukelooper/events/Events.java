package me.reagan.jukelooper.events;

import me.reagan.jukelooper.data.DataManager;
import me.reagan.jukelooper.juke.JukeManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
    private JukeManager juke;
    private DataManager data;

    public Events(JukeManager juke, DataManager data) {
        this.juke = juke;
        this.data = data;
    }

    // Use jukebox with button
    @EventHandler
    public void onButtonPress(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !event.getPlayer().hasPermission("jukelooper.use")) {
            if (block.getType().equals(Material.STONE_BUTTON) || block.getType().equals(Material.OAK_BUTTON)
                    || block.getType().equals(Material.SPRUCE_BUTTON) || block.getType().equals(Material.BIRCH_BUTTON)
                    || block.getType().equals(Material.JUNGLE_BUTTON) || block.getType().equals(Material.ACACIA_BUTTON)
                    || block.getType().equals(Material.DARK_OAK_BUTTON)) {
                Switch button = (Switch) block.getState().getBlockData();
                BlockFace attached = button.getFacing().getOppositeFace();
                if (block.getRelative(attached).getType().equals(Material.JUKEBOX)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // Interact with jukebox
    @EventHandler
    public void onJukeboxInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!event.getClickedBlock().getType().equals(Material.JUKEBOX)) {
            return;
        }
        if (!event.getPlayer().hasPermission("jukelooper.use")) {
            return;
        }
        Jukebox box = (Jukebox) event.getClickedBlock().getState();
        // Right click
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Material record = player.getInventory().getItemInMainHand().getType();
            if (box.isPlaying()) {
                if (juke.getJukeboxs().containsKey(box.getLocation())) {
                    // Eject
                    juke.stopPlaying(box.getLocation());
                    data.removeJukebox(box);
                }
            } else {
                if (!record.equals(Material.AIR) && record.isRecord()) {
                    // Play new disk
                    juke.startPlaying(box.getLocation(), record);
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    event.setCancelled(true);
                } else {
                    // Eject
                    juke.stopPlaying(box.getLocation());
                    data.removeJukebox(box);
                }
            }
        }
    }

    // Remove jukebox
    @EventHandler
    public void onJukeboxRemove(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.JUKEBOX)) {
            return;
        }
        Jukebox box = (Jukebox) event.getBlock().getState();
        if (!event.getPlayer().hasPermission("jukelooper.remove") && data.checkJukebox(box)) {
            event.setCancelled(true);
            return;
        }
        juke.stopPlaying(box.getLocation());
        data.removeJukebox(box);
    }

    // Redstone event
    @EventHandler
    public void onRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.STONE_BUTTON) || block.getType().equals(Material.OAK_BUTTON)
                || block.getType().equals(Material.SPRUCE_BUTTON) || block.getType().equals(Material.BIRCH_BUTTON)
                || block.getType().equals(Material.JUNGLE_BUTTON) || block.getType().equals(Material.ACACIA_BUTTON)
                || block.getType().equals(Material.DARK_OAK_BUTTON)) {
            Powerable button = (Powerable) block.getBlockData();
            for (BlockFace blockFace : JukeManager.blockFaces) {
                if (!button.isPowered() && block.getRelative(blockFace).getType().equals(Material.JUKEBOX)) {
                    Location location = block.getRelative(blockFace).getLocation();
                    if (juke.findStorage(location) != null) {
                        juke.nextDisc(location);
                    }
                }
            }
        }
    }
}
