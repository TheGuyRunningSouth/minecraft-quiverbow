package com.domochevsky.quiverbow.Renderer;
/*
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.ArmsAssistant.Model_AA;

public class Render_AA extends RenderLiving
{
	private static ResourceLocation texture = new ResourceLocation("quiverchevsky", "textures/entity/ArmsAssistant.png");
	
	public Render_AA() 
	{
		super(renderManager, new Model_AA(), 1.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		// Add upgrade sensitive version here
		if (entity instanceof Entity_AA)
		{
			
		}
		
		return texture;
	}

	
	@Override
	protected void renderEquippedItems(EntityLivingBase entity, float unknown)
    {
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
        super.renderEquippedItems(entity, unknown);
        
        ItemStack itemstack = entity.getHeldItem();
        
        if (itemstack != null && itemstack.getItem() != null) 
        { 
        	 GL11.glPushMatrix();

             GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

             float scale = 0.625F;

             GL11.glTranslatef(0.3F, 0.2F, -0.25F);		// 0.0F, 0.1875F, 0.0F, left/right, up/down, forward/backward?
             GL11.glScalef(scale, -scale, scale);
             GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);	// -100
             GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

             int color = itemstack.getItem().getColorFromItemStack(itemstack, 0);
             float colorR = (float) (color >> 16 & 255) / 255.0F;
             float colorB = (float) (color >> 8 & 255) / 255.0F;
             float colorG = (float) (color & 255) / 255.0F;
             GL11.glColor4f(colorR, colorB, colorG, 1.0F);
            
             this.renderManager.itemRenderer.renderItem(entity, itemstack, 0);

             GL11.glPopMatrix();
        }
        
        Entity_AA turret = (Entity_AA) entity;
        
        if (turret.hasWeaponUpgrade)	// Has a second weapon rail, so drawing the weapon from that
        {
        	itemstack = entity.getEquipmentInSlot(1);
            
            if (itemstack != null && itemstack.getItem() != null) 
            { 
            	 GL11.glPushMatrix();

                 GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

                 float scale = 0.625F;

                 GL11.glTranslatef(0.6F, 0.5F, -0.25F);		// 0.0F, 0.1875F, 0.0F, left/right, up/down, forward/backward?
                 GL11.glScalef(scale, -scale, scale);
                 GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);	// -100
                 GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

                 int color = itemstack.getItem().getColorFromItemStack(itemstack, 0);
                 float colorR = (float) (color >> 16 & 255) / 255.0F;
                 float colorB = (float) (color >> 8 & 255) / 255.0F;
                 float colorG = (float) (color & 255) / 255.0F;
                 GL11.glColor4f(colorR, colorB, colorG, 1.0F);
                 
                 this.renderManager.itemRenderer.renderItem(entity, itemstack, 0);

                 GL11.glPopMatrix();
            }
        }
        
        this.renderStoredItems((Entity_AA) entity);
    }
	
	
	private void renderStoredItems(Entity_AA turret)
	{
		int slot = 0;
		float modX = 0;
		float modY = 0;
		
		int iconsPerRow = 4;
		int iconMulti = 1;
		int iconsDrawn = 0;
		
		while (slot < turret.storage.length)
		{
			ItemStack itemstack = turret.storage[slot];
			
			//System.out.println("[RENDER] Itemstack in slot " + slot + " is " + itemstack);
	        
	        if (itemstack != null && itemstack.getItem() != null) 
	        { 
	        	GL11.glPushMatrix();

		 	        GL11.glTranslatef(0.32F, 0.35F + modY, -0.35F + modX);
		 	        
		 	        modX += 0.15f;	// One step back
	
		 	        float scale = 0.08F;	// Smaller, to make that less blatant
	
		 	        GL11.glScalef(scale, -scale, scale);
		 	        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
	
		 	        int color = itemstack.getItem().getColorFromItemStack(itemstack, 0);
		 	        float f4 = (float)(color >> 16 & 255) / 255.0F;
		 	        float f5 = (float)(color >> 8 & 255) / 255.0F;
		 	        float f2 = (float)(color & 255) / 255.0F;
		 	        GL11.glColor4f(f4, f5, f2, 1.0F);
		 	        
		 	        this.renderManager.itemRenderer.renderItem(turret, itemstack, 0);

	 	        GL11.glPopMatrix();
	 	        
	 	       iconsDrawn += 1;
	        }
	        
	        if (iconsDrawn == (iconsPerRow * iconMulti))	// This many items are shown per row
	        {
	        	modX = 0;		// Reset
	        	modY = 0.15f;	// One row down
	        	iconMulti += 1;
	        }
			
			slot += 1;
		}
	}
}
*/