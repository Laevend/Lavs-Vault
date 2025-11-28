package coffee.laeven.lavsvault.utils;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Laeven
 */
public class ItemUtils
{	
	/**
	 * Checks an item stack to see if it has a custom display name.
	 * If it has a custom display name it will use that, if it does
	 * not, it will use the material as its display name.
	 * @param stack ItemStack
	 * @return ItemStack name
	 */
//	public static String getName(ItemStack stack)
//	{
//		return stack.getItemMeta().hasDisplayName() ? PaperUtils.toString(stack.getItemMeta().displayName()) : MaterialUtils.getNameFromMaterial(stack.getType());
//	}
	
	/**
	 * Returns an empty item stack
	 * @return
	 */
	public static ItemStack emptyItem()
	{
		return new ItemStack(Material.AIR);
	}
	
	/**
	 * Checks if this item stack is null or is air
	 * @param stack The item stack
	 * @return Is this item stack empty?
	 */
	public static boolean isNullOrAir(ItemStack stack)
	{
		return stack == null || stack.getType().equals(Material.AIR) ? true : false;
	}
	
	/**
	 * Returns true if ItemStack has lore
	 * @param stack ItemStack
	 * @return true/false
	 */
	public static boolean hasLore(ItemStack stack)
	{
		ItemMeta meta = stack.getItemMeta();
		return meta.hasLore();
	}
	
	/**
	 * Returns true if ItemStack has item flags
	 * @param stack ItemStack
	 * @return true/false
	 */
	public static boolean hasItemFlags(ItemStack stack)
	{
		ItemMeta meta = stack.getItemMeta();
		return meta.getItemFlags().size() > 0;
	}
	
	/**
	 * Returns true if ItemStack has an item flag of this type
	 * @param stack ItemStack
	 * @param flag ItemFlag to check for
	 * @return true/false
	 */
	public static boolean hasItemFlag(ItemStack stack,ItemFlag flag)
	{
		ItemMeta meta = stack.getItemMeta();
		return meta.hasItemFlag(flag);
	}
	
	/**
	 * Returns true if ItemStack has enchants
	 * @param stack ItemStack
	 * @return true/false
	 */
	public static boolean hasEnchantments(ItemStack stack)
	{
		ItemMeta meta = stack.getItemMeta();
		return meta.hasEnchants();
	}
	
	/**
	 * Returns true if ItemStack has an enchant of this type
	 * @param stack ItemStack
	 * @param ench Enchant to check for
	 * @return true/false
	 */
	public static boolean hasEnchantment(ItemStack stack,Enchantment ench)
	{
		ItemMeta meta = stack.getItemMeta();
		return meta.hasEnchant(ench);
	}
	
	/**
	 * Returns true if ItemStack has attribute modifiers 
	 * @param stack ItemStack
	 * @return true/false
	 */
	public static boolean hasAttributes(ItemStack stack)
	{
		ItemMeta meta = stack.getItemMeta();
		return meta.hasAttributeModifiers();
	}
	
	/**
	 * Returns true if ItemStack has an attribute modifier of this type
	 * 
	 * <p>This method also calls @link #hasAttributes(ItemStack) before checking for the
	 * existence of this attribute
	 * @param stack ItemStack
	 * @param att Attribute modifier to check for
	 * @return true/false
	 */
	public static boolean hasAttribute(ItemStack stack,Attribute att)
	{
		if(!ItemUtils.hasAttributes(stack)) { return false; }
		
		ItemMeta meta = stack.getItemMeta();
		return meta.getAttributeModifiers().containsKey(att);
	}
	
	/**
	 * Returns true if ItemStack has an attribute modifier of this type that is active in this slot
	 * 
	 * <p>This method also calls @link #hasAttributes(ItemStack) and @link #hasAttribute(ItemStack, Attribute)
	 * before checking for the existence of this attribute modifier at this equipment slot 
	 * @param stack ItemStack
	 * @param att Attribute modifier to check for
	 * @param slot EquipmentSlot this attribute modifies
	 * @return true/false
	 */
	public static boolean hasAttributeAtSlot(ItemStack stack,Attribute att,EquipmentSlotGroup slot)
	{
		if(!ItemUtils.hasAttributes(stack)) { return false; }
		if(!ItemUtils.hasAttribute(stack,att)) { return false; }
		
		ItemMeta meta = stack.getItemMeta();
		
		for(AttributeModifier mod : meta.getAttributeModifiers(att))
		{
			if(mod.getSlotGroup().toString().equals(slot.toString())) { return true; }
		}
		
		return false;
	}
	
	/**
	 * Creates an array of item stacks based on the amount needed
	 * @param amount Amount of this material
	 * @param mat Material type
	 * @return Array of ItemStacks
	 */
	public static ItemStack[] createStacks(int amount,Material mat)
	{
		if(amount < 1) { return null; }
		
		int stackSize = mat.getMaxStackSize();
		int totalStacks = amount / stackSize;
		int remainder = amount % stackSize;
		
		if(totalStacks == 0)
		{
			return new ItemStack[] { new ItemStack(mat,remainder) };
		}
		
		ItemStack[] stacks;
		
		if(remainder > 0)
		{
			stacks = new ItemStack[totalStacks + 1];
			stacks[stacks.length - 2] = new ItemStack(mat,remainder);
		}
		else
		{
			stacks = new ItemStack[totalStacks];
		}
		
		for(int i = 0; i < totalStacks; i++)
		{
			stacks[i] = new ItemStack(mat,stackSize);
		}
		
		return stacks;
	}
}
