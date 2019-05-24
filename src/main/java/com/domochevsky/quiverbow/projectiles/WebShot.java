package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class WebShot extends _ProjectileBase
{
	public WebShot(World world) { super(world); }

	public WebShot(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
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
		
		// Is the space free?
		if (this.world.getBlockState( new BlockPos((int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ)).getMaterial() == Material.AIR)
    	{
			// Putting a web there!
			this.world.setBlockState(new BlockPos((int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ), Blocks.WEB.getDefaultState(), 3);
    	}
    	
    	// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), (byte) 15, (byte) 4);
        this.playSound(SoundEvents.ENTITY_GENERIC_SPLASH, 0.4F, 2.0F);
        
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 3;	// Type 3, icon
		type[1] = 7;	// Length, snowball (misused as web ball)
		type[2] = 2;	// Width
		
		return type; // Fallback, 0 0 0
	}
}
