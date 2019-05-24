package com.domochevsky.quiverbow.AI;
/*
import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class AI_Communication 
{
	// Couldn't reload, meaning we're out now. They also seem to want to be informed about this
	public static void tellOwnerAboutAmmo(Entity_AA turret, boolean secondRail)
	{
		// Is empty, so telling the owner about this
		EntityPlayer owner = turret.world.getPlayerEntityByName(turret.ownerName);
	
		if (owner == null) { return; }	// Might not be online right now
		
		// My name
		String turretName = "[ARMS ASSISTANT " + turret.getEntityId() + "]";
		if (turret.getCustomNameTag() != null && !turret.getCustomNameTag().isEmpty()) { turretName = "[" + turret.getCustomNameTag() + "]"; }

		if (secondRail)
		{
			owner.sendMessage(new TextComponentString(TextFormatting.GRAY + turretName + ": Second rail is out of ammunition."));
		}
		else
		{
			owner.sendMessage(new TextComponentString(TextFormatting.GRAY + turretName + ": First rail is out of ammunition."));
		}
	}
	
	
	public static void tellOwnerAboutDeath(Entity_AA turret)
	{
		// Is empty, so telling the owner about this
		EntityPlayer owner = turret.world.getPlayerEntityByName(turret.ownerName);
	
		if (owner == null) { return; }	// Might not be online right now
		
		// My name
		String turretName = "ARMS ASSISTANT " + turret.getEntityId();
		if (turret.getCustomNameTag() != null && !turret.getCustomNameTag().isEmpty()) { turretName = turret.getCustomNameTag(); }

		owner.sendMessage(new TextComponentString(TextFormatting.GRAY + turretName + " was destroyed!"));
	}
	
	
	public static void tellOwnerAboutHealth(Entity_AA turret)
	{
		// Is empty, so telling the owner about this
		EntityPlayer owner = turret.world.getPlayerEntityByName(turret.ownerName);
	
		if (owner == null) { return; }	// Might not be online right now
		
		// My name
		String turretName = "[ARMS ASSISTANT " + turret.getEntityId() + "]";
		if (turret.getCustomNameTag() != null && !turret.getCustomNameTag().isEmpty()) { turretName = "[" + turret.getCustomNameTag() + "]"; }

		owner.sendMessage(new TextComponentString(TextFormatting.GRAY + turretName + ": Warning. Structural integrity is below 30%."));
	}
}
*/