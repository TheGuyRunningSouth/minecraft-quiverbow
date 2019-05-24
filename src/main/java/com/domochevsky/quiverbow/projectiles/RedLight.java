package com.domochevsky.quiverbow.projectiles;

import io.netty.buffer.ByteBuf;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class RedLight extends _ProjectileBase implements IEntityAdditionalSpawnData
{
	public int targetsHitMax;
	
	public RedLight(World world) { super(world); }

	public RedLight(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
        
        this.ownerX = entity.posX;
        this.ownerY = entity.posY + entity.getEyeHeight();
        this.ownerZ = entity.posZ;
    }
	
	
	@Override
	public boolean doDropOff() { return false; }	// Affected by gravity?

	
	@Override
	public void doFlightSFX()
	{
		if (this.ticksExisted > this.ticksInAirMax) { this.setDead(); }	// There's only so long we can exist
		
		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), (byte) 4, (byte) 1);
		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), (byte) 10, (byte) 1);
	}
	
	
	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null) 		// We hit a living thing!
    	{		
			// Damage
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float)this.damage);
            target.entityHit.hurtResistantTime = 0;	// No immunity frames
            this.targetsHit += 1;					// Punched through one more entity
            
            // Bonus
            EntityLightningBolt bolt = new EntityLightningBolt(this.world, target.entityHit.posX, target.entityHit.posY, target.entityHit.posZ, false);
            this.world.addWeatherEffect(bolt);
        }
		else
		{
			// Let's blast through terrain on hit
			
			int x = target.getBlockPos().getX();
			int y = target.getBlockPos().getY();
			int z = target.getBlockPos().getZ();
			
			IBlockState meta = this.world.getBlockState(new BlockPos(x, y, z));
			Block toBeBroken = meta.getBlock();
			
			
			boolean breakThis = true;
			
			if (toBeBroken.getHarvestLevel(meta) > 1) 
			{ 
				breakThis = false; 
				this.targetsHit += 1;	// Thicker materials
			}
			
			if (toBeBroken.getHarvestLevel(meta) > 2) 
			{ 
				breakThis = false; 
				this.targetsHit += 2;	// Even thicker materials
			}
			
			if (toBeBroken.getHarvestLevel(meta) > 3) 
			{ 
				breakThis = false; 
				this.targetsHit += 3;	// Super thick material
			}
			
	    	if (toBeBroken.getMaterial(meta) == Material.WATER) { breakThis = false; }
	    	
	    	if (toBeBroken == Blocks.WATER) { breakThis = false; }
	    	if (toBeBroken == Blocks.FLOWING_WATER) { breakThis = false; }
	    	
	    	if (toBeBroken == Blocks.OBSIDIAN) 
	    	{ 
	    		breakThis = false; 
	    		this.targetsHit += 2;	// Thicker materials
	    	}
	    	
	    	if (toBeBroken == Blocks.IRON_BLOCK) 
	    	{ 
	    		breakThis = false; 
	    		this.targetsHit += 2;	// Thicker materials
	    	}
	    	
	    	if (breakThis)	// Sorted out all blocks we don't want to break. Checking the rest now
	    	{
	    		// Glass breaking
	        	Helper.tryBlockBreak(this.world, this, target, 3);	// Very Strong
	    	}
			
			this.targetsHit += 1;	// Punched through one more block, no matter if we managed to break it
		}
		
		// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), (byte) 10, (byte) 2);
		//this.world.playSoundAtEntity(this, "ambient.weather.thunder", 0.7F, 0.5F);
		
		if (this.targetsHit > this.targetsHitMax) { this.setDead(); }	// Went through the maximum, so ending now
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 6;	// Type 6, beam weapon (lightning red)
		type[1] = 2;	// Length
		type[2] = 2;	// Width
		
		return type;
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/rod.png"; }	// Our projectile texture
	
	
	@Override
	public void writeSpawnData(ByteBuf buffer) 			// save extra data on the server
    {
		buffer.writeDouble(this.ownerX);
		buffer.writeDouble(this.ownerY);
		buffer.writeDouble(this.ownerZ);
    }
    
	@Override
	public void readSpawnData(ByteBuf additionalData) 	// read it on the client
	{ 
		this.ownerX = additionalData.readDouble();
		this.ownerY = additionalData.readDouble();
		this.ownerZ = additionalData.readDouble();
	}
}
