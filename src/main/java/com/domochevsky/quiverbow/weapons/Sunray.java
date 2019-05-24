package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.SunLight;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Sunray extends _WeaponBase
{
	public Sunray() { super(1); }

	private String nameInternal = "Sunray";
	
	private int MaxTicks;
	private int LightMin;
	private int FireDur;
	
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
    {
		double dur = (1d / this.Cooldown) * (this.Cooldown - this.getCooldown(stack)); // Display durability
		return 1d - dur;	// Reverse again. Tch
    }
	
	/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{  
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/Sunray");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/Sunray_Empty");
	}
	*/
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) 
	{
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) { return new ActionResult(EnumActionResult.SUCCESS, stack); }								// Not doing this on client side
		
		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return new ActionResult(EnumActionResult.PASS, stack);
	}
	
	
	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet
		
		Helper.knockUserBack(entity, this.Kickback);			// Kickback
		
		// Firing a beam that goes through walls
		SunLight shot = new SunLight(world, entity, (float) this.Speed);

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 		// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;								// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)
		
		// The moving end point
		shot.damage = dmg;
		shot.fireDuration = this.FireDur;
		
		shot.ignoreFrustumCheck = true;
		shot.ticksInAirMax = this.MaxTicks;
		
		world.spawnEntity(shot); 	// Firing!
		
		// SFX
		entity.playSound(SoundEvents.ENTITY_BLAZE_DEATH, 0.7F, 2.0F);
		entity.playSound(SoundEvents.ENTITY_FIREWORK_BLAST, 2.0F, 0.1F);
		
		this.setCooldown(stack, this.Cooldown);
	}
	
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) 	// Overhauled default
	{
		if (world.isRemote) { return; }	// Not doing this on client side
		BlockPos oneAbove = new BlockPos(entity.posX, entity.posY + 1, entity.posZ);
		int light = (int) world.getLightBrightness(oneAbove);
   		
   		if (light >= this.LightMin) 
   		{ 
   			if (this.getCooldown(stack) > 0) { this.setCooldown(stack, this.getCooldown(stack) - 1); }	// Cooling down
   			if (this.getCooldown(stack) == 1) { this.doCooldownSFX(world, entity); }					// One tick before cooldown is done with, so SFX now
   		} 
	}
	
	
	@Override
	void doCooldownSFX(World world, Entity entity) // Server side
	{ 
		entity.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 0.5F);
		entity.playSound(SoundEvents.ENTITY_CAT_HISS, 0.6F, 2.0F);
	} 
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
	    super.addInformation(stack, world, list, flag);
	    
	    if (stack.getTagCompound() != null)
	    {
	    	double dur = (1d / Cooldown) * (Cooldown - this.getCooldown(stack));
	    	double displayDur = (dur * 100);	// Casting to int. We only need the full digits
	    	
	    	list.add(TextFormatting.BLUE + "Charge: " + (int) displayDur + "%");
	    }
	    
	    list.add(TextFormatting.BLUE + "Damage: " + DmgMin + " - " + DmgMax);
	    
	    list.add(TextFormatting.GREEN + "Sets target on fire for " + FireDur + " sec.");
	    list.add(TextFormatting.GREEN + "Passes through walls.");
	   
	    list.add(TextFormatting.RED + "Charges for " + this.displayInSec(Cooldown) + " sec after use.");
	    list.add(TextFormatting.RED + "Requires strong light to charge.");
	    
	    list.add("The refurbished beacon emits a low hum.");
    }
	
	
	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config) 
	{ 
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();
		
		this.DmgMin = config.get(this.nameInternal, "What damage are my arrows dealing, at least? (default 14)", 14).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage are my arrows dealing, tops? (default 20)", 20).getInt();
		
		this.Speed = 4.0f;
    	this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 3)", 3).getInt();
    	
    	this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 120 ticks)", 120).getInt();
    	
    	this.FireDur = config.get(this.nameInternal, "How long is what I hit on fire? (default 10s)", 10).getInt();
    	this.MaxTicks = config.get(this.nameInternal, "How long does my beam exist, tops? (default 60 ticks)", 60).getInt();
    	this.LightMin = config.get(this.nameInternal, "What light level do I need to recharge, at least? (default 12)", 12).getInt();
    	
    	this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. Too damn bright for their taste.)", false).getBoolean();
	}
    
	
	@Override
    public void addRecipes()
	{ 
		if (Enabled)
        {
			// Using a beacon and solar panels/Daylight Sensors, meaning a nether star is required. So this is a high power item
			/*GameRegistry.addRecipe(new ItemStack(this), "bs ", "oos", " rt",
            		'b', Blocks.BEACON,
            		'o', Blocks.OBSIDIAN,
            		's', Blocks.DAYLIGHT_DETECTOR,
            		't', Blocks.TRIPWIRE_HOOK,
            		'r', Items.REPEATER
            );*/
        }
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu
	}
	
	
	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{ 
		if (this.getCooldown(stack) > 0) { return "Sunray2_empty"; }	// Still charging
		
		return "Sunray2";	// Regular
	}
	
	
	/*@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list) 	// getSubItems
	{
		list.add(new ItemStack(item, 1, 0));	// Only one, and it's full
	}*/
}
