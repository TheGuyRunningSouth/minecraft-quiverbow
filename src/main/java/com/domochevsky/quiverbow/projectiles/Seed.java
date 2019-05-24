package com.domochevsky.quiverbow.projectiles;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;

public class Seed extends _ProjectileBase
{
	public Seed(World world) { super(world); }
	
	
	public Seed(World world, Entity entity, float speed, float accHor, float AccVert) 
    {
        super(world);
        this.doSetup(entity, speed, accHor, AccVert, entity.rotationYaw, entity.rotationPitch);
    }
	
	
	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null) 
    	{
    		target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) this.damage);
    		
    		target.entityHit.hurtResistantTime = 0;	// No rest for the wicked

        }        
        else 
        {
        	// Hit the terrain        	
        	int x = target.getBlockPos().getX();
    		int y = target.getBlockPos().getY();
    		int z = target.getBlockPos().getZ();
    		
    		Block hitBlock = this.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    		Block aboveHitBlock = this.world.getBlockState(new BlockPos(x, y + 1, z)).getBlock();
    		
    		// Glass breaking
    		Helper.tryBlockBreak(this.world, this, target, 0);
    		
    		if (hitBlock == Blocks.FARMLAND && aboveHitBlock.getMaterial(this.world.getBlockState(new BlockPos(x,y+1,z))) == Material.AIR)
    		{
    			// Hit a farmland block and the block above is free. Planting a melon seed now
    			this.world.setBlockState(new BlockPos(x, y + 1, z), Blocks.MELON_STEM.getDefaultState(), 3);
    		}
        }
    	
        //this.world.playSoundAtEntity(this, "random.click", 0.2F, 3.0F);
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 3;	// Type 3, item icon
		type[1] = 4;	// Length, misused as item type. melon seeds
		type[2] = 2;	// Width
		
		return type;
	}
}
