package coffee.laeven.lavsvault.vault;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;

import coffee.laeven.lavsvault.utils.DelayUtils;
import coffee.laeven.lavsvault.utils.SoundUtils;

public class EquipmentCheck implements Listener
{
	/**
	 * Prevents a player using vaulted weapons or equipment when attacking with melee
	 */
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Player attacker)
		{ 
			if(VaultCtrl.isVaultedCannotUse(attacker.getInventory().getItemInMainHand()) || VaultCtrl.isVaultedCannotUse(attacker.getInventory().getItemInOffHand()))
			{
				SoundUtils.VAULTED_ITEM.play(attacker);
				e.setCancelled(true);
				e.setDamage(0);
			}
			
			checkEquipment(attacker);
		}
		
		if(e.getEntity() instanceof Player victim) { checkEquipment(victim); }
	}
	
	/**
	 * Prevents a player using vaulted equipment outside pvp or pve
	 */
	@EventHandler
	public void onDamage(EntityDamageEvent e)
	{
		if(!(e.getEntity() instanceof Player victim)) { return; }
		checkEquipment(victim);
	}
	
	/**
	 * Prevents a player using vaulted weapons or equipment when attacking with range
	 */
	@EventHandler
	public void onProjectile(ProjectileHitEvent e)
	{
		if(e.getEntity().getShooter() instanceof Player attacker)
		{ 
			if(VaultCtrl.isVaultedCannotUse(attacker.getInventory().getItemInMainHand()) || VaultCtrl.isVaultedCannotUse(attacker.getInventory().getItemInOffHand()))
			{
				SoundUtils.VAULTED_ITEM.play(attacker);
				e.setCancelled(true);
			}
			
			checkEquipment(attacker);
		}
		
		if(e.getHitEntity() instanceof Player victim) { checkEquipment(victim); }
	}
	
	/**
	 * Prevents armour equipment being equipped or retrieved from an armour stand
	 */
	@EventHandler
	public void onInteractArmourStand(PlayerArmorStandManipulateEvent e)
	{
		if(!VaultCtrl.isVaultedCannotObtain(e.getArmorStandItem()) && !VaultCtrl.isVaultedCannotObtain(e.getPlayerItem())) { return; }
		SoundUtils.VAULTED_ITEM.play(e.getPlayer());
		e.setCancelled(true);
	}
	
	/**
	 * Prevents players gliding with elytra or other armour equipment with the glide attribute
	 */
	@EventHandler
	public void onGlide(EntityToggleGlideEvent e)
	{
		if(!(e.getEntity() instanceof Player glider)) { return; }
		
		if(checkEquipment(glider))
		{
			SoundUtils.VAULTED_ITEM.play(glider);
			e.setCancelled(false);
		}
	}
	
	/**
	 * Prevents a player equipping armour by moving it into its equipment slot
	 */
	@EventHandler
	public void onEquipmentSlotCheck(InventoryClickEvent e)
	{
		// The raw slot displays as -999 when clicking outside the inventory, don't ask me why
		// Raw slot displays -1 when clicking on the edge of an inventory...
		if(e.getRawSlot() == -999 || e.getRawSlot() == -1) { return; }
		
		// W H A T   I S   T H I S???
		if(e.getAction().equals(InventoryAction.NOTHING)) { return; }
		
		if(e.getClickedInventory().getType() != InventoryType.PLAYER) { return; }
		
		if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT)
		{
			DelayUtils.executeDelayedBukkitTask(() ->
			{
				if(checkEquipment((Player) e.getView().getPlayer()))
				{
					e.setCancelled(true);
				}
			},1);
			
			return;
		}
		
		if(e.getSlotType() != SlotType.ARMOR) { return; }
		
		if(checkEquipment((Player) e.getView().getPlayer()))
		{
			e.setCancelled(true);
		}
		
		if(VaultCtrl.isVaultedCannotUse(e.getCursor()))
		{
			SoundUtils.VAULTED_ITEM.play((Player) e.getWhoClicked());
			e.setCancelled(true);
		}
	}
	
	/**
	 * Forcibly unequip vaulted armour
	 * @return 
	 */
	private boolean checkEquipment(Player p)
	{
		boolean forceUnequip = false;
		
		if(VaultCtrl.isVaultedCannotUse(p.getInventory().getHelmet())) { unequipHelm(p); forceUnequip = true; }
		if(VaultCtrl.isVaultedCannotUse(p.getInventory().getChestplate())) { unequipChestplate(p); forceUnequip = true; }
		if(VaultCtrl.isVaultedCannotUse(p.getInventory().getLeggings())) { unequipLeggings(p); forceUnequip = true; }
		if(VaultCtrl.isVaultedCannotUse(p.getInventory().getBoots())) { unequipBoots(p); forceUnequip = true; }
		
		return forceUnequip;
	}
	
	/**
	 * Attempts to forcibly unequip the players helmet
	 * <p>
	 * Assumes {@link #isVaultedCannotUse(ItemStack)} or {@link #isVaultedCannotUse(Material)} has already been checked
	 */
	private void unequipHelm(Player p)
	{
		// Check if inventory has an empty slot
		if(p.getInventory().firstEmpty() == -1)
		{
			// No empty slots, check if we can drop it.
			if(!VaultCtrl.isVaultedCannotObtain(p.getInventory().getHelmet()))
			{
				// Can drop, drop instead of destroy
				p.getWorld().dropItemNaturally(p.getLocation().clone().add(0d,0.1d,0d),p.getInventory().getHelmet());
			}
		}
		else
		{
			// Empty slot found, unequip
			p.getInventory().addItem(p.getInventory().getHelmet());
		}
		
		SoundUtils.ARMOUR_CRACK.play(p);
		p.getInventory().setHelmet(null);
	}
	
	/**
	 * Attempts to forcibly unequip the players chestplate
	 * <p>
	 * Assumes {@link #isVaultedCannotUse(ItemStack)} or {@link #isVaultedCannotUse(Material)} has already been checked
	 */
	private void unequipChestplate(Player p)
	{
		// Check if inventory has an empty slot
		if(p.getInventory().firstEmpty() == -1)
		{
			// No empty slots, check if we can drop it.
			if(!VaultCtrl.isVaultedCannotObtain(p.getInventory().getChestplate()))
			{
				// Can drop, drop instead of destroy
				p.getWorld().dropItemNaturally(p.getLocation().clone().add(0d,0.1d,0d),p.getInventory().getChestplate());
			}
		}
		else
		{
			// Empty slot found, unequip
			p.getInventory().addItem(p.getInventory().getChestplate());
		}
		
		SoundUtils.ARMOUR_CRACK.play(p);
		p.getInventory().setChestplate(null);
	}
	
	/**
	 * Attempts to forcibly unequip the players leggings
	 * <p>
	 * Assumes {@link #isVaultedCannotUse(ItemStack)} or {@link #isVaultedCannotUse(Material)} has already been checked
	 */
	private void unequipLeggings(Player p)
	{
		// Check if inventory has an empty slot
		if(p.getInventory().firstEmpty() == -1)
		{
			// No empty slots, check if we can drop it.
			if(!VaultCtrl.isVaultedCannotObtain(p.getInventory().getLeggings()))
			{
				// Can drop, drop instead of destroy
				p.getWorld().dropItemNaturally(p.getLocation().clone().add(0d,0.1d,0d),p.getInventory().getLeggings());
			}
		}
		else
		{
			// Empty slot found, unequip
			p.getInventory().addItem(p.getInventory().getLeggings());
		}
		
		SoundUtils.ARMOUR_CRACK.play(p);
		p.getInventory().setLeggings(null);
	}
	
	/**
	 * Attempts to forcibly unequip the players boots
	 * <p>
	 * Assumes {@link #isVaultedCannotUse(ItemStack)} or {@link #isVaultedCannotUse(Material)} has already been checked
	 */
	private void unequipBoots(Player p)
	{
		// Check if inventory has an empty slot
		if(p.getInventory().firstEmpty() == -1)
		{
			// No empty slots, check if we can drop it.
			if(!VaultCtrl.isVaultedCannotObtain(p.getInventory().getBoots()))
			{
				// Can drop, drop instead of destroy
				p.getWorld().dropItemNaturally(p.getLocation().clone().add(0d,0.1d,0d),p.getInventory().getBoots());
			}
		}
		else
		{
			// Empty slot found, unequip
			p.getInventory().addItem(p.getInventory().getBoots());
		}
		
		SoundUtils.ARMOUR_CRACK.play(p);
		p.getInventory().setBoots(null);
	}
}
