package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ShotPotion;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class LapisShot extends _ProjectileBase
{
	// Potion effects, special for the Lapis Coil
	public ShotPotion pot1;
	public ShotPotion pot2;
	public ShotPotion pot3;
	
		
	public LapisShot(World world) { super(world); }
	
	public LapisShot(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	@Override
	public void onImpact(RayTraceResult movPos)	// Server-side
	{
		if (movPos.entityHit != null) 		// We hit a living thing!
    	{	
			movPos.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) this.damage);	// Damage gets applied here
			
			//System.out.println("[LAPIS SHOT] Hit entity: " + movPos.entityHit);
			
			movPos.entityHit.hurtResistantTime = 0;	// No immunity frames
            
            if (movPos.entityHit instanceof EntityLivingBase)	// We hit a LIVING living thing!
            {
	            EntityLivingBase entitylivingbase = (EntityLivingBase) movPos.entityHit;
	           
	            Helper.applyPotionEffect(entitylivingbase, pot1);
	            Helper.applyPotionEffect(entitylivingbase, pot2);
	            Helper.applyPotionEffect(entitylivingbase, pot3);
            }
            
            this.setDead();		// We've hit something, so begone with the projectile
        }
		
		else // Hit the terrain
		{
			//Helper.tryBlockBreak(this.worldObj, this, movPos, 1);
			
			if (Helper.tryBlockBreak(this.world, this, movPos, 1)) { this.setDead(); } // Going straight through a thing
            else	// Didn't manage to break that block, so we're stuck now for a short while
            {
            	this.stuckBlockPos = movPos.getBlockPos();
            	//this.stuckBlockX = movPos.blockX;
                //this.stuckBlockY = movPos.blockY;
                //this.stuckBlockZ = movPos.blockZ;
                
                this.stuckBlock = this.world.getBlockState(this.stuckBlockPos).getBlock();
                this.inState = this.world.getBlockState(this.stuckBlockPos);
                
                this.motionX = (double)((float)(movPos.hitVec.x - this.posX));
                this.motionY = (double)((float)(movPos.hitVec.y - this.posY));
                this.motionZ = (double)((float)(movPos.hitVec.z - this.posZ));
                
                float distance = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                
                this.posX -= this.motionX / (double)distance * 0.05000000074505806D;
                this.posY -= this.motionY / (double)distance * 0.05000000074505806D;
                this.posZ -= this.motionZ / (double)distance * 0.05000000074505806D;
                
                this.inGround = true;
                
                this.arrowShake = 7;

                if (this.stuckBlock.getMaterial(this.inState) != Material.AIR)
                {
                    this.stuckBlock.onEntityCollidedWithBlock(this.world, new BlockPos(this.stuckBlockX, this.stuckBlockY, this.stuckBlockZ), this.inState, this);
                }
            }
		}
    	
		// SFX
    	this.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.5F);
        NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), (byte) 3, (byte) 4);
        
        //this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public void doFlightSFX() 
	{ 
		NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), (byte) 1, (byte) 2);
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 2;	// Type 2, generic projectile
		type[1] = 8;	// Length
		type[2] = 2;	// Width
		
		return type; // Fallback, 0 0 0
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/lapis.png"; }	// Our projectile texture
}
