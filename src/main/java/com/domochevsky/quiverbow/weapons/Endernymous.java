package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
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
import com.domochevsky.quiverbow.ammo.EnderQuartzClip;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderAno;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Endernymous extends _WeaponBase
{
	public Endernymous()
	{
		super(8); 	// Max ammo placeholder

		ItemStack ammo = Helper.getAmmoStack(EnderQuartzClip.class, 0);
		this.setMaxDamage(ammo.getMaxDamage());	// Fitting our max capacity to the magazine
	}


	private String nameInternal = "Hidden Ender Pistol";
	private int MaxTicks;

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderNymous");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderNymous_Empty");
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
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side, mob usable
	{
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet

		Helper.knockUserBack(entity, this.Kickback);			// Kickback

		// SFX
		entity.playSound(SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, 1.4F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), (byte) 6, (byte) 4);

		this.setCooldown(stack, this.Cooldown);	// Cooling down now

		this.fireShot(world, entity);	// Firing!

		if (this.consumeAmmo(stack, entity, 1)) 	// We're done here
		{
			this.dropMagazine(world, stack, entity);
		}
	}


	// Single firing action for something that fires multiple per trigger
	private void fireShot(World world, Entity entity)
	{
		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 1 and 10 (inclusive both)
		dmg += this.DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		EnderAno shot = new EnderAno(world, entity, (float) this.Speed);
		shot.damage = dmg;
		shot.ticksInAirMax = this.MaxTicks;

		world.spawnEntity(shot); 	// Firing
	}


	private void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 60);
			return;
		}

		ItemStack clipStack = Helper.getAmmoStack(EnderQuartzClip.class, stack.getItemDamage());	// Unloading all ammo into that clip

		stack.setItemDamage(this.getMaxDamage());	// Emptying out

		// Creating the clip
		EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
		entityitem.setPickupDelay(10);

		// And dropping it
		if (entity.captureDrops) { entity.capturedDrops.add(entityitem); }
		else { world.spawnEntity(entityitem); }

		// SFX
		entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.3F);
	}


	@Override
	void doCooldownSFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.BLOCK_GLASS_STEP, 0.3F, 0.3F); // Not quite sure which one random.glass was tbh
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (world.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Ender Quartz: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Ender Quartz: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax);
		list.add(TextFormatting.GREEN + "Anonymous.");
		list.add(TextFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");
		list.add(TextFormatting.YELLOW + "Crouch-use to drop the");
		list.add(TextFormatting.YELLOW + "current clip.");
		list.add(TextFormatting.YELLOW + "Craft with an Ender Quartz");
		list.add(TextFormatting.YELLOW + "Clip to reload.");
		list.add("A weapon for those desiring");
		list.add("to stay unknown.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 16)", 16).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 24)", 24).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 5.0 BPT (Blocks Per Tick))", 5.0).getDouble();
		this.MaxTicks = config.get(this.nameInternal, "How long does my projectile exist, tops? (default 40 ticks)", 40).getInt();

		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 1)", 1).getInt();

		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 20 ticks)", 20).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "e e", "ofo", "oto",
					'o', Blocks.OBSIDIAN,
					'e', Blocks.END_STONE,
					't', Blocks.TRIPWIRE_HOOK,
					'f', Items.FLINT_AND_STEEL
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Ammo
		Helper.registerAmmoRecipe(EnderQuartzClip.class, this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (this.getCooldown(stack) > 0) { return "EnderNymous_hot"; }	// Cooling down
		return "EnderNymous";
	}
}
