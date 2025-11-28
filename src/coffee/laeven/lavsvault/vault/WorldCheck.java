package coffee.laeven.lavsvault.vault;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BrewingStartEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import coffee.laeven.lavsvault.utils.DelayUtils;
import coffee.laeven.lavsvault.utils.Logg;
import coffee.laeven.lavsvault.utils.SoundUtils;
import coffee.laeven.lavsvault.utils.data.DataUtils;

public class WorldCheck implements Listener
{
	/**
	 * Prevents a player holding onto vaulted items
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		VaultCtrl.scanPlayerInventory(e.getPlayer());
	}
	
	/**
	 * Prevents a player dropping vaulted items
	 */
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e)
	{
		if(DataUtils.has("drop_bypass",e.getItemDrop().getItemStack()))
		{
			e.getItemDrop().setWillAge(false);
			DataUtils.remove("drop_bypass",e.getItemDrop().getItemStack());
			return;
		}
		
		if(VaultCtrl.isVaultedCannotObtain(e.getItemDrop().getItemStack()))
		{
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents a block dropping vaulted items
	 */
	@EventHandler
	public void onBlockDrop(BlockDropItemEvent e)
	{
		for(Iterator<Item> it = e.getItems().iterator(); it.hasNext();)
		{
			Item item = it.next();
			Logg.debug("Item " + item.getItemStack().getType());
			
			if(DataUtils.has("drop_bypass",item.getItemStack()))
			{
				item.setWillAge(false);
				DataUtils.remove("drop_bypass",item.getItemStack());
				continue;
			}
			
			if(VaultCtrl.isVaultedCannotObtain(item.getItemStack()))
			{
				it.remove();
			}
		}
	}
	
	/**
	 * Prevents an entity dropping vaulted items
	 */
	@EventHandler
	public void onEntityDrop(EntityDropItemEvent e)
	{
		if(DataUtils.has("drop_bypass",e.getItemDrop().getItemStack()))
		{
			e.getItemDrop().setWillAge(false);
			DataUtils.remove("drop_bypass",e.getItemDrop().getItemStack());
			return;
		}
		if(VaultCtrl.isVaultedCannotObtain(e.getItemDrop().getItemStack()))
		{
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents items spawning that are of vaulted items
	 */
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e)
	{
		if(DataUtils.has("drop_bypass",e.getEntity().getItemStack()))
		{
			e.getEntity().setWillAge(false);
			DataUtils.remove("drop_bypass",e.getEntity().getItemStack());
			return;
		}
		
		if(VaultCtrl.isVaultedCannotObtain(e.getEntity().getItemStack()))
		{
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents an entity dropping vaulted items on death
	 */
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e)
	{
		for(Iterator<ItemStack> it = e.getDrops().iterator(); it.hasNext();)
		{
			ItemStack stack = it.next();
			
			if(VaultCtrl.isVaultedCannotObtain(stack))
			{
				it.remove();
			}
		}
	}
	
	/**
	 * Prevents a player placing a vaulted block
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		if(VaultCtrl.isVaultedCannotUse(e.getBlock().getType()))
		{
			SoundUtils.VAULTED_ITEM.play(e.getPlayer());
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents a block dispensing vaulted items (e.g a dispenser)
	 */
	@EventHandler
	public void onBlockDispense(BlockDispenseEvent e)
	{
		if(VaultCtrl.isVaultedCannotObtain(e.getItem()))
		{
			SoundUtils.VAULTED_ITEM.play(e.getBlock().getLocation());
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents a block dispensing vaulted items from a loot table (e.g a vault in a trial chamber)
	 */
	@EventHandler
	public void onBlockDispenseLoot(BlockDispenseLootEvent e)
	{
		for(Iterator<ItemStack> it = e.getDispensedLoot().iterator(); it.hasNext();)
		{
			ItemStack stack = it.next();
			
			if(VaultCtrl.isVaultedCannotObtain(stack))
			{
				it.remove();
			}
		}
	}
	
	/**
	 * Prevents a block dispensing vaulted equipment items
	 */
	@EventHandler
	public void onBlockDispenseArmour(BlockDispenseArmorEvent e)
	{
		if(VaultCtrl.isVaultedCannotObtain(e.getItem()))
		{
			SoundUtils.VAULTED_ITEM.play(e.getBlock().getLocation());
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents a crafter crafting a recipe that results in an item that is vaulted
	 */
	@EventHandler
	public void onCrafterCraft(CrafterCraftEvent e)
	{
		if(VaultCtrl.isVaultedCannotObtain(e.getResult()))
		{
			SoundUtils.VAULTED_ITEM.play(e.getBlock().getLocation());
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents a crafter crafting a recipe that results in an item that is vaulted
	 */
	@EventHandler
	public void onCraft(CraftItemEvent e)
	{
		if(VaultCtrl.isVaultedCannotObtain(e.getRecipe().getResult()))
		{
			SoundUtils.VAULTED_ITEM.play((Player) e.getWhoClicked());
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents player consuming a vaulted item
	 */
	@EventHandler
	public void onItemConsume(PlayerItemConsumeEvent e)
	{
		if(VaultCtrl.isVaultedCannotUse(e.getItem()))
		{
			SoundUtils.VAULTED_ITEM.play(e.getPlayer());
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents a player picking up a vaulted item
	 */
	@EventHandler
	public void onPickup(EntityPickupItemEvent e)
	{
		if(!(e.getEntity() instanceof Player)) { return; }
		
		if(VaultCtrl.isVaultedCannotObtain(e.getItem().getItemStack()))
		{
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents a player using a vaulted item to break a block
	 */
	@EventHandler
	public void onBreakBlock(BlockBreakEvent e)
	{
		if(VaultCtrl.isVaultedCannotUse(e.getPlayer().getInventory().getItemInMainHand()))
		{
			SoundUtils.VAULTED_ITEM.play(e.getPlayer());
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents a player fishing for vaulted items
	 */
	@EventHandler
	public void onPlayerFish(PlayerFishEvent e)
	{
		if(VaultCtrl.isVaultedCannotUse(e.getPlayer().getInventory().getItemInMainHand()))
		{
			SoundUtils.VAULTED_ITEM.play(e.getPlayer());
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents a player launching a vaulted item
	 */
	@EventHandler
	public void onPlayerFish(ProjectileLaunchEvent e)
	{
		if(e.getEntity().getShooter() instanceof Player attacker)
		{ 
			if(VaultCtrl.isVaultedCannotUse(attacker.getInventory().getItemInMainHand()) || VaultCtrl.isVaultedCannotUse(attacker.getInventory().getItemInOffHand()))
			{
				SoundUtils.VAULTED_ITEM.play(attacker);
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e)
	{
		if(e.getEntity() instanceof Player shooter)
		{ 
			if(VaultCtrl.isVaultedCannotUse(shooter.getInventory().getItemInMainHand()) || VaultCtrl.isVaultedCannotUse(shooter.getInventory().getItemInOffHand()))
			{
				SoundUtils.VAULTED_ITEM.play(shooter);
				e.setCancelled(true);
			}
			
			if(VaultCtrl.isVaultedCannotUseTippedArrow(e.getConsumable()))
			{
				DataUtils.set("drop_bypass",1,e.getConsumable());
				shooter.getWorld().dropItemNaturally(shooter.getLocation(),e.getConsumable());
				SoundUtils.VAULTED_ITEM.play(shooter);
				e.setCancelled(true);
			}
		}
	}
	
	/**
	 * Prevents a player attempting to enchant any equipment
	 */
	@EventHandler
	public void onPrepEnchant(PrepareItemEnchantEvent e)
	{
		if(!VaultCtrl.isVaultedEnchants()) { return; }
		SoundUtils.VAULTED_ITEM.play(e.getEnchanter());
		e.setCancelled(true);
	}
	
	/**
	 * Prevents a player enchanting any equipment
	 */
	@EventHandler
	public void onEnchant(EnchantItemEvent e)
	{
		if(VaultCtrl.isVaultedEnchants())
		{
			SoundUtils.VAULTED_ITEM.play(e.getEnchanter());
			e.setCancelled(true);
			return;
		}
		
		// Removes vaulted enchantments from appearing on enchantables
		for(Iterator<Entry<Enchantment, Integer>> it = e.getEnchantsToAdd().entrySet().iterator(); it.hasNext();)
		{
			Entry<Enchantment,Integer> ench = it.next();
			
			if(VaultCtrl.isVaultedCannotObtainEnchant(ench.getKey()))
			{
				it.remove();
			}
		}
	}
	
	/**
	 * Prevents a player using an anvil to create a vaulted enchanted item
	 */
	@EventHandler
	public void onEnchant(PrepareAnvilEvent e)
	{
		if(VaultCtrl.isVaultedCannotObtain(e.getResult()))
		{
			e.setResult(null);
			e.getViewers().forEach(v -> 
			{
				v.closeInventory(Reason.CANT_USE);
				SoundUtils.VAULTED_ITEM.play((Player) v);
			});
		}
	}
	
	/**
	 * Prevents a player brewing vaulted pots
	 */
	@EventHandler
	public void onBrewFuel(BrewingStandFuelEvent e)
	{
		if(VaultCtrl.isVaultedPots())
		{
			e.setConsuming(false);
			e.setCancelled(true);
		}
	}
	
	/**
	 * Prevents a player brewing vaulted pots
	 */
	@EventHandler
	public void onBrewStart(BrewingStartEvent e)
	{
		if(VaultCtrl.isVaultedPots())
		{
			e.getBlock().breakNaturally();
			
			DelayUtils.executeDelayedBukkitTask(() ->
			{
				e.getBlock().setType(Material.AIR);
			},1);
			
		}
	}
}
