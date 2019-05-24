package com.domochevsky.quiverbow;

import java.util.ArrayList;

//import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.ammo.BoxOfFlintDust;
import com.domochevsky.quiverbow.ammo.ColdIronClip;
import com.domochevsky.quiverbow.ammo.EnderQuartzClip;
import com.domochevsky.quiverbow.ammo.GatlingAmmo;
import com.domochevsky.quiverbow.ammo.GoldMagazine;
import com.domochevsky.quiverbow.ammo.LapisMagazine;
import com.domochevsky.quiverbow.ammo.LargeNetherrackMagazine;
import com.domochevsky.quiverbow.ammo.LargeRedstoneMagazine;
import com.domochevsky.quiverbow.ammo.LargeRocket;
import com.domochevsky.quiverbow.ammo.NeedleMagazine;
import com.domochevsky.quiverbow.ammo.ObsidianMagazine;
//import com.domochevsky.quiverbow.ammo.PackedUpAA;
import com.domochevsky.quiverbow.ammo.Part_GatlingBarrel;
import com.domochevsky.quiverbow.ammo.Part_GatlingBody;
import com.domochevsky.quiverbow.ammo.RedstoneMagazine;
import com.domochevsky.quiverbow.ammo.RocketBundle;
import com.domochevsky.quiverbow.ammo.SeedJar;
import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.blocks.FenLight;
import com.domochevsky.quiverbow.models.AATH_Model;
import com.domochevsky.quiverbow.models.AquaAcc_Model;
import com.domochevsky.quiverbow.models.CoinTosser_Mod_Model;
import com.domochevsky.quiverbow.models.CoinTosser_Model;
import com.domochevsky.quiverbow.models.CrossbowAutoImp_Model;
import com.domochevsky.quiverbow.models.CrossbowAuto_Model;
import com.domochevsky.quiverbow.models.CrossbowDouble_Model;
import com.domochevsky.quiverbow.models.Crossbow_Model;
import com.domochevsky.quiverbow.models.DragonBox_Model;
import com.domochevsky.quiverbow.models.ERA_Model;
import com.domochevsky.quiverbow.models.EnderNymous_Model;
import com.domochevsky.quiverbow.models.EnderRifle_Model;
import com.domochevsky.quiverbow.models.FenFire_Model;
import com.domochevsky.quiverbow.models.FlintDuster_Model;
import com.domochevsky.quiverbow.models.FrostLancer_Model;
import com.domochevsky.quiverbow.models.LapisCoil_Model;
import com.domochevsky.quiverbow.models.LightningRed_Model;
import com.domochevsky.quiverbow.models.MediGun_Model;
import com.domochevsky.quiverbow.models.Mortar_Model;
import com.domochevsky.quiverbow.models.NetherBellows_Model;
import com.domochevsky.quiverbow.models.OSP_Model;
import com.domochevsky.quiverbow.models.OSR_Model;
import com.domochevsky.quiverbow.models.OWR_Model;
import com.domochevsky.quiverbow.models.PTT_Model;
import com.domochevsky.quiverbow.models.Potatosser_Model;
import com.domochevsky.quiverbow.models.QuadBox_Model;
import com.domochevsky.quiverbow.models.RPG_Model;
import com.domochevsky.quiverbow.models.RedSprayer_Model;
import com.domochevsky.quiverbow.models.SeedSweeper_Model;
import com.domochevsky.quiverbow.models.Seedling_Model;
import com.domochevsky.quiverbow.models.SnowCannon_Model;
import com.domochevsky.quiverbow.models.SoulCairn_Model;
import com.domochevsky.quiverbow.models.SugarEngine_Model;
import com.domochevsky.quiverbow.models.Sunray_Model;
import com.domochevsky.quiverbow.models.ThornSpitter_Model;
import com.domochevsky.quiverbow.net.PacketHandler;
import com.domochevsky.quiverbow.projectiles.BigRocket;
import com.domochevsky.quiverbow.projectiles.BlazeShot;
import com.domochevsky.quiverbow.projectiles.CoinShot;
import com.domochevsky.quiverbow.projectiles.ColdIron;
import com.domochevsky.quiverbow.projectiles.EnderAccelerator;
import com.domochevsky.quiverbow.projectiles.EnderAno;
import com.domochevsky.quiverbow.projectiles.EnderShot;
import com.domochevsky.quiverbow.projectiles.FenGoop;
import com.domochevsky.quiverbow.projectiles.FlintDust;
import com.domochevsky.quiverbow.projectiles.HealthBeam;
import com.domochevsky.quiverbow.projectiles.LapisShot;
import com.domochevsky.quiverbow.projectiles.NetherFire;
import com.domochevsky.quiverbow.projectiles.OSP_Shot;
import com.domochevsky.quiverbow.projectiles.OSR_Shot;
import com.domochevsky.quiverbow.projectiles.OWR_Shot;
import com.domochevsky.quiverbow.projectiles.PotatoShot;
import com.domochevsky.quiverbow.projectiles.ProxyThorn;
import com.domochevsky.quiverbow.projectiles.RedLight;
import com.domochevsky.quiverbow.projectiles.RedSpray;
import com.domochevsky.quiverbow.projectiles.RegularArrow;
import com.domochevsky.quiverbow.projectiles.Sabot_Arrow;
import com.domochevsky.quiverbow.projectiles.Sabot_Rocket;
import com.domochevsky.quiverbow.projectiles.ScopedPredictive;
import com.domochevsky.quiverbow.projectiles.Seed;
import com.domochevsky.quiverbow.projectiles.SmallRocket;
import com.domochevsky.quiverbow.projectiles.SnowShot;
import com.domochevsky.quiverbow.projectiles.SoulShot;
import com.domochevsky.quiverbow.projectiles.SugarRod;
import com.domochevsky.quiverbow.projectiles.SunLight;
import com.domochevsky.quiverbow.projectiles.Thorn;
import com.domochevsky.quiverbow.projectiles.WaterShot;
import com.domochevsky.quiverbow.projectiles.WebShot;
import com.domochevsky.quiverbow.recipes.Recipe_Ammo;
//import com.domochevsky.quiverbow.recipes.Recipe_ERA;
import com.domochevsky.quiverbow.recipes.Recipe_Weapon;
import com.domochevsky.quiverbow.weapons.AA_Targeter;
import com.domochevsky.quiverbow.weapons.AquaAccelerator;
import com.domochevsky.quiverbow.weapons.CoinTosser;
import com.domochevsky.quiverbow.weapons.CoinTosser_Mod;
import com.domochevsky.quiverbow.weapons.Crossbow_Auto;
import com.domochevsky.quiverbow.weapons.Crossbow_AutoImp;
import com.domochevsky.quiverbow.weapons.Crossbow_Blaze;
import com.domochevsky.quiverbow.weapons.Crossbow_Compact;
import com.domochevsky.quiverbow.weapons.Crossbow_Double;
import com.domochevsky.quiverbow.weapons.DragonBox;
import com.domochevsky.quiverbow.weapons.DragonBox_Quad;
import com.domochevsky.quiverbow.weapons.ERA;
import com.domochevsky.quiverbow.weapons.EnderBow;
import com.domochevsky.quiverbow.weapons.EnderRifle;
import com.domochevsky.quiverbow.weapons.Endernymous;
import com.domochevsky.quiverbow.weapons.FenFire;
import com.domochevsky.quiverbow.weapons.FlintDuster;
import com.domochevsky.quiverbow.weapons.FrostLancer;
import com.domochevsky.quiverbow.weapons.LapisCoil;
import com.domochevsky.quiverbow.weapons.LightningRed;
import com.domochevsky.quiverbow.weapons.MediGun;
import com.domochevsky.quiverbow.weapons.Mortar_Arrow;
import com.domochevsky.quiverbow.weapons.Mortar_Dragon;
import com.domochevsky.quiverbow.weapons.NetherBellows;
import com.domochevsky.quiverbow.weapons.OSP;
import com.domochevsky.quiverbow.weapons.OSR;
import com.domochevsky.quiverbow.weapons.OWR;
import com.domochevsky.quiverbow.weapons.Potatosser;
import com.domochevsky.quiverbow.weapons.PowderKnuckle;
import com.domochevsky.quiverbow.weapons.PowderKnuckle_Mod;
import com.domochevsky.quiverbow.weapons.ProximityNeedler;
import com.domochevsky.quiverbow.weapons.QuiverBow;
import com.domochevsky.quiverbow.weapons.RPG;
import com.domochevsky.quiverbow.weapons.RPG_Imp;
import com.domochevsky.quiverbow.weapons.RedSprayer;
import com.domochevsky.quiverbow.weapons.SeedSweeper;
import com.domochevsky.quiverbow.weapons.Seedling;
import com.domochevsky.quiverbow.weapons.SilkenSpinner;
import com.domochevsky.quiverbow.weapons.SnowCannon;
import com.domochevsky.quiverbow.weapons.SoulCairn;
import com.domochevsky.quiverbow.weapons.SugarEngine;
import com.domochevsky.quiverbow.weapons.Sunray;
import com.domochevsky.quiverbow.weapons.ThornSpitter;
import com.domochevsky.quiverbow.weapons._WeaponBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid=Main.modId, name="QuiverBow", version="2.0.1-alpha")
public class Main
{
	public static final String modId = "quiverchevsky";
	@Instance("quiverchevsky")
	public static Main instance;
	
	@SidedProxy(clientSide="com.domochevsky.quiverbow.ClientProxy", serverSide="com.domochevsky.quiverbow.CommonProxy")
	public static CommonProxy proxy;
	
	protected Configuration config;										// Accessible from other files this way
	
	// TODO: Overhaul all of this to use arraylist.
	public static ArrayList<_WeaponBase> weapons = new ArrayList<_WeaponBase>();	// Holder array for all (fully set up) possible weapons
	public static ArrayList<_AmmoBase> ammo = new ArrayList<_AmmoBase>();			// Same with ammo, since they got recipes as well
	//private static String[] weaponType = new String[60];		// For Battle Gear 2
	
	@SideOnly(Side.CLIENT)
	public static ArrayList<ModelBase> models;	// Client side only
	
	public static Block fenLight = null;
	
	private static int projectileCount = 1;	// A running number, to register projectiles by
	
	// Config
	public static boolean breakGlass;				// If this is false then we're not allowed to break blocks with projectiles (Don't care about TNT)
	public static boolean useModels;				// If this is false then we're reverting back to held icons
	public static boolean noCreative;				// If this is true then disabled weapons won't show up in the creative menu either
	public static boolean allowTurret;				// If this is false then the Arms Assistant will not be available
	public static boolean allowTurretPlayerAttacks;	// If this is false then the AA is not allowed to attack players (ignores them)
	public static boolean restrictTurretRange;		// If this is false then we're not capping the targeting range at 32 blocks
	public static boolean sendBlockBreak;			// If this is false then Helper.tryBlockBreak() won't send a BlockBreak event. Used by protection plugins.
	
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		this.config = new Configuration(event.getSuggestedConfigurationFile());	// Starting config
		
		this.config.load();	// And loading it up
		
		breakGlass = this.config.get("generic", "Can we break glass and other fragile things with our projectiles? (default true)", true).getBoolean();
		sendBlockBreak = this.config.get("generic", "Do we send a BlockBreak event when breaking things with our projectiles? (default true)", true).getBoolean();
		useModels = this.config.get("generic", "Are we using models or icons for held weapons? (default true for models. False for icons)", true).getBoolean();
		noCreative = this.config.get("generic", "Are we removing disabled weapons from the creative menu too? (default false. On there, but uncraftable)", false).getBoolean();
		
		allowTurret = this.config.get("Arms Assistant", "Am I enabled? (default true)", true).getBoolean();
		restrictTurretRange = this.config.get("Arms Assistant", "Is my firing range limited to a maximum of 32 blocks? (default true. Set false for 'Shoot as far as your weapon can handle'.)", true).getBoolean();
		
		// Item registry here
		this.registerAmmo();
		this.registerWeapons(event.getSide().isClient());
		//this.registerProjectiles();
		//this.registerBlocks();
		
		//addAllProps(event, this.config);	// All items are registered now. Making recipes and recording props
		
		this.config.save();				// Done with config, saving it
		
		PacketHandler.initPackets();	// Used for sending particle packets, so I can do my thing purely on the server side
		
		//TODO: Fix Arms Assitant
		// Registering the Arms Assistant
		//EntityRegistry.registerModEntity(null, Entity_AA.class, "quiverchevsky_turret", 0, this, 80, 1, true);
		//EntityRegistry.registerModEntity(Entity_BB.class, "quiverchevsky_flyingBB", 1, this, 80, 1, true);
		
		proxy.registerTurretRenderer();
		
		// Do I have to register a crafting listener of sorts? To what end?
	//	RecipeSorter.register("quiverchevsky:recipehandler", Recipe_ERA.class, RecipeSorter.Category.SHAPED, "after:minecraft:shapeless");
	//	RecipeSorter.register("quiverchevsky:recipehandler_2", Recipe_Weapon.class, RecipeSorter.Category.SHAPED, "after:minecraft:shapeless");
	//	RecipeSorter.register("quiverchevsky:recipehandler_3", Recipe_Ammo.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		
		Listener listener = new Listener();
		
		FMLCommonHandler.instance().bus().register(listener);
		MinecraftForge.EVENT_BUS.register(listener);
		
		if (event.getSide().isServer()) { return; }	// Client-only from this point.
		
		ListenerClient listenerClient = new ListenerClient();
		
		FMLCommonHandler.instance().bus().register(listenerClient);
		MinecraftForge.EVENT_BUS.register(listenerClient);
	}
	
	
	/*@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// Everything should be registered by this point. Letting ForgeUpdater know what my version is (if it's installed)
		//FMLInterModComms.sendMessage("forgeupdater", "updaterInfo", "{id='quiverbow', minType='2', formats=[QuiverBow_$mc_$v.zip, QuiverBow_1.7.10_$v.zip]}");
		// id, as it shows up on curse.com, in the URL
		// minType: 0 = alpha, 1 = beta, 2 = release
		// Format is for the file and how it's aranged, eg QuiverBow_1.7.10_b100.zip, $mc and $v are wildcards

		if (Loader.isModLoaded("battlegear2"))
		{
			System.out.println("[QUIVERBOW] Making myself known to Battle Gear 2.");
			int counter = 0;

			while (counter < weapons.length && weapons[counter] != null)
			{
				registerWithBattleGear2(weapons[counter], weaponType[counter]);	// Mod intercompatibility

				counter += 1;
			}
		}
	}*/
	
	
	void registerAmmo()		// Items with which weapons can be reloaded
	{
		this.addAmmo(new ArrowBundle(), "ArrowBundle");
		this.addAmmo(new RocketBundle(), "RocketBundle");
		
		this.addAmmo(new GatlingAmmo(), "SugarMagazine");
		this.addAmmo(new Part_GatlingBody(), "Part_SEBody");
		this.addAmmo(new Part_GatlingBarrel(), "Part_SEBarrel");
		
		this.addAmmo(new LargeRocket(), "LargeRocket");
		this.addAmmo(new ColdIronClip(), "ColdIronClip");
		this.addAmmo(new BoxOfFlintDust(),"BoxOfFlintDust");
		this.addAmmo(new SeedJar(), "SeedJar");
		
		this.addAmmo(new ObsidianMagazine(), "ObsidianMagazine");
		this.addAmmo(new GoldMagazine(), "GoldMagazine");
		this.addAmmo(new NeedleMagazine(), "ThornMagazine");
		this.addAmmo(new LapisMagazine(), "LapisMagazine");
		this.addAmmo(new RedstoneMagazine(), "RedstoneMagazine");
		
		this.addAmmo(new LargeNetherrackMagazine(), "LargeNetherrackMagazine");
		this.addAmmo(new LargeRedstoneMagazine(), "LargeRedstoneMagazine");
		
		//this.addAmmo(new PackedUpAA(), "TurretSpawner");
		//this.addAmmo(new PackedUpBB(), "FlyingAASpawner");
		
		this.addAmmo(new EnderQuartzClip(), "EnderQuartzMagazine");
	}
	
	
	void registerWeapons(boolean isClient)	// The weapons themselves
	{
		this.addWeapon(new Crossbow_Compact(), new Crossbow_Model(), "Crossbow", isClient, "dual");
	//	this.addWeapon(new Crossbow_Double(), new CrossbowDouble_Model(), "CrossbowDouble", isClient, "mainhand");
	//	this.addWeapon(new Crossbow_Blaze(), new Crossbow_Model(), "CrossbowBlaze", isClient, "mainhand");
		this.addWeapon(new Crossbow_Auto(), new CrossbowAuto_Model(), "CrossbowAuto", isClient, "mainhand");
	//	this.addWeapon(new Crossbow_AutoImp(), new CrossbowAutoImp_Model(), "CrossbowAutoImp", isClient, "mainhand");
		
		this.addWeapon(new CoinTosser(), new CoinTosser_Model(), "CoinTosser", isClient, "mainhand");
	//	this.addWeapon(new CoinTosser_Mod(), new CoinTosser_Mod_Model(), "CoinTosser_Mod", isClient, "mainhand");
		
	//	this.addWeapon(new DragonBox(), new DragonBox_Model(), "DragonBox", isClient, "mainhand");
	//	this.addWeapon(new DragonBox_Quad(), new QuadBox_Model(), "DragonBox_Quad", isClient, "mainhand");
		
	//	this.addWeapon(new LapisCoil(), new LapisCoil_Model(), "LapisCoil", isClient, "mainhand");
	//	this.addWeapon(new ThornSpitter(), new ThornSpitter_Model(), "ThornSpitter", isClient, "dual");
	//	this.addWeapon(new ProximityNeedler(), new PTT_Model(), "ProxyNeedler", isClient, "mainhand");
	//	this.addWeapon(new SugarEngine(), new SugarEngine_Model(), "SugarEngine", isClient, "mainhand");
		
	//	this.addWeapon(new RPG(), new RPG_Model(), "RPG", isClient, "mainhand");
	//	this.addWeapon(new RPG_Imp(), new RPG_Model(), "RPG_Imp", isClient, "mainhand");
		
	//	this.addWeapon(new Mortar_Arrow(), new Mortar_Model(), "MortarArrow", isClient, "mainhand");
	//	this.addWeapon(new Mortar_Dragon(), new Mortar_Model(), "MortarRocket", isClient, "mainhand");
		
	//	this.addWeapon(new Seedling(), new Seedling_Model(), "Seedling", isClient, "dual");
	//	this.addWeapon(new Potatosser(), new Potatosser_Model(), "Potatosser", isClient, "mainhand");
	//	this.addWeapon(new SnowCannon(), new SnowCannon_Model(), "SnowCannon", isClient, "dual");
		
	//	this.addWeapon(new QuiverBow(), null, "QuiverBow", isClient, "mainhand");
		
	//	this.addWeapon(new EnderBow(), null, "EnderBow", isClient, "mainhand");
	//	this.addWeapon(new EnderRifle(), new EnderRifle_Model(), "EnderRifle", isClient, "mainhand");
	//	this.addWeapon(new FrostLancer(), new FrostLancer_Model(), "FrostLancer", isClient, "mainhand");
		
	//	this.addWeapon(new OSP(), new OSP_Model(), "OSP", isClient, "dual");
	//	this.addWeapon(new OSR(), new OSR_Model(), "OSR", isClient, "mainhand");
	//	this.addWeapon(new OWR(), new OWR_Model(), "OWR", isClient, "mainhand");
		
	//	this.addWeapon(new FenFire(), new FenFire_Model(), "FenFire", isClient, "dual");
	//	this.addWeapon(new FlintDuster(), new FlintDuster_Model(), "FlintDuster", isClient, "mainhand");
		
	//	this.addWeapon(new LightningRed(), new LightningRed_Model(), "LightningRed", isClient, "mainhand");
	//	this.addWeapon(new Sunray(), new Sunray_Model(), "Sunray", isClient, "mainhand");
		
	//	this.addWeapon(new PowderKnuckle(), null, "PowderKnuckle", isClient, "dual");
	//	this.addWeapon(new PowderKnuckle_Mod(), null, "PowderKnuckle_Mod", isClient, "dual");
		
	//	this.addWeapon(new NetherBellows(), new NetherBellows_Model(), "NetherBellows", isClient, "mainhand");
	//	this.addWeapon(new RedSprayer(), new RedSprayer_Model(), "Redsprayer", isClient, "mainhand");
		
	//	this.addWeapon(new SoulCairn(), new SoulCairn_Model(), "SoulCairn", isClient, "dual");
	//	this.addWeapon(new AquaAccelerator(), new AquaAcc_Model(), "AquaAccelerator", isClient, "dual");
	//	this.addWeapon(new SilkenSpinner(), new AquaAcc_Model(), "SilkenSpinner", isClient, "dual");
		
	//	this.addWeapon(new SeedSweeper(), new SeedSweeper_Model(), "SeedSweeper", isClient, "mainhand");
	//	this.addWeapon(new MediGun(), new MediGun_Model(), "RayOfHope", isClient, "mainhand");
		
	//	this.addWeapon(new ERA(), new ERA_Model(), "ERA", isClient, "mainhand");
		
	//	this.addWeapon(new AA_Targeter(), new AATH_Model(), "AATargeter", isClient, "dual");
		
	//	this.addWeapon(new Endernymous(), new EnderNymous_Model(), "EnderNymous", isClient, "dual");
	}
	
	
	void registerProjectiles()	// Entities that get shot out of weapons as projectiles
	{
		this.addProjectile(RegularArrow.class, true, "Arrow");
		this.addProjectile(BlazeShot.class, true, "Blaze");
		this.addProjectile(CoinShot.class, true, "Coin");
		this.addProjectile(SmallRocket.class, true, "RocketSmall");
		
		this.addProjectile(LapisShot.class, true, "Lapis");
		this.addProjectile(Thorn.class, true, "Thorn");
		this.addProjectile(ProxyThorn.class, true, "ProxyThorn");
		this.addProjectile(SugarRod.class, true, "Sugar");
		
		this.addProjectile(BigRocket.class, true, "RocketBig");
		
		this.addProjectile(Sabot_Arrow.class, true, "SabotArrow");
		this.addProjectile(Sabot_Rocket.class, true, "SabotRocket");
		
		this.addProjectile(Seed.class, true, "Seed");
		this.addProjectile(PotatoShot.class, true, "Potato");
		this.addProjectile(SnowShot.class, true, "Snow");
		
		this.addProjectile(ScopedPredictive.class, true, "Predictive");
		this.addProjectile(EnderShot.class, true, "Ender");
		this.addProjectile(ColdIron.class, true, "ColdIron");
		
		this.addProjectile(OSP_Shot.class, true, "OSP");
		this.addProjectile(OSR_Shot.class, true, "OSR");
		this.addProjectile(OWR_Shot.class, true, "OWR");
		
		this.addProjectile(FenGoop.class, true, "FenLight");
		this.addProjectile(FlintDust.class, true, "FlintDust");
		
		this.addProjectile(RedLight.class, true, "RedLight");
		this.addProjectile(SunLight.class, true, "SunLight");
		
		this.addProjectile(NetherFire.class, true, "NetherFire");
		this.addProjectile(RedSpray.class, true, "RedSpray");
		
		this.addProjectile(SoulShot.class, true, "Soul");
		
		this.addProjectile(WaterShot.class, true, "Water");
		this.addProjectile(WebShot.class, true, "Web");
		
		this.addProjectile(HealthBeam.class, true, "Health");
		
		this.addProjectile(EnderAccelerator.class, true, "ERA");
		this.addProjectile(EnderAno.class, true, "Ano");
	}
	
	
	private void registerBlocks()		// Blocks we can place
	{
		fenLight = new FenLight(Material.GLASS);
		//GameRegistry.registerBlock(fenLight, "quiverchevsky_FenLight");
	}
	
	
	private void addAmmo(_AmmoBase ammoBase, String name)
	{
		if (Main.ammo == null) { Main.ammo = new ArrayList<_AmmoBase>(); }
		
		Main.ammo.add(ammoBase);
		
		//GameRegistry.registerItem(ammoBase, "ammochevsky_" + name); // And register it
	}
	
	// Helper function for taking care of weapon registration
	private void addWeapon(_WeaponBase weapon, ModelBase model, String weaponName, boolean isClient, String handedness)
	{
		if (Main.weapons == null) { Main.weapons = new ArrayList<_WeaponBase>(); }
		
		Main.weapons.add(weapon);
		
		//GameRegistry.registerItem(weapon, "weaponchevsky_" + weaponName);	// And register it
		
		weapon.setUniqueName(weaponName);
		
		if (isClient && useModels && model != null)	// Do we care about models? And if we do, do we got a custom weapon model? :O
		{
			if (Main.models == null) { Main.models = new ArrayList<ModelBase>(); }	// Init
			
			Main.models.add(model);								// Keeping track of it
			proxy.registerWeaponRenderer(weapon, (byte) Main.models.indexOf(model));	// And registering its renderer
		}
	}
	
	
	private void addProjectile(Class<? extends Entity> entityClass, boolean hasRenderer, String name)
	{
		EntityRegistry.registerModEntity(null, entityClass, "projectilechevsky_" + name, projectileCount, this, 80, 1, true);
		
		if (hasRenderer) { proxy.registerProjectileRenderer(entityClass); } // Entity-specific renderer
		
		projectileCount += 1;
	}
	
	
	// Adding props and recipes for all registered weapons now
	private static void addAllProps(FMLPreInitializationEvent event, Configuration config)
	{
		// Ammo first
		for(_AmmoBase ammunition : ammo)
		{
		    ammunition.addRecipes();
		}
		
		// Weapons last
		for(_WeaponBase weapon : weapons)
		{
		    weapon.addProps(event, config);
		    weapon.addRecipes();
		}
	}
	
	
	private static void registerWithBattleGear2(Item item, String wield)
	{
		//Where hand is a case-insensitive String ("both" -or- "dual" for one-handed items,  "right" -or- "mainhand" -or- "left" -or- "offhand" for two-handed on designated side)
		//Where itemStack is an ItemStack instance specific enough of the item
		FMLInterModComms.sendMessage("battlegear2", wield, new ItemStack(item));
	}
	
	@Mod.EventBusSubscriber
	public static class RegistrationHandler
	{
		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event)
		{
			ItemHandler.register(event.getRegistry());
		}
	}
	@SubscribeEvent
	public static void registerItems(ModelRegistryEvent event)
	{
		ItemHandler.registerModels();
	}
}