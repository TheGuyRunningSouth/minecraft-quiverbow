package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
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
import com.domochevsky.quiverbow.ammo.BoxOfFlintDust;
import com.domochevsky.quiverbow.projectiles.FlintDust;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FlintDuster  extends _WeaponBase
{
	public FlintDuster()
	{
		super(256);
		this.setCreativeTab(CreativeTabs.TOOLS);		// Tool, so on the tool tab
	}

	private String nameInternal = "Flint Duster";

	private int Dmg;
	private int MaxBlocks;

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/FlintDrill");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/FlintDrill_Empty");
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
		// Ignoring cooldown for firing purposes

		// SFX
		entity.playSound(SoundEvents.ENTITY_BAT_TAKEOFF, 0.5F, 0.6F);

		// Ready
		FlintDust shot = new FlintDust(world, entity, (float) this.Speed);

		// Properties
		shot.damage = this.Dmg;
		shot.ticksInAirMax = this.MaxBlocks;

		// Go
		world.spawnEntity(shot);

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, 4);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (world.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Flint Dust: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Flint Dust: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.BLUE + "Damage: " + this.Dmg);
		list.add(TextFormatting.BLUE + "Range: Roughly " + this.MaxBlocks + " Blocks.");

		list.add(TextFormatting.GREEN + "A mining tool.");

		list.add(TextFormatting.YELLOW + "Craft with up to 8 Boxes of");
		list.add(TextFormatting.YELLOW + "Flint Dust to reload.");

		list.add("The quartz is barely visible");
		list.add("beneath the dust coating.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.Speed = 1.5f;	// Fixed value

		this.Dmg = config.get(this.nameInternal, "What damage am I dealing? (default 1)", 1).getInt();
		this.MaxBlocks = config.get(this.nameInternal, "How much range do I have? (default ~7 blocks)", 7).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. They have no interest in dirt.)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Flint Duster (Empty)
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "qhq", "qpq", "tsi",
					'p', Blocks.PISTON,
					's', Blocks.STICKY_PISTON,
					'h', Blocks.HOPPER,
					'q', Blocks.QUARTZ_BLOCK,
					'i', Items.IRON_INGOT,
					't', Blocks.TRIPWIRE_HOOK
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		ItemStack stack = Helper.getAmmoStack(BoxOfFlintDust.class, 0);

		Helper.makeAmmoRecipe(stack, 1, 32, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 2, 64, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 3, 92, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 4, 128, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 5, 160, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 6, 192, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 7, 224, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 8, 256, this.getMaxDamage(), this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "FlintDuster_empty"; }		// empty
		if (this.getCooldown(stack) > 0) { return "FlintDuster_hot"; }	// Firing

		return "FlintDuster";	// Regular
	}
}
