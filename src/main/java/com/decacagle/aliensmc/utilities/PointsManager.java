package com.decacagle.aliensmc.utilities;

import com.decacagle.aliensmc.AliensGames;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PointsManager {

    private File pointsFile;
    private FileConfiguration pointsConfig;
    private AliensGames plugin;

    private String fileName;

    public PointsManager(AliensGames plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        createPointsFile();
    }

    private void createPointsFile() {
        pointsFile = new File(plugin.getDataFolder(), fileName);

        if (!pointsFile.exists()) {
            plugin.saveResource("points.yml", false);
        }

        this.pointsConfig = YamlConfiguration.loadConfiguration(pointsFile);
    }

    public int getPoints(Player player) {
        return pointsConfig.getInt("points." + player.getName(), 0);
    }

    public void addPoints(Player player, int amount) {
        int current = getPoints(player);
        pointsConfig.set("points." + player.getName(), current + amount);
        save();
    }

    public void setPoints(String username, int amount) {
        pointsConfig.set("points." + username, amount);
        save();
    }

    public void deletePoints(String username) {
        pointsConfig.set("points." + username, null);
        save();
    }

    // not needed now, but may be handy later if dealing with purchases and whatnot
    public void removePoints(Player player, int amount) {
        addPoints(player, -amount);
    }

    private void save() {
        try {
            pointsConfig.save(new File(plugin.getDataFolder(), fileName));
        } catch (IOException e) {
            plugin.logger.severe("Failed to save points.yml!");
            plugin.logger.severe("e: " + e.getMessage());
        }
    }

    public List<Map.Entry<String, Integer>> getOrderedLeaderboard() {
        ConfigurationSection section = pointsConfig.getConfigurationSection("points");
        if (section == null) return Collections.emptyList();

        Map<String, Integer> map = new HashMap<>();
        for (String key : section.getKeys(false)) {
            map.put(key, pointsConfig.getInt("points." + key));
        }

        return map.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .toList();
    }

}
