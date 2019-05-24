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

public class BoxOfFlintDust extends _AmmoBase
{

	public String name = "itemFlintDust";
	public BoxOfFlintDust()
	{
		this.setMaxDamage(16);
		this.setCreativeTab(CreativeTabs.TOOLS);	// On the combat tab by default, since this is amunition
		setRegistryName(name);
		this.setHasSubtypes(true);
	}
	
	
	@Override
	String getIconPath() { return "Bundle_Flint"; }
	
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) 
	{
		list.add("All boxed up.");
	}
	
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Box of Flint Dust"; }
	
	
	@Override
	public void addRecipes() 
	{ 
		// A box of flint dust (4 dust per flint, meaning 32 per box), merged with wooden planks
        /*GameRegistry.addShapelessRecipe(new ItemStack(this),
                Items.FLINT,
                Items.FLINT,
                Items.FLINT,
                Items.FLINT,
                Items.FLINT,
                Items.FLINT,
                Items.FLINT,
                Items.FLINT,
                Blocks.PLANKS
        );*/ 
	}
}
