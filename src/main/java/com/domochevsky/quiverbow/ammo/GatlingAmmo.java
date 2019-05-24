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
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GatlingAmmo extends _AmmoBase
{
	public String name = "itemGatlingAmmo";
	public GatlingAmmo()
	{
		this.setMaxStackSize(1);
		this.setMaxDamage(200);
		this.setCreativeTab(CreativeTabs.COMBAT);	// On the combat tab by default, since this is amunition
		this.setRegistryName(name);
		this.setHasSubtypes(true);
	}
	
	
/*	@Override
	String getIconPath() { return "GatlingAmmo"; }
	
	
	@SideOnly(Side.CLIENT)
	private IIcon Icon;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_Empty;
	
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1IconRegister) 
	{ 
		Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/GatlingAmmo");
		Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:ammo/GatlingAmmo_Empty");
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
		
		int counter = 4;
		
		while (counter > 0)	// Doing it 4 times, to speed that process up a bit
		{
			if (player.inventory.hasItemStack(new ItemStack(Items.REEDS)) && player.inventory.hasItemStack(new ItemStack(Items.STICK)))
			{
				// ...why does this not work? Is it because I'm in creative? Yes, it is. :|
				int dmg = stack.getItemDamage() - 1;
				stack.setItemDamage(dmg);
				
				//System.out.println("Set ITEM DMG to " + dmg + ".");
				player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Items.REEDS))).shrink(1);
				player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(Items.STICK))).shrink(1);
				
				// SFX
				doSFX = true;
			}
			// else, doesn't have what it takes
			else
			{
				//player.addChatMessage(new ChatComponentText(TextFormatting.RED + "[" + this.getItemStackDisplayName(stack) + "] Can't find sticks or sugar canes."));
			}
			
			counter -= 1;
		}
		
		if (doSFX) { player.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 1.50F); }
		
		return new ActionResult(EnumActionResult.PASS, stack);
    }
	
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) 
	{
		list.add(TextFormatting.BLUE + "Sugar Rods: " + (this.getMaxDamage() - stack.getItemDamage()) + " / " + this.getMaxDamage());
		list.add(TextFormatting.YELLOW + "Use clip to fill it with Sugar");
		list.add(TextFormatting.YELLOW + "Canes and Sticks.");
		list.add("A loading helper, full of");
		list.add("sugar cane-wrapped sticks.");
		
		/*if (!player.inventory.hasItem(Items.REEDS))
		{
			list.add(TextFormatting.RED + "You don't have sugar canes.");
		}
		if (!player.inventory.hasItem(Items.STICK))
		{
			list.add(TextFormatting.RED + "You don't have sticks.");
		}
		
		if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.RED + "Does not work in creative mode.");
		}*/
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Clip of Sugar Rods"; }
	
	
	@Override
	public void addRecipes() 
	{
		// First, the clip itself (empty)
		/*GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "y y", "y y", "yxy",
		         'x', Items.iron_ingot, 
		         'y', Blocks.planks
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
