package com.decacagle.aliensmc;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

// fucking kill me
public class ConfigManager {

    private AliensGames plugin;
    private FileConfiguration fc;

    public boolean debugMode;
    public int hostTimeoutSeconds;

    // Red Light Green Light Values
    public boolean rlglEnabled;
    public boolean rlglExperimental;
    public String gameWorldTitleRLGL;
    public int minimumPlayersRLGL;
    public int gameDurationSecondsRLGL;
    public int minimumToggleTimeSecondsRLGL;
    public int maximumToggleTimeSecondsRLGL;
    public int timeBeforeStartSecondsRLGL;
    public int gracePeriodTicksRLGL;
    public double spawnpointXRLGL, spawnpointYRLGL, spawnpointZRLGL, spawnpointYawRLGL, spawnpointPitchRLGL;
    public double boundsAXRLGL, boundsAYRLGL, boundsAZRLGL, boundsBXRLGL, boundsBYRLGL, boundsBZRLGL;
    public String prettyTitleRLGL;

    // Hide and Seek Values
    public boolean hnsEnabled;
    public boolean hnsExperimental;
    public String gameWorldTitleHNS;
    public int minimumPlayersHNS;
    public int gameDurationSecondsHNS;
    public int seekerSpawnTimeSecondsHNS;
    public double spawnpointXHNS, spawnpointYHNS, spawnpointZHNS, spawnpointYawHNS, spawnpointPitchHNS;
    public double mapLocXHNS, mapLocYHNS, mapLocZHNS, mapLocYawHNS, mapLocPitchHNS;
    public double escapeXHNS, escapeYHNS, escapeZHNS;
    public double boundsAXHNS, boundsAYHNS, boundsAZHNS, boundsBXHNS, boundsBYHNS, boundsBZHNS;
    public String prettyTitleHNS;

    // Glass Bridge Values
    public boolean gbEnabled;
    public boolean gbExperimental;
    public String gameWorldTitleGB;
    public int minimumPlayersGB;
    public int startingLivesGB;
    public int gameDurationSecondsGB;
    public double spawnpointXGB, spawnpointYGB, spawnpointZGB, spawnpointYawGB, spawnpointPitchGB;
    public double bridgeSpawnpointXGB, bridgeSpawnpointYGB, bridgeSpawnpointZGB, bridgeSpawnpointYawGB, bridgeSpawnpointPitchGB;
    public double vipSpawnpointXGB, vipSpawnpointYGB, vipSpawnpointZGB, vipSpawnpointYawGB, vipSpawnpointPitchGB;
    public double boundsAXGB, boundsAYGB, boundsAZGB, boundsBXGB, boundsBYGB, boundsBZGB;
    public String prettyTitleGB;

    // Special Game Values
    public boolean sgEnabled;
    public boolean sgExperimental;
    public String gameWorldTitleSG;
    public boolean useDarknessEffectSG, useBlindnessEffectSG;
    public int darknessEffectAmplifierSG, blindnessEffectAmplifierSG;
    public int minimumPlayersSG;
    public int gameDurationSecondsSG;
    public int timeBeforeStartSecondsSG;
    public double spawnpointXSG, spawnpointYSG, spawnpointZSG, spawnpointYawSG, spawnpointPitchSG;
    public double boundsAXSG, boundsAYSG, boundsAZSG, boundsBXSG, boundsBYSG, boundsBZSG;
    public String prettyTitleSG;

    // Murder Mystery Values
    public boolean mmEnabled;
    public boolean mmExperimental;
    public String gameWorldTitleMM;
    public int minimumPlayersMM;
    public int gameDurationSecondsMM;
    public int timeBeforeStartSecondsMM;
    public double spawnpointXMM, spawnpointYMM, spawnpointZMM, spawnpointYawMM, spawnpointPitchMM;
    public double mapLocXMM, mapLocYMM, mapLocZMM, mapLocYawMM, mapLocPitchMM;
    public String prettyTitleMM;

    public ConfigManager(AliensGames plugin) {
        this.plugin = plugin;
        this.fc = plugin.getConfig();
        loadValues(null);
    }

    public void loadValues(@Nullable CommandSender requester) {
        plugin.logger.info("Loading AliensGames config...");
        if (requester != null) requester.sendRichMessage("<yellow>Reloading AliensGames config...");

        this.debugMode = b("debug_mode");
        this.hostTimeoutSeconds = i("host_timeout_seconds");

        // Red Light Green Light Values
        this.rlglEnabled = b("rlgl_enabled");
        this.rlglExperimental = b("rlgl_experimental");
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
        this.boundsAXRLGL = d("bounds_a_x_rlgl");
        this.boundsAYRLGL = d("bounds_a_y_rlgl");
        this.boundsAZRLGL = d("bounds_a_z_rlgl");
        this.boundsBXRLGL = d("bounds_b_x_rlgl");
        this.boundsBYRLGL = d("bounds_b_y_rlgl");
        this.boundsBZRLGL = d("bounds_b_z_rlgl");
        this.prettyTitleRLGL = s("pretty_title_rlgl");

        // Hide and Seek Values
        this.hnsEnabled = b("hns_enabled");
        this.hnsExperimental = b("hns_experimental");
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
        this.boundsAXHNS = d("bounds_a_x_hns");
        this.boundsAYHNS = d("bounds_a_y_hns");
        this.boundsAZHNS = d("bounds_a_z_hns");
        this.boundsBXHNS = d("bounds_b_x_hns");
        this.boundsBYHNS = d("bounds_b_y_hns");
        this.boundsBZHNS = d("bounds_b_z_hns");
        this.prettyTitleHNS = s("pretty_title_hns");

        // Glass Bridge Values
        this.gbEnabled = b("gb_enabled");
        this.gbExperimental = b("gb_experimental");
        this.gameWorldTitleGB = s("game_world_title_gb");
        this.minimumPlayersGB = i("minimum_players_gb");
        this.startingLivesGB = i("starting_lives_gb");
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
        this.boundsAXGB = d("bounds_a_x_gb");
        this.boundsAYGB = d("bounds_a_y_gb");
        this.boundsAZGB = d("bounds_a_z_gb");
        this.boundsBXGB = d("bounds_b_x_gb");
        this.boundsBYGB = d("bounds_b_y_gb");
        this.boundsBZGB = d("bounds_b_z_gb");
        this.prettyTitleGB = s("pretty_title_gb");

        // Special Game Settings
        this.sgEnabled = b("sg_enabled");
        this.sgExperimental = b("sg_experimental");
        this.gameWorldTitleSG = s("game_world_title_sg");
        this.useDarknessEffectSG = b("use_darkness_effect_sg");
        this.useBlindnessEffectSG = b("use_blindness_effect_sg");
        this.darknessEffectAmplifierSG = i("darkness_effect_amplitude_sg");
        this.blindnessEffectAmplifierSG = i("blindness_effect_amplitude_sg");
        this.minimumPlayersSG = i("minimum_players_sg");
        this.gameDurationSecondsSG = i("game_duration_seconds_sg");
        this.timeBeforeStartSecondsSG = i("time_before_start_sg");
        this.spawnpointXSG = d("spawnpoint_x_sg");
        this.spawnpointYSG = d("spawnpoint_y_sg");
        this.spawnpointZSG = d("spawnpoint_z_sg");
        this.spawnpointYawSG = d("spawnpoint_yaw_sg");
        this.spawnpointPitchSG = d("spawnpoint_pitch_sg");
        this.boundsAXSG = d("bounds_a_x_sg");
        this.boundsAYSG = d("bounds_a_y_sg");
        this.boundsAZSG = d("bounds_a_z_sg");
        this.boundsBXSG = d("bounds_b_x_sg");
        this.boundsBYSG = d("bounds_b_y_sg");
        this.boundsBZSG = d("bounds_b_z_sg");
        this.prettyTitleSG = s("pretty_title_sg");

        // Murder Mystery Settings
        this.mmEnabled = b("mm_enabled");
        this.mmExperimental = b("mm_experimental");
        this.gameWorldTitleMM = s("game_world_title_mm");
        this.minimumPlayersMM = i("minimum_players_mm");
        this.gameDurationSecondsMM = i("game_duration_seconds_mm");
        this.timeBeforeStartSecondsMM = i("time_before_start_mm");
        this.spawnpointXMM = d("spawnpoint_x_mm");
        this.spawnpointYMM = d("spawnpoint_y_mm");
        this.spawnpointZMM = d("spawnpoint_z_mm");
        this.spawnpointYawMM = d("spawnpoint_yaw_mm");
        this.spawnpointPitchMM = d("spawnpoint_pitch_mm");
        this.mapLocXMM = d("map_loc_x_mm");
        this.mapLocYMM = d("map_loc_y_mm");
        this.mapLocZMM = d("map_loc_z_mm");
        this.mapLocYawMM = d("map_loc_yaw_mm");
        this.mapLocPitchMM = d("map_loc_pitch_mm");
        this.prettyTitleMM = s("pretty_title_mm");

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
