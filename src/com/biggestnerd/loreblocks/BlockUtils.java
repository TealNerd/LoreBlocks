package com.biggestnerd.loreblocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Bed;

public class BlockUtils {

	private static BlockFace[] cardinals = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}; 
	public static List<Material> doorTypes = new ArrayList<Material>(Arrays.asList(
            Material.WOODEN_DOOR, Material.IRON_DOOR_BLOCK,
            Material.ACACIA_DOOR, Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR, Material.JUNGLE_DOOR,
            Material.SPRUCE_DOOR, Material.WOOD_DOOR));
	
	public static Block getAttachedChest(Block block) {
		if(block == null) return null;
		Material mat = block.getType();
		if(mat == Material.CHEST || mat == Material.TRAPPED_CHEST) {
			for(BlockFace face : cardinals) {
				Block b = block.getRelative(face);
				if(b.getType() == mat) {
					return b;
				}
			}
		}
		return null;
	}
	
	public static Block getRealBlock(Block block) {
		if(block == null) return null;
		Block b = block;
		switch(block.getType()) {
		case CHEST:
		case TRAPPED_CHEST:
			if(!LoreBlockDAO.getInstance().hasLore(block)) {
				b = getAttachedChest(block);
			}
			if(b == null) {
				b = block;
			}
			break;
		case WOODEN_DOOR:
        case IRON_DOOR_BLOCK:
        case ACACIA_DOOR:
        case BIRCH_DOOR:
        case DARK_OAK_DOOR:
        case JUNGLE_DOOR:
        case SPRUCE_DOOR:
        case WOOD_DOOR:
        	if(!doorTypes.contains(block.getRelative(BlockFace.UP).getType())) {
        		b = block.getRelative(BlockFace.DOWN);
        	}
        	break;
        case BED_BLOCK:
        	if (((Bed) block.getState().getData()).isHeadOfBed()) {
                b = block.getRelative(((Bed) block.getState().getData()).getFacing().getOppositeFace());
            }
            break;
		default:
			b = block;
			break;
		}
		return b;
	}
	
	static final ArrayList<Material> liquids = new ArrayList<Material>(Arrays.asList(
			Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA));
	public static boolean isLiquid(Block block) {
		return liquids.contains(block.getType());
	}
}
