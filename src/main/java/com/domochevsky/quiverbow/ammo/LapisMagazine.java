package com.domochevsky.quiverbow.ammo;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
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

public class LapisMagazine extends _AmmoBase
{
	public String name = "itemLapisMag";
	public LapisMagazine()
	{
		this.setMaxStackSize(1);	// No stacking, since we're filling these up
		this.setRegistryName(name);
		this.setMaxDamage(150);		// Filled with lapis
		this.setCreativeTab(CreativeTabs.COMBAT);	// On the combat tab by default, since this is amunition
		
		this.setHasSubtypes(true);
	}
	
	/*
	@SideOnly(Side.CLIENT)
	private IIcon Icon_6;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_5;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_4;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_3;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_2;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_1;
	
	@SideOnly(Side.CLIENT)
	private IIcon Icon_Empty;
	
	//private ItemStack lapisStack = new ItemStack(Items.dye, 1, 4);
	
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1IconRegister) 
	{ 
		this.Icon_6 = par1IconRegister.registerIcon("quiverchevsky:ammo/LapisAmmo_6");
		this.Icon_5 = par1IconRegister.registerIcon("quiverchevsky:ammo/LapisAmmo_5");
		this.Icon_4 = par1IconRegister.registerIcon("quiverchevsky:ammo/LapisAmmo_4");
		this.Icon_3 = par1IconRegister.registerIcon("quiverchevsky:ammo/LapisAmmo_3");
		this.Icon_2 = par1IconRegister.registerIcon("quiverchevsky:ammo/LapisAmmo_2");
		this.Icon_1 = par1IconRegister.registerIcon("quiverchevsky:ammo/LapisAmmo_1");
		
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:ammo/LapisAmmo_0");
	}

	
	@Override
    public IIcon getIconFromDamage(int meta) 
    {
		if (meta == this.getMaxDamage()) { return this.Icon_Empty; }
		
		else if (meta >= 125) { return this.Icon_1; }	// Indicating fill status based on the number of blocks in here
		else if (meta >= 100) { return this.Icon_2; }
		else if (meta >= 75) { return this.Icon_3; }
		else if (meta >= 50) { return this.Icon_4; }
		else if (meta >= 25) { return this.Icon_5; }
		
		return this.Icon_6;
    }
	*/
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) 
    {  
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) { return new ActionResult(EnumActionResult.SUCCESS, stack); }				// Not doing this on client side
		if (stack.getItemDamage() == 0) { return new ActionResult(EnumActionResult.SUCCESS, stack); }	// Already fully loaded
		if (stack.getItemDamage() < 25) { return new ActionResult(EnumActionResult.SUCCESS, stack); }	// No room for another lapis block
		
		boolean doSFX = false;
		
		//if (player.inventory.hasItemStack(this.lapisStack))
		if (player.inventory.hasItemStack(new ItemStack(Item.getItemFromBlock(Blocks.LAPIS_BLOCK))))
		{
			//this.consumeItemStack(player.inventory, this.lapisStack);	// We're just grabbing what we need from the inventory
			
			int dmg = stack.getItemDamage() - 25;
			stack.setItemDamage(dmg);
			player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Item.getItemFromBlock(Blocks.LAPIS_BLOCK)))).shrink(1);
			// We're just grabbing what we need from the inventory
			
			// SFX
			doSFX = true;
		}
		// else, doesn't have what it takes
		
		if (doSFX) { player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.2F); }
		
		return new ActionResult(EnumActionResult.PASS, stack);
    }
	
	
	// Metadata sensitive version of consumeInventoryItem
	// Takes 1 from the stack
	/*private void consumeItemStack(InventoryPlayer inventory, ItemStack stack)
	{
		if (inventory == null) { return; }				// No inventory?
		if (inventory.mainInventory == null) { return; }// No items?
		if (stack == null) { return; }					// Nothing to remove?
		
		int counter = 0;
		
		while (counter < inventory.mainInventory.length)
		{
			if (inventory.mainInventory[counter] != null)
			{
				if (inventory.mainInventory[counter].getItem() == stack.getItem())
				{
					if (inventory.mainInventory[counter].getItemDamage() == stack.getItemDamage())
					{
						// Seems to check out
						if (inventory.mainInventory[counter].stackSize >= 1)
						{
							inventory.mainInventory[counter].stackSize -= 1;	// Taking from the stack
							
							if (inventory.mainInventory[counter].stackSize <= 0)	// Is it depleted now?
							{
								inventory.mainInventory[counter] = null;	// Deleting it then
							}
							
							return;	// We're done here
						}
					}
					// else, different damage value
				}
				// else, not the same item
			}
			// else, nothing in that slot
			
			counter += 1;
		}
	}*/
	
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) 
	{
		list.add(TextFormatting.BLUE + "Lapis: " + (this.getMaxDamage() - stack.getItemDamage()) + " / " + this.getMaxDamage());
		list.add(TextFormatting.YELLOW + "Use magazine to fill it with Lapis Blocks.");
		list.add("A loading helper, full of toxic blue stuff.");		
		
		//if (!player.inventory.hasItem(Item.getItemFromBlock(Blocks.LAPIS_BLOCK))) { list.add(TextFormatting.RED + "You don't have Lapis Blocks."); }
		//if (player.capabilities.isCreativeMode) { list.add(TextFormatting.RED + "Does not work in creative mode."); }
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Lapis Magazine"; }
	
	
	@Override
	public void addRecipes() 
	{
		/*GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xgx",
		         'x', Blocks.GLASS_PANE, 
		         'g', new ItemStack(Items.DYE, 1, 4)
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
