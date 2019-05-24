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

public class EnderQuartzClip extends _AmmoBase
{
	public String name = "itemQuartzClip";
	public EnderQuartzClip()
	{
		this.setMaxStackSize(1);	// No stacking, since we're filling these up
		setRegistryName(name);
		this.setMaxDamage(8);
		this.setCreativeTab(CreativeTabs.COMBAT);	// On the combat tab by default, since this is amunition
		
		this.setHasSubtypes(true);
	}
	
	
	/*@SideOnly(Side.CLIENT)
	private IIcon Icon;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_Empty;
	
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1IconRegister) 
	{ 
		Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/EnderAmmo");
		Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:ammo/EnderAmmo_Empty");
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
		
		if (player.inventory.hasItemStack(new ItemStack(Items.ENDER_PEARL)) && player.inventory.hasItemStack(new ItemStack(Items.QUARTZ)))
		{
			int dmg = stack.getItemDamage() - 1;
			stack.setItemDamage(dmg);
			player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Items.ENDER_PEARL))).shrink(1);
			// We're just grabbing what we need from the inventory
			player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Items.QUARTZ))).shrink(1);
			
			// SFX
			player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.3F);
		}
		// else, doesn't have what it takes
		
		return new ActionResult(EnumActionResult.PASS, stack);
    }
	
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) 
	{
		list.add(TextFormatting.BLUE + "Ender Quartz: " + (this.getMaxDamage() - stack.getItemDamage()) + " / " + this.getMaxDamage());
		list.add(TextFormatting.YELLOW + "Use magazine to fill it with");
		list.add(TextFormatting.YELLOW + "Ender Pearls and Quartz.");
		list.add("A clip full of");
		list.add("quartz-encased ender pearls.");
		
		//if (!player.inventory.hasItem(Items.ENDER_PEARL)) { list.add(TextFormatting.RED + "You don't have ender pearls."); }
		//if (!player.inventory.hasItem(Items.QUARTZ)) { list.add(TextFormatting.RED + "You don't have quartz."); }
		//if (player.capabilities.isCreativeMode) { list.add(TextFormatting.RED + "Does not work in creative mode."); }
	}
	
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Ender Quartz Clip"; }
	
	
	@Override
	public void addRecipes() 
	{
		/*GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "xxx", "ixi", "iii",
		         'x', Items.quartz, 
		         'i', Items.iron_ingot
		 );*/
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> list) 	// getSubItems
	{
		list.add(new ItemStack(this, 1, 0));
	}
	
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) { return true; }	// Always showing this bar, since it acts as ammo display
}
