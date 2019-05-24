package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.projectiles.EntityNormalArrow;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class QuiverBow extends _WeaponBase
{
	public QuiverBow() { super(256); }


	//public static final String[] bowPullIconNameArray = new String[] {"pulling_0", "pulling_1", "pulling_2"};

	String nameInternal = "Bow with Quiver";
/*
	@SideOnly(Side.CLIENT)
	private IIcon pull_0;

	@SideOnly(Side.CLIENT)
	private IIcon pull_1;

	@SideOnly(Side.CLIENT)
	private IIcon pull_2;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon("quiverchevsky:weapons/QBow_idle");

		this.pull_0 = par1IconRegister.registerIcon("quiverchevsky:weapons/QBow_pulling_0");
		this.pull_1 = par1IconRegister.registerIcon("quiverchevsky:weapons/QBow_pulling_1");
		this.pull_2 = par1IconRegister.registerIcon("quiverchevsky:weapons/QBow_pulling_2");
	}


	@SideOnly(Side.CLIENT)
	public IIcon getItemIconForUseDuration(int state) // Inventory display
	{
		if (state == 0) { return this.pull_0; }
		else if (state == 1) { return this.pull_1; }
		else if (state == 2) { return this.pull_2; }

		return this.pull_2; // Fallback
	}


	@Override									// This is for inventory display. Comes in with metadata
	public IIcon getIconFromDamage(int meta)
	{
		return this.itemIcon;
	}


	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) 	// On hand display
	{
		if(player.getItemInUse() == null) { return this.itemIcon; }

		int Pulling = stack.getMaxItemUseDuration() - useRemaining;	// Displaying the bow drawing animation based on the use state

		if (Pulling >= 18) { return this.pull_2; }
		else if (Pulling > 13) { return this.pull_1; }
		else if (Pulling > 0) { return this.pull_0; }

		return this.itemIcon;
	}
*/

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World par2World, EntityLivingBase entity, int par4)
	{
		EntityPlayer player = (EntityPlayer)entity;
		if (!par2World.isRemote)
		{
			int j = this.getMaxItemUseDuration(stack) - par4;		// Reduces the durability by the ItemInUseCount (probably 1 for anything that isn't a tool)

			ArrowLooseEvent event = new ArrowLooseEvent(player, stack, par2World, j, true);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled()) { return; }
			j = event.getCharge();

			if (this.getDamage(stack) == this.getMaxDamage()) {	return; }		// No arrows in the quiver? Getting out of here early

			float f = j / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if (f < 0.1D) { return; }
			if (f > 1.0F) { f = 1.0F; }

			EntityArrow entityarrow = new EntityNormalArrow(par2World, player /*,f * 2.0F*/);
			if (f == 1.0F) { entityarrow.setIsCritical(true); }

			player.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

			if (player.capabilities.isCreativeMode) { entityarrow.pickupStatus = PickupStatus.DISALLOWED; }
			else
			{
				entityarrow.pickupStatus = PickupStatus.ALLOWED;
				stack.setItemDamage(this.getDamage(stack) + 1);		// Reversed. MORE Damage for a shorter durability bar
			}

			par2World.spawnEntity(entityarrow);

		}
	}


	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		ArrowNockEvent event = new ArrowNockEvent(player, stack, hand, world, true);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) { return new ActionResult(EnumActionResult.SUCCESS, stack); }

		// Are there any arrows in the quiver?
		//TODO: What the heck did setItemInUse() *do*?
		// if (this.getDamage(stack) < this.getMaxDamage()) {	player.setItemInUse(stack, this.getMaxItemUseDuration(stack)); }

		return new ActionResult(EnumActionResult.PASS, stack);
	}


	@Override
	public int getMaxItemUseDuration(ItemStack stack) { return 72000; }


	@Override
	public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.BOW; }


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Quiver: INFINITE / " + this.getMaxDamage() + " Arrows");
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Quiver: " + ammo + " / " + this.getMaxDamage() + " Arrows");
		//}

		list.add(TextFormatting.GREEN + "Holds more Arrows.");

		list.add("Craft with up to 8 arrow bundles to reload.");
		list.add("A quiver has been sewn to this bow.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. They don't know how to span the string.)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()	// Enabled defines whether or not the item can be crafted. Reloading existing weapons is always permitted.
	{
		if (this.Enabled)
		{
			// One quiverbow with 256 damage value (empty)
			/*GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "zxy", "xzy", "zxy",
					'x', Items.STICK,
					'y', Items.STRING,
					'z', Items.LEATHER
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Ammo
		ItemStack bundle = Helper.getAmmoStack(ArrowBundle.class, 0);

		Helper.makeAmmoRecipe(bundle, 1, 8, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(bundle, 2, 16, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(bundle, 3, 24, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(bundle, 4, 32, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(bundle, 5, 40, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(bundle, 6, 48, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(bundle, 7, 56, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(bundle, 8, 64, this.getMaxDamage(), this);
	}
}
