package com.domochevsky.quiverbow.ammo;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class SeedJar extends _AmmoBase
{
	public String name = "itemSeedJar";
	public SeedJar()	// Holds seeds for the Seed Sweeper (512, for 8 per shot with 64 shots total), loaded directly into the weapon
	{
		this.setMaxStackSize(1);	// No stacking, since we're filling these up
		this.setRegistryName(name);
		this.setMaxDamage(512);		// Filled with gold nuggets (8 shots with 9 scatter, 24 with 3 scatter)
		this.setCreativeTab(CreativeTabs.COMBAT);	// On the combat tab by default, since this is amunition
		
		this.setHasSubtypes(true);
	}
	
	
	@Override
	String getIconPath() { return "SeedJar"; }
	
	/*
	@SideOnly(Side.CLIENT)
	private IIcon Icon;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_Empty;
	
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1IconRegister) 
	{ 
		Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/SeedJar");
		Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:ammo/SeedJar_Empty");
	}
	
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass)	// This is for on-hand display. The difference may be useful later
	{
		if (stack.getItemDamage() == this.getMaxDamage()) { return Icon_Empty; }
		
		return Icon;
	}
	
	
	@Override
    public IIcon getIconFromDamage(int meta) 
    {
		if (meta == this.getMaxDamage()) { return Icon_Empty; }
		
		return Icon;
    }
	*/
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) 
    {  
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) { return new ActionResult(EnumActionResult.SUCCESS, stack); }	// Not doing this on client side
		
		if (stack.getItemDamage() == 0) { return new ActionResult(EnumActionResult.SUCCESS, stack); }	// Already fully loaded
		
		boolean doSFX = false;
		
		int counter = 8;
		
		while (counter > 0)	// Doing it 8 times, to speed that process up a bit
		{
			boolean proceed = false;
			
			if (player.inventory.hasItemStack(new ItemStack(Items.MELON_SEEDS)))
			{				
				player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Items.MELON_SEEDS))).shrink(1);
				proceed = true;
			}
			
			else if (player.inventory.hasItemStack(new ItemStack(Items.PUMPKIN_SEEDS)))
			{
				player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Items.PUMPKIN_SEEDS))).shrink(1);
				proceed = true;
			}
			
			else if (player.inventory.hasItemStack(new ItemStack(Items.WHEAT_SEEDS)))
			{
				player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Items.WHEAT_SEEDS))).shrink(1);
				proceed = true;
			}
			// else, doesn't have what it takes
			
			if (proceed)
			{
				int dmg = stack.getItemDamage() - 1;
				stack.setItemDamage(dmg);
				doSFX = true;
			}
			
			counter -= 1;
		}
		
		if (doSFX) { player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 0.7F); }
		
		return new ActionResult(EnumActionResult.PASS, stack);
    }
	
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) 
	{
		list.add(TextFormatting.BLUE + "Seeds: " + (this.getMaxDamage() - stack.getItemDamage()) + " / " + this.getMaxDamage());
		
		list.add(TextFormatting.YELLOW + "Use jar to fill it with seeds.");
		
		/*if (player.inventory.hasItem(Items.MELON_SEEDS)) { list.add(TextFormatting.GREEN + "You have melon seeds."); }
		if (player.inventory.hasItem(Items.PUMPKIN_SEEDS)) { list.add(TextFormatting.GREEN + "You have pumpkin seeds."); }
		if (player.inventory.hasItem(Items.WHEAT_SEEDS)) { list.add(TextFormatting.GREEN + "You have wheat seeds."); }
		
		if (player.capabilities.isCreativeMode) { list.add(TextFormatting.RED + "Does not work in creative mode."); }*/
		
		list.add("You could preserve apples with this, too.");
	}
	
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) { return "Seed Jar"; }
	
	
	@Override
	public void addRecipes() 
	{
	/*	GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "gwg", "g g", "gig",
		         'g', Blocks.GLASS_PANE, 
		         'i', Items.IRON_INGOT,
		         'w', Blocks.WOODEN_BUTTON
		 ); */
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> list) 	// getSubItems
	{
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, this.getMaxDamage() ));
	}
	
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) { return true; }	// Always showing this bar, since it acts as ammo display
}
