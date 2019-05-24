package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PowderKnuckle extends _WeaponBase
{
	public PowderKnuckle() { super(8); }

	private String nameInternal = "Powder Knuckle";

	private double ExplosionSize;

	private boolean dmgTerrain;

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/PowderKnuckle");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/PowderKnuckle_Empty");
	}
	*/


	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float sideX, float sideY, float sideZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) { return EnumActionResult.FAIL; }	// Not doing this on client side

		// Right click
		if (this.getDamage(stack) >= this.getMaxDamage()) { return EnumActionResult.FAIL; }	// Not loaded

		if (!player.capabilities.isCreativeMode) { this.consumeAmmo(stack, player, 1); }

		world.createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), (float) this.ExplosionSize, true);

		NetHelper.sendParticleMessageToAllPlayers(world, player.getEntityId(), (byte) 3, (byte) 4);	// smoke

		return EnumActionResult.SUCCESS;
	}


	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		if (player.world.isRemote) { return false; }	// Not doing this on client side

		if (this.getDamage(stack) >= this.getMaxDamage())
		{
			entity.attackEntityFrom(DamageSource.causePlayerDamage(player), this.DmgMin);
			entity.hurtResistantTime = 0;	// No invincibility frames

			return false; 				// We're not loaded, getting out of here with minimal damage
		}

		this.consumeAmmo(stack, entity, 1);

		// SFX
		NetHelper.sendParticleMessageToAllPlayers(entity.world, player.getEntityId(), (byte) 3, (byte) 4);	// smoke

		// Dmg
		entity.setFire(2);																	// Setting fire to them for 2 sec, so pigs can drop cooked porkchops
		entity.world.createExplosion(player, entity.posX, entity.posY + 0.5D, entity.posZ, (float) this.ExplosionSize, this.dmgTerrain); 	// 4.0F is TNT

		entity.attackEntityFrom(DamageSource.causePlayerDamage(player), this.DmgMax);	// Dealing damage directly. Screw weapon attributes

		return false;
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Gunpowder: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Gunpowder: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.BLUE + "Damage: " + (this.DmgMax + 1));

		list.add(TextFormatting.GREEN + "Explosion with radius " + this.ExplosionSize + " on hit.");

		list.add(TextFormatting.YELLOW + "Punch to attack mobs, Use to attack terrain.");
		list.add(TextFormatting.YELLOW + "Craft with up to 8 gunpowder to reload.");

		list.add("Not safe for use.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What's my minimum damage, when I'm empty? (default 1)", 1).getInt();
		this.DmgMax = config.get(this.nameInternal, "What's my maximum damage when I explode? (default 18)", 18).getInt();

		this.ExplosionSize = config.get(this.nameInternal, "How big are my explosions? (default 1.5 blocks. TNT is 4.0 blocks)", 1.5).getDouble();
		this.dmgTerrain = config.get(this.nameInternal, "Can I damage terrain, when in player hands? (default true)", true).getBoolean(true);

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. They don't know where the trigger on this thing is.)", false).getBoolean(false);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Powder Knuckle with 8 damage value (empty)
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "yyy", "xzx", "x x",
					'x', Items.LEATHER,
					'y', Items.IRON_INGOT,
					'z', Items.STICK
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		ItemStack stack = new ItemStack(Items.GUNPOWDER);

		Helper.makeAmmoRecipe(stack, 1, 1, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 2, 2, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 3, 3, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 4, 4, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 5, 5, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 6, 6, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 7, 7, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 8, 8, this.getMaxDamage(), this);
	}
}
