package com.decacagle.aliensmc.games.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class GlassBridgeSpace {

    public double z1, z2;
    public World world;
    public boolean shattered = false;

    public static final double RIGHT_X1 = 967.7;
    public static final double RIGHT_X2 = 970.3;
    public static final double LEFT_X1 = 971.7;
    public static final double LEFT_X2 = 974.3;

    public final double LOWER_Y = 95.0;
    public final double UPPER_Y = 96.1;

    public final int Y = 95;

    // rightSafe defines whether the right glass of this space is the 'safe' side
    // if rightSafe is false, the left side will be the safe side
    public boolean rightSafe;

    public GlassBridgeSpace(double z1, double z2, World world, boolean rightSafe) {
        this.z1 = z1;
        this.z2 = z2;
        this.world = world;
        this.rightSafe = rightSafe;
    }

    public void shatter() {
        // start with x-values for the glass on the right side
        int x1 = (int) (RIGHT_X1 + 1.0);;
        int x2 = (int) (RIGHT_X2 - 1.0);;
        // if the right side is the safe side, replace x-values with left glass values
        if (rightSafe) {
            x1 = (int) (LEFT_X1 + 1.0);;
            x2 = (int) (LEFT_X2  - 1.0);;
        }
        int z1 = (int) (this.z1 + 1.0);
        int z2 = (int) (this.z2 - 1.0);

        world.setBlockData(x1, Y, z1, Material.AIR.createBlockData());
        world.setBlockData(x2, Y, z1, Material.AIR.createBlockData());
        world.setBlockData(x1, Y, z2, Material.AIR.createBlockData());
        world.setBlockData(x2, Y, z2, Material.AIR.createBlockData());

        Location midpoint = new Location(world, x2, Y, z2);

        world.playSound(midpoint, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);

        shattered = true;

    }

    public boolean playerIsOnSpace(Player player) {
        double pX = player.getX();
        double pY = player.getY();
        double pZ = player.getZ();

        return (pX >= RIGHT_X1 && pX <= LEFT_X2) && (pZ >= z1 && pZ <= z2) && (pY >= LOWER_Y && pY <= UPPER_Y);

    }

    public boolean playerOnSafeSide(Player player) {
        double pX = player.getX();

        return (rightSafe ? (pX >= RIGHT_X1 && pX <= RIGHT_X2) : (pX >= LEFT_X1 && pX <= LEFT_X2));

    }

    public void explode() {

        int rx1 = (int) (RIGHT_X1 + 1.0);
        int rx2 = (int) (RIGHT_X2 - 1.0);
        int lx1 = (int) (LEFT_X1 + 1.0);
        int lx2 = (int) (LEFT_X2 - 1.0);

        int z1 = (int) (this.z1 + 1.0);
        int z2 = (int) (this.z2 - 1.);

        world.setBlockData(rx1, Y, z1, Material.AIR.createBlockData());
        world.setBlockData(rx2, Y, z1, Material.AIR.createBlockData());
        world.setBlockData(rx1, Y, z2, Material.AIR.createBlockData());
        world.setBlockData(rx2, Y, z2, Material.AIR.createBlockData());

        world.setBlockData(lx1, Y, z1, Material.AIR.createBlockData());
        world.setBlockData(lx2, Y, z1, Material.AIR.createBlockData());
        world.setBlockData(lx1, Y, z2, Material.AIR.createBlockData());
        world.setBlockData(lx2, Y, z2, Material.AIR.createBlockData());

        Location rmidpoint = new Location(world, rx2, Y, z2);
        Location lmidpoint = new Location(world, lx2, Y, z2);

        world.playSound(rmidpoint, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
        world.playSound(lmidpoint, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);

        world.createExplosion(rmidpoint, 10, false, false);
        world.createExplosion(lmidpoint, 10, false, false);

    }

}
