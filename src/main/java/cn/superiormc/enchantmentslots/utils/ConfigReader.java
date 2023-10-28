package cn.superiormc.enchantmentslots.utils;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.hooks.CheckValidHook;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ConfigReader {

    public static boolean getDebug() {
        return EnchantmentSlots.instance.getConfig().getBoolean("settings.debug", false);
    }
    public static String getEnchantmentName(Enchantment enchantment) {
        return EnchantmentSlots.instance.getConfig().getString("enchant-name." + enchantment.getKey().getKey(), enchantment.getKey().getKey());
    }
    public static boolean getCloseInventory() {
        return EnchantmentSlots.instance.getConfig().getBoolean("settings.close-inventory-if-reached-limit", true);
    }
    public static boolean getRemoveExtraEnchants() {
        return EnchantmentSlots.instance.getConfig().getBoolean("settings.remove-illegal-extra-enchant", true);
    }
    public static boolean getAtFirstOrLast() {
        return EnchantmentSlots.instance.getConfig().getBoolean("settings.add-lore.at-first-or-last", false);
    }
    public static String getEnchantPlaceholder() {
        return EnchantmentSlots.instance.getConfig().getString("settings.add-lore.placeholder.enchants.format",
                "&6  {enchant_name}");
    }
    public static String getEmptySlotPlaceholder() {
        return EnchantmentSlots.instance.getConfig().getString("settings.add-lore.placeholder.empty-slots.format",
                "&7  --- Empty Slot ---");
    }
    public static boolean getBlackCreativeMode() {
        return EnchantmentSlots.instance.getConfig().getBoolean("settings.add-lore.black-creative-mode", true);
    }
    public static boolean getEnchantItemTrigger() {
        return EnchantmentSlots.instance.getConfig().getBoolean("settings.add-lore.trigger.EnchantItemEvent.enabled", true);
    }
    public static boolean getInventoryClickTrigger() {
        return EnchantmentSlots.instance.getConfig().getBoolean("settings.add-lore.trigger.InventoryClickEvent.enabled", true);
    }
    public static boolean getAutoAddSlotsLimit() {
        return EnchantmentSlots.instance.getConfig().getBoolean("settings.add-lore.trigger.Packet.enabled", true);
    }
    public static boolean getAutoAddSlotsAutoCheck() {
        return EnchantmentSlots.instance.getConfig().getBoolean("settings.add-lore.trigger.Packet.auto-check", true);
    }
    public static List<String> getAutoAddSlotsItems() {
        return EnchantmentSlots.instance.getConfig().getStringList("settings.add-lore.trigger.Packet.whitelist-items");
    }
    public static List<String> getDisplayLore() {
        return EnchantmentSlots.instance.getConfig().getStringList("settings.add-lore.display-value");
    }
    public static int getDefaultLimits(Player player, ItemStack itemStack) {
        ConfigurationSection section = EnchantmentSlots.instance.getConfig().
                getConfigurationSection("settings.default-slots-by-item." +
                        CheckValidHook.checkValid(itemStack));
        if (ConfigReader.getDebug()) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] Item ID: " + CheckValidHook.checkValid(itemStack));
        }
        if (section == null) {
            section = EnchantmentSlots.instance.getConfig().
                    getConfigurationSection("settings.default-slots");
        }
        ConfigurationSection conditionSection = EnchantmentSlots.instance.getConfig().
                getConfigurationSection("settings.slots-conditions");
        if (section == null) {
            return 5;
        }
        if (conditionSection == null) {
            return section.getInt("default", 5);
        }
        Set<String> groupNameSet = conditionSection.getKeys(false);
        List<Integer> result = new ArrayList<>();
        for (String groupName : groupNameSet) {
            if (section.getInt(groupName, 0) != 0 && Condition.getBoolean(player, conditionSection.getStringList(groupName))) {
                result.add(section.getInt(groupName));
            }
            else {
                if (section.getInt("default", 0) != 0) {
                    result.add(section.getInt("default", 5));
                }
            }
        }
        if (result.size() == 0) {
            result.add(5);
        }
        return Collections.max(result);
    }
    public static int getMaxLimits(Player player, ItemStack itemStack) {
        ConfigurationSection section = EnchantmentSlots.instance.getConfig().
                getConfigurationSection("settings.max-slots-by-item." +
                        CheckValidHook.checkValid(itemStack));
        if (section == null) {
            section = EnchantmentSlots.instance.getConfig().
                    getConfigurationSection("settings.max-slots");
        }
        ConfigurationSection conditionSection = EnchantmentSlots.instance.getConfig().
                getConfigurationSection("settings.slots-conditions");
        if (section == null) {
            return -1;
        }
        if (conditionSection == null) {
            return section.getInt("default", -1);
        }
        Set<String> groupNameSet = conditionSection.getKeys(false);
        List<Integer> result = new ArrayList<>();
        for (String groupName : groupNameSet) {
            if (section.getInt(groupName, 0) != 0 && Condition.getBoolean(player, conditionSection.getStringList(groupName))) {
                result.add(section.getInt(groupName));
            }
            else {
                if (section.getInt("default") != 0) {
                    result.add(section.getInt("default", -1));
                }
            }
        }
        if (result.size() == 0) {
            result.add(-1);
        }
        return Collections.max(result);
    }
    public static String getMessages(String key) {
        return ColorParser.parse(EnchantmentSlots.instance.getConfig().getString("messages." + key));
    }
}
