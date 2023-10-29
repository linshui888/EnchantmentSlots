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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetCreativeSlots extends GeneralPackets{

    // 客户端发给服务端
    public SetCreativeSlots() {
        super();
    }

    @Override
    protected void initPacketAdapter() {
        packetAdapter = new PacketAdapter(EnchantmentSlots.instance, ListenerPriority.LOWEST, PacketType.Play.Client.SET_CREATIVE_SLOT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (ConfigReader.getDebug()) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §f" +
                            "Found SetCreativeSlots packet.");
                }
                PacketContainer packet = event.getPacket();
                StructureModifier<ItemStack> itemStackStructureModifier = packet.getItemModifier();
                ItemStack clientItemStack = itemStackStructureModifier.read(0);
                if (clientItemStack.getType().isAir()) {
                    return;
                }
                int maxEnchantments = ItemLimits.getMaxEnchantments(event.getPlayer(), clientItemStack);
                if (clientItemStack.getEnchantments().size() > 0 &&
                        clientItemStack.getEnchantments().size() >= maxEnchantments) {
                    if (ConfigReader.getRemoveExtraEnchants()) {
                        int removeAmount = clientItemStack.getEnchantments().size() - maxEnchantments;
                        for (Enchantment enchant : clientItemStack.getEnchantments().keySet()) {
                            if (removeAmount <= 0) {
                                break;
                            }
                            ItemMeta meta = clientItemStack.getItemMeta();
                            meta.removeEnchant(enchant);
                            clientItemStack.setItemMeta(meta);
                            removeAmount--;
                        }
                    }
                }
                ItemStack serverItemStack = ItemModify.clientToServer(event.getPlayer(), clientItemStack);
                itemStackStructureModifier.write(0, serverItemStack);
            }
        };
    }
}
