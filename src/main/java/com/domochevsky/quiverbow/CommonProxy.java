package com.domochevsky.quiverbow;

import com.domochevsky.quiverbow.ammo._AmmoBase;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy 
{
    public void registerProjectileRenderer(Class<? extends Entity> entityClass) { }
	public void registerWeaponRenderer(Item item, byte number) { }
	
	public void registerTurretRenderer() {  }
	

	public void preInit(FMLPreInitializationEvent preEvent)
	{
		
	}
	public void registerItemRenderer(_AmmoBase ammunition, int i, ResourceLocation registryName) {
		// Intentionally left empty. Not handled server side
		
		
	}
}
