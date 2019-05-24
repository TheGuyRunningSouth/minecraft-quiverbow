package com.domochevsky.quiverbow.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class Sabot_Arrow extends _ProjectileBase
{
	public float speed;
	
	public Sabot_Arrow(World world) { super(world); }

	public Sabot_Arrow(World world, Entity entity, float speed)
    {
        super(world);
        this.speed = speed;
        this.doSetup(entity, speed);
    }
	
	
	@Override
	public void onImpact(RayTraceResult target)	// Server-side
	{
		if (target.entityHit != null) 	// Hit a entity
    	{
    		target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) 3);
    		target.entityHit.hurtResistantTime = 0; // No immunity frames
        }
    	else	// Hit the terrain
    	{
			// Glass breaking
    		Helper.tryBlockBreak(this.world, this, target, 1);
    	}

    	// Spawning a rose of arrows here
		this.fireArrow(1.0f, 0.0f);
		this.fireArrow(180.0f, 0.0f);
		this.fireArrow(90.0f, 0.0f);
		this.fireArrow(-90.0f, 0.0f);
		this.fireArrow(45.0f, -45.0f);
		this.fireArrow(-45.0f, -45.0f);
		this.fireArrow(135.0f, -45.0f);
		this.fireArrow(-135.0f, 45.0f);
    	
    	// SFX
        this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 3.0F);
        NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), (byte) 11, (byte) 4);
       
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	private void fireArrow(float accHor, float accVert)
	{
		RegularArrow arrow = new RegularArrow(this.world, this, this.speed / 2, accHor, accVert);	// Half speed
    	
		arrow.damage = this.damage;
    	arrow.shootingEntity = this.shootingEntity;
    	
    	this.world.spawnEntity(arrow);
	}
	
	
	@Override
    public boolean attackEntityFrom(DamageSource source, float par2) // Big rockets can be swatted out of the way with a bit of expertise
    {
    	if (this.getIsInvulnerable()) { return false; }
        else	// Not invulnerable
        {
            //this.setBeenAttacked();

            if (source.getTrueSource() instanceof EntityLivingBase) 	// Damaged by a entity
            {
                Vec3d vec3 = source.getTrueSource().getLookVec();	// Which is looking that way...

                if (vec3 != null) 
                {
                    this.motionX = vec3.x;
                    this.motionY = vec3.y;
                    this.motionZ = vec3.z;
                }

                if (source.getTrueSource() instanceof EntityLivingBase) { this.shootingEntity = (EntityLivingBase)source.getTrueSource(); }

                return true;
            }
            // else, not damaged by an entity
    	}
    	
    	return false;
    }
	
	
	@Override
	public byte[] getRenderType()	// Called by the renderer. Expects a 3 item byte array
	{
		byte[] type = new byte[3];
		
		type[0] = 2;	// Type 2, projectile
		type[1] = 10;	// Length
		type[2] = 3;	// Width
		
		return type; // Fallback, 0 0 0
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/arrowsabot.png"; }
}
