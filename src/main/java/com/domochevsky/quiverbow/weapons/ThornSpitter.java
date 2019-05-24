package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.NeedleMagazine;
import com.domochevsky.quiverbow.projectiles.Thorn;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ThornSpitter extends _WeaponBase
{
	public ThornSpitter() 
	{ 
		super(64); 
		
		ItemStack ammo = Helper.getAmmoStack(NeedleMagazine.class, 0);
		this.setMaxDamage(ammo.getMaxDamage());	// Fitting our max capacity to the magazine
	}

	private String nameInternal = "Thorn Spitter";
	
	/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{  
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/ThornSpitter");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/ThornSpitter_Empty");
	}
	*/
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) 
	{
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) { return new ActionResult(EnumActionResult.SUCCESS, stack); }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return new ActionResult(EnumActionResult.SUCCESS, stack); }	// Is empty

		if (player.isSneaking())	// Dropping the magazine
		{
			this.dropMagazine(world, stack, player);
			return new ActionResult(EnumActionResult.PASS, stack);
		}
		
		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return new ActionResult(EnumActionResult.PASS, stack);
	}
	
	
	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet
		this.setCooldown(stack, this.Cooldown);
		
		this.setBurstFire(stack, 4);		// Setting the thorns left to shoot to 4, then going through that via onUpdate
	}
	
	
	private void dropMagazine(World world, ItemStack stack, Entity entity) 
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{ 
			this.setCooldown(stack, 40);
			return; 
		}
		
		ItemStack clipStack = Helper.getAmmoStack(NeedleMagazine.class, stack.getItemDamage());	// Unloading all ammo into that clip
		
		stack.setItemDamage(this.getMaxDamage());	// Emptying out
		
		// Creating the clip
    	EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
    	entityitem.setPickupDelay(10);
        
        // And dropping it
        if (entity.captureDrops) { entity.capturedDrops.add(entityitem); }
        else { world.spawnEntity(entityitem); }
        
        // SFX
        entity.playSound(SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, 1.7F, 1.3F);
	}
	
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) 	// Overhauled default
	{
		if (world.isRemote) { return; }	// Not doing this on client side
		
		if (this.getCooldown(stack) > 0) { this.setCooldown(stack, this.getCooldown(stack) - 1); }	// Cooling down
		if (this.getCooldown(stack) == 1) { this.doCooldownSFX(world, entity); }					// One tick before cooldown is done with, so SFX now
		
		if (this.getBurstFire(stack) > 0)
		{
			this.setBurstFire(stack, this.getBurstFire(stack) - 1); // One done
			
			if (stack.getItemDamage() < stack.getMaxDamage() && holdingItem)	// Can only do it if we're loaded and holding the weapon
			{
				this.doBurstFire(stack, world, entity);
				
				if (this.consumeAmmo(stack, entity, 1)) 	// We're done here
				{ 
					this.dropMagazine(world, stack, entity);
					return;
				}
				// else, still has ammo left. Continue.
			}
			// else, either not loaded or not held
		}
	}
	
	
	private void doBurstFire(ItemStack stack, World world, Entity entity) 
	{ 
		int dmg_range = DmgMax - DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)
		
		// Firing
		Thorn projectile = new Thorn(world, (EntityLivingBase) entity, (float) Speed);
		projectile.damage = dmg;
		
		world.spawnEntity(projectile); 
		
		// SFX
		entity.playSound(SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, 0.6F, 0.6F);
	}
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
	    super.addInformation(stack, world, list, flag);
	    
	    /*if (player.capabilities.isCreativeMode)
	    {
		    list.add(TextFormatting.BLUE + "Thorns: INFINITE / " + this.getMaxDamage());
	    }
	    else
	    {*/
	    	int ammo = this.getMaxDamage() - this.getDamage(stack);
		    list.add(TextFormatting.BLUE + "Thorns: " + ammo + " / " + this.getMaxDamage());
	    //}
	    
    	list.add(TextFormatting.BLUE + "Damage: " + DmgMin + " - " + DmgMax);
    	
    	list.add(TextFormatting.GREEN + "Burst 4 when firing.");
    	
    	list.add(TextFormatting.RED + "Cooldown for " + this.displayInSec(Cooldown) + " sec on use.");
	    
    	list.add(TextFormatting.YELLOW + "Craft with a Thorn");
	    list.add(TextFormatting.YELLOW + "Magazine to reload.");
	   
	    list.add("Built with experimental quad-piston tech.");
    }
	
	
	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config) 
	{ 
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();
		
		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 1)", 1).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 2)", 2).getInt();
		
		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 1.75 BPT (Blocks Per Tick))", 1.75).getDouble();
    	
    	this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 10 ticks)", 10).getInt();
    	
    	this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}
    
	
	@Override
    public void addRecipes()
	{ 
		if (Enabled)
        {            
            // One Thorn Spitter (empty)
    		/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "bib", "php", "sts",
	                't', Blocks.TRIPWIRE_HOOK,
	                'b', Blocks.IRON_BARS,
	                'i', Items.IRON_INGOT,
	                'h', Blocks.HOPPER,
	         		's', Blocks.STICKY_PISTON,
	         		'p', Blocks.PISTON
	        );*/
        }
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu
		
		Helper.registerAmmoRecipe(NeedleMagazine.class, this);
	}
	
	
	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{ 
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "ThornSpitter_empty"; }
		if (this.getCooldown(stack) > 0) { return "ThornSpitter_hot"; }	// Cooling down
		
		return "ThornSpitter";	// Regular
	}
}