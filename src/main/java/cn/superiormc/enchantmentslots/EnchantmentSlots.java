package cn.superiormc.enchantmentslots;

import cn.superiormc.enchantmentslots.commands.MainCommand;
import cn.superiormc.enchantmentslots.commands.MainTab;
import cn.superiormc.enchantmentslots.events.PlayerClick;
import cn.superiormc.enchantmentslots.events.PlayerEnchant;
import cn.superiormc.enchantmentslots.events.PlayerInventory;
import cn.superiormc.enchantmentslots.packet.*;
import cn.superiormc.enchantmentslots.utils.ConfigReader;
import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class EnchantmentSlots extends JavaPlugin {

    public static JavaPlugin instance;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        registerEvents();
        registerCommands();
        registerPackets();
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §fPlugin is disabled. Author: PQguanfang.");
    }

    private void registerEvents() {
        if (ConfigReader.getEnchantItemTrigger()) {
            Bukkit.getPluginManager().registerEvents(new PlayerEnchant(), this);
        }
        if (ConfigReader.getInventoryClickTrigger()) {
            Bukkit.getPluginManager().registerEvents(new PlayerClick(), this);
        }
        Bukkit.getPluginManager().registerEvents(new PlayerInventory(), this);
    }

    public void registerCommands() {
        Objects.requireNonNull(Bukkit.getPluginCommand("enchantmentslots")).setExecutor(new MainCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("enchantmentslots")).setTabCompleter(new MainTab());
    }

    public void registerPackets() {
        if (ConfigReader.getRegisterRemoveLore()) {
            new SetCreativeSlots();
            new WindowClick();
        }
        new SetSlots();
        new WindowItem();
    }
}
