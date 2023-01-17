package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.ReplacementConditionValues;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.logging.Level;

import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

public class ReplacementEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public ReplacementEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler
    public void handleReplacement (PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            int heldItemSlot = event.getPlayer().getInventory().getHeldItemSlot();
            boolean isMainHand = event.getHand() == EquipmentSlot.HAND;

            // Delay replacing by 3 ticks to give all other handlers time to do their thing. Especially
            // important for wands.
            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                ItemStack item = isMainHand ? event.getPlayer().getInventory().getItem(heldItemSlot) : event.getPlayer().getInventory().getItemInOffHand();
                CustomItemValues custom = itemSet.getItem(item);

                if (custom != null) {
                    List<ReplacementConditionValues> conditions = custom.getReplacementConditions();
                    ReplacementConditionValues.ConditionOperation op = custom.getConditionOp();
                    boolean replace = false;
                    boolean firstCond = true;
                    Player player = event.getPlayer();
                    int replaceIndex = -1;
                    boolean[] trueConditions = new boolean[conditions.size()];

                    for (ReplacementConditionValues cond : conditions) {
                        replaceIndex++;
                        if (op == ReplacementConditionValues.ConditionOperation.AND) {
                            if (replace || firstCond) {
                                replace = checkCondition(cond, player);
                            }

                            firstCond = false;
                        } else if (op == ReplacementConditionValues.ConditionOperation.OR) {
                            if (!replace || firstCond) {
                                replace = checkCondition(cond, player);
                            }

                            firstCond = false;
                        } else {
                            if (!replace || firstCond) {
                                replace = checkCondition(cond, player);
                            }

                            firstCond = false;
                        }

                        trueConditions[replaceIndex] = replace;
                    }

                    for (boolean bool : trueConditions) {
                        if (bool) {
                            replace = true;
                            break;
                        }
                    }

                    if (replace) {
                        switch (op) {
                            case AND:
                                CustomItemValues replaceItem;
                                for (ReplacementConditionValues condition : conditions) {
                                    replaceItems(condition, player);
                                }

                                replaceItem = conditions.get(replaceIndex).getReplaceItem();

                                boolean replaceSelf = false;
                                for (ReplacementConditionValues condition : conditions) {
                                    if (condition.getItem().getName().equals(custom.getName())) {
                                        replaceSelf = true;
                                        break;
                                    }
                                }

                                if (!replaceSelf) {
                                    item.setAmount(item.getAmount() - 1);
                                }

                                if (replaceItem != null) {
                                    ItemStack stack = wrap(replaceItem).create(1);
                                    if (item.getAmount() <= 0) {
                                        if (isMainHand) {
                                            player.getInventory().setItem(heldItemSlot, stack);
                                        } else {
                                            player.getInventory().setItemInOffHand(stack);
                                        }
                                    } else {
                                        ItemUtils.giveCustomItem(itemSet, player, replaceItem);
                                    }
                                } else {
                                    Bukkit.getLogger().log(Level.WARNING, "The item: " + custom.getDisplayName() + " tried to replace itself with nothing. This indicates an error during exporting or a bug in the plugin.");
                                }

                                break;
                            case OR:
                                for (int index = 0; index < conditions.size(); index++) {
                                    if (trueConditions[index]) replaceIndex = index;
                                }

                                if (conditions.get(replaceIndex).getCondition() == ReplacementConditionValues.ReplacementCondition.HASITEM) {
                                    replaceItems(conditions.get(replaceIndex), player);
                                }

                                if (!conditions.get(replaceIndex).getItem().getName().equals(custom.getName()))
                                    item.setAmount(item.getAmount() - 1);

                                replaceItem = conditions.get(replaceIndex).getReplaceItem();
                                if (replaceItem != null) {
                                    ItemStack stack = wrap(replaceItem).create(1);
                                    if (item.getAmount() <= 0) {
                                        if (isMainHand) {
                                            player.getInventory().setItem(heldItemSlot, stack);
                                        } else {
                                            player.getInventory().setItemInOffHand(stack);
                                        }
                                    } else {
                                        ItemUtils.giveCustomItem(itemSet, player, replaceItem);
                                    }
                                } else {
                                    Bukkit.getLogger().log(Level.WARNING, "The item: " + custom.getDisplayName() + " tried to replace itself with nothing. This indicates an error during exporting or a bug in the plugin.");
                                }
                                break;
                            case NONE:
                                for (int index = 0; index < conditions.size(); index++) {
                                    if (trueConditions[index]) {
                                        replaceIndex = index;
                                        break;
                                    }
                                }

                                if (conditions.get(replaceIndex).getCondition() == ReplacementConditionValues.ReplacementCondition.HASITEM) {
                                    replaceItems(conditions.get(replaceIndex), player);
                                }

                                if (!conditions.get(replaceIndex).getItem().getName().equals(custom.getName()))
                                    item.setAmount(item.getAmount() - 1);

                                replaceItem = conditions.get(replaceIndex).getReplaceItem();
                                if (replaceItem != null) {
                                    ItemStack stack = wrap(replaceItem).create(1);
                                    if (item.getAmount() <= 0) {
                                        if (isMainHand) {
                                            player.getInventory().setItem(heldItemSlot, stack);
                                        } else {
                                            player.getInventory().setItemInOffHand(stack);
                                        }
                                    } else {
                                        ItemUtils.giveCustomItem(itemSet, player, replaceItem);
                                    }
                                } else {
                                    Bukkit.getLogger().log(Level.WARNING, "The item: " + custom.getDisplayName() + " tried to replace itself with nothing. This indicates an error during exporting or a bug in the plugin.");
                                }

                                break;
                            default:
                                break;

                        }
                    }
                }
            }, 3L);
        }
    }

    private boolean checkCondition(ReplacementConditionValues cond, Player player) {
        int counted = 0;
        for (ItemStack stack : player.getInventory()) {
            CustomItemValues inventoryItem = itemSet.getItem(stack);
            if (inventoryItem != null) {
                switch(cond.getCondition()) {
                    case HASITEM:
                        if (inventoryItem.getName().equals(cond.getItem().getName())) {
                            counted += stack.getAmount();
                        }
                        break;
                    case MISSINGITEM:
                        if (inventoryItem.getName().equals(cond.getItem().getName())) {
                            return false;
                        }

                        break;
                    case ISBROKEN:
                        break;
                    default:
                        break;

                }
            }
        }

        if (cond.getCondition() == ReplacementConditionValues.ReplacementCondition.MISSINGITEM) {
            return true;
        }

        if (cond.getCondition() == ReplacementConditionValues.ReplacementCondition.HASITEM) {
            switch (cond.getOperation()) {
                case ATMOST:
                    return counted <= cond.getValue();
                case ATLEAST:
                    return counted >= cond.getValue();
                case EXACTLY:
                    return counted == cond.getValue();
                case NONE:
                    return counted > 0;
                default:
                    break;
            }
        }

        return false;
    }

    public static String checkBrokenCondition(List<ReplacementConditionValues> conditions) {
        for (ReplacementConditionValues cond : conditions) {
            if (cond.getCondition() == ReplacementConditionValues.ReplacementCondition.ISBROKEN) {
                return cond.getReplaceItem().getName();
            }
        }

        return null;
    }

    private void replaceItems(ReplacementConditionValues condition, Player player) {
        if (condition.getCondition() == ReplacementConditionValues.ReplacementCondition.HASITEM) {
            int conditionValue = condition.getValue();
            if (condition.getOperation() == ReplacementConditionValues.ReplacementOperation.NONE) {
                conditionValue = 1;
            }

            for (ItemStack stack : player.getInventory()) {
                CustomItemValues inventoryItem = itemSet.getItem(stack);
                if (inventoryItem != null && inventoryItem.getName().equals(condition.getItem().getName())) {
                    if (condition.getOperation() == ReplacementConditionValues.ReplacementOperation.ATLEAST ||
                            condition.getOperation() == ReplacementConditionValues.ReplacementOperation.NONE) {
                        if (stack.getAmount() < conditionValue) {
                            conditionValue -= stack.getAmount();
                            stack.setAmount(0);
                        } else {
                            stack.setAmount(stack.getAmount() - conditionValue);
                            conditionValue = 0;
                        }
                    } else if (condition.getOperation() == ReplacementConditionValues.ReplacementOperation.ATMOST
                            || condition.getOperation() == ReplacementConditionValues.ReplacementOperation.EXACTLY) {
                        stack.setAmount(0);
                    }
                }
            }
        }
    }

}
