package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.WebShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SilkenSpinner extends _WeaponBase
{
	public SilkenSpinner()
	{
		super(8);
		this.setCreativeTab(CreativeTabs.TOOLS);		// This is a tool
	}

	private String nameInternal = "Silken Spinner";

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/WebGun");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/WebGun_Empty");
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

		// SFX
		//world.playSoundAtEntity(entity, "tile.piston.out", 1.0F, 2.0F);

		// Firing
		WebShot projectile = new WebShot(world, entity, (float) this.Speed);
		world.spawnEntity(projectile); 			// Firing!

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Webs: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Webs: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.GREEN + "Places cobweb on hit.");

		list.add(TextFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");

		list.add(TextFormatting.YELLOW + "Craft with up to 8 Cobwebs to reload.");

		list.add("Feels vaguely sticky.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5).getDouble();

		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 20 ticks)", 20).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. Potentially abusable for free cobwebs.)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "ihi", "gpg", "tsi",
					'p', Blocks.PISTON,
					's', Blocks.STICKY_PISTON,
					't', Blocks.TRIPWIRE_HOOK,
					'i', Items.IRON_INGOT,
					'h', Blocks.HOPPER,
					'g', Blocks.GLASS_PANE
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Making web out of string
		/*GameRegistry.addRecipe(new ItemStack(Blocks.web), "s s", " s ", "s s",
				's', Items.STRING
				);*/

		// Ammo
		ItemStack stack = new ItemStack(Blocks.WEB);

		Helper.makeAmmoRecipe(stack, 1, 1, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 2, 2, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 3, 3, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 4, 4, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 5, 5, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 6, 6, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 7, 7, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 8, 8, this.getMaxDamage(), this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "WebGun_empty"; }	// empty

		return "WebGun";	// Regular
	}
}
