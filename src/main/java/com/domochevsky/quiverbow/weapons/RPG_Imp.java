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
import com.domochevsky.quiverbow.ammo.LargeRocket;
import com.domochevsky.quiverbow.projectiles.BigRocket;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RPG_Imp extends _WeaponBase
{
	public RPG_Imp() { super(1); }

	private String nameInternal = "Improved Rocket Launcher";
	public double ExplosionSize;
	private boolean dmgTerrain;		// Can our projectile damage terrain?
/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/RPG_Improved");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/RPG_Improved_Empty");
	}

*/
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand )
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

		// Firing
		BigRocket rocket = new BigRocket(world, entity, (float) this.Speed);	// Projectile Speed. Inaccuracy Hor/Vert
		rocket.explosionSize = this.ExplosionSize;
		rocket.dmgTerrain = this.dmgTerrain;

		world.spawnEntity(rocket); 		// shoom.

		// SFX
		entity.playSound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 2.0F, 0.6F);

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, 60);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Rockets: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Rockets: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.GREEN + "Explosion with radius " + this.ExplosionSize + " on hit.");
		list.add(TextFormatting.GREEN + "Loads pre-made rockets.");

		list.add(TextFormatting.YELLOW + "Craft with 1 Big Rocket to reload.");

		list.add("Detonates on impact.");
		list.add("This seems fairly safe.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();
		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 2.0 BPT (Blocks Per Tick))", 2.0).getDouble();
		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 3)", 3).getInt();
		this.ExplosionSize = config.get(this.nameInternal, "How big are my explosions? (default 4.0 blocks, like TNT)", 4.0).getDouble();
		this.dmgTerrain = config.get(this.nameInternal, "Can I damage terrain, when in player hands? (default true)", true).getBoolean(true);

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Improved Rocket Launcher (empty)
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "xxx", "yzy", "xxx",
					'x', Blocks.OBSIDIAN, 							// Adding an obsidian frame to the RPG
					'y', Items.IRON_INGOT,
					'z', Helper.getWeaponStackByClass(RPG.class, true)
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Fill the launcher with 1 big rocket
		Helper.makeAmmoRecipe(Helper.getAmmoStack(LargeRocket.class, 0), 1, 1, this.getMaxDamage(), this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "RPG_Imp_empty"; }	// Empty

		return "RPG_Imp";	// Regular
	}
}
