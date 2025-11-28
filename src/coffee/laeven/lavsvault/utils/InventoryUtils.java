package coffee.laeven.lavsvault.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import coffee.laeven.lavsvault.utils.data.DataUtils;
import coffee.laeven.lavsvault.vault.VaultCtrl;

/**
 * @author Laeven
 */
public class InventoryUtils
{
	public static void scanAndDropVaultedItemsFromInventory(Player player)
	{
		ItemStack[] clonedContents = player.getInventory().getContents();
		
		for(int i = 0; i < clonedContents.length; i++)
		{
			if(ItemUtils.isNullOrAir(clonedContents[i])) { continue; }
			
			if(VaultCtrl.isVaultedCannotObtain(clonedContents[i].getType()))
			{
				DataUtils.set("drop_bypass",1,clonedContents[i]);
				player.getWorld().dropItemNaturally(player.getLocation(),clonedContents[i]);
				clonedContents[i] = null;
			}
		}

		player.getInventory().setContents(clonedContents);
	}
}
