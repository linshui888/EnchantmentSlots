package cn.superiormc.enchantmentslots.utils;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class Condition {

    public static boolean getBoolean(Player player, List<String> condition) {
        boolean conditionTrueOrFasle = true;
        for (String singleCondition : condition){
            if (singleCondition.startsWith("none")){
                return true;
            } else if (singleCondition.startsWith("world: "))
            {
                int i = 0;
                for (String str : singleCondition.substring(7).split(";;")){
                    if (str.equals(player.getWorld().getName())){
                        break;
                    }
                    i ++;
                }
                if (i == singleCondition.substring(7).split(";;").length){
                    conditionTrueOrFasle = false;
                    break;
                }
            } else if (singleCondition.startsWith("permission: ") && player != null)
            {
                for (String str : singleCondition.substring(12).split(";;")){
                    if(!player.hasPermission(str)){
                        conditionTrueOrFasle = false;
                        break;
                    }
                }
            } else if (EnchantmentSlots.instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && singleCondition.startsWith("placeholder: ") &&
            player != null) {
                try {
                    if (singleCondition.split(";;").length == 3) {
                        String[] conditionString = singleCondition.substring(13).split(";;");
                        String placeholder = conditionString[0];
                        String conditionValue = conditionString[1];
                        String value = conditionString[2];
                        if (conditionValue.equals("!=")) {
                            placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                            if (placeholder.equals(value)) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("==")){
                            placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                            if(!placeholder.equals(value)){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("!*=")) {
                            placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                            if (placeholder.contains(value)) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("*=")){
                            placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                            if(!placeholder.contains(value)){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals(">=")){
                            placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                            if(!(Double.parseDouble(placeholder) >= Double.parseDouble(value))){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals(">")){
                            placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                            if (!(Double.parseDouble(placeholder) > Double.parseDouble(value))){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("<=")){
                            placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                            if (!(Double.parseDouble(placeholder) <= Double.parseDouble(value))){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if(conditionValue.equals("<")){
                            placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                            if (!(Double.parseDouble(placeholder) < Double.parseDouble(value))){
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("=")) {
                            placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                            if (!(Double.parseDouble(placeholder) == Double.parseDouble(value))) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                    }
                    else {
                        return false;
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    return false;
                }
            }
        }
        return conditionTrueOrFasle;
    }
}
