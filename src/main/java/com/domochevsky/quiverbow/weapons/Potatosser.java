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
import net.minecraftforge.oredict.OreDictionary;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.PotatoShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Potatosser extends _WeaponBase
{
	public Potatosser() { super(14); }

	private String nameInternal = "Potatosser";
	private boolean shouldDrop;

/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/Potatosser");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/Potatosser_Empty");
	}
*/

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) { return new ActionResult(EnumActionResult.SUCCESS, stack); }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return new ActionResult(EnumActionResult.SUCCESS, stack); }	// Is empty

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}


	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet

		// SFX
		entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.7F, 0.4F);

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 						// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;											// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		// Firing
		PotatoShot shot = new PotatoShot(world, entity, (float) this.Speed);
		shot.damage = dmg;
		shot.setDrop(this.shouldDrop);

		world.spawnEntity(shot);

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@Override
	void doCooldownSFX(World world, Entity entity) // Server side
	{
		entity.playSound(SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, 0.3F, 3.0F);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);

		/*if (player.capabilities.isCreativeMode)
		{
			list.add(TextFormatting.BLUE + "Potatoes: INFINITE / " + this.getMaxDamage());
		}
		else
		{*/
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(TextFormatting.BLUE + "Potatoes: " + ammo + " / " + this.getMaxDamage());
		//}

		list.add(TextFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax);

		list.add(TextFormatting.GREEN + "Missed POTATOes are edible.");

		list.add(TextFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");

		list.add(TextFormatting.YELLOW + "Craft with up to 7 Potatoes and");
		list.add(TextFormatting.YELLOW + "1 Coal/Charcoal to reload.");

		list.add("It smells kinda appetizing.");

		int cool = this.getCooldown(stack);

		if (cool > 0) { list.add(TextFormatting.RED + "COOKING (" + cool + ")"); }
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 2)", 2).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 5)", 5).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5).getDouble();
		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 15)", 15).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);

		this.shouldDrop = config.get(this.nameInternal, "Do I drop naked POTATOes on misses? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			/*// One POTATOsser (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "xax", "zbx", "cdy",
					'a', Blocks.TRAPDOOR,
					'b', Blocks.PISTON,
					'c', Blocks.TRIPWIRE_HOOK,
					'd', Blocks.STICKY_PISTON,
					'x', Blocks.IRON_BARS,
					'y', Items.IRON_INGOT,
					'z', Items.FLINT_AND_STEEL
					);*/
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		int maxAmmo = this.getMaxDamage();
		int emptyAmmo = 0;
		/*
		// Fill the cannon with 1 POTATO + 1 COAL (1 ammo)
		for (int ix = maxAmmo; ix > emptyAmmo; ix--)
		{
			GameRegistry.addShapelessRecipe(new ItemStack(this, 1, (ix - 1) ),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					Items.COAL,
					new ItemStack(this, 1, ix)
					);
		}

		// Fill the cannon with 2 POTATOes + 1 COAL (2 ammo)
		for (int ix = maxAmmo; ix > emptyAmmo + 1; ix--)
		{
			GameRegistry.addShapelessRecipe(new ItemStack(this, 1, (ix - 2) ),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					Items.COAL,
					new ItemStack(this, 1, ix)
					);
		}

		// Fill the cannon with 3 POTATOes + 1 COAL (3 ammo)
		for (int ix = maxAmmo; ix > emptyAmmo + 2; ix--)
		{
			GameRegistry.addShapelessRecipe(new ItemStack(this, 1, (ix - 3) ),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					Items.COAL,
					new ItemStack(this, 1, ix)
					);
		}

		// Fill the cannon with 4 POTATOes + 1 COAL (4 ammo)
		for (int ix = maxAmmo; ix > emptyAmmo + 3; ix--)
		{
			GameRegistry.addShapelessRecipe(new ItemStack(this, 1, (ix - 4) ),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					Items.COAL,
					new ItemStack(this, 1, ix)
					);
		}

		// Fill the cannon with 5 POTATOes + 1 COAL (5 ammo)
		for (int ix = maxAmmo; ix > emptyAmmo + 4; ix--)
		{
			GameRegistry.addShapelessRecipe(new ItemStack(this, 1, (ix - 5) ),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					Items.COAL,
					new ItemStack(this, 1, ix)
					);
		}

		// Fill the cannon with 6 POTATOes + 1 COAL (6 ammo)
		for (int ix = maxAmmo; ix > emptyAmmo + 5; ix--)
		{
			GameRegistry.addShapelessRecipe(new ItemStack(this, 1, (ix - 6) ),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					Items.COAL,
					new ItemStack(this, 1, ix)
					);
		}

		// Fill the cannon with 7 POTATOes + 1 COAL (7 ammo)
		for (int ix = maxAmmo; ix > emptyAmmo + 6; ix--)
		{
			GameRegistry.addShapelessRecipe(new ItemStack(this, 1, (ix - 7) ),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(Items.POTATO, 1, OreDictionary.WILDCARD_VALUE),
					Items.COAL,
					new ItemStack(this, 1, ix)
					);
		}*/
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		return "Potatosser";	// Regular
	}
}
