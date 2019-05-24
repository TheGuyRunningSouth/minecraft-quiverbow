package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.BlazeShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Crossbow_Blaze extends _WeaponBase
{
	public Crossbow_Blaze() { super(1); }

	private String nameInternal = "Blaze Crossbow";

	private int FireDur;
	private int FireDurInGround;

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowBlaze");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowBlaze_Empty");
	}
*/

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
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
		if (this.getCooldown(stack) != 0) { return; }	// Hasn't cooled down yet

		// SFX
		//world.playSoundAtEntity(entity, "random.bow", 1.0F, 0.5F);

		// Firing
		BlazeShot entityarrow = new BlazeShot(world, entity, (float) this.Speed);

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 	// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);// Range will be between 0 and 10
		dmg += this.DmgMin;							// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		entityarrow.damage = dmg;
		entityarrow.knockbackStrength = this.Knockback;	// Comes with an inbuild knockback II
		entityarrow.fireDuration = this.FireDur;
		entityarrow.ticksInGroundMax = 200;			// 200 ticks for 10 sec

		world.spawnEntity(entityarrow);	// pew

		// SFX
		//world.playSoundAtEntity(entity, "random.wood_click", 1.0F, 0.5F);
		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY + 0.5D, entity.posZ, 0.0D, 0.0D, 0.0D);

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, 10);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Rods: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Rods: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax);
		list.add(TextFormatting.GREEN + "Fire for " + this.FireDur + " sec on hit.");
		list.add(TextFormatting.GREEN + "Knockback " + this.Knockback + " on hit.");
		list.add(TextFormatting.YELLOW + "Craft with 1 Blaze Rod to reload.");
		list.add("Black smoke is slowly curling up from it.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);

		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 20)", 20).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 30)", 30).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 3.0 BPT (Blocks Per Tick))", 3.0).getDouble();
		this.Knockback = config.get(this.nameInternal, "How hard do I knock the target back when firing? (default 2)", 2).getInt();

		this.FireDur = config.get(this.nameInternal, "How long is the target on fire? (default 15 sec)", 15).getInt();
		this.FireDurInGround = config.get(this.nameInternal, "How long do I keep burning when stuck in the ground? (default 10 sec)", 10).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One blaze crossbow (empty)
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "bib", "ici", "bib",
					'b', Items.BLAZE_POWDER,
					'i', Items.IRON_INGOT,
					'c', Helper.getWeaponStackByClass(Crossbow_Compact.class, true)
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		/*GameRegistry.addShapelessRecipe(new ItemStack(this),	// Fill the empty blaze crossbow with one rod
				Items.BLAZE_ROD,
				new ItemStack(this, 1 , this.getMaxDamage())
				);*/
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "CrossbowBlaze_empty"; }		// empty

		return "CrossbowBlaze";	// Regular
	}
}
