package com.domochevsky.quiverbow.ammo;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RedstoneMagazine extends _AmmoBase
{
	public String name = "itemRedMag";
	public RedstoneMagazine()
	{
		this.setMaxStackSize(1);	// No stacking, since we're filling these up
		this.setRegistryName(name);
		this.setMaxDamage(64);		// Filled with gold nuggets (8 shots with 9 scatter, 24 with 3 scatter)
		this.setCreativeTab(CreativeTabs.COMBAT);	// On the combat tab by default, since this is amunition
		
		this.setHasSubtypes(true);
	}
	
	/*
	@SideOnly(Side.CLIENT)
	private IIcon Icon;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_Empty;
	
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1IconRegister) 
	{ 
		Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/RedstoneAmmo");
		Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:ammo/RedstoneAmmo_Empty");
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
		if (world.isRemote) { return new ActionResult(EnumActionResult.SUCCESS, stack); }				// Not doing this on client side
		if (stack.getItemDamage() == 0) { return new ActionResult(EnumActionResult.SUCCESS, stack); }	// Already fully loaded
		
		if (player.isSneaking())
		{
			this.fillEight(stack, world, player);
			return new ActionResult(EnumActionResult.PASS, stack);
		}
		// else, not sneaking, so just filling one
		
		boolean doSFX = false;
		
		if (player.inventory.hasItemStack(new ItemStack(Items.REDSTONE)))
		{
			int dmg = stack.getItemDamage() - 1;
			stack.setItemDamage(dmg);
			
			player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Items.REDSTONE))).shrink(1);
			//player.inventory.consumeInventoryItem(Items.REDSTONE);	// We're just grabbing what we need from the inventory
			
			// SFX
			doSFX = true;
		}
		// else, doesn't have what it takes
		
		if (doSFX) { player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.3F); }
		
		return new ActionResult(EnumActionResult.SUCCESS, stack);
    }
	
	
	private void fillEight(ItemStack stack, World world, EntityPlayer player)
	{
		boolean doSFX = false;
		
		int counter = 0;
		
		while (counter < 8)
		{
			if (player.inventory.hasItemStack(new ItemStack(Items.REDSTONE)))
			{
				int dmg = stack.getItemDamage() - 1;
				stack.setItemDamage(dmg);
				
				player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Items.REDSTONE))).shrink(1);
				//player.inventory.consumeInventoryItem(Items.REDSTONE);	// We're just grabbing what we need from the inventory
				
				doSFX = true;
			}
			// else, doesn't have what it takes
			
			counter += 1;
		}
		
		if (doSFX) { player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.2F); }
	}
	
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) 
	{
		list.add(TextFormatting.BLUE + "Redstone: " + (this.getMaxDamage() - stack.getItemDamage()) + " / " + this.getMaxDamage());
		list.add(TextFormatting.YELLOW + "Use magazine to fill it with Redstone.");
		list.add(TextFormatting.YELLOW + "Crouch-use to fill it with 8 Redstone.");
		list.add("A loading helper, full of red dust.");
		
		//if (!player.inventory.hasItem(Items.REDSTONE)) { list.add(TextFormatting.RED + "You don't have Redstone."); }
		//if (player.capabilities.isCreativeMode) { list.add(TextFormatting.RED + "Does not work in creative mode."); }
	}
	
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Redstone Magazine"; }
	
	
	@Override
	public void addRecipes() 
	{
		/*GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xgx",
		         'x', Items.iron_ingot,
		         'g', Items.redstone
		 );*/
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> list) 	// getSubItems
	{
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, this.getMaxDamage()));
	}
	
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) { return true; }	// Always showing this bar, since it acts as ammo display
}
