package cc.pixeleast.rushmode.language;

import com.andrei1058.bedwars.api.language.Language;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;

import static cc.pixeleast.rushmode.language.MessagePath.*;

public class Message {

    public Message() {
        Language.getLanguages().forEach(language -> {
            YamlConfiguration yml = language.getYml();
            yml.addDefault(MESSAGES_ARENA_START_TUTORIAL, Arrays.asList("&a" + "▬".repeat(72),
                    "&f&l                       Bed Wars Rush", "", "&e&l        All generators are maxed! Your bed has three",
                    "&e&l       layers of protection! Left click while holding",
                    "&e&l             wool to activate bridge building!", "", "&a" + "▬".repeat(72)));
            yml.addDefault(MESSAGES_BRIDGE_MODE_ACTIVATED, "&a&lBRIDGE MODE ACTIVATED");
            yml.addDefault(MESSAGES_BRIDGE_MODE_DEACTIVATED, "&c&lBRIDGE MODE DEACTIVATED");
            yml.addDefault(MESSAGES_ITEM_NOT_PURCHASABLE, "&l{itemName} &c cannot be purchased in rush mode!");

            yml.options().copyDefaults(true);
            language.save();
        });
    }
}
