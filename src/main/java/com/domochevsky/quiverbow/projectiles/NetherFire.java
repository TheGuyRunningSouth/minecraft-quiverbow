package com.domochevsky.quiverbow.projectiles;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class NetherFire extends _ProjectileBase
{	
	public NetherFire(World world) { super(world); }

	public NetherFire(World world, Entity entity, float speed, float accHor, float AccVert) 
    {
        super(world);
        this.doSetup(entity, speed, accHor, AccVert, entity.rotationYaw, entity.rotationPitch);
    }
	
	
	@Override
	public void doFlightSFX()
	{
		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), (byte) 4, (byte) 2);
	}
	
	
	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null) 		// We hit a living thing!
    	{
			// Damage
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) this.damage);
			target.entityHit.hurtResistantTime = 0;
			
			// Effect
			target.entityHit.setFire(this.fireDuration);
			
        }        
        else 
        { 
        	BlockPos oneAbove = new BlockPos(target.getBlockPos().getX(), target.getBlockPos().getY()+1, target.getBlockPos().getZ());
        	Block block = this.world.getBlockState(target.getBlockPos()).getBlock();
			
			// Glass breaking
        	Helper.tryBlockBreak(this.world, this, target, 1);	// Medium
            
        	// Let's create fire here (if we're allowed to)
        	if (this.world.getGameRules().getBoolean("doFireTick") && block != Blocks.FIRE)
        	{
        		if (this.world.getBlockState(oneAbove) == Blocks.AIR.getDefaultState())
	        	{
	        		// the block above the block we hit is air, so let's set it on fire!
	        		this.world.setBlockState(oneAbove, Blocks.FIRE.getDefaultState(), 3);
	        	}
        		// else, not a airblock above this
        	}
        	
        	// Have we hit snow? Turning that into snow layer
        	if (block == Blocks.SNOW)
        	{
        		this.world.setBlockState(target.getBlockPos(), Blocks.SNOW_LAYER.getDefaultState(), 3);
        	}
        	
        	// Have we hit snow layer? Melting that down into nothing
        	else if (block == Blocks.SNOW_LAYER)
        	{
        		/* TODO: Fix snow layer melting
        		IBlockState currentMeta = this.world.getBlockState(target.getBlockPos());
        		// Is this taller than 0? Melting it down then
        		if (currentMeta > 0) { this.world.setBlock(target.getBlockPos(), Blocks.SNOW_LAYER, currentMeta - 1, 3); }
        		// Is this 0 already? Turning it into air
        		else { this.world.setBlockToAir(target.getBlockPos()); }
        		*/
        	}
        	
        	// Have we hit ice? Turning that into water
        	else if (block == Blocks.ICE)
        	{
        		this.world.setBlockState(target.getBlockPos(), Blocks.WATER.getDefaultState(), 3);
        	}
        	
        	Block topBlock = this.world.getBlockState(oneAbove).getBlock();
        	
        	// Did we hit grass? Burning it
        	if (topBlock.getMaterial(this.world.getBlockState(oneAbove)) == Material.PLANTS)
        	{
        		this.world.setBlockState(oneAbove, Blocks.FIRE.getDefaultState(), 3);
        	}
        	if (block.getMaterial(this.world.getBlockState(oneAbove)) == Material.PLANTS)
        	{
        		this.world.setBlockState(target.getBlockPos(), Blocks.FIRE.getDefaultState(), 3);
        	}
        }
    	
		// SFX
    	this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.7F, 1.5F);
    	this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);	
    	
        this.setDead();		// We've hit something, so begone with the projectile. hitting glass only once
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 2;	// Type 2, generic projectile
		type[1] = 2;	// Length
		type[2] = 2;	// Width
		
		return type;
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/netherspray.png"; }	// Our projectile texture
}
