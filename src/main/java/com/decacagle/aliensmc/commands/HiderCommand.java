package com.decacagle.aliensmc.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class HiderCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        if (commandSourceStack.getSender() instanceof Player player) {
            giveHiderArmor(player);
        } else {
            commandSourceStack.getSender().sendRichMessage("<red>You're not a player!");
        }
    }

    public void giveHiderArmor(Player player) {

        ItemStack hiderChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta hiderChestplateMeta = (LeatherArmorMeta) hiderChestplate.getItemMeta();
        hiderChestplateMeta.setColor(Color.GREEN);
        hiderChestplateMeta.displayName(Component.text("Hider's Chestplate"));
        hiderChestplate.setItemMeta(hiderChestplateMeta);

        player.getInventory().setChestplate(hiderChestplate);

        player.sendRichMessage("<green>You're a hider! Good luck!");
    }

}
