package com.domochevsky.quiverbow.blocks;

import java.util.List;
import java.util.Random;

import com.domochevsky.quiverbow.Helper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class FenLight extends Block
{
	float size = 0.25F;
	float sizeMin = 0.375F;
	float sizeMax = 0.625F;
	
	public FenLight(Material material) 
	{
		super(material);
		this.setLightLevel(0.95F);	// Light, yo
		this.setHardness(0.2F);
		this.setResistance(10.0F);
		//this.setStepSound(SoundEvents.BLOCK_GLASS_STEP);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		//this.setBlockTextureName("glowstone");
		//this.setBlockName("Fen Light");
		//this.setBlockBounds(sizeMin, sizeMin, sizeMin, sizeMax, sizeMax, sizeMax);
	}
	
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		// Let's try a small cube for starters EDIT: Let's not. No stepping stones here
        /*return AxisAlignedBB.getAABBPool().getAABB(
        		(double)x + this.getBlockBoundsMinX(), (double)y + this.getBlockBoundsMinY(), (double)z + this.getBlockBoundsMinZ(),
        		(double)x + this.getBlockBoundsMaxX(), (double)y + this.getBlockBoundsMaxY(), (double)z + this.getBlockBoundsMaxZ()
        );*/
		return null;
    }
	/*
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
		//int meta = world.getBlockState(x, y, z);
		
		float plusX = 0;
		float plusY = 0;
		float plusZ = 0;
		
		//if (meta == 0)	{ plusY = 0.375F; }			// Bottom, so need to move to the top
		//else if (meta == 1) { plusY = -0.375F; } 	// Top, so need to move to the bottom
		//else if (meta == 2) { plusZ = 0.375F; }		// East, so need to move west
		//else if (meta == 3) { plusZ = -0.375F; }	// West, so need to move east
		//else if (meta == 4) { plusX = 0.375F; } 	// North, so need to move south
		//else if (meta == 5) { plusX = -0.375F; } 	// South, so need to move north
		
		// this.setBlockBounds(sizeMin + plusX, sizeMin + plusY, sizeMin + plusZ, sizeMax + plusX, sizeMax + plusY, sizeMax + plusZ);
    }
	*/
	
	@Override
	public boolean isOpaqueCube(IBlockState State) { return false; }
	
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
		if (!world.isRemote)
		{
			/* TODO: Fix fen light self-breaking
			// Checking here to see if the block we're attached to is valid (and breaking if it isn't)
			int meta = world.getBlockState(pos);	// Contains the side we're attached to
			
			if (meta == 0) { if ( !Helper.hasValidMaterial(world, fromPos) ) { world.setBlockToAir(pos); } } // Bottom, checking Top
			else if (meta == 1) { if ( !Helper.hasValidMaterial(world, x, y - 1, z) ) { world.setBlockToAir(x, y, z); } } // Top, checking Bottom
			else if (meta == 2) { if ( !Helper.hasValidMaterial(world, x, y, z + 1) ) { world.setBlockToAir(x, y, z); } }	// East
			else if (meta == 3) { if ( !Helper.hasValidMaterial(world, x, y, z - 1) ) { world.setBlockToAir(x, y, z); } }	// West
			else if (meta == 4) { if ( !Helper.hasValidMaterial(world, x + 1, y, z) ) { world.setBlockToAir(x, y, z); } }	// North
			else if (meta == 5) { if ( !Helper.hasValidMaterial(world, x - 1, y, z) ) { world.setBlockToAir(x, y, z); } }	// South
			*/
		}
    }
	
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) 
	{
		// If this gets called then someone wants the light to turn back into air, since the timer ran out
		if (!world.isRemote) 
		{ 
			world.setBlockToAir(pos); 
			
			// SFX
	    	for (int i = 0; i < 8; ++i) { world.spawnParticle(EnumParticleTypes.SLIME, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D); }
		}
	}
	
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) { return null; } // Dropping nothing. We're gonna stay behind the scenes
}
