package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
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
import com.domochevsky.quiverbow.ammo.RocketBundle;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.Sabot_Rocket;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Mortar_Dragon extends _WeaponBase
{
	public Mortar_Dragon() { super(8); }


	private String nameInternal = "Dragon Mortar";

	private int FireDur;
	private double ExplosionSize;

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/DragonMortar");
	}


	@Override
	public IIcon getIconFromDamage(int meta)	// This is for inventory display. Comes in with metadata. Only gets called on client side
	{
		return this.Icon; 	// Full, default
	}

*/
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) { return new ActionResult(EnumActionResult.SUCCESS, stack); }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return new ActionResult(EnumActionResult.SUCCESS, stack); }	// Is empty

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return new ActionResult(EnumActionResult.PASS, stack);
	}


	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		// Good to go (already verified)
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet

		Helper.knockUserBack(entity, this.Kickback);			// Kickback

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		// Firing
		Sabot_Rocket projectile = new Sabot_Rocket(world, entity, (float) this.Speed);
		projectile.damage = dmg;
		projectile.fireDuration = this.FireDur;
		projectile.explosionSize = this.ExplosionSize;

		world.spawnEntity(projectile); 	// Firing!

		// SFX
		entity.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
		entity.playSound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 1.0F, 0.5F);

		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), (byte) 11, (byte) 1);

		// The parent function will take care of ammo and cooldown
		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@Override
	void doCooldownSFX(World world, Entity entity) // Server side
	{
		entity.playSound(SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, 0.6F, 2.0F);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Rocket Bundles: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Rocket Bundles: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax + " per rocket.");
		list.add(TextFormatting.GREEN + "Fire for " + this.FireDur + " sec on hit.");
		list.add(TextFormatting.GREEN + "Scatter 8 on impact.");
		list.add(TextFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");
		list.add(TextFormatting.YELLOW + "Craft with up to 8 Rocket Bundles to reload.");
		list.add("Filled with rockets, strapped to more rockets.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage are my rockets dealing, at least? (default 4)", 4).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage are my rockets dealing, tops? (default 6)", 6).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5).getDouble();
		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 3)", 3).getInt();

		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 20 ticks)", 20).getInt();

		this.FireDur = config.get(this.nameInternal, "How long is what I hit on fire? (default 6s)", 6).getInt();
		this.ExplosionSize = config.get(this.nameInternal, "How big are my explosions? (default 1.0 blocks, for no terrain damage. TNT is 4.0 blocks)", 1.0).getDouble();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Dragon Mortar (Empty)
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "ipi", "isr", "tsf",
					't', Blocks.TRIPWIRE_HOOK,
					'i', Items.IRON_INGOT,
					's', Blocks.STICKY_PISTON,
					'p', Blocks.PISTON,
					'r', Items.REPEATER,
					'f', Items.FLINT_AND_STEEL
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		ItemStack ammo = Helper.getAmmoStack(RocketBundle.class, 0);

		Helper.makeAmmoRecipe(ammo, 1, 1, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 2, 2, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 3, 3, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 4, 4, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 5, 5, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 6, 6, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 7, 7, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 8, 8, this.getMaxDamage(), this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		return "MortarDragon";	// Regular
	}
}
