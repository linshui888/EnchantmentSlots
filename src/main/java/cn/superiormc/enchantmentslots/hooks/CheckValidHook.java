package cn.superiormc.enchantmentslots.hooks;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import com.willfp.eco.core.items.Items;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSlot;
import com.willfp.ecoarmor.sets.ArmorUtils;
import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.ItemUtilsKt;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.th0rgal.oraxen.api.OraxenItems;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.manager.ItemManager;

public class CheckValidHook {

    public static String checkValid(ItemStack itemStack) {
        if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            CustomStack customStack = CustomStack.byItemStack(itemStack);
            if (customStack != null) {
                return customStack.getId();
            }
        }
        if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("Oraxen")) {
            String tempVal1 = OraxenItems.getIdByItem(itemStack);
            if (tempVal1 != null) {
                return tempVal1;
            }
        }
        if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("MMOItems")) {
            String tempVal1 = MMOItems.getID(itemStack);
            if (tempVal1 != null) {
                return tempVal1;
            }
        }
        if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("EcoItems")) {
            EcoItem tempVal1 = ItemUtilsKt.getEcoItem(itemStack);
            if (tempVal1 != null) {
                return tempVal1.getID();
            }
        }
        if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("EcoArmor")) {
            ArmorSet tempVal1 = ArmorUtils.getSetOnItem(itemStack);
            if (tempVal1 != null) {
                return tempVal1.getId();
            }
        }
        if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("eco")) {
            Bukkit.getConsoleSender().sendMessage(Items.toLookupString(itemStack));
            if (Items.getCustomItem(itemStack) != null) {
                return Items.getCustomItem(itemStack).getKey().getKey();
            }
        }
        if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
            String tempVal1 = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(itemStack);
            if (tempVal1 != null) {
                return tempVal1;
            }
        }
        if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("NeigeItems")) {
            if (ItemManager.INSTANCE.isNiItem(itemStack) != null) {
                return ItemManager.INSTANCE.isNiItem(itemStack).getId();
            }
        }
        return itemStack.getType().getKey().getKey().toLowerCase();
    }
}
