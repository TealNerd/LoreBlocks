package com.biggestnerd.loreblocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class BlockListener implements Listener {

	private LoreBlockDAO dao;
	
	public BlockListener() {
		dao = LoreBlockDAO.getInstance();
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.isCancelled()) return;
		ItemStack item = event.getItemInHand();
		if(!item.hasItemMeta() || !item.getItemMeta().hasLore()) return;
		String lore = item.getItemMeta().getLore().get(0);
		if(!dao.addLoreBlock(BlockUtils.getRealBlock(event.getBlock()), lore)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.isCancelled()) return;
		Block block = BlockUtils.getRealBlock(event.getBlock());
		if(dao.hasLore(block)) {
			handleBlockBreak(block);
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if(event.isCancelled()) return;
		Iterator<Block> iterator = event.blockList().iterator();
		ArrayList<Block> blocks = new ArrayList<Block>();
		while(iterator.hasNext()) {
			Block block = BlockUtils.getRealBlock(iterator.next());
			if(dao.hasLore(block)) {
				if(blocks.contains(block)) {
					block.getDrops().clear();
					iterator.remove();
					continue;
				}
				blocks.add(block);
				handleBlockBreak(block);
				block.getDrops().clear();
				iterator.remove();
			}
		}
	}
	
	@EventHandler
	public void onEntityBreakDoor(EntityBreakDoorEvent event){
		if(event.isCancelled()) return;
		Block block = BlockUtils.getRealBlock(event.getBlock());
		if(dao.hasLore(block)) {
			event.setCancelled(true);
			handleBlockBreak(block);
		}
	}
	
	@EventHandler
	public void onPistonExtend(BlockPistonExtendEvent event) {
		for(Block block : event.getBlocks()) {
			if(dao.hasLore(BlockUtils.getRealBlock(block))) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPistonRetract(BlockPistonRetractEvent event) {
		for(Block block : event.getBlocks()) {
			if(dao.hasLore(BlockUtils.getRealBlock(block))) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if(event.isCancelled()) return;
		Block block = BlockUtils.getRealBlock(event.getBlock());
		if(dao.hasLore(block)) {
			event.setCancelled(true);
			handleBlockBreak(block);
		}
	}
	
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		Block block = BlockUtils.getRealBlock(event.getToBlock());
		if(dao.hasLore(block)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		Block block = BlockUtils.getRealBlock(event.getBlock());
		if(block.getType().hasGravity() && dao.hasLore(block)) {
			event.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void handleBlockBreak(Block block) {
		String lore = dao.getLoreForBlock(block);
		ItemStack item = new ItemStack(block.getType(), 1);
		item.setDurability(block.getData());
		System.out.println(item.getType() + ":" + item.getDurability() + " lore: " + lore);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(new LinkedList<String>(Arrays.asList(new String[]{lore})));
		item.setItemMeta(meta);
		dropItemAtLocation(block.getLocation(), item);
		block.setType(Material.AIR);
		dao.removeLoreBlock(block);
	}
	
	private void dropItemAtLocation(final Location l, final ItemStack is) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(LoreBlocks.getInstance(), new Runnable() {
			@Override
			public void run() {
				l.getWorld().dropItem(l.add(0.5, 0.5, 0.5), is).setVelocity(new Vector(0, 0.05, 0));
			}
		}, 1);
	}
}
