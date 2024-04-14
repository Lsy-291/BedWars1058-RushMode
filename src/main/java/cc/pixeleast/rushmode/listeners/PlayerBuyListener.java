package cc.pixeleast.rushmode.listeners;

import cc.pixeleast.rushmode.utils.Misc;
import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import com.andrei1058.bedwars.api.language.Language;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import static cc.pixeleast.rushmode.RushMode.config;
import static cc.pixeleast.rushmode.configuration.ConfigPath.GENERAL_BANNED_ITEMS;
import static cc.pixeleast.rushmode.configuration.ConfigPath.SOUNDS_CANT_BUY;
import static cc.pixeleast.rushmode.language.MessagePath.MESSAGES_ITEM_NOT_PURCHASABLE;

public class PlayerBuyListener implements Listener {

    @EventHandler
    public void onPlayerBuy(ShopBuyEvent event) {
        if (!Misc.isRushArena(event.getArena())) return;

        Player player = event.getBuyer();
        ItemStack item = event.getCategoryContent().getItemStack(player);
        if (config.getList(GENERAL_BANNED_ITEMS).contains(item.getType().name())) {
            event.setCancelled(true);
            player.sendMessage(Language.getMsg(player, MESSAGES_ITEM_NOT_PURCHASABLE).replace("{itemName}", item.getItemMeta().getDisplayName()));
            player.playSound(player.getLocation(), Sound.valueOf(config.getString(SOUNDS_CANT_BUY)), 1, 1);
        }
    }
}
