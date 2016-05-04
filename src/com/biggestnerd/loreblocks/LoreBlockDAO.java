package com.biggestnerd.loreblocks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.metadata.FixedMetadataValue;

public class LoreBlockDAO {

	private static LoreBlockDAO instance;
	
	private Database db;
	
	private LoreBlockDAO() {
		LoreBlocks plugin = LoreBlocks.getInstance();
		ConfigurationSection config = plugin.getConfig().getConfigurationSection("sql");
		String dbName = config.getString("dbname");
		String host = config.getString("host");
		String pass = config.getString("pass");
		int port = config.getInt("port");
		String user = config.getString("user");
		db = new Database(host, port, dbName, user, pass, plugin.getLogger());
		db.connect();
		initializeTables();
		loadMetadata();
	}
	
	private void initializeTables() {
		db.execute("CREATE TABLE IF NOT EXISTS loreblocks ("
					+ "pos VARCHAR(100) PRIMARY KEY,"
					+ "lore VARCHAR(100) NOT NULL)");
	}
	
	private void loadMetadata() {
		try {
			PreparedStatement ps = db.prepareStatement("SELECT * FROM loreblocks");
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				Location loc = locationFromString(result.getString("pos"));
				String lore = result.getString("lore");
				loc.getBlock().setMetadata("lore", new FixedMetadataValue(LoreBlocks.getInstance(), lore));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static final Pattern locationPattern = Pattern.compile("^Location{world=(.*),x=([-]?[0-9]+\\.?[0-9]*?),y=([-]?[0-9]+\\.?[0-9]*?),z=([-]?[0-9]+\\.?[0-9]*?),.*}$");
	private Location locationFromString(String locString) {
		Matcher locMatcher = locationPattern.matcher(locString);
		if(locMatcher.find()) {
			String world = locMatcher.group(1);
			double x = Double.parseDouble(locMatcher.group(2));
			double y = Double.parseDouble(locMatcher.group(3));
			double z = Double.parseDouble(locMatcher.group(4));
			return new Location(Bukkit.getWorld(world), x, y, z);
		}
		return null;
	}
	
	public boolean addLoreBlock(Block block, String lore) {
		if(block.getType() == Material.AIR || BlockUtils.isLiquid(block))return false; //no lore on air or liquids
		try {
			PreparedStatement ps = db.prepareStatement("INSERT INTO loreblocks (pos,lore) VALUES (?,?)");
			ps.setString(1, block.getLocation().toString());
			ps.setString(2, lore);
			ps.execute();
			block.setMetadata("lore", new FixedMetadataValue(LoreBlocks.getInstance(), lore));
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	public String getLoreForBlock(Block block) {
		if(block.hasMetadata("lore")) {
			return block.getMetadata("lore").get(0).asString();
		}
		try {
			PreparedStatement ps = db.prepareStatement("SELECT * FROM loreblocks WHERE pos=?");
			ps.setString(1, block.getLocation().toString());
			ResultSet result = ps.executeQuery();
			if(result.next()) {
				String lore = result.getString("lore");
				block.setMetadata("lore", new FixedMetadataValue(LoreBlocks.getInstance(), lore));
				return lore;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public void removeLoreBlock(Block block) {
		try {
			PreparedStatement ps = db.prepareStatement("DELETE FROM loreblocks WHERE pos=?");
			ps.setString(1, block.getLocation().toString());
			ps.execute();
			block.removeMetadata("lore", LoreBlocks.getInstance());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void moveLoreBlock(Block block, Block toBlock) {
		String lore = getLoreForBlock(block);
		removeLoreBlock(block);
		addLoreBlock(toBlock, lore);
	}

	public boolean hasLore(Block block) {
		return getLoreForBlock(block) != null;
	}
	
	public static LoreBlockDAO getInstance() {
		return instance != null ? instance : (instance = new LoreBlockDAO());
	}
}
