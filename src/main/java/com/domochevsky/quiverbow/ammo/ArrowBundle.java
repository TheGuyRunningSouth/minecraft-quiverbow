package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ArrowBundle extends _AmmoBase
{	
	
	private final String name = "itemarrowbundle";
	
	
	public ArrowBundle()
	{
		setRegistryName(name);
	}
	@Override
	String getIconPath() { return "Bundle_Arrows"; }
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) 
	{
		list.add("Holds 8 arrows, tightly packed.");
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Arrow Bundle"; }
	
	
	@Override
	public void addRecipes() 
	{ 
		/*// One arrow bundle, holding 8 arrows
		GameRegistry.addRecipe(new ItemStack(this), "xxx", "xyx", "xxx",
                'x', Items.ARROW,
                'y', Items.STRING
        );
		
		// Bundle of arrows back to 8 arrows
        GameRegistry.addShapelessRecipe(new ItemStack(Items.ARROW, 8), new ItemStack(this) );*/
	}
	
	public String getName()
	{
		return name;
	}
}
