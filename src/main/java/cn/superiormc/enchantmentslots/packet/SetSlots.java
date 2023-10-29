package cn.superiormc.enchantmentslots.packet;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.utils.ConfigReader;
import cn.superiormc.enchantmentslots.utils.ItemLimits;
import cn.superiormc.enchantmentslots.utils.ItemModify;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetSlots extends GeneralPackets{
    public SetSlots() {
        super();
    }
    @Override
    protected void initPacketAdapter(){
        packetAdapter = new PacketAdapter(EnchantmentSlots.instance, ListenerPriority.MONITOR, PacketType.Play.Server.SET_SLOT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (ConfigReader.getDebug()) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §f" +
                            "Found SetSlots packet.");
                }
                if (event.getPlayer() == null) {
                    return;
                }
                if (ConfigReader.getBlackCreativeMode() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    return;
                }
                PacketContainer packet = event.getPacket();
                StructureModifier<ItemStack> itemStackStructureModifier = packet.getItemModifier();
                ItemStack serverItemStack = itemStackStructureModifier.read(0);
                if (serverItemStack.getType().isAir()) {
                    return;
                }
                int maxEnchantments = ItemLimits.getMaxEnchantments(event.getPlayer(), serverItemStack);
                if (packet.getIntegers().read(0) == 0 && serverItemStack.getEnchantments().size() > 0 &&
                        serverItemStack.getEnchantments().size() >= maxEnchantments) {
                    if (ConfigReader.getRemoveExtraEnchants() && serverItemStack.hasItemMeta()) {
                        int removeAmount = serverItemStack.getEnchantments().size() - maxEnchantments;
                        for (Enchantment enchant : serverItemStack.getEnchantments().keySet()) {
                            if (removeAmount <= 0) {
                                break;
                            }
                            ItemMeta meta = serverItemStack.getItemMeta();
                            meta.removeEnchant(enchant);
                            serverItemStack.setItemMeta(meta);
                            removeAmount--;
                        }
                    }
                }
                ItemModify.addLore(event.getPlayer(), serverItemStack, true);
                ItemStack clientItemStack = ItemModify.serverToClient(event.getPlayer(), serverItemStack);
                // client 是加过 Lore 的，server 是没加过的！
                itemStackStructureModifier.write(0, clientItemStack);
            }
        };
    }
}
