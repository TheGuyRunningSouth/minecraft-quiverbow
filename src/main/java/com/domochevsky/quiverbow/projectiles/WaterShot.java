package com.domochevsky.quiverbow.projectiles;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.net.NetHelper;

public class WaterShot extends _ProjectileBase
{
	public WaterShot(World world) { super(world); }

	public WaterShot(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	
	@Override
	public void doFlightSFX() 
	{
		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), (byte) 14, (byte) 4);
	}

	
	@Override
	public void onImpact(RayTraceResult target) 
	{		
		double posiX = 0;
    	double posiY = 0;
    	double posiZ = 0;
    	
    	int plusX = 0;
		int plusY = 0;
		int plusZ = 0;
    	
    	if (target.entityHit != null) // hit a entity
    	{
    		posiX = target.entityHit.posX;
    		posiY = target.entityHit.posY;
    		posiZ = target.entityHit.posZ;
    	}
    	else // hit the terrain
        {
    		posiX = target.getBlockPos().getX();
    		posiY = target.getBlockPos().getY();
    		posiZ = target.getBlockPos().getZ();
    		
    		if (target.sideHit == EnumFacing.DOWN) { plusY = -1; } // Bottom
    		else if (target.sideHit == EnumFacing.UP) { plusY = 1; } // Top
    		else if (target.sideHit == EnumFacing.EAST) { plusZ = -1; } // East
    		else if (target.sideHit == EnumFacing.WEST) { plusZ = 1; } // West
    		else if (target.sideHit == EnumFacing.NORTH) { plusX = -1; } // North
    		else if (target.sideHit == EnumFacing.SOUTH) { plusX = 1; } // South
        }
    	
    	// Nether Check
		if (this.world.provider.isNether())
        {
			/*this.world.playSoundEffect((double)((float)this.posX + 0.5F), (double)((float)this.posY + 0.5F), (double)((float)this.posZ + 0.5F), 
					"random.fizz", 0.5F, 2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F);*/

			NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), (byte) 11, (byte) 4);
            
            return; // No water in the nether, yo
        }
		
		// Is the space free?
		if (this.world.getBlockState( new BlockPos((int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ)).getMaterial() == Material.AIR)
    	{
			// Can we edit this block at all?
			if (this.shootingEntity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) this.shootingEntity;
				if (!player.canPlayerEdit( new BlockPos((int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ), EnumFacing.DOWN, null ) ) { return; }	// Nope
			}

			// Putting water there!
			this.world.setBlockState(new BlockPos((int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ), Blocks.FLOWING_WATER.getDefaultState(), 3);
    	}
    	
    	// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), (byte) 14, (byte) 4);
       //this.world.playSoundAtEntity(this, "random.splash", 1.0F, 1.0F);
        
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 3;	// Type 3, icon
		type[1] = 6;	// Length, water bucket
		type[2] = 2;	// Width
		
		return type; // Fallback, 0 0 0
	}
}
