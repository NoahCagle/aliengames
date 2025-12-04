package com.decacagle.aliensmc.commands;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.utilities.Globals;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KeyCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        if (commandSourceStack.getSender() instanceof Player player) {
            giveKeys(player);
        } else {
            commandSourceStack.getSender().sendRichMessage("<red>You're not a player!");
        }
    }

    public void giveKeys(Player player) {

        ItemStack purpleKey = new ItemStack(Globals.PURPLE_KEY_TYPE);
        ItemMeta purpleMeta = purpleKey.getItemMeta();
        ItemStack tielKey = new ItemStack(Globals.TIEL_KEY_TYPE);
        ItemMeta tielMeta = purpleKey.getItemMeta();
        ItemStack brownKey = new ItemStack(Globals.BROWN_KEY_TYPE);
        ItemMeta brownMeta = purpleKey.getItemMeta();

        purpleMeta.displayName(Component.text(Globals.PURPLE_KEY_NAME)
                .color(NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.ITALIC, false));
        purpleKey.setItemMeta(purpleMeta);

        tielMeta.displayName(Component.text(Globals.TIEL_KEY_NAME)
                .color(NamedTextColor.BLUE)
                .decoration(TextDecoration.ITALIC, false));

        tielKey.setItemMeta(tielMeta);

        brownMeta.displayName(Component.text(Globals.BROWN_KEY_NAME)
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));

        brownKey.setItemMeta(brownMeta);

        player.getInventory().addItem(purpleKey);
        player.getInventory().addItem(tielKey);
        player.getInventory().addItem(brownKey);

    }

}
