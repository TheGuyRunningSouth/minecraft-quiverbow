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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.RegularArrow;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Crossbow_Double extends _WeaponBase
{
	public Crossbow_Double() { super(2); }

	private String nameInternal = "Double Crossbow";


	/* Icons
	@SideOnly(Side.CLIENT)
	public IIcon Icon_Half;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowDouble");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowDouble_Empty");
		this.Icon_Half = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowDouble_Half");
	}


	@Override
	public IIcon getIcon(ItemStack stack, int pass)
	{
		if (this.getDamage(stack) >= this.getMaxDamage()) { return this.Icon_Empty; }	// Empty
		if (this.getDamage(stack) == 1) { return this.Icon_Half; }						// One arrow on the bay

		return this.Icon;
	}
	// This is for on-hand display. Only gets called on client side. Ideally won't get used at all once models are fully integrated


	@Override
	public IIcon getIconFromDamage(int meta)	// This is for inventory display. Comes in with metadata. Only gets called on client side
	{
		if (meta == this.getMaxDamage()) { return this.Icon_Empty; }	// Empty
		if (meta == 1) { return this.Icon_Half; }

		return this.Icon; 	// Full, default
	}
*/

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) { return new ActionResult(EnumActionResult.SUCCESS,stack); }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return new ActionResult(EnumActionResult.SUCCESS, stack); }	// Is empty

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return new ActionResult(EnumActionResult.PASS, stack);
	}


	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		if (this.getCooldown(stack) != 0) { return; }	// Hasn't cooled down yet

		// SFX
		world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 0.5F);

		RegularArrow entityarrow = new RegularArrow(world, entity, (float) this.Speed);

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 						// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;											// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		entityarrow.damage = dmg;
		entityarrow.knockbackStrength = this.Knockback;	// Comes with an inbuild knockback II

		world.spawnEntity(entityarrow);	// pew

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@Override
	void doCooldownSFX(World world, Entity entity) // Server side
	{
		world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.5F, 0.4F);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Bolts: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Bolts: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax);
		list.add(TextFormatting.GREEN + "Knockback " + this.Knockback + " on hit.");
		list.add(TextFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");
		list.add(TextFormatting.YELLOW + "Craft with 1 or 2 Arrows to reload.");
		list.add("A sticky piston powers the");
		list.add("reloading mechanism.");

		if (this.getCooldown(stack) != 0) {list.add(TextFormatting.RED + "RE-TAUTING! (" + this.getCooldown(stack) + ")"); }
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 14)", 14).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 20)", 20).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5).getDouble();
		this.Knockback = config.get(this.nameInternal, "How hard do I knock the target back when firing? (default 2)", 2).getInt();
		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 25 ticks)", 25).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
		/*	// One empty double crossbow (upgraded from regular crossbow)
			GameRegistry.addShapelessRecipe(new ItemStack(this, 1 , this.getMaxDamage()),
					Blocks.STICKY_PISTON,
					Items.REPEATER,
					Helper.getWeaponStackByClass(Crossbow_Compact.class, true)
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		/*GameRegistry.addShapelessRecipe(new ItemStack(this),	// Fill the empty crossbow with two arrows
				Items.ARROW,
				Items.ARROW,
				new ItemStack(this, 1 , this.getMaxDamage())
				);

		GameRegistry.addShapelessRecipe(new ItemStack(this, 1, 1),	// Fill the empty crossbow with one arrow
				Items.ARROW,
				new ItemStack(this, 1 , this.getMaxDamage())
				);

		GameRegistry.addShapelessRecipe(new ItemStack(this),	// Fill the half empty crossbow with one arrow
				Items.ARROW,
				new ItemStack(this, 1 , 1)
				);*/
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() == 1) { return "CrossbowDouble_half"; }						// One arrow gone
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "CrossbowDouble_empty"; }	// empty

		return "CrossbowDouble";	// Regular
	}
}
