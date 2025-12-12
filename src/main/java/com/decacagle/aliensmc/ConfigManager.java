package com.decacagle.aliensmc;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

// fucking kill me
public class ConfigManager {

    private AliensGames plugin;
    private FileConfiguration fc;

    public boolean debugMode;

    // Red Light Green Light Values
    public String gameWorldTitleRLGL;
    public int minimumPlayersRLGL;
    public int gameDurationSecondsRLGL;
    public int minimumToggleTimeSecondsRLGL;
    public int maximumToggleTimeSecondsRLGL;
    public int timeBeforeStartSecondsRLGL;
    public int gracePeriodTicksRLGL;
    public double spawnpointXRLGL, spawnpointYRLGL, spawnpointZRLGL, spawnpointYawRLGL, spawnpointPitchRLGL;
    public String prettyTitleRLGL;

    // Hide and Seek Values
    public String gameWorldTitleHNS;
    public int minimumPlayersHNS;
    public int gameDurationSecondsHNS;
    public int seekerSpawnTimeSecondsHNS;
    public double spawnpointXHNS, spawnpointYHNS, spawnpointZHNS, spawnpointYawHNS, spawnpointPitchHNS;
    public double mapLocXHNS, mapLocYHNS, mapLocZHNS, mapLocYawHNS, mapLocPitchHNS;
    public double escapeXHNS, escapeYHNS, escapeZHNS;
    public String prettyTitleHNS;

    // Glass Bridge Values
    public String gameWorldTitleGB;
    public int minimumPlayersGB;
    public int gameDurationSecondsGB;
    public double spawnpointXGB, spawnpointYGB, spawnpointZGB, spawnpointYawGB, spawnpointPitchGB;
    public double bridgeSpawnpointXGB, bridgeSpawnpointYGB, bridgeSpawnpointZGB, bridgeSpawnpointYawGB, bridgeSpawnpointPitchGB;
    public double vipSpawnpointXGB, vipSpawnpointYGB, vipSpawnpointZGB, vipSpawnpointYawGB, vipSpawnpointPitchGB;
    public String prettyTitleGB;

    // Special Game Values
    public String gameWorldTitleSG;
    public boolean useDarknessEffectSG;
    public int minimumPlayersSG;
    public int gameDurationSecondsSG;
    public int timeBeforeStartSecondsSG;
    public double spawnpointXSG, spawnpointYSG, spawnpointZSG, spawnpointYawSG, spawnpointPitchSG;
    public String prettyTitleSG;

    public ConfigManager(AliensGames plugin) {
        this.plugin = plugin;
        this.fc = plugin.getConfig();
        loadValues(null);
    }

    public void loadValues(@Nullable CommandSender requester) {
        plugin.logger.info("Loading AliensGames config...");
        if (requester != null) requester.sendRichMessage("<yellow>Reloading AliensGames config...");

        this.debugMode = b("debug_mode");

        // Red Light Green Light Values
        this.gameWorldTitleRLGL = s("game_world_title_rlgl");
        this.minimumPlayersRLGL = i("minimum_players_rlgl");
        this.gameDurationSecondsRLGL = i("game_duration_seconds_rlgl");
        this.minimumToggleTimeSecondsRLGL = i("minimum_toggle_time_seconds");
        this.maximumToggleTimeSecondsRLGL = i("maximum_toggle_time_seconds");
        this.timeBeforeStartSecondsRLGL = i("time_before_start_rlgl");
        this.gracePeriodTicksRLGL = i("grace_period_ticks_rlgl");
        this.spawnpointXRLGL = d("spawnpoint_x_rlgl");
        this.spawnpointYRLGL = d("spawnpoint_y_rlgl");
        this.spawnpointZRLGL = d("spawnpoint_z_rlgl");
        this.spawnpointYawRLGL = d("spawnpoint_yaw_rlgl");
        this.spawnpointPitchRLGL = d("spawnpoint_pitch_rlgl");
        this.prettyTitleRLGL = s("pretty_title_rlgl");

        // Hide and Seek Values
        this.gameWorldTitleHNS = s("game_world_title_hns");
        this.minimumPlayersHNS = i("minimum_players_hns");
        this.gameDurationSecondsHNS = i("game_duration_seconds_hns");
        this.seekerSpawnTimeSecondsHNS = i("seeker_spawn_time_seconds_hns");
        this.spawnpointXHNS = d("spawnpoint_x_hns");
        this.spawnpointYHNS = d("spawnpoint_y_hns");
        this.spawnpointZHNS = d("spawnpoint_z_hns");
        this.spawnpointYawHNS = d("spawnpoint_yaw_hns");
        this.spawnpointPitchHNS = d("spawnpoint_pitch_hns");
        this.mapLocXHNS = d("map_loc_x_hns");
        this.mapLocYHNS = d("map_loc_y_hns");
        this.mapLocZHNS = d("map_loc_z_hns");
        this.mapLocYawHNS = d("map_loc_yaw_hns");
        this.mapLocPitchHNS = d("map_loc_pitch_hns");
        this.escapeXHNS = d("escape_x_hns");
        this.escapeYHNS = d("escape_y_hns");
        this.escapeZHNS = d("escape_z_hns");
        this.prettyTitleHNS = s("pretty_title_hns");

        // Glass Bridge Values
        this.gameWorldTitleGB = s("game_world_title_gb");
        this.minimumPlayersGB = i("minimum_players_gb");
        this.gameDurationSecondsGB = i("game_duration_seconds_gb");
        this.spawnpointXGB = d("spawnpoint_x_gb");
        this.spawnpointYGB = d("spawnpoint_y_gb");
        this.spawnpointZGB = d("spawnpoint_z_gb");
        this.spawnpointYawGB = d("spawnpoint_yaw_gb");
        this.spawnpointPitchGB = d("spawnpoint_pitch_gb");
        this.bridgeSpawnpointXGB = d("bridge_spawnpoint_x_gb");
        this.bridgeSpawnpointYGB = d("bridge_spawnpoint_y_gb");
        this.bridgeSpawnpointZGB = d("bridge_spawnpoint_z_gb");
        this.bridgeSpawnpointYawGB = d("bridge_spawnpoint_yaw_gb");
        this.bridgeSpawnpointPitchGB = d("bridge_spawnpoint_pitch_gb");
        this.vipSpawnpointXGB = d("vip_spawnpoint_x_gb");
        this.vipSpawnpointYGB = d("vip_spawnpoint_y_gb");
        this.vipSpawnpointZGB = d("vip_spawnpoint_z_gb");
        this.vipSpawnpointYawGB = d("vip_spawnpoint_yaw_gb");
        this.vipSpawnpointPitchGB = d("vip_spawnpoint_pitch_gb");
        this.prettyTitleGB = s("pretty_title_gb");

        // Special Game Settings
        this.gameWorldTitleSG = s("game_world_title_sg");
        this.useDarknessEffectSG = b("use_darkness_effect_sg");
        this.minimumPlayersSG = i("minimum_players_sg");
        this.gameDurationSecondsSG = i("game_duration_seconds_sg");
        this.timeBeforeStartSecondsSG = i("time_before_start_sg");
        this.spawnpointXSG = d("spawnpoint_x_sg");
        this.spawnpointYSG = d("spawnpoint_y_sg");
        this.spawnpointZSG = d("spawnpoint_z_sg");
        this.spawnpointYawSG = d("spawnpoint_yaw_sg");
        this.spawnpointPitchSG = d("spawnpoint_pitch_sg");
        this.prettyTitleSG = s("pretty_title_sg");

        plugin.logger.info("AliensGames config loaded!");
        if (requester != null) requester.sendRichMessage("<green>AliensGames config reloaded!");
    }

    private double d(String key) {
        if (!fc.contains(key)) plugin.getLogger().warning("Missing config key: " + key);
        return fc.getDouble(key);
    }

    private int i(String key) {
        if (!fc.contains(key)) plugin.getLogger().warning("Missing config key: " + key);
        return fc.getInt(key);
    }

    private String s(String key) {
        if (!fc.contains(key)) plugin.getLogger().warning("Missing config key: " + key);
        return fc.getString(key);
    }

    private boolean b(String key) {
        if (!fc.contains(key)) plugin.getLogger().warning("Missing config key: " + key);
        return fc.getBoolean(key);
    }
}
