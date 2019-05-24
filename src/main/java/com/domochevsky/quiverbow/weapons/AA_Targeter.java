package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Main;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AA_Targeter extends _WeaponBase
{
	public AA_Targeter() 
	{ 
		super(1); 
		this.setCreativeTab(CreativeTabs.TOOLS);	// This is a tool
	}	// Not consuming ammo	
	
	private String nameInternal = "Arms Assistant Targeting Helper";
	
	public double targetingDistance = 64;
	
/*	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister) { this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/AA_Targeter"); }
	
	
	@Override								
	public IIcon getIconFromDamage(int meta) { return this.Icon; }
	*/
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) 
	{
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) { return new ActionResult(EnumActionResult.SUCCESS, stack); }	// Not doing this on client side
		
		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		
		return new ActionResult(EnumActionResult.PASS,stack);
	}
	
	
	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side, mob usable
	{
		this.setDamage(stack, 1);	// Set to be firing
		this.setCooldown(stack, 4);	// 4 ticks
		
		// SFX
       // world.playSoundAtEntity(entity, "random.click", 0.3F, 2.0F);
	}
	
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) 	// Overhauled default
	{
		if (world.isRemote) { return; }	// Not doing this on client side
		
		if (this.getCooldown(stack) > 0) // Active right now, so ticking down
		{ 
			this.setCooldown(stack, this.getCooldown(stack) - 1); 
			
			if (this.getCooldown(stack) >= 0) { this.setDamage(stack, 0); }	// Back to inactive
		}
	}
	
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) { return false; }	// No point in showing the bar for this
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
	    super.addInformation(stack, world, list, flag);
	    
    	list.add(TextFormatting.BLUE + "Use to let Arms Assistants fire at a common target.");
    	list.add(TextFormatting.BLUE + "When riding an AA: Use to fire weapon rails.");
    	list.add(TextFormatting.RED + "All involved AA need the Communications Upgrade.");
    	list.add("'Over there!'");
    }
	
	
	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config) 
	{ 
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();
		
		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. They're not friends with AAs.)", false).getBoolean(true);
	}
    
	
	@Override
    public void addRecipes() 
	{ 
		if (Enabled)
        {
		/*	GameRegistry.addRecipe(new ItemStack(this), "bi ", "iri", " it",
            		'b', Blocks.NOTEBLOCK,
            		'r', Items.REPEATER,
            		't', Blocks.TRIPWIRE_HOOK,
            		'i', Items.IRON_INGOT
            );*/
        }
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu
	}
	
	
	/*@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list)
	{
		list.add(new ItemStack(item, 1, 0));
	}*/
	
	
	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{ 
		return "AATH";	// Regular
	}
}
