package com.biggestnerd.loreblocks;

import org.bukkit.block.Block;

public class LoreBlocksAPI {

	/**
	 * @param block the block to check for lore
	 * @return true if the block has lore
	 */
	public static boolean hasLore(Block block) {
		return LoreBlockDAO.getInstance().hasLore(block);
	}
	
	/**
	 * @param block the block to check for lore
	 * @param lore the lore to check if the block has
	 * @return whether or not the block has the specified lore
	 */
	public static boolean hasLore(Block block, String lore) {
		String blore = LoreBlockDAO.getInstance().getLoreForBlock(block);
		return blore != null ? blore.equals(lore) : false;
	}
	
	/**
	 * @param block the block you want lore for
	 * @return the lore for that block, or null if it has no lore
	 */
	public static String getLore(Block block) {
		return LoreBlockDAO.getInstance().getLoreForBlock(block);
	}
}
