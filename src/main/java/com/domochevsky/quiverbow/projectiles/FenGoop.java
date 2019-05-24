package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class FenGoop extends _ProjectileBase
{	
	public FenGoop(World world) { super(world); }

	public FenGoop(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	public int lightTick;
	
	
	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null) // hit a entity
    	{
    		target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) 0);	// No dmg, but knockback
            target.entityHit.hurtResistantTime = 0;
            target.entityHit.setFire(fireDuration); 	// Some minor fire, for flavor
    	}
    	else // hit the terrain
        {        	
        	int plusX = 0;
    		int plusY = 0;
    		int plusZ = 0;
    		
    		int posiX = target.getBlockPos().getX();
    		int posiY = target.getBlockPos().getY();
    		int posiZ = target.getBlockPos().getZ();

    		//Block targetBlock = this.worldObj.getBlock(posiX, posiY, posiZ);
    		
    		// Is the attached block a valid material?
    		boolean canPlace = false;
    		if ( Helper.hasValidMaterial(this.world, target.getBlockPos()) ) { canPlace = true; }
    		
        	// Glass breaking
            if ( Helper.tryBlockBreak(this.world, this, target, 0)) { canPlace = false; }
    		
    		if (target.sideHit == EnumFacing.DOWN) { plusY = -1; } 		// Bottom		
    		else if (target.sideHit == EnumFacing.UP) { plusY = 1; } 	// Top
    		else if (target.sideHit == EnumFacing.WEST) { plusZ = -1; } 	// East
    		else if (target.sideHit == EnumFacing.EAST){ plusZ = 1; } 	// West
    		else if (target.sideHit == EnumFacing.NORTH){ plusX = -1; } 	// North
    		else if (target.sideHit == EnumFacing.SOUTH) { plusX = 1; } 	// South
    		
    		// Is the space free?
    		if (this.world.getBlockState(new BlockPos((int)target.getBlockPos().getX() + plusX, (int)target.getBlockPos().getY() + plusY, (int)target.getBlockPos().getZ() + plusZ)).getMaterial() == Material.AIR ||
    				this.world.getBlockState(new BlockPos((int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ)).getMaterial() == Material.FIRE ||
    				this.world.getBlockState(new BlockPos((int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ)).getMaterial() == Material.GRASS ||
    				this.world.getBlockState(new BlockPos((int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ)).getMaterial() == Material.SNOW ||
    				this.world.getBlockState(new BlockPos((int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ)).getMaterial() == Material.WATER)
        	{
    			// Putting light there (if we can)
    			if (canPlace)
    			{
    				// TODO: Fix FenFire light placement
	    			//this.world.setBlock(posiX + plusX, posiY + plusY, posiZ + plusZ, Main.fenLight, 0, 3);
	    			//this.world.setBlockMetadataWithNotify(posiX + plusX, posiY + plusY, posiZ + plusZ, target.sideHit, 3);
	    			
	    			if (this.lightTick != 0) 
	    			{ 
	    				//this.world.scheduleBlockUpdate(posiX + plusX, posiY + plusY, posiZ + plusZ, Main.fenLight, this.lightTick); 
	    			}
	    			// else, stays on indefinitely
    			}
    			// else, can't place. The block isn't of a valid material
        	}
    		// else, none of the allowed materials
        }
    	
    	// SFX
    	for (int i = 0; i < 8; ++i) { this.world.spawnParticle(EnumParticleTypes.SLIME, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D); }
        this.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1.0F, 1.0F);
        
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 3;	// Type 3, item
		type[1] = 5;	// Length, misused as item type. glowstone dust
		type[2] = 2;	// Width
		
		return type;
	}
}
