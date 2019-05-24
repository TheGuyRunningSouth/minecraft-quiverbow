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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.GoldMagazine;
import com.domochevsky.quiverbow.projectiles.CoinShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CoinTosser_Mod extends _WeaponBase
{
	private String nameInternal = "Modified Coin Tosser";
	private boolean shouldDrop;


	public CoinTosser_Mod()
	{
		super(72);	// 18 shots, meaning scatter 4 with 72 nuggets

		ItemStack gold = Helper.getAmmoStack(GoldMagazine.class, 0);
		this.setMaxDamage(gold.getMaxDamage());	// Fitting our max capacity to the magazine
	}

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/CoinTosser_Modified");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/CoinTosser_Modified_Empty");
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
			return new ActionResult(EnumActionResult.SUCCESS, stack);
		}

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return new ActionResult(EnumActionResult.PASS, stack);
	}


	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		if (this.getCooldown(stack) != 0) { return; }	// Hasn't cooled down yet

		Helper.knockUserBack(entity, this.Kickback);			// Kickback

		// SFX
		world.playSound(null, entity.posX, entity.posY, entity.posZ,  SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 3.0F);

		this.setCooldown(stack, this.Cooldown);	// Cooling down now

		boolean centerShot = true;
		int counter = 0;

		while (counter < 3)
		{
			this.fireShot(world, entity, centerShot);

			if (centerShot) { centerShot = false; }	// Only doing that once

			if (this.consumeAmmo(stack, entity, 1)) 	// We're done here
			{
				this.dropMagazine(world, stack, entity);
				return;
			}
			// else, still has ammo left. Continue.

			counter += 1;
		}
	}


	private void fireShot(World world, Entity entity, boolean centerShot)
	{
		float spreadHor = 0;
		float spreadVert = 0;

		if (!centerShot)	// Spread
		{
			float spreadMax = 4;
			float spreadHalf = spreadMax / 2;

			spreadHor = world.rand.nextFloat() * spreadMax - spreadHalf;
			spreadVert = world.rand.nextFloat() * spreadMax - spreadHalf;
		}
		// else, dead center

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 		// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;								// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		CoinShot shot = new CoinShot(world, entity, (float) this.Speed, spreadHor, spreadVert);

		shot.damage = dmg;
		shot.setDrop(this.shouldDrop);

		world.spawnEntity(shot);
	}


	private void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 60);
			return;
		}

		ItemStack clipStack = Helper.getAmmoStack(GoldMagazine.class, stack.getItemDamage());	// Unloading all ammo into that clip

		stack.setItemDamage(this.getMaxDamage());	// Emptying out

		// Creating the clip
		EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
		entityitem.setPickupDelay(10);

		// And dropping it
		if (entity.captureDrops) { entity.capturedDrops.add(entityitem); }
		else { world.spawnEntity(entityitem); }

		// SFX
		world.playSound(null, entity.posX,entity.posY, entity.posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 0.5F);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Gold Nuggets: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Gold Nuggets: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax + " per Nugget");
		list.add(TextFormatting.GREEN + "Scatter 3 when firing.");
		list.add(TextFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");
		list.add(TextFormatting.YELLOW + "Crouch-use to drop the current magazine.");
		list.add(TextFormatting.YELLOW + "Craft with a Gold Magazine to reload.");
		list.add("Retrofit with double-piston tech.");
		list.add("More efficient, but just as heavy.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing per nugget, at least? (default 1)", 1).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing per nugget, tops? (default 3)", 3).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5).getDouble();

		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 1)", 1).getInt();

		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 15 ticks)", 15).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);

		this.shouldDrop = config.get(this.nameInternal, "Do I drop gold nuggets on misses? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// Modifying the Coin Tosser with double piston tech
			/*GameRegistry.addShapelessRecipe(new ItemStack(this, 1 , this.getMaxDamage()),
					Helper.getWeaponStackByClass(CoinTosser.class, true),
					Blocks.STICKY_PISTON,
					Blocks.TRIPWIRE_HOOK,
					Items.IRON_INGOT,
					Items.IRON_INGOT
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Ammo
		Helper.registerAmmoRecipe(GoldMagazine.class, this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "CoinTosser_Mod_empty"; }	// Not loaded
		if (this.getCooldown(stack) > 0) { return "CoinTosser_Mod_hot"; }				// Cooling down

		return "CoinTosser_Mod";	// Regular
	}
}
