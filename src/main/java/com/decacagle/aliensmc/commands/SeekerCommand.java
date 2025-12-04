package com.decacagle.aliensmc.commands;

import com.decacagle.aliensmc.utilities.Globals;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class SeekerCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        if (commandSourceStack.getSender() instanceof Player player) {
            giveSeekerArmor(player);
        } else {
            commandSourceStack.getSender().sendRichMessage("<red>You're not a player!");
        }
    }

    public void giveSeekerArmor(Player player) {

        ItemStack seekerChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta seekerChestplateMeta = (LeatherArmorMeta) seekerChestplate.getItemMeta();
        seekerChestplateMeta.setColor(Color.RED);
        seekerChestplateMeta.displayName(Component.text("Seeker's Chestplate"));
        seekerChestplate.setItemMeta(seekerChestplateMeta);

        player.getInventory().setChestplate(seekerChestplate);

        player.sendRichMessage("<red>You're a seeker! Good luck!");

        giveKnife(player);

    }

    public void giveKnife(Player player) {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(Globals.DAGGER_NAME)
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false));

        item.setItemMeta(meta);

        player.getInventory().addItem(item);

        player.sendRichMessage("<green>You've been given a knife. Be careful, it's sharp!");
    }

}
