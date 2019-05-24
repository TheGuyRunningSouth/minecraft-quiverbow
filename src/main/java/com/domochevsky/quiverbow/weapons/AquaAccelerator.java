package com.domochevsky.quiverbow.weapons;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.FillBucketEvent;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.WaterShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AquaAccelerator extends _WeaponBase
{
	public AquaAccelerator() 
	{ 
		super(1); 
		this.setCreativeTab(CreativeTabs.TOOLS);		// This is a tool
	}
	
	private String nameInternal = "Aqua Accelerator";
	
	
	/*@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{  
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/WaterGun");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/WaterGun_Empty");
	}
	
	*/
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) 
    {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) { return new ActionResult(EnumActionResult.SUCCESS, stack); }				// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) 	// Is empty
		{ 
			this.checkReloadFromWater(stack, world, player);// See if you can reload
			return new ActionResult(EnumActionResult.PASS, stack); 
		}	

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
    	
    	return new ActionResult(EnumActionResult.PASS, stack);
    }
	
	
	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side, mob usable
	{
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet
		
		// SFX
		world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 1.0F, 2.0F);
		
		// Firing
		WaterShot projectile = new WaterShot(world, entity, (float) Speed);
		world.spawnEntity(projectile);
		
		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);	// Cooling down now
	}
	
	
	private void checkReloadFromWater(ItemStack stack, World world, EntityPlayer player)
    {
		RayTraceResult movingobjectposition = Helper.getMovingObjectPositionFromPlayer(world, player, 1);
		FillBucketEvent event = new FillBucketEvent(player, stack, world, movingobjectposition);
        
		if (MinecraftForge.EVENT_BUS.post(event)) { return; }
		
        RayTraceResult movObj = Helper.getMovingObjectPositionFromPlayer(world, player, 1);

        if (movObj == null) { return; }	// Didn't click on anything in particular
        else
        {            
            if (movObj.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                ;
                
                if (!world.canMineBlockBody(player, movObj.getBlockPos())) { return; }					// Not allowed to mine this, getting out of here
                if (!player.canPlayerEdit(movObj.getBlockPos(), movObj.sideHit, stack)) { return; }	// Not allowed to edit this, getting out of here

                Material material = world.getBlockState(movObj.getBlockPos()).getMaterial();
                IBlockState meta = world.getBlockState(movObj.getBlockPos());

                // Is this water?
                if (material == Material.WATER && meta == Blocks.WATER.getDefaultState())
                {
                	world.setBlockToAir(movObj.getBlockPos());
                	stack.setItemDamage(0);
                	
                    return;
                }
                // else, not water
            }
            // else, didn't click on a block
        }
    }
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
	    super.addInformation(stack, world, list, flag);
	    
	  /*  if (player.capabilities.isCreativeMode)
	    {
		    list.add(TextFormatting.BLUE + "Buckets: INFINITE / " + this.getMaxDamage());
	    }
	    else
	    {*/
	    	int ammo = this.getMaxDamage() - this.getDamage(stack);
		    list.add(TextFormatting.BLUE + "Buckets: " + ammo + " / " + this.getMaxDamage());
	    //}
	    
	    list.add(TextFormatting.YELLOW + "Craft with 1 Water Bucket to reload.");
	    list.add("Kinda slippery.");
    }
	
	
	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config) 
	{ 
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();
		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5).getDouble();
		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false)", false).getBoolean(true);
	}
    
	
	@Override
    public void addRecipes()
	{ 
		if (Enabled)
        {
			/*// One Aqua Accelerator (empty)
            GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "ihi", "gpg", "iti",		
                   'p', Blocks.PISTON,
                   't', Blocks.TRIPWIRE_HOOK,
                   'i', Items.IRON_INGOT,
                   'h', Blocks.HOPPER,
                   'g', Blocks.GLASS_PANE
            );*/
        }
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu
		
		/*// Fill the AA with one water bucket
        GameRegistry.addShapelessRecipe(new ItemStack(this),						
        		Items.WATER_BUCKET, 
        		new ItemStack(this, 1 , this.getMaxDamage())	// Empty
        );*/
	}
	
	
	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{ 
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "AquaAcc_empty"; }	// empty
		
		return "AquaAcc";	// Regular
	}
}
