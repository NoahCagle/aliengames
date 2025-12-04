package com.decacagle.aliensmc.commands;

import com.decacagle.aliensmc.AliensGames;
import com.decacagle.aliensmc.utilities.Globals;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KnifeCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        if (commandSourceStack.getSender() instanceof Player player) {
            giveKnife(player);
        } else {
            commandSourceStack.getSender().sendRichMessage("<red>You're not a player!");
        }
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
