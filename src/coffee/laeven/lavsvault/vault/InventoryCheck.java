package coffee.laeven.lavsvault.vault;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import coffee.laeven.lavsvault.utils.Logg;
import coffee.laeven.lavsvault.utils.SoundUtils;
import coffee.laeven.lavsvault.utils.data.DataUtils;

public class InventoryCheck implements Listener
{
	/**
	 * Prevents a player looting a vaulted item
	 */
	@EventHandler
	public void onDragCheck(InventoryDragEvent e)
	{
		if(e.getRawSlots().size() == 1)
		{
			Bukkit.getPluginManager().callEvent(new InventoryClickEvent(e.getView(),null,e.getRawSlots().iterator().next(),e.getType() == DragType.SINGLE ? ClickType.RIGHT : ClickType.LEFT,null));
			return;
		}
		
		if(e.getInventory().getType() == InventoryType.PLAYER) { return; }
		
		if(VaultCtrl.isVaultedCannotObtain(e.getCursor()))
		{
			SoundUtils.VAULTED_ITEM.play((Player) e.getWhoClicked());
			e.setCancelled(true);
		}
		
		for(ItemStack stack : e.getNewItems().values())
		{
			if(!VaultCtrl.isVaultedCannotObtain(stack)) { continue; }
			SoundUtils.VAULTED_ITEM.play((Player) e.getWhoClicked());
			e.setCancelled(true);
			return;
		}
	}
	
	/**
	 * Prevents a player looting a vaulted item
	 */
	@EventHandler
	public void onSlotCheck(InventoryClickEvent e)
	{
		Logg.verb("Slot " + e.getSlot() + " raw " + e.getRawSlot(),Logg.VerbGroup.INVENTORY_CHECK);
		// The raw slot displays as -999 when clicking outside the inventory, don't ask me why
		// Raw slot displays -1 when clicking on the edge of an inventory...
		if(e.getRawSlot() == -999 || e.getRawSlot() == -1) { return; }
		
		// W H A T   I S   T H I S???
		if(e.getAction().equals(InventoryAction.NOTHING)) { return; }
		
		if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT)
		{
			// If this is a player inventory and nothing else, allow shift clicking
			if(e.getView().getTopInventory().getType() == InventoryType.CRAFTING && e.getView().getBottomInventory().getType() == InventoryType.PLAYER) { return; }
			
			if(VaultCtrl.isVaultedCannotObtain(e.getView().getItem(e.getRawSlot())))
			{
				SoundUtils.VAULTED_ITEM.play((Player) e.getWhoClicked());
				e.setCancelled(true);
			}
			
			return;
		}
		
		if(e.getClickedInventory().getType() == InventoryType.PLAYER)
		{
			if(VaultCtrl.isVaultedCannotObtain(e.getCursor()))
			{
				SoundUtils.VAULTED_ITEM.play((Player) e.getWhoClicked());
				DataUtils.set("drop_bypass",1,e.getCursor());
				e.getWhoClicked().dropItem(e.getCursor());
				e.getWhoClicked().setItemOnCursor(null);
				e.setCancelled(true);
				return;
			}
			
			if(VaultCtrl.isVaultedCannotObtain(e.getCurrentItem()))
			{
				SoundUtils.VAULTED_ITEM.play((Player) e.getWhoClicked());
				DataUtils.set("drop_bypass",1,e.getCurrentItem());
				e.getWhoClicked().dropItem(e.getCurrentItem());
				e.setCurrentItem(null);
				e.setCancelled(true);
			}
			
			return;
		}
		
		if(VaultCtrl.isVaultedCannotObtain(e.getCursor()) || VaultCtrl.isVaultedCannotObtain(e.getView().getItem(e.getRawSlot())))
		{
			SoundUtils.VAULTED_ITEM.play((Player) e.getWhoClicked());
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents a player using a vaulted item
	 */
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		if(VaultCtrl.isVaultedCannotUse(e.getItem()))
		{
			SoundUtils.VAULTED_ITEM.play(e.getPlayer());
			e.setCancelled(true);
			return;
		}
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && VaultCtrl.isVaultedCannotUse(e.getClickedBlock().getType()))
		{
			SoundUtils.VAULTED_ITEM.play(e.getPlayer());
			e.setCancelled(true);
			return;
		}
	}
	
	/**
	 * Prevents a player selecting a villager trade with vaulted items
	 */
	@EventHandler
	public void onTradeSelect(TradeSelectEvent e)
	{
		MerchantRecipe recipe = e.getMerchant().getRecipe(e.getIndex());
		
		for(ItemStack stack : recipe.getIngredients())
		{
			if(!VaultCtrl.isVaultedCannotObtain(stack)) { continue; }
			SoundUtils.VAULTED_ITEM.play((Player) e.getWhoClicked());
			e.setCancelled(true);
			e.setResult(Result.DENY);
			return;
		}
		
		if(VaultCtrl.isVaultedCannotObtain(recipe.getResult()))
		{
			SoundUtils.VAULTED_ITEM.play((Player) e.getWhoClicked());
			e.setCancelled(true);
			e.setResult(Result.DENY);
		}
	}
}
