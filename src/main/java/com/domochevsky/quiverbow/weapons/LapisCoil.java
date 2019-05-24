package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
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
import com.domochevsky.quiverbow.ammo.LapisMagazine;
import com.domochevsky.quiverbow.projectiles.LapisShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LapisCoil extends _WeaponBase
{
	public LapisCoil()
	{
		super(100);

		ItemStack ammo = Helper.getAmmoStack(LapisMagazine.class, 0);
		this.setMaxDamage(ammo.getMaxDamage());	// Fitting our max capacity to the magazine
	}

	private String nameInternal = "Lapis Coil";

	int Weakness_Strength;
	int Weakness_Duration;
	int Nausea_Duration;
	int Hunger_Strength;
	int Hunger_Duration;

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/LapisCoil");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/LapisCoil_Empty");
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
		// SFX
		entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.5F);
		entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 3.0F);

		// Gas
		ShotPotion effect1 = new ShotPotion();

		effect1.potion = MobEffects.NAUSEA;	// Nausea
		effect1.Strength = 1;
		effect1.Duration = this.Nausea_Duration;

		ShotPotion effect2 = new ShotPotion();

		effect2.potion = MobEffects.HUNGER;
		effect2.Strength = this.Hunger_Strength;
		effect2.Duration = this.Hunger_Duration;

		ShotPotion effect3 = new ShotPotion();

		effect3.potion = MobEffects.WEAKNESS;
		effect3.Strength = this.Weakness_Strength;
		effect3.Duration = this.Weakness_Duration;

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		// Projectile
		LapisShot projectile = new LapisShot(world, entity, (float) this.Speed);
		projectile.damage = dmg;

		projectile.ticksInGroundMax = 100;	// 5 sec before it disappears

		projectile.pot1 = effect1;
		projectile.pot2 = effect2;
		projectile.pot3 = effect3;

		world.spawnEntity(projectile); 		// Firing!

		this.setCooldown(stack, 4);	// For visual purposes
		if (this.consumeAmmo(stack, entity, 1)) { this.dropMagazine(world, stack, entity); }
	}


	private void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 60);
			return;
		}

		ItemStack clipStack = Helper.getAmmoStack(LapisMagazine.class, stack.getItemDamage());	// Unloading all ammo into that clip

		stack.setItemDamage(this.getMaxDamage());	// Emptying out

		// Creating the clip
		EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
		entityitem.setPickupDelay(10);

		// And dropping it
		if (entity.captureDrops) { entity.capturedDrops.add(entityitem); }
		else { world.spawnEntity(entityitem); }

		// SFX
		//world.playSoundAtEntity(entity, "random.break", 1.0F, 0.5F);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Lapis: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Lapis: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax);

		list.add(TextFormatting.GREEN + "Weakness " + this.Weakness_Strength + " for " + this.displayInSec(this.Nausea_Duration) + " sec on hit.");
		list.add(TextFormatting.GREEN + "Nausea 1 for " + this.displayInSec(this.Nausea_Duration) + " sec on hit.");
		list.add(TextFormatting.GREEN + "Hunger " + this.Hunger_Strength + " for " + this.displayInSec(this.Hunger_Duration) + " sec on hit.");

		list.add(TextFormatting.YELLOW + "Crouch-use to drop the current magazine.");
		list.add(TextFormatting.YELLOW + "Craft with a Lapis Magazine to reload.");

		list.add("Redstone-powered and highly toxic.");
		list.add("It's covered in blue dust.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 1)", 1).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 3)", 3).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5).getDouble();

		this.Weakness_Strength = config.get(this.nameInternal, "How strong is my Weakness effect? (default 2)", 2).getInt();
		this.Weakness_Duration = config.get(this.nameInternal, "How long does my Weakness effect last? (default 40 ticks)", 40).getInt();
		this.Nausea_Duration = config.get(this.nameInternal, "How long does my Nausea effect last? (default 40 ticks)", 40).getInt();
		this.Hunger_Strength = config.get(this.nameInternal, "How strong is my Hunger effect? (default 2)", 2).getInt();
		this.Hunger_Duration = config.get(this.nameInternal, "How long does my Hunger effect last? (default 40 ticks)", 40).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One lapis coil (empty)
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "z z", "axa", " y ",
					'x', Blocks.PISTON,
					'y', Blocks.LEVER,
					'z', Items.IRON_INGOT,
					'a', Items.REPEATER
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		Helper.registerAmmoRecipe(LapisMagazine.class, this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "LapisCoil2_empty"; }		// Empty
		if (this.getCooldown(stack) > 0) { return "LapisCoil2_hot"; }	// Hot

		return "LapisCoil2";	// Regular
	}
}