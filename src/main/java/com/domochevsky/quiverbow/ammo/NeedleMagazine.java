package com.domochevsky.quiverbow.ammo;

import java.util.List;

import javax.annotation.Nonnull;

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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NeedleMagazine extends _AmmoBase
{
	public String name = "itemNeedleMag";
	public NeedleMagazine()
	{
		super();
		this.setRegistryName(name);
		this.setMaxStackSize(1);	// No stacking, since we're filling these up
		this.setMaxDamage(64);		// Filled with cactus thorns
		this.setHasSubtypes(true);	// Filled and empty
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
		Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/NeedleAmmo");
		Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:ammo/NeedleAmmo_Empty");
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
		
		if (player.inventory.hasItemStack(new ItemStack(Item.getItemFromBlock(Blocks.CACTUS))))
		{
			int dmg = stack.getItemDamage() - 1;
			stack.setItemDamage(dmg);
			player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Item.getItemFromBlock(Blocks.CACTUS)))).shrink(1);
			//player.inventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.CACTUS));	// We're just grabbing what we need from the inventory
			
			// SFX
			doSFX = true;
		}
		// else, doesn't have what it takes
		
		if (doSFX) { player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 1.3F); }
		
		return new ActionResult(EnumActionResult.PASS, stack);
    }
	
	
	private void fillEight(ItemStack stack, World world, EntityPlayer player)
	{
		boolean doSFX = false;
		
		int counter = 0;
		
		while (counter < 8)
		{
			if (player.inventory.hasItemStack(new ItemStack(Item.getItemFromBlock(Blocks.CACTUS))))
			{
				int dmg = stack.getItemDamage() - 1;
				stack.setItemDamage(dmg);
				player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Item.getItemFromBlock(Blocks.CACTUS)))).shrink(1);
				//player.inventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.CACTUS));	// We're just grabbing what we need from the inventory
				
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
		list.add(TextFormatting.BLUE + "Thorns: " + (this.getMaxDamage() - stack.getItemDamage()) + " / " + this.getMaxDamage());
		list.add(TextFormatting.YELLOW + "Use magazine to fill it with Cacti.");
		list.add(TextFormatting.YELLOW + "Crouch-use to fill it with 8 Cacti.");
		list.add("A loading helper, full of cactus thorns.");
		
		//if (!player.inventory.hasItem(Item.getItemFromBlock(Blocks.CACTUS))) { list.add(TextFormatting.RED + "You don't have cacti."); }
		//if (player.capabilities.isCreativeMode) { list.add(TextFormatting.RED + "Does not work in creative mode."); }
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Thorn Magazine"; }
	
	
	@Override
	public void addRecipes() 
	{
		/*GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xix",
		         'x', Items.leather, 
		         'i', Items.iron_ingot
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
