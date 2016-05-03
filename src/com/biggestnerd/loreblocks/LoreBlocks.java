package com.biggestnerd.loreblocks;

import org.bukkit.plugin.java.JavaPlugin;

public class LoreBlocks extends JavaPlugin {

	private static LoreBlocks instance;
	
	private BlockListener listener;
	
	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		reloadConfig();
		listener = new BlockListener();
		getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public static LoreBlocks getInstance() {
		return instance;
	}
}
