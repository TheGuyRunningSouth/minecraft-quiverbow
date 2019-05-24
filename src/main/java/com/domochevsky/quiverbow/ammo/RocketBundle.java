package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RocketBundle extends _AmmoBase
{	
	public RocketBundle()
	{
		setRegistryName(name);
	}
	public String name = "itemRocketBundle";
	@Override
	String getIconPath() { return "Bundle_Rockets"; }
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) 
	{
		list.add("Holds 8 rockets. Highly volatile.");
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Rocket Bundle"; }
	
	
	@Override
	public void addRecipes() 
	{ 
        // A bundle of rockets (8)
        /*GameRegistry.addRecipe(new ItemStack(this), "xxx", "xyx", "xxx",						
                'x', Items.FIREWORKS, 
                'y', Items.STRING
        );  
       
        // Bundle of rockets back to 8 rockets
        //GameRegistry.addShapelessRecipe(new ItemStack(Items.FIREWORKS, 8), new ItemStack(this));*/
	}
}
