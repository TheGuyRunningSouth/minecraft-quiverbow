package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.GatlingAmmo;
import com.domochevsky.quiverbow.ammo.Part_GatlingBarrel;
import com.domochevsky.quiverbow.ammo.Part_GatlingBody;
import com.domochevsky.quiverbow.projectiles.SugarRod;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SugarEngine extends _WeaponBase
{
	public SugarEngine()
	{
		super(200); 	// Max ammo placeholder

		ItemStack ammo = Helper.getAmmoStack(GatlingAmmo.class, 0);
		this.setMaxDamage(ammo.getMaxDamage());	// Fitting our max capacity to the magazine
	}

	private String nameInternal = "Sugar Engine";

	public float Spread;

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/SugarGatling");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/SugarGatling_Empty");
	}
*/

	int getSpinupTime() { return 30; }	// Time in ticks until we can start firing


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
		if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
		// Weapon is ready, so we can spin up now. set spin-down immunity to x ticks and spin up
		stack.getTagCompound().setInteger("spinDownImmunity", 20);	// Can't spin down for 20 ticks. Also indicates our desire to spin up

		if (stack.getTagCompound().getInteger("spinning") < this.getSpinupTime()) { return; } // Not ready yet, so keep spinning up
		// else, we're ready

		this.setBurstFire(stack, 4);		// Setting the rods left to fire to 4, then going through that via onUpdate (Will be constantly refreshed if we're still spinning)
	}


	private void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 80);
			return;
		}

		ItemStack clipStack = Helper.getAmmoStack(GatlingAmmo.class, stack.getItemDamage());	// Unloading all ammo into that clip

		stack.setItemDamage(this.getMaxDamage());	// Emptying out

		// Creating the clip
		EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
		//entityitem.delayBeforeCanPickup = 10;

		// And dropping it
		if (entity.captureDrops) { entity.capturedDrops.add(entityitem); }
		else { world.spawnEntity(entityitem); }

		// SFX
		//world.playSoundAtEntity(entity, "random.break", 1.0F, 0.5F);
	}


	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) 	// Overhauled default
	{
		if (world.isRemote) { return; }	// Not doing this on client side

		if (this.getCooldown(stack) > 0) { this.setCooldown(stack, this.getCooldown(stack) - 1); }	// Cooling down
		if (this.getCooldown(stack) == 1) { this.doCooldownSFX(world, entity); }					// One tick before cooldown is done with, so SFX now

		if (stack.getTagCompound() == null) { stack.setTagCompound(new NBTTagCompound()); }	// Init

		if (stack.getTagCompound().getInteger("spinDownImmunity") == 0)	// Not firing and no immunity left, so spinning down
		{
			if (stack.getTagCompound().getInteger("spinning") > 0)
			{
				stack.getTagCompound().setInteger("spinning", stack.getTagCompound().getInteger("spinning") - 1);

				//this.doSpinSFX(stack, world, entity);
			}
			// else, not spinning
		}
		else	// We're currently immune to spinning down, so decreasing that immunity time until we actually can
		{
			stack.getTagCompound().setInteger("spinDownImmunity", stack.getTagCompound().getInteger("spinDownImmunity") - 1);

			// Also assuming that we're trying to fire, so spinning up (This is a workaround for the fact that onRightClick isn't called every tick)
			if (stack.getTagCompound().getInteger("spinning") < this.getSpinupTime())
			{
				stack.getTagCompound().setInteger("spinning", stack.getTagCompound().getInteger("spinning") + 1);
			}
			// else, we've reached full spin

			//this.doSpinSFX(stack, world, entity);	// Spin down SFX
		}

		if (this.getBurstFire(stack) > 0)
		{
			this.setBurstFire(stack, this.getBurstFire(stack) - 1); // One done

			if (stack.getItemDamage() < stack.getMaxDamage() && holdingItem)	// Can only do it if we're loaded and holding the weapon
			{
				this.doBurstFire(stack, world, entity);

				if (this.consumeAmmo(stack, entity, 1)) { this.dropMagazine(world, stack, entity); }	// You're empty
			}
			// else, either not loaded or not held
		}
	}


	private void doBurstFire(ItemStack stack, World world, Entity entity)
	{
		Helper.knockUserBack(entity, this.Kickback);		// Kickback

		// Firing
		float spreadHor = world.rand.nextFloat() * this.Spread - (this.Spread / 2);		// Spread between -4 and 4 at ( (0.0 to 1.0) * 16 - 8)
		float spreadVert = world.rand.nextFloat() * this.Spread - (this.Spread / 2);

		int dmg_range = this.DmgMax - this.DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		SugarRod projectile = new SugarRod(world, entity, (float) this.Speed, spreadHor, spreadVert);
		projectile.damage =dmg;

		world.spawnEntity(projectile);

		// SFX
		entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.2F);
		entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.6F, 3.0F);
	}


	private void doSpinSFX(ItemStack stack, World world, EntityPlayer player)
	{
		// SFX
		int spin = stack.getTagCompound().getInteger("spinning");

		float volume = 0.8F;
		float pitch = 1.8F;

		// Increasing in frequency as we spin up
		if (spin == 1) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }			// +4
		else if (spin == 5) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }	// +4
		else if (spin == 9) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }	// +4

		else if (spin == 13) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }	// +3
		else if (spin == 16) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }	// +3
		else if (spin == 19) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }	// +3

		else if (spin == 21) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }	// +2
		else if (spin == 23) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }	// +2
		else if (spin == 25) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }	// +2

		else if (spin == 27) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }	// +1
		else if (spin == 28) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }	// +1
		else if (spin == 29) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }	// +1

		else if (spin >= 30) { world.playSound(player, player.getPosition(), SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch); }	// +++
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Sugar Rods: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Sugar Rods: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax + " per rod.");

		list.add(TextFormatting.GREEN + "Fires ~20 rods per second.");

		list.add(TextFormatting.RED + "Start-up Time: " + this.displayInSec(this.getSpinupTime()) + " sec.");

		list.add(TextFormatting.YELLOW + "Crouch-use to drop the current clip.");
		list.add(TextFormatting.YELLOW + "Craft with 1 Clip of Sugar Rods");
		list.add(TextFormatting.YELLOW + "to reload when empty.");

		list.add("So many barrels. Why so many barrels?");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 1)", 1).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 3)", 3).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 2.0 BPT (Blocks Per Tick))", 2.0).getDouble();

		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 1)", 1).getInt();
		this.Spread = (float) config.get(this.nameInternal, "How accurate am I? (default 10 spread)", 10).getDouble();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true. They'll probably figure it out.)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Sugar Gatling (empty)
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "b b", "b b", " m ",
					'b', Helper.getAmmoStack(Part_GatlingBarrel.class, 0),
					'm', Helper.getAmmoStack(Part_GatlingBody.class, 0)
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Reloading with gatling ammo, setting its clip metadata as ours (Need to be empty for that)
		Helper.registerAmmoRecipe(GatlingAmmo.class, this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "SugarGatling_empty"; }
		if (this.getCooldown(stack) > 0) { return "SugarGatling_hot"; }	// Cooling down

		return "SugarGatling";	// Regular
	}
}
