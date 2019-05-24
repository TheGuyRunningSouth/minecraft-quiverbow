package com.domochevsky.quiverbow.projectiles;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class FlintDust extends _ProjectileBase implements IEntityAdditionalSpawnData
{	
	public FlintDust(World world) { super(world); }

	public FlintDust(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
        
        this.ownerX = entity.posX;
        this.ownerY = entity.posY + 1.0d;
        this.ownerZ = entity.posZ;
    }
	
	
	@Override
	public boolean doDropOff() { return false; }	// Affected by gravity? Nope

	
	@Override
	public void doFlightSFX()
	{
		if (this.shootingEntity == null) { return; }	// Shouldn't be a thing
		
		Vec3d vec_entity = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec_shooter = new Vec3d(this.shootingEntity.posX, this.shootingEntity.posY, this.shootingEntity.posZ);
        
        double distance = vec_entity.distanceTo(vec_shooter);	// The distance between this entity and the shooter
        
        //System.out.println("[ENTITY] Distance to shooter: " + distance);
        if (distance > this.ticksInAirMax - 2) { this.setDead(); }	// Starting 0.5 blocks in front of the player and ends one+ block after the target. So ending now
        NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), (byte) 3, (byte) 1);
	}
	
	
	@Override
	public void onImpact(RayTraceResult target)
	{
		if (target.entityHit != null) 		// We hit a living thing!
    	{		
			// Damage
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float)this.damage);
        }
		else	// Hit the terrain
    	{
    		Block block = this.world.getBlockState(target.getBlockPos()).getBlock();            
            IBlockState meta = this.world.getBlockState(target.getBlockPos());
            
            boolean breakThis = true;
            
            // Checking here against invalid blocks
        	if (block == Blocks.BEDROCK) { breakThis = false; }
        	else if (block == Blocks.WATER) { breakThis = false; }
        	else if (block == Blocks.FLOWING_WATER) { breakThis = false; }
        	else if (block == Blocks.LAVA) { breakThis = false; }
        	else if (block == Blocks.FLOWING_LAVA) { breakThis = false; }
        	else if (block == Blocks.OBSIDIAN) { breakThis = false; }
        	else if (block == Blocks.MOB_SPAWNER) { breakThis = false; }
        	
        	else if (block.getMaterial(meta) == Material.WATER) { breakThis = false; }
        	else if (block.getMaterial(meta) == Material.LAVA) { breakThis = false; }
        	else if (block.getMaterial(meta) == Material.AIR) { breakThis = false; }
        	else if (block.getMaterial(meta) == Material.PORTAL) { breakThis = false; }
        	
        	else if (block.getHarvestLevel(meta) > 0) { breakThis = false; }
        	else if (block.getBlockHardness(meta, this.world, target.getBlockPos()) > 3) { breakThis = false; }
        	
        	if (this.shootingEntity instanceof EntityPlayerMP)
        	{
        		GameType gametype = this.world.getWorldInfo().getGameType();
            	int event = ForgeHooks.onBlockBreakEvent(this.world, gametype, (EntityPlayerMP) this.shootingEntity, target.getBlockPos());
               
            	if (event == -1) { breakThis = false; }	// Not allowed to do this
        	}
            
            if (breakThis)	// Nothing preventing us from breaking this block!
            {            	
            	this.world.setBlockToAir(target.getBlockPos());
            	block.dropBlockAsItem(this.world, target.getBlockPos(), meta, 0);
            }
    	}
		
		// SFX
		for (int i = 0; i < 4; ++i) { this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D); }
		this.playSound(SoundEvents.BLOCK_GRAVEL_BREAK, 1.0F, 1.0F);
		
		this.setDead();	// Hit something, so begone.
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 5;	// Type 5, beam weapon (Flint dust)
		type[1] = 2;	// Length
		type[2] = 2;	// Width
		
		return type;
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/flint.png"; }	// Our projectile texture
	
	
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
