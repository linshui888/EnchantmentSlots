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

import java.util.ArrayList;
import java.util.List;

// 服务端发给客户端
public class WindowItem extends GeneralPackets{

    public WindowItem() {
        super();
    }

    @Override
    protected void initPacketAdapter() {
        packetAdapter = new PacketAdapter(EnchantmentSlots.instance, ListenerPriority.MONITOR, PacketType.Play.Server.WINDOW_ITEMS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (ConfigReader.getDebug()) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §f" +
                            "Found WindowsItem packet.");
                }
                if (event.getPlayer() == null) {
                    return;
                }
                if (ConfigReader.getBlackCreativeMode() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    return;
                }
                PacketContainer packet = event.getPacket();
                StructureModifier<List<ItemStack>> itemStackStructureModifier = packet.getItemListModifier();
                List<ItemStack> serverItemStack = itemStackStructureModifier.read(0);
                List<ItemStack> clientItemStack = new ArrayList<>();
                for (ItemStack itemStack : serverItemStack) {
                    if (itemStack.getType().isAir()) {
                        clientItemStack.add(itemStack);
                        continue;
                    }
                    int maxEnchantments = ItemLimits.getMaxEnchantments(event.getPlayer(), itemStack);
                    if (itemStack.getEnchantments().size() > 0 &&
                            itemStack.getEnchantments().size() >= maxEnchantments) {
                        if (ConfigReader.getRemoveExtraEnchants()) {
                            int removeAmount = itemStack.getEnchantments().size() - maxEnchantments;
                            for (Enchantment enchant : itemStack.getEnchantments().keySet()) {
                                if (removeAmount <= 0) {
                                    break;
                                }
                                ItemMeta meta = itemStack.getItemMeta();
                                meta.removeEnchant(enchant);
                                itemStack.setItemMeta(meta);
                                removeAmount--;
                            }
                        }
                    }
                    ItemModify.addLore(event.getPlayer(), itemStack, true);
                    clientItemStack.add(ItemModify.serverToClient(event.getPlayer(), itemStack));
                }
                // client 是加过 Lore 的，server 是没加过的！
                itemStackStructureModifier.write(0, clientItemStack);
            }
        };
    }
}
