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
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.SoulShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SoulCairn extends _WeaponBase
{
	public SoulCairn()
	{
		super(1);
		this.setCreativeTab(CreativeTabs.TOOLS);		// Tool, so on the tool tab
	}

	private String nameInternal = "Soul Cairn";

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/SoulCairn");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/SoulCairn_Empty");
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

		// Self Harm
		entity.attackEntityFrom(DamageSource.causeThrownDamage(entity, entity), 2);				// A sacrifice in blood

		// Projectile
		SoulShot projectile = new SoulShot(world, entity, (float) this.Speed);
		world.spawnEntity(projectile); 															// Firing!

		// SFX
		entity.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
		entity.playSound(SoundEvents.BLOCK_NOTE_BASS, 1.0F, 0.4F);

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, 20);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Soul Battery: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Soul Battery: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.GREEN + "Kills target on hit.");
		list.add(TextFormatting.GREEN + "Gain mob egg on hit.");

		list.add(TextFormatting.RED + "Do not aim at sentient beings.");

		list.add(TextFormatting.YELLOW + "Craft with 1 Diamond to reload.");

		list.add("A tool to End (and start) all life.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();
		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 3.0 BPT (Blocks Per Tick))", 3.0).getDouble();
		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 4)", 4).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. This is easily abusable.)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			/*// One Soul Cairn (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "e e", "epe", "oto",
					'o', Blocks.OBSIDIAN,
					'e', Blocks.END_STONE,
					't', Blocks.TRIPWIRE_HOOK,
					'p', Blocks.PISTON
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Reload with 1 DIAMOND
		/*GameRegistry.addShapelessRecipe(new ItemStack(this),
				Items.DIAMOND,
				new ItemStack(this, 1 , this.getMaxDamage())
				);*/
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "SoulCairn_empty"; }	// empty

		return "SoulCairn";	// Regular
	}
}
