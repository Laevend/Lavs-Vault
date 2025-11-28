package coffee.laeven.lavsvault.vault;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import coffee.laeven.lavsvault.config.Configurable;
import coffee.laeven.lavsvault.config.item.ConfigItem;
import coffee.laeven.lavsvault.utils.InventoryUtils;
import coffee.laeven.lavsvault.utils.ItemUtils;
import coffee.laeven.lavsvault.utils.Logg;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

public class VaultCtrl
{
	private static Set<Material> materialsCannotObtain = new HashSet<>();
	private static Set<Material> materialsCannotUse = new HashSet<>();
	
	private static Set<String> enchantmentCannotObtain = new HashSet<>();
	private static Set<String> enchantmentCannotUse = new HashSet<>();
	
	private static Set<PotionType> potsCannotObtain = new HashSet<>();
	private static Set<PotionType> potsCannotUse = new HashSet<>();
	
	private static Set<PotionType> arrowCannotObtain = new HashSet<>();
	private static Set<PotionType> arrowCannotUse = new HashSet<>();
	
	private static boolean vaultEnchants = false;
	private static boolean vaultPots = false;
	private static boolean vaultTippedArrows = false;
	
	/**
	 * If an item/block cannot be obtained (either by looting, picking it up etc.)
	 * @param stack Stack to check
	 * @return True if item/block is vaulted, false otherwise
	 */
	public static boolean isVaultedCannotObtain(ItemStack stack)
	{
		if(ItemUtils.isNullOrAir(stack)) { return false; }
		
		// if item is enchanted and all enchanted items are vaulted
		if(isVaultedEnchanted(stack)) { return true; }
		
		// If item is a pot and all pots are vaulted
		if(isVaultedPots(stack)) { return true; }
		
		// If item is a tipped arrow and all tipped arrows are vaulted
		if(isVaultedTippedArrows(stack)) { return true; }
		
		// If a specific enchant is vaulted
		if(isVaultedCannotObtainEnchant(stack)) { return true; }
		
		// If a specific pot is vaulted
		if(isVaultedCannotObtainPot(stack)) { return true; }
		
		// If a specific tipped arrow is vaulted
		if(isVaultedCannotObtainTippedArrow(stack)) { return true; }
		
		return isVaultedCannotObtain(stack.getType());
	}
	
	public static boolean isVaultedCannotObtain(Material mat)
	{
		Objects.requireNonNull(mat,"Material cannot be null!");
		return materialsCannotObtain.contains(mat);
	}
	
	/**
	 * If an item/block cannot be used, eaten, equipped etc.
	 * @param stack Stack to check
	 * @return True if item/block is vaulted, false otherwise
	 */
	public static boolean isVaultedCannotUse(ItemStack stack)
	{
		if(ItemUtils.isNullOrAir(stack)) { return false; }
		
		// if item is enchanted and all enchanted items are vaulted
		if(isVaultedEnchanted(stack)) { return true; }
		
		// If item is a pot and all pots are vaulted
		if(isVaultedPots(stack)) { return true; }
		
		// If item is a tipped arrow and all tipped arrows are vaulted
		if(isVaultedTippedArrows(stack)) { return true; }
		
		// If a specific enchant is vaulted
		if(isVaultedCannotUseEnchant(stack)) { return true; }
		
		// If a specific pot is vaulted
		if(isVaultedCannotUsePot(stack)) { return true; }
		
		// If a specific tipped arrow is vaulted
		if(isVaultedCannotUseTippedArrow(stack)) { return true; }
		
		return isVaultedCannotUse(stack.getType());
	}
	
	public static boolean isVaultedCannotUse(Material mat)
	{
		Objects.requireNonNull(mat,"Material cannot be null!");
		return materialsCannotUse.contains(mat);
	}
	
	private static boolean isVaultedEnchanted(ItemStack stack)
	{
		if(!vaultEnchants) { return false; }
		if(stack.getItemMeta().hasEnchants()) { return true; }
		
		if(stack.getItemMeta() instanceof EnchantmentStorageMeta eMeta)
		{
			return eMeta.hasStoredEnchants();
		}
		
		return false;
	}
	
	private static boolean isVaultedCannotObtainEnchant(ItemStack stack)
	{
		if(stack.getItemMeta() instanceof EnchantmentStorageMeta eMeta)
		{
			if(eMeta.hasStoredEnchants())
			{
				for(Enchantment ench : eMeta.getStoredEnchants().keySet())
				{
					if(!enchantmentCannotObtain.contains(ench.getKey().getKey())) { continue; }
					return true;
				}
			}
		}
		
		if(!stack.getItemMeta().hasEnchants()) { return false; }
		
		for(Enchantment ench : stack.getEnchantments().keySet())
		{
			if(!enchantmentCannotObtain.contains(ench.getKey().getKey())) { continue; }
			return true;
		}
		
		return false;
	}
	
	public static boolean isVaultedCannotObtainEnchant(Enchantment ench)
	{
		return enchantmentCannotObtain.contains(ench.getKey().getKey());
	}
	
	public static Set<String> getVaultedCannotObtainEnchants()
	{
		return enchantmentCannotObtain;
	}
	
	private static boolean isVaultedCannotUseEnchant(ItemStack stack)
	{
		if(stack.getItemMeta() instanceof EnchantmentStorageMeta eMeta)
		{
			if(eMeta.hasStoredEnchants())
			{
				for(Enchantment ench : eMeta.getStoredEnchants().keySet())
				{
					if(!enchantmentCannotUse.contains(ench.getKey().getKey())) { continue; }
					return true;
				}
			}
		}
		
		if(!stack.getItemMeta().hasEnchants()) { return false; }
		
		for(Enchantment ench : stack.getEnchantments().keySet())
		{
			if(!enchantmentCannotUse.contains(ench.getKey().getKey())) { continue; }
			return true;
		}
		
		return false;
	}
	
	public static boolean isVaultedCannotUseEnchant(Enchantment ench)
	{
		return enchantmentCannotUse.contains(ench.getKey().getKey());
	}
	
	private static boolean isVaultedPots(ItemStack stack)
	{
		if(!vaultPots) { return false; }
		if(!(stack.getType() == Material.POTION || stack.getType() == Material.SPLASH_POTION || stack.getType() == Material.LINGERING_POTION)) { return false; }
		return true;
	}
	
	private static boolean isVaultedCannotObtainPot(ItemStack stack)
	{
		if(!(stack.getType() == Material.POTION || stack.getType() == Material.SPLASH_POTION || stack.getType() == Material.LINGERING_POTION)) { return false; }
		if(!(stack.getItemMeta() instanceof PotionMeta meta)) { return false; }
		
		return potsCannotObtain.contains(meta.getBasePotionType());
	}
	
	private static boolean isVaultedCannotUsePot(ItemStack stack)
	{
		if(!(stack.getType() == Material.POTION || stack.getType() == Material.SPLASH_POTION || stack.getType() == Material.LINGERING_POTION)) { return false; }
		if(!(stack.getItemMeta() instanceof PotionMeta meta)) { return false; }
		
		return potsCannotUse.contains(meta.getBasePotionType());
	}
	
	private static boolean isVaultedTippedArrows(ItemStack stack)
	{
		if(!vaultTippedArrows) { return false; }
		if(stack.getType() != Material.TIPPED_ARROW) { return false; }
		return true;
	}
	
	private static boolean isVaultedCannotObtainTippedArrow(ItemStack stack)
	{
		if(stack.getType() != Material.TIPPED_ARROW) { return false; }
		if(!(stack.getItemMeta() instanceof PotionMeta meta)) { return false; }
		
		return arrowCannotObtain.contains(meta.getBasePotionType());
	}
	
	public static boolean isVaultedCannotUseTippedArrow(ItemStack stack)
	{
		if(stack.getType() != Material.TIPPED_ARROW) { return false; }
		if(!(stack.getItemMeta() instanceof PotionMeta meta)) { return false; }
		
		return arrowCannotUse.contains(meta.getBasePotionType());
	}
	
	public static boolean isVaultedCannotUseTippedArrow(PotionType type)
	{
		return arrowCannotUse.contains(type);
	}
	
	public static boolean isVaultedEnchants()
	{
		return vaultEnchants;
	}
	
	public static boolean isVaultedPots()
	{
		return vaultPots;
	}

	public static boolean isVaultedTippedArrows()
	{
		return vaultTippedArrows;
	}

	public static boolean reloadVaultedMaterials()
	{
		materialsCannotObtain.clear();
		enchantmentCannotObtain.clear();
		potsCannotObtain.clear();
		arrowCannotObtain.clear();
		boolean noCastingFailures = true;
		
		System.out.println(VaultCtrl.Config.CANNOT_OBTAIN.get() + " - " + VaultCtrl.Config.CANNOT_OBTAIN.get().size());
		System.out.println(VaultCtrl.Config.CANNOT_USE.get() + " - " + VaultCtrl.Config.CANNOT_USE.get().size());
		
		for(String material : VaultCtrl.Config.CANNOT_OBTAIN.get())
		{
			// If enchant
			if(material.startsWith("Enchant::"))
			{
				String enchantName = material.split("::")[1];
				if(enchantName == null || enchantName.isEmpty() || enchantName.isBlank())
				{
					Logg.error("Enchantment missing!");
					noCastingFailures = false;
					continue;
				}
				
				Enchantment ench = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.fromString(enchantName));
				
				if(ench == null)
				{
					Logg.error("Enchant '" + enchantName + "' is not a real Enchantment!");
					noCastingFailures = false;
					continue;
				}
				
				enchantmentCannotObtain.add(enchantName);
				Logg.verb("Adding vaulted OBTAIN enchant -> " + enchantName,Logg.VerbGroup.VAULT_CTRL);
			}
			// If pot
			else if(material.startsWith("Pot::"))
			{
				String potName = material.split("::")[1];
				if(potName == null || potName.isEmpty() || potName.isBlank())
				{
					Logg.error("Enchantment missing!");
					noCastingFailures = false;
					continue;
				}
				
				try 
				{
					PotionType potType = PotionType.valueOf(potName.toUpperCase());
					potsCannotObtain.add(potType);
					Logg.verb("Adding vaulted OBTAIN pot -> " + material,Logg.VerbGroup.VAULT_CTRL);
				}
				catch(Exception e)
				{
					Logg.error("Pot PotionType '" + material + "' is not a real PotionType!");
					noCastingFailures = false;
				}
			}
			// If tipped arrow
			else if(material.startsWith("Tipped::"))
			{
				String arrowName = material.split("::")[1];
				if(arrowName == null || arrowName.isEmpty() || arrowName.isBlank())
				{
					Logg.error("Enchantment missing!");
					noCastingFailures = false;
					continue;
				}
				
				try 
				{
					PotionType arrowType = PotionType.valueOf(arrowName.toUpperCase());
					arrowCannotObtain.add(arrowType);
					Logg.verb("Adding vaulted OBTAIN tipped arrow -> " + material,Logg.VerbGroup.VAULT_CTRL);
				}
				catch(Exception e)
				{
					Logg.error("Arrow PotionType '" + material + "' is not a real PotionType!");
					noCastingFailures = false;
				}
			}
			else
			{
				try 
				{
					Material vaultedMaterial = Material.valueOf(material.toUpperCase());
					materialsCannotObtain.add(vaultedMaterial);
					Logg.verb("Adding vaulted OBTAIN item/block -> " + material,Logg.VerbGroup.VAULT_CTRL);
				}
				catch(Exception e)
				{
					Logg.error("Material '" + material + "' is not a real material!");
					noCastingFailures = false;
				}
			}
		}
		
		materialsCannotUse.clear();
		enchantmentCannotUse.clear();
		potsCannotUse.clear();
		arrowCannotUse.clear();
		
		for(String material : VaultCtrl.Config.CANNOT_USE.get())
		{
			// If enchant
			if(material.startsWith("Enchant::"))
			{
				String enchantName = material.split("::")[1];
				if(enchantName == null || enchantName.isEmpty() || enchantName.isBlank())
				{
					Logg.error("Enchantment missing!");
					noCastingFailures = false;
					continue;
				}
				
				Enchantment ench = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.fromString(enchantName.toLowerCase()));
				
				if(ench == null)
				{
					Logg.error("Enchant '" + enchantName + "' is not a real Enchantment!");
					noCastingFailures = false;
					continue;
				}
				
				enchantmentCannotUse.add(enchantName);
				Logg.verb("Adding vaulted OBTAIN enchant -> " + enchantName,Logg.VerbGroup.VAULT_CTRL);
			}
			// If pot
			else if(material.startsWith("Pot::"))
			{
				String potName = material.split("::")[1];
				if(potName == null || potName.isEmpty() || potName.isBlank())
				{
					Logg.error("Enchantment missing!");
					noCastingFailures = false;
					continue;
				}
				
				try 
				{
					PotionType potType = PotionType.valueOf(potName.toUpperCase());
					potsCannotUse.add(potType);
					Logg.verb("Adding vaulted OBTAIN pot -> " + material,Logg.VerbGroup.VAULT_CTRL);
				}
				catch(Exception e)
				{
					Logg.error("Pot PotionType '" + material + "' is not a real PotionType!");
					noCastingFailures = false;
				}
			}
			// If tipped arrow
			else if(material.startsWith("Tipped::"))
			{
				String arrowName = material.split("::")[1];
				if(arrowName == null || arrowName.isEmpty() || arrowName.isBlank())
				{
					Logg.error("Enchantment missing!");
					noCastingFailures = false;
					continue;
				}
				
				try 
				{
					PotionType arrowType = PotionType.valueOf(arrowName.toUpperCase());
					arrowCannotUse.add(arrowType);
					Logg.verb("Adding vaulted OBTAIN tipped arrow -> " + material,Logg.VerbGroup.VAULT_CTRL);
				}
				catch(Exception e)
				{
					Logg.error("Arrow PotionType '" + material + "' is not a real PotionType!");
					noCastingFailures = false;
				}
			}
			else
			{
				try 
				{
					Material vaultedMaterial = Material.valueOf(material.toUpperCase());
					materialsCannotUse.add(vaultedMaterial);
					Logg.verb("Adding vaulted USE item/block -> " + material,Logg.VerbGroup.VAULT_CTRL);
				}
				catch(Exception e)
				{
					Logg.error("Material '" + material + "' is not a real material!");
					noCastingFailures = false;
				}
			}
		}
		
		vaultEnchants = VaultCtrl.Config.NO_ENCHANTS.get();
		vaultPots = VaultCtrl.Config.NO_POTS.get();
		vaultTippedArrows = VaultCtrl.Config.NO_TIPPED_ARROWS.get();
		
		removeRecipesContainingVaultedItems();
		scanAllPlayers();
		
		return noCastingFailures;
	}
	
	/**
	 * Prevents the manufacturing of vaulted items by removing their recipes
	 */
	private static void removeRecipesContainingVaultedItems()
	{
		Bukkit.resetRecipes();
		
		for(Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext();)
		{
			Recipe recipe = it.next();
			
			if(VaultCtrl.isVaultedCannotObtain(recipe.getResult()))
			{
				it.remove();
			}
		}
	}
	
	/**
	 * Scan all players inventories for vaulted items
	 */
	private static void scanAllPlayers()
	{
		Bukkit.getOnlinePlayers().forEach(p -> scanPlayerInventory(p));
	}
	
	/**
	 * Scan a players inventory for vaulted items
	 */
	public static void scanPlayerInventory(Player p)
	{
		InventoryUtils.scanAndDropVaultedItemsFromInventory(p);
	}
	
	/**
	 * Configurable values for pearl and riptide trident cooldown instances
	 */
	public static class Config implements Configurable
	{
		public static final ConfigItem<List<String>> CANNOT_OBTAIN = new ConfigItem<>("vault.cannot_obtain",new ArrayList<>(),"List of items/blocks that cannot be picked up, dropped, or transferred into or out of containers.");
		
		public static final ConfigItem<List<String>> CANNOT_USE = new ConfigItem<>("vault.cannot_use",new ArrayList<>(),"List of items/blocks that cannot be used, equipped, eaten, or placed.");
		
		public static final ConfigItem<Boolean> NO_ENCHANTS = new ConfigItem<>("vault.no_enchants",false,"Toggles enchants being vaulted. When true, players can no longer enchant equipment, use anvils to enchant equipment, buy enchantment books or equipment from villagers, loot enchanted books or equipment from chests, or use enchanted equipment.");
		
		public static final ConfigItem<Boolean> NO_POTS = new ConfigItem<>("vault.no_pots",false,"Toggles potions being vaulted. When true, players can no longer brew potions, buy potions from villagers, loot potions, or use potions.");
		
		public static final ConfigItem<Boolean> NO_TIPPED_ARROWS = new ConfigItem<>("vault.no_tipped_arrows",false,"Toggles tipped arrows being vaulted. When true, players can no longer craft tipped arrow, buy tipped arrows from villagers, loot tipped arrows, or use tipped arrows.");
	}
}
