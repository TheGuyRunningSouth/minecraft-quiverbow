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
import com.domochevsky.quiverbow.ammo.LargeRedstoneMagazine;
import com.domochevsky.quiverbow.projectiles.RedSpray;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RedSprayer extends _WeaponBase
{
	public RedSprayer()
	{
		super(200);

		ItemStack ammo = Helper.getAmmoStack(LargeRedstoneMagazine.class, 0);
		this.setMaxDamage(ammo.getMaxDamage());	// Fitting our max capacity to the magazine
	}

	private String nameInternal = "Redstone Sprayer";

	private int Wither_Strength;
	private int Wither_Duration;
	private int Blindness_Duration;

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/RedSprayer");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/RedSprayer_Empty");
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
		this.setCooldown(stack, this.Cooldown);

		// SFX
		//entity.world.playSoundAtEntity(entity, "random.fizz", 0.7F, 1.5F);

		int counter = 0;

		while (counter < 5)
		{
			this.fireSingle(world, entity);

			if (this.consumeAmmo(stack, entity, 1)) 	// We're done here
			{
				this.dropMagazine(world, stack, entity);
				return;
			}
			// else, still has ammo left. Continue.

			counter += 1;
		}
	}


	private void fireSingle(World world, Entity entity)
	{
		// Gas
		ShotPotion effect1 = new ShotPotion();

		effect1.potion = MobEffects.WITHER;
		effect1.Strength = this.Wither_Strength;
		effect1.Duration = this.Wither_Duration;

		ShotPotion effect2 = new ShotPotion();

		effect2.potion = MobEffects.BLINDNESS;
		effect2.Strength = 1;
		effect2.Duration = this.Blindness_Duration;

		// Spread
		float spreadHor = world.rand.nextFloat() * 20 - 10;								// Spread between -10 and 10
		float spreadVert = world.rand.nextFloat() * 20 - 10;

		RedSpray shot = new RedSpray(entity.world, entity, (float) this.Speed, spreadHor, spreadVert);

		shot.pot1 = effect1;
		shot.pot2 = effect2;

		entity.world.spawnEntity(shot);
	}


	private void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 40);
			return;
		}

		ItemStack clipStack = Helper.getAmmoStack(LargeRedstoneMagazine.class, stack.getItemDamage());	// Unloading all ammo into that clip

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
			list.add(TextFormatting.BLUE + "Redstone: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Redstone: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.GREEN + "Wither " + this.Wither_Strength + " for " + this.displayInSec(this.Wither_Duration) + " sec on hit.");
		list.add(TextFormatting.GREEN + "Blindness 1 for " + this.displayInSec(this.Blindness_Duration) + " sec on hit.");

		list.add(TextFormatting.RED + "Does not deal direct damage.");

		list.add(TextFormatting.YELLOW + "Crouch-use to drop the current magazine.");
		list.add(TextFormatting.YELLOW + "Craft with a Large Redstone Magazine");
		list.add(TextFormatting.YELLOW + "to reload.");

		list.add("The muzzle is slightly corroded.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 0.5 BPT (Blocks Per Tick))", 0.5).getDouble();

		this.Wither_Strength = config.get(this.nameInternal, "How strong is my Wither effect? (default 2)", 2).getInt();
		this.Wither_Duration = config.get(this.nameInternal, "How long does my Wither effect last? (default 20 ticks)", 20).getInt();
		this.Blindness_Duration = config.get(this.nameInternal, "How long does my Blindness effect last? (default 20 ticks)", 20).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One redstone sprayer (empty)
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "zxz", "aba", "zyz",
					'x', Blocks.PISTON,
					'y', Blocks.TRIPWIRE_HOOK,
					'z', Items.IRON_INGOT,
					'a', Items.REPEATER,
					'b', Blocks.STICKY_PISTON
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		Helper.registerAmmoRecipe(LargeRedstoneMagazine.class, this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "RedSprayer_empty"; }
		if (this.getCooldown(stack) > 0) { return "RedSprayer_hot"; }

		return "RedSprayer";
	}
}
