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

public class ObsidianMagazine extends _AmmoBase
{
	public String name = "itemObsidianMag";
	public ObsidianMagazine()
	{
		this.setMaxStackSize(1);
		this.setMaxDamage(16);
		this.setCreativeTab(CreativeTabs.COMBAT);	// On the combat tab by default, since this is amunition
		this.setRegistryName(name);
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
		Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/ObsidianAmmo");
		Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:ammo/ObsidianAmmo_Empty");
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
		
		Item obs = Item.getItemFromBlock(Blocks.OBSIDIAN);
		
		if (player.inventory.hasItemStack(new ItemStack(Items.GUNPOWDER)) && player.inventory.hasItemStack(new ItemStack(obs)))
		{
			int dmg = stack.getItemDamage() - 1;
			stack.setItemDamage(dmg);
			
			player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Items.GUNPOWDER))).shrink(1);
			player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(obs))).shrink(1);
			// We're just grabbing what we need from the inventory
			
			
			// SFX
			doSFX = true;
		}
		// else, doesn't have what it takes
		
		if (doSFX) { player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.30F); }
		
		return new ActionResult(EnumActionResult.PASS, stack);
    }
	
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) 
	{
		list.add(TextFormatting.BLUE + "Splints: " + (this.getMaxDamage() - stack.getItemDamage()) + " / " + this.getMaxDamage());
		
		list.add(TextFormatting.YELLOW + "Use magazine to fill it with");
		list.add(TextFormatting.YELLOW + "Gunpowder and Obsidian.");
		
		list.add("A loading helper, full of");
		list.add("obsidian splints.");
		
		//if (!player.inventory.hasItem(Items.GUNPOWDER)) { list.add(TextFormatting.RED + "You don't have gunpowder."); }
		//if (!player.inventory.hasItem(Item.getItemFromBlock(Blocks.OBSIDIAN))) { list.add(TextFormatting.RED + "You don't have OBSIDIAN."); }
		
		//if (player.capabilities.isCreativeMode) { list.add(TextFormatting.RED + "Does not work in creative mode."); }
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Obsidian Magazine"; }
	
	
	@Override
	public void addRecipes() 
	{
		/*GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xox",
		         'x', Items.iron_ingot, 
		         'o', Blocks.OBSIDIAN
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
