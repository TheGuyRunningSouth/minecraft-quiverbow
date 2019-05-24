package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class LargeRocket extends _AmmoBase
{	
	public LargeRocket()
	{
		setRegistryName(name);
	}
	public String name = "itemRocketLarge";
	@Override
	String getIconPath() { return "Bundle_BigRocket"; }
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) 
	{
		list.add("A big rocket. Very dangerous.");
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Big Rocket"; }
	
	
	@Override
	public void addRecipes() 
	{ 
		// A big rocket
    	/*GameRegistry.addRecipe(new ItemStack(this), "zaa", "aya", "aab",
                'y', Blocks.tnt,
        		'z', Blocks.planks,
        		'a', Items.paper,
        		'b', Items.string
    	);*/
	}
}
