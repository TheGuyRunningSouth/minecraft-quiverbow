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
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.projectiles.RegularArrow;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Crossbow_Auto extends _WeaponBase
{
	public Crossbow_Auto()
	{
		super(8);
		this.setRegistryName("CrossbowAuto");
	}

	private String nameInternal = "Auto-Crossbow";
/*
	@SideOnly(Side.CLIENT)
	public IIcon Icon_Unchambered;	// Only relevant if you're using the non-model version

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)	// We got need for a non-typical icon currently. Will be phased out
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowAuto");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowAuto_Empty");
		this.Icon_Unchambered = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowAuto_Unchambered");
	}


	@Override
	public IIcon getIcon(ItemStack stack, int pass)	// Onhand display
	{
		if (this.getDamage(stack) >= this.getMaxDamage()) { return this.Icon_Empty; }
		if (!this.getChambered(stack)) { return this.Icon_Unchambered; }	// Not chambered

		return this.Icon;
	}

*/
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) { return new ActionResult(EnumActionResult.SUCCESS, stack); }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return new ActionResult(EnumActionResult.SUCCESS, stack); }	// Is empty

		if (!this.getChambered(stack)) // No arrow on the rail
		{
			if (player.isSneaking()) { this.setChambered(stack, world, player, true); } // Setting up a new arrow

			return new ActionResult(EnumActionResult.PASS, stack);	// Done here either way
		}

		if (player.isSneaking()) { return new ActionResult(EnumActionResult.SUCCESS, stack); }	// Still sneaking, even though you have an arrow on the rail? Not having it

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
		int dmg_range = this.DmgMax - this.DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		entityarrow.damage = dmg;
		entityarrow.knockbackStrength = this.Knockback;	// Comes with an inbuild knockback II

		world.spawnEntity(entityarrow);	// pew

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
		this.setChambered(stack, world, entity, false);	// That bolt has left the rail
	}


	private boolean getChambered(ItemStack stack)
	{
		if (stack.getTagCompound() == null) { return false; }	// Doesn't have a tag

		return stack.getTagCompound().getBoolean("isChambered");
	}


	private void setChambered(ItemStack stack, World world, Entity entity, boolean toggle)
	{
		if (stack.getTagCompound() == null) { stack.setTagCompound(new NBTTagCompound()); }	// Init

		stack.getTagCompound().setBoolean("isChambered", toggle);	// Done, we're good to go again

		// SFX
		world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.8F, 0.5F);
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
		list.add(TextFormatting.YELLOW + "Crouch-use to ready a bolt.");
		list.add(TextFormatting.YELLOW + "Craft with 1 Arrow Bundle to reload.");
		list.add("Pistons power the bolt feeder.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 10)", 10).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 16)", 16).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5).getDouble();
		this.Knockback = config.get(this.nameInternal, "How hard do I knock the target back when firing? (default 1)", 1).getInt();
		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 10 ticks)", 10).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. They don't know how to rechamber me.)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One auto-crossbow (empty)
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "iii", "pcp", " t ",
					'i', Items.IRON_INGOT,
					'p', Blocks.PISTON,
					't', Blocks.TRIPWIRE_HOOK,
					'c', Helper.getWeaponStackByClass(Crossbow_Double.class, true)
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu


		/*GameRegistry.addShapelessRecipe(new ItemStack(this),	// Fill the empty auto-crossbow with one arrow bundle
				Helper.getAmmoStack(ArrowBundle.class, 0),
				new ItemStack(this, 1 , this.getMaxDamage())
				);*/
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "CrossbowAuto_empty"; }	// Empty
		if (!this.getChambered(stack)) { return "CrossbowAuto_unchambered"; }				// Not chambered

		return "CrossbowAuto";	// Regular
	}
}
