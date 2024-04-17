package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.ReplacementConditionEntry;
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
                KciItem custom = itemSet.getItem(item);

                if (custom != null) {
                    List<ReplacementConditionEntry> conditions = custom.getReplacementConditions();
                    ReplacementConditionEntry.ConditionOperation op = custom.getConditionOp();
                    boolean replace = false;
                    boolean firstCond = true;
                    Player player = event.getPlayer();
                    int replaceIndex = -1;
                    boolean[] trueConditions = new boolean[conditions.size()];

                    for (ReplacementConditionEntry cond : conditions) {
                        replaceIndex++;
                        if (op == ReplacementConditionEntry.ConditionOperation.AND) {
                            if (replace || firstCond) {
                                replace = checkCondition(cond, player);
                            }

                            firstCond = false;
                        } else if (op == ReplacementConditionEntry.ConditionOperation.OR) {
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
                                KciItem replaceItem;
                                for (ReplacementConditionEntry condition : conditions) {
                                    replaceItems(condition, player);
                                }

                                replaceItem = conditions.get(replaceIndex).getReplaceItem();

                                boolean replaceSelf = false;
                                for (ReplacementConditionEntry condition : conditions) {
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

                                if (conditions.get(replaceIndex).getCondition() == ReplacementConditionEntry.ReplacementCondition.HASITEM) {
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

                                if (conditions.get(replaceIndex).getCondition() == ReplacementConditionEntry.ReplacementCondition.HASITEM) {
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

    private boolean checkCondition(ReplacementConditionEntry cond, Player player) {
        int counted = 0;
        for (ItemStack stack : player.getInventory()) {
            KciItem inventoryItem = itemSet.getItem(stack);
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

        if (cond.getCondition() == ReplacementConditionEntry.ReplacementCondition.MISSINGITEM) {
            return true;
        }

        if (cond.getCondition() == ReplacementConditionEntry.ReplacementCondition.HASITEM) {
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

    public static String checkBrokenCondition(List<ReplacementConditionEntry> conditions) {
        for (ReplacementConditionEntry cond : conditions) {
            if (cond.getCondition() == ReplacementConditionEntry.ReplacementCondition.ISBROKEN) {
                return cond.getReplaceItem().getName();
            }
        }

        return null;
    }

    private void replaceItems(ReplacementConditionEntry condition, Player player) {
        if (condition.getCondition() == ReplacementConditionEntry.ReplacementCondition.HASITEM) {
            int conditionValue = condition.getValue();
            if (condition.getOperation() == ReplacementConditionEntry.ReplacementOperation.NONE) {
                conditionValue = 1;
            }

            for (ItemStack stack : player.getInventory()) {
                KciItem inventoryItem = itemSet.getItem(stack);
                if (inventoryItem != null && inventoryItem.getName().equals(condition.getItem().getName())) {
                    if (condition.getOperation() == ReplacementConditionEntry.ReplacementOperation.ATLEAST ||
                            condition.getOperation() == ReplacementConditionEntry.ReplacementOperation.NONE) {
                        if (stack.getAmount() < conditionValue) {
                            conditionValue -= stack.getAmount();
                            stack.setAmount(0);
                        } else {
                            stack.setAmount(stack.getAmount() - conditionValue);
                            conditionValue = 0;
                        }
                    } else if (condition.getOperation() == ReplacementConditionEntry.ReplacementOperation.ATMOST
                            || condition.getOperation() == ReplacementConditionEntry.ReplacementOperation.EXACTLY) {
                        stack.setAmount(0);
                    }
                }
            }
        }
    }

}
