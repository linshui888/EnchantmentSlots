package cn.superiormc.enchantmentslots.utils;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import com.willfp.ecoenchants.enchants.EcoEnchant;
import com.willfp.ecoenchants.enchants.EcoEnchants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import su.nightexpress.excellentenchants.enchantment.impl.ExcellentEnchant;
import su.nightexpress.excellentenchants.enchantment.registry.EnchantRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ItemModify {

    public static ItemStack serverToClient(Player player, ItemStack serverItemStack) {
        if (ItemLimits.getRealMaxEnchantments(player, serverItemStack) == 0) {
            return serverItemStack;
        }
        ItemStack clientItemStack = serverItemStack.clone();
        if (!clientItemStack.hasItemMeta()) {
            ItemMeta tempMeta = Bukkit.getItemFactory().getItemMeta(clientItemStack.getType());
            clientItemStack.setItemMeta(tempMeta);
        }
        ItemMeta itemMeta = clientItemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (ConfigReader.getAtFirstOrLast()) {
            for (String line : ConfigReader.getDisplayLore()) {
                if (line.equals("{enchants}")) {
                    for (Enchantment enchantment : clientItemStack.getEnchantments().keySet()) {
                        lore.add(ColorParser.parse(
                                ConfigReader.getEnchantPlaceholder().
                                        replace("{enchant_name}", getEnchantName(enchantment))));
                    }
                    continue;
                }
                if (line.equals("{empty_slots}")) {
                    int i = ItemLimits.getMaxEnchantments(player, clientItemStack) - clientItemStack.getEnchantments().size();
                    while (i > 0) {
                        lore.add(ColorParser.parse(ConfigReader.getEmptySlotPlaceholder()));
                        i--;
                    }
                    continue;
                }
                lore.add(ColorParser.parse(line)
                        .replace("{slot_amount}", String.valueOf(ItemLimits.getMaxEnchantments(player, serverItemStack))
                        .replace("{enchant_amount}", String.valueOf(clientItemStack.getEnchantments().size()))));

            }
        }
        if (itemMeta.hasLore()) {
            lore.addAll(itemMeta.getLore());
        }
        if (!ConfigReader.getAtFirstOrLast()) {
            for (String line : ConfigReader.getDisplayLore()) {
                if (line.equals("{enchants}")) {
                    for (Enchantment enchantment : clientItemStack.getEnchantments().keySet()) {
                        lore.add(ColorParser.parse(
                                ConfigReader.getEnchantPlaceholder().
                                        replace("{enchant_name}", getEnchantName(enchantment))));
                    }
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    continue;
                }
                if (line.equals("{empty_slots}")) {
                    int i = ItemLimits.getMaxEnchantments(player, clientItemStack) - clientItemStack.getEnchantments().size();
                    while (i > 0) {
                        lore.add(ColorParser.parse(ConfigReader.getEmptySlotPlaceholder()));
                        i--;
                    }
                    continue;
                }
                lore.add(ColorParser.parse(line)
                        .replace("{slot_amount}", String.valueOf(ItemLimits.getMaxEnchantments(player, serverItemStack)))
                        .replace("{enchant_amount}", String.valueOf(clientItemStack.getEnchantments().size())));

            }
        }
        itemMeta.setLore(lore);
        if (ConfigReader.getDebug()) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §fAdded lore: " + lore + ".");
        }
        clientItemStack.setItemMeta(itemMeta);
        return clientItemStack;
    }

    public static ItemStack clientToServer(Player player, ItemStack clientItemStack) {
        if (ItemLimits.getRealMaxEnchantments(player, clientItemStack) == 0) {
            return clientItemStack;
        }
        ItemStack serverItemStack = clientItemStack.clone();
        if (!serverItemStack.hasItemMeta()) {
            ItemMeta tempMeta = Bukkit.getItemFactory().getItemMeta(serverItemStack.getType());
            serverItemStack.setItemMeta(tempMeta);
        }
        ItemMeta itemMeta = serverItemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        List<String> newLore = new ArrayList<>();
        if (itemMeta.hasLore()) {
            lore = itemMeta.getLore();
            bigfor:
            for (String str : lore) {
                Pattern pattern2 = Pattern.compile(ColorParser.parse(
                                ConfigReader.getEnchantPlaceholder()).
                        replace("{enchant_name}", "(.*)"),
                        Pattern.CASE_INSENSITIVE);
                Matcher matcher2 = pattern2.matcher(str);
                if (matcher2.find()) {
                    for (Enchantment enchantment : serverItemStack.getEnchantments().keySet()) {
                        if (matcher2.group().equals(getEnchantName(enchantment))) {
                            continue bigfor;
                        }
                    }
                }
                if (str.equalsIgnoreCase(ColorParser.parse(ConfigReader.getEmptySlotPlaceholder()))) {
                    continue;
                }
                for (String configStr : ConfigReader.getDisplayLore()) {
                    if (!configStr.equals("{enchants}") && !configStr.equals("{empty_slots}")) {
                        itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                        Pattern pattern1 = Pattern.compile(ColorParser.parse(configStr)
                                .replace("{slot_amount}", "(\\d+)")
                                .replace("{enchant_amount}", "(\\d+)"), Pattern.CASE_INSENSITIVE);
                        Matcher matcher1 = pattern1.matcher(str);
                        EnchantmentSlots.instance.saveConfig();
                        if (matcher1.find()) {
                            continue bigfor;
                        }
                    }
                }
                newLore.add(str);
            }
        }
        if (lore.isEmpty() || lore.get(0).isEmpty()) {
            itemMeta.setLore(null);
        } else {
            itemMeta.setLore(newLore);
        }
        serverItemStack.setItemMeta(itemMeta);
        return serverItemStack;
    }

    public static void addLore(Player player, ItemStack item, boolean fromPacket) {
        if (!ConfigReader.getAutoAddSlotsLimit() && fromPacket) {
            return;
        }
        if (item == null) {
            return;
        }
        if (item.getType() == Material.ENCHANTED_BOOK) {
            return;
        }
        if (ItemLimits.getRealMaxEnchantments(player, item) != 0) {
            return;
        }
        if (!item.hasItemMeta()) {
            ItemMeta tempMeta = Bukkit.getItemFactory().getItemMeta(item.getType());
            item.setItemMeta(tempMeta);
        }
        ItemMeta meta = item.getItemMeta();
        if (ItemLimits.canEnchant(item)) {
            meta.getPersistentDataContainer().set(ItemLimits.ENCHANTMENT_SLOTS_KEY,
                    PersistentDataType.INTEGER,
                    ConfigReader.getDefaultLimits(player, item));
            item.setItemMeta(meta);
        }
    }

    public static String getEnchantName(Enchantment enchantment) {
        if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("EcoEnchants")) {
            EcoEnchant ecoEnchant = EcoEnchants.getByKey(enchantment.getKey());
            if (ConfigReader.getDebug()) {
                Bukkit.getConsoleSender().sendMessage(EcoEnchants.keySet() + "");
            }
            if (ecoEnchant != null) {
                return ecoEnchant.getDisplayName();
            }
        } else if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("ExcellentEnchants")) {
            ExcellentEnchant excellentEnchant = EnchantRegistry.getByKey(enchantment.getKey());
            if (ConfigReader.getDebug()) {
                Bukkit.getConsoleSender().sendMessage(EnchantRegistry.REGISTRY_MAP.keySet() + "");
            }
            if (excellentEnchant != null) {
                return excellentEnchant.getDisplayName();
            }
        }
        return ConfigReader.getEnchantmentName(enchantment);
    }
}
