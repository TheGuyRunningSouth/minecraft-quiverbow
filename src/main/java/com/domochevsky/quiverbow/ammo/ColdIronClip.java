package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ColdIronClip extends _AmmoBase
{	
	public ColdIronClip()
	{
		setRegistryName(name);
	}
	public String name = "itemColdIron";
	@Override
	String getIconPath() { return "Bundle_Frost"; }
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) 
	{
		list.add("Holds 4 ice-laced iron ingots."); 
		list.add("Cool to the touch."); 
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Cold Iron Clip"; }
	
	
	@Override
	public void addRecipes() 
	{ 
		// A bundle of ice-laced iron ingots (4), merged with a slime ball
        /*GameRegistry.addShapelessRecipe(new ItemStack(this),
                Items.IRON_INGOT,
                Items.IRON_INGOT,
                Items.IRON_INGOT,
                Items.IRON_INGOT,
                Blocks.ICE,
                Blocks.ICE,
                Blocks.ICE,
                Blocks.ICE,
                Items.SLIME_BALL
        );*/
	}
}
