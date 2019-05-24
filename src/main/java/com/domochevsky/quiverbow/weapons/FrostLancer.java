package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ShotPotion;
import com.domochevsky.quiverbow.ammo.ColdIronClip;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.ColdIron;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FrostLancer extends _WeaponBase
{
	public FrostLancer() { super(4); }

	private String nameInternal = "Frost Lancer";

	public int ZoomMax;

	public int Slowness_Str;
	public int Slowness_Dur;

	private int Nausea_Str;
	private int Nausea_Dur;

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/FrostLancer");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/FrostLancer_Empty");
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
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet

		Helper.knockUserBack(entity, this.Kickback);			// Kickback

		// SFX
		entity.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.8F, 1.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), (byte) 3, (byte) 1);	// smoke

		// Firing
		ColdIron shot = new ColdIron(world, entity, (float) this.Speed);

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		shot.damage = dmg;

		shot.knockbackStrength = this.Knockback;

		// Gas
		ShotPotion effect1 = new ShotPotion();

		effect1.potion = MobEffects.SLOWNESS;	// Slowness
		effect1.Strength = this.Slowness_Str;
		effect1.Duration = this.Slowness_Dur;

		shot.pot1 = effect1;

		ShotPotion effect2 = new ShotPotion();

		effect2.potion = MobEffects.HUNGER;
		effect2.Strength = 1;
		effect2.Duration = this.Nausea_Dur;

		shot.pot2 = effect2;

		world.spawnEntity(shot); 	// Firing!

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) 	// Overhauled
	{
		if (world.isRemote) // Not doing this on client side
		{
			//ZoomInterface.checkClientZoom(world, entity, stack, this.ZoomMax);	// client zoom
			return;
		}

		if (this.getCooldown(stack) > 0) { this.setCooldown(stack, this.getCooldown(stack) - 1); }	// Cooling down
		if (this.getCooldown(stack) == 1) { this.doCooldownSFX(world, entity); }					// One tick before cooldown is done with, so SFX now
	}



	@Override
	void doCooldownSFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, 0.7F, 0.2F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), (byte) 3, (byte) 1);	// smoke
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Cold Iron: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Cold Iron: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax);

		list.add(TextFormatting.GREEN + "Slowness " + this.Slowness_Str + " for " + this.displayInSec(this.Slowness_Dur) + " sec on hit.");
		list.add(TextFormatting.GREEN + "Nausea 1 for " + this.displayInSec(this.Nausea_Dur) + " sec on hit.");

		list.add(TextFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");

		list.add(TextFormatting.YELLOW + "Craft with 1 Cold Iron Clip to reload.");
		list.add(TextFormatting.YELLOW + "Crouch to zoom.");

		list.add("A quartz ender-eye scope is attached.");
		list.add("It's staring past you with aloof disdain.");

		if (this.getCooldown(stack) > 0)
		{
			list.add(TextFormatting.RED + "COOLING DOWN! (" + this.getCooldown(stack) + ")");
		}
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 9)", 9).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 18)", 18).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 3.5 BPT (Blocks Per Tick))", 3.5).getDouble();

		this.Knockback = config.get(this.nameInternal, "How hard do I knock the target back when firing? (default 3)", 3).getInt();
		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 4)", 4).getInt();

		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 40 ticks)", 40).getInt();

		this.ZoomMax = config.get(this.nameInternal, "How far can I zoom in? (default 20. Lower means more zoom)", 20).getInt();

		this.Slowness_Str = config.get(this.nameInternal, "How strong is my Slowness effect? (default 3)", 3).getInt();
		this.Slowness_Dur = config.get(this.nameInternal, "How long does my Slowness effect last? (default 120 ticks)", 120).getInt();

		this.Nausea_Dur = config.get(this.nameInternal, "How long does my Nausea effect last? (default 120 ticks)", 120).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// Upgrade of the EnderRifle

			// One Frost Lancer (empty)
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "qiq", "prs", " o ",
					'o', Blocks.OBSIDIAN,
					'q', Items.QUARTZ,
					'i', Items.IRON_INGOT,
					'p', Blocks.PISTON,
					's', Blocks.STICKY_PISTON,
					'r', Helper.getWeaponStackByClass(EnderRifle.class, true)	// One empty Ender Rifle
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Reloading with one Frost Clip
		/*GameRegistry.addShapelessRecipe(new ItemStack(this),
				new ItemStack(this, 1 , this.getMaxDamage()),
				Helper.getAmmoStack(ColdIronClip.class, 0)
				);*/
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "FrostLancer_empty"; }
		if (this.getCooldown(stack) > 0) { return "FrostLancer_hot"; }	// Cooling down

		return "FrostLancer";	// Regular
	}
}
