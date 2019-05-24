package com.domochevsky.quiverbow;

import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.weapons.QuiverBow;
import com.domochevsky.quiverbow.weapons._WeaponBase;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemHandler
{
	public static void register(IForgeRegistry<Item> registry)
	{
		for(_AmmoBase ammunition : Main.ammo)
		{
			registry.register(ammunition);
			System.out.println("[QuiverBow] Registered ammuntion type: "+ ammunition.getRegistryName());
		}
		
		for(_WeaponBase weapon : Main.weapons)
		{
			registry.register(weapon);
			System.out.println("[QuiverBow] Registered Weapon: "+ weapon.getRegistryName());
		}
	}

	public static void registerModels() {
		for(_AmmoBase ammunition : Main.ammo)
		{
			Main.proxy.registerItemRenderer(ammunition, 0, ammunition.getRegistryName());
		}
		
	}
}
