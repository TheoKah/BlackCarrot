package com.carrot.blackcarrot.listener;

import java.util.Optional;

import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.carrot.blackcarrot.data.BlackData;
import com.carrot.blackcarrot.data.Item;

//- usage -> action blocked if rightclicking with this item in hand
//- possession -> item confiscated if entering inventory
//- crafting -> remove the recipe to craft the item
//- place -> prevent item from being placed in the world
//- break -> prevent item from being breaked
//- drop -> prevent item from being dropped in the world

public class BlackEventListener
{

	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onPlayerInteract(InteractEvent event, @First Player player)
	{
		Optional<ItemStack> mainItem = player.getItemInHand(HandTypes.MAIN_HAND);
		Optional<ItemStack> offItem = player.getItemInHand(HandTypes.OFF_HAND);

		Optional<Item> mainRule = Optional.empty();
		Optional<Item> offRule = Optional.empty();

		if (mainItem.isPresent()) {
			mainRule = BlackData.match(mainItem.get(), player.getWorld().getUniqueId());
		}
		if (offItem.isPresent()) {
			offRule = BlackData.match(offItem.get(), player.getWorld().getUniqueId());
		}

		if (mainRule.isPresent()) {
			if (mainRule.get().getPerm(BlackData.Type.POSSESS) && !player.hasPermission("blackcarrot.bypass.possess." + mainItem.get().getType().getId())) {
				for (Optional<ItemStack> taken = player.getInventory().query(mainItem.get()).poll(); taken.isPresent() ; taken = player.getInventory().query(mainItem.get()).poll()) {
					player.sendMessage(Text.of(TextColors.YELLOW, taken.get().getTranslation().get(), " x", taken.get().getQuantity(), TextColors.RED, " have been removed from your inventory"));
				}
				event.setCancelled(true);
			}
		}

		if (offRule.isPresent()) {
			if (offRule.get().getPerm(BlackData.Type.POSSESS) && !player.hasPermission("blackcarrot.bypass.possess." + offItem.get().getType().getId())) {
				for (Optional<ItemStack> taken = player.getInventory().query(offItem.get()).poll(); taken.isPresent() ; taken = player.getInventory().query(offItem.get()).poll()) {
					player.sendMessage(Text.of(TextColors.YELLOW, taken.get().getTranslation().get(), " x", taken.get().getQuantity(), TextColors.RED, " have been removed from your inventory"));
				}
				event.setCancelled(true);
			}
		}

		if (!event.isCancelled()) {
			if (mainRule.isPresent()) {
				if (mainRule.get().getPerm(BlackData.Type.USE) && !player.hasPermission("blackcarrot.bypass.use." + mainItem.get().getType().getId())) {
					player.sendMessage(Text.of(TextColors.RED, "You cannot use ", TextColors.YELLOW, mainItem.get().getTranslation().get()));
					event.setCancelled(true);
				}
			}
			if (offRule.isPresent()) {
				if (offRule.get().getPerm(BlackData.Type.USE) && !player.hasPermission("blackcarrot.bypass.use." + offItem.get().getType().getId())) {
					player.sendMessage(Text.of(TextColors.RED, "You cannot use ", TextColors.YELLOW, offItem.get().getTranslation().get()));
					event.setCancelled(true);
				}
			}
		}
	}

	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event, @First Player player)
	{
		for (Inventory item : player.getInventory().slots()) {
			if (item.peek().isPresent()) {
				if (BlackData.match(item.peek().get(), player.getWorld().getUniqueId(), BlackData.Type.POSSESS).isPresent()) {
					if (!player.hasPermission("blackcarrot.bypass.possess." + item.peek().get().getType().getId())) {
						for (Optional<ItemStack> taken = player.getInventory().query(item.peek().get()).poll(); taken.isPresent() ; taken = player.getInventory().query(item.peek().get()).poll()) {
							player.sendMessage(Text.of(TextColors.YELLOW, taken.get().getTranslation().get(), " x", taken.get().getQuantity(), TextColors.RED, " have been removed from your inventory"));
						}
					}
				}
			}
		}
	}

	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onGetItem(ChangeInventoryEvent event, @First Player player)
	{
		for (Transaction<ItemStackSnapshot> tr : event.getTransactions()) {
			if (BlackData.match(tr.getDefault(), player.getWorld().getUniqueId(), BlackData.Type.POSSESS).isPresent() && !player.hasPermission("blackcarrot.bypass.possess." + tr.getDefault().getType().getId())) {
				ItemStack stack = tr.getDefault().createStack();
				for (Optional<ItemStack> taken = player.getInventory().query(stack).poll(); taken.isPresent() ; taken = player.getInventory().query(stack).poll()) {
					player.sendMessage(Text.of(TextColors.YELLOW, taken.get().getTranslation().get(), " x", taken.get().getQuantity(), TextColors.RED, " have been removed from your inventory"));
				}
			}
		}
	}

}
