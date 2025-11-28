package coffee.laeven.lavsvault.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import coffee.laeven.lavsvault.LavsVault;
import coffee.laeven.lavsvault.config.YamlConfig;
import coffee.laeven.lavsvault.utils.Logg;
import coffee.laeven.lavsvault.utils.PrintUtils;
import coffee.laeven.lavsvault.utils.structs.TabTree;
import coffee.laeven.lavsvault.utils.structs.TabTree.Node;
import coffee.laeven.lavsvault.vault.VaultCtrl;

public class VaultCommand extends BaseCommand
{
	private static final String TOGGLE_VERBOSE_MESSAGES = "toggle-verbose-messages";
	private static final String TOGGLE_DEBUG = "toggle-debug";
	private static final String RELOAD_CONFIG = "reload-config";

	// ec toggle-verbose-messages
	
	// ec toggle-cooldown-viewing
	
	// ec reload-config
	
	public void onCommand(CommandSender sender,String[] args)
	{
		if(args.length < 1)
		{
			PrintUtils.error(sender,"Not enough arguments!");
			return;
		}
		
		if(!assertArgument(args[0],TOGGLE_VERBOSE_MESSAGES,TOGGLE_DEBUG,RELOAD_CONFIG))
		{
			PrintUtils.error(sender,"Bad arguments!");
			return;
		}
		
		switch(args[0])
		{
			case TOGGLE_VERBOSE_MESSAGES -> toggleVerboseMessages(sender);
			case TOGGLE_DEBUG -> toggleDebug(sender);
			case RELOAD_CONFIG -> reloadConfig(sender);
		}
	}
	
	private void toggleVerboseMessages(CommandSender sender)
	{
		Logg.setHideVerbose(Logg.isHideVerbose() ? false : true);
		
		Logg.Config.HIDE_VERBOSE.set(Logg.isHideVerbose());
		LavsVault.getConfigFile().saveConfig();
		
		if(Logg.isHideVerbose())
		{
			PrintUtils.info(sender,"Verbose messages are now hidden.");
			return;
		}
		
		PrintUtils.info(sender,"Verbose messages are now shown.");
	}
	
	private void toggleDebug(CommandSender sender)
	{
		if(!(sender instanceof Player p)) { PrintUtils.error(sender,"Cannot call this command from console or command block!"); return; }
//		CooldownCtrl.setDebugMode(p,CooldownCtrl.isInDebugMode(p) ? false : true);
//		
//		if(CooldownCtrl.isInDebugMode(p))
//		{
//			PrintUtils.info(sender,"You are now in debug mode.");
//			return;
//		}
		
		PrintUtils.info(sender,"You are no longer in debug mode.");
	}
	
	private void reloadConfig(CommandSender sender)
	{
		PrintUtils.info(sender,"Reloading config...");
		LavsVault.instance().reloadConfig();
		
		if(((YamlConfig) LavsVault.getConfigFile()).isLoaded())
		{
			PrintUtils.success(sender,"Config reloaded!");
			PrintUtils.info(sender,"Rebuilding memory of vaulted items...");
			
			if(!VaultCtrl.reloadVaultedMaterials())
			{
				PrintUtils.error(sender,"Some vault materials could not be added to the vault as they are not of a valid material.");
				PrintUtils.error(sender,"Check the console for which materials could not be added and check your config to ensure you've spelt them correctly!");
			}
			
			PrintUtils.success(sender,"Vault rebuilt!");
		}
		else
		{
			PrintUtils.error(sender,"Error reloading config! Check console!");
		}
	}
	
	private static TabTree tree = new TabTree();
	
	public VaultCommand()
	{
		tree.getRoot().addBranch(TOGGLE_VERBOSE_MESSAGES);
		tree.getRoot().addBranch(TOGGLE_DEBUG);
		tree.getRoot().addBranch(RELOAD_CONFIG);
	}
	
	@Override
	public List<String> onTab(CommandSender sender,String[] args)
	{
		Node nextNode = tree.getRoot();
		if(args == null || args.length == 1) { return new ArrayList<>(nextNode.branches.keySet()); }
		return Collections.emptyList();
	}
}
