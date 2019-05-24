package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import com.domochevsky.quiverbow.projectiles.SnowShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SnowCannon extends _WeaponBase
{
	public SnowCannon() { super(64); }


	private String nameInternal = "Snow Cannon";

	private int Effect = 2;			// The id of the effect
	private int Slow_Strength;		// -15% speed per level. Lvl 3 = -45%
	private int Slow_Duration;

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/SnowCannon");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/SnowCannon_Empty");
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
		entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);

		this.setCooldown(stack, this.Cooldown);	// Cooling down now

		int counter = 0;

		while (counter < 4)	// Scatter 4
		{
			this.fireShot(world, entity);	// Firing!

			if (this.consumeAmmo(stack, entity, 1)) { return; }
			// else, still has ammo left. Continue.

			counter += 1;
		}
	}


	private void fireShot(World world, Entity entity)
	{
		float spreadHor = world.rand.nextFloat() * 20 - 10;								// Spread between -5 and 5
		float spreadVert = world.rand.nextFloat() * 20 - 10;

		SnowShot snow = new SnowShot(world, entity, (float) this.Speed, spreadHor, spreadVert);

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		snow.damage = dmg;

		ShotPotion effect = new ShotPotion();

		effect.potion = MobEffects.SLOWNESS;
		effect.Strength = this.Slow_Strength;
		effect.Duration = this.Slow_Duration;

		snow.pot1 = effect;

		world.spawnEntity(snow);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Snow: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Snow: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax + " per Snowball");

		list.add(TextFormatting.GREEN + "Scatter 4 when firing.");
		list.add(TextFormatting.GREEN + "Slowness " + this.Slow_Strength + " for " + this.displayInSec(this.Slow_Duration) + " sec on hit.");

		list.add(TextFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");

		list.add(TextFormatting.YELLOW + "Craft with up to 8 Snow Blocks to reload.");

		list.add("Hoarfrost is forming around the trigger.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 1)", 1).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 2)", 2).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5).getDouble();
		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 2)", 2).getInt();
		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 15 ticks)", 15).getInt();

		this.Slow_Strength = config.get(this.nameInternal, "How strong is my Slowness effect? (default 3)", 3).getInt();
		this.Slow_Duration = config.get(this.nameInternal, "How long does my Slowness effect last? (default 40 ticks)", 40).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One redstone sprayer (empty)
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "zxz", "zbz", "aya",
					'x', Blocks.PISTON,
					'y', Blocks.TRIPWIRE_HOOK,
					'z', Blocks.WOOL,
					'a', Blocks.OBSIDIAN,
					'b', Blocks.STICKY_PISTON
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		ItemStack stack = new ItemStack(Blocks.SNOW);

		Helper.makeAmmoRecipe(stack, 1, 4, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 2, 8, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 3, 12, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 4, 16, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 5, 20, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 6, 24, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 7, 28, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 8, 32, this.getMaxDamage(), this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "SnowCannon_empty"; }
		if (this.getCooldown(stack) > 0) { return "SnowCannon_hot"; }	// Cooling down

		return "SnowCannon";	// Regular
	}
}
