package delta.cion.tokyo.test_plugin.pvp;

import net.minestom.server.item.Material;

import java.util.HashMap;
import java.util.Map;

public class DamageBasics {

	private static final Map<Material, Float> DAMAGE_BASICS = new HashMap<>();

	private static boolean registered = false;

	public static float getDamage(Material weapon) {
		if (!DAMAGE_BASICS.containsKey(weapon)) return 1;
		return DAMAGE_BASICS.get(weapon);
	}

	public static void registerAll() {
		if (registered) return;
		registerSwords();
		registerAxes();
		registerPickaxes();
		registerShovels();
		registerHoes();
		registerOther();
		registered = true;
	};

	private static void registerSwords() {
		DAMAGE_BASICS.put(Material.NETHERITE_SWORD, 8F);
		DAMAGE_BASICS.put(Material.DIAMOND_SWORD, 7F);
		DAMAGE_BASICS.put(Material.IRON_SWORD, 6F);
		DAMAGE_BASICS.put(Material.STONE_SWORD, 5F);
		DAMAGE_BASICS.put(Material.GOLDEN_SWORD, 4F);
		DAMAGE_BASICS.put(Material.WOODEN_SWORD, 4F);
	}

	private static void registerAxes() {
		DAMAGE_BASICS.put(Material.NETHERITE_AXE, 10F);
		DAMAGE_BASICS.put(Material.DIAMOND_AXE, 9F);
		DAMAGE_BASICS.put(Material.IRON_AXE, 9F);
		DAMAGE_BASICS.put(Material.STONE_AXE, 9F);
		DAMAGE_BASICS.put(Material.GOLDEN_AXE, 7F);
		DAMAGE_BASICS.put(Material.WOODEN_AXE, 7F);
	}

	private static void registerPickaxes() {
		DAMAGE_BASICS.put(Material.NETHERITE_PICKAXE, 6F);
		DAMAGE_BASICS.put(Material.DIAMOND_PICKAXE, 5F);
		DAMAGE_BASICS.put(Material.IRON_PICKAXE, 4F);
		DAMAGE_BASICS.put(Material.STONE_PICKAXE, 3F);
		DAMAGE_BASICS.put(Material.GOLDEN_PICKAXE, 2F);
		DAMAGE_BASICS.put(Material.WOODEN_PICKAXE, 2F);
	}

	private static void registerShovels() {
		DAMAGE_BASICS.put(Material.NETHERITE_SHOVEL, 6.5F);
		DAMAGE_BASICS.put(Material.DIAMOND_SHOVEL, 5.5F);
		DAMAGE_BASICS.put(Material.IRON_SHOVEL, 4.5F);
		DAMAGE_BASICS.put(Material.STONE_SHOVEL, 3.5F);
		DAMAGE_BASICS.put(Material.GOLDEN_SHOVEL, 2.5F);
		DAMAGE_BASICS.put(Material.WOODEN_SHOVEL, 2.5F);
	}

	private static void registerHoes() {
		DAMAGE_BASICS.put(Material.NETHERITE_HOE, 5F);
		DAMAGE_BASICS.put(Material.DIAMOND_HOE, 4F);
		DAMAGE_BASICS.put(Material.IRON_HOE, 3F);
		DAMAGE_BASICS.put(Material.STONE_HOE, 2F);
		DAMAGE_BASICS.put(Material.GOLDEN_HOE, 1F);
		DAMAGE_BASICS.put(Material.WOODEN_HOE, 1F);
	}


	private static void registerOther() {
		DAMAGE_BASICS.put(Material.TRIDENT, 9F);
		DAMAGE_BASICS.put(Material.MACE, 6F);
	}

}
