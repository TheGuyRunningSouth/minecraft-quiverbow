package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Part_GatlingBarrel extends _AmmoBase
{	
	public String name = "itemGatBarrel";
	public Part_GatlingBarrel()
	{
		this.setRegistryName(name);
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.MATERIALS);	// On the combat tab by default, but this isn't ammo. It's a building part
	}
	
	@Override
	String getIconPath() { return "Gatling_Barrel"; }
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) 
	{
		list.add("A barrel, waiting for 3 companions");
		list.add("and a main body."); 
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{ 
		return "Sugar Engine Barrel";
	}
	
	
	@Override
	public void addRecipes() 
	{ 
        // Sugar Gatling, barrel
        // Piston accelerators? Sticky, regular + iron walls
        /*GameRegistry.addRecipe(new ItemStack(this), "i i", "ipi", "isi",
                'i', Items.iron_ingot,
                'p', Blocks.piston,
                's', Blocks.sticky_piston*/
    	//);
	}

}
