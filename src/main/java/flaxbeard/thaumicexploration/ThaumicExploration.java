package flaxbeard.thaumicexploration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;

import baubles.api.BaubleType;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import flaxbeard.thaumicexploration.block.BlockBootsIce;
import flaxbeard.thaumicexploration.block.BlockBoundChest;
import flaxbeard.thaumicexploration.block.BlockBoundJar;
import flaxbeard.thaumicexploration.block.BlockCrucibleSouls;
import flaxbeard.thaumicexploration.block.BlockEverburnUrn;
import flaxbeard.thaumicexploration.block.BlockEverfullUrn;
import flaxbeard.thaumicexploration.block.BlockFloatyCandle;
import flaxbeard.thaumicexploration.block.BlockReplicator;
import flaxbeard.thaumicexploration.block.BlockSoulBrazier;
import flaxbeard.thaumicexploration.block.BlockThinkTank;
import flaxbeard.thaumicexploration.block.BlockTrashJar;
import flaxbeard.thaumicexploration.chunkLoader.ChunkLoaderCallback;
import flaxbeard.thaumicexploration.commands.CommandAlterRate;
import flaxbeard.thaumicexploration.commands.CommandCheckWarp;
import flaxbeard.thaumicexploration.common.CommonProxy;
import flaxbeard.thaumicexploration.common.ConfigTX;
import flaxbeard.thaumicexploration.enchantment.EnchantmentBinding;
import flaxbeard.thaumicexploration.enchantment.EnchantmentDisarm;
import flaxbeard.thaumicexploration.enchantment.EnchantmentNightVision;
import flaxbeard.thaumicexploration.entity.EntityTaintacleMinion;
import flaxbeard.thaumicexploration.event.TXBootsEventHandler;
import flaxbeard.thaumicexploration.event.TXEventHandler;
import flaxbeard.thaumicexploration.event.TXTickHandler;
import flaxbeard.thaumicexploration.gui.TXGuiHandler;
import flaxbeard.thaumicexploration.integration.TTIntegration;
import flaxbeard.thaumicexploration.item.ItemBauble;
import flaxbeard.thaumicexploration.item.ItemBaubleDiscountRing;
import flaxbeard.thaumicexploration.item.ItemBlankSeal;
import flaxbeard.thaumicexploration.item.ItemBrain;
import flaxbeard.thaumicexploration.item.ItemChestSeal;
import flaxbeard.thaumicexploration.item.ItemChestSealLinked;
import flaxbeard.thaumicexploration.item.ItemFoodTalisman;
import flaxbeard.thaumicexploration.item.ItemJarSeal;
import flaxbeard.thaumicexploration.item.ItemStabilizerBelt;
import flaxbeard.thaumicexploration.item.ItemTXArmorSpecial;
import flaxbeard.thaumicexploration.item.ItemTaintSeedFood;
import flaxbeard.thaumicexploration.misc.TXPotion;
import flaxbeard.thaumicexploration.misc.TXTaintPotion;
import flaxbeard.thaumicexploration.misc.brazier.SoulBrazierQueue;
import flaxbeard.thaumicexploration.research.ModRecipes;
import flaxbeard.thaumicexploration.research.ModResearch;
import flaxbeard.thaumicexploration.tile.TileEntityAutoSorter;
import flaxbeard.thaumicexploration.tile.TileEntityBoundChest;
import flaxbeard.thaumicexploration.tile.TileEntityBoundJar;
import flaxbeard.thaumicexploration.tile.TileEntityCrucibleSouls;
import flaxbeard.thaumicexploration.tile.TileEntityEverburnUrn;
import flaxbeard.thaumicexploration.tile.TileEntityEverfullUrn;
import flaxbeard.thaumicexploration.tile.TileEntityFloatyCandle;
import flaxbeard.thaumicexploration.tile.TileEntityReplicator;
import flaxbeard.thaumicexploration.tile.TileEntitySoulBrazier;
import flaxbeard.thaumicexploration.tile.TileEntityThinkTank;
import flaxbeard.thaumicexploration.tile.TileEntityTrashJar;
import flaxbeard.thaumicexploration.wand.StaffRodTransmutative;
import flaxbeard.thaumicexploration.wand.WandRodAmberOnUpdate;
import flaxbeard.thaumicexploration.wand.WandRodBreadOnUpdate;
import flaxbeard.thaumicexploration.wand.WandRodNecromancerOnUpdate;
import flaxbeard.thaumicexploration.wand.WandRodTransmutationOnUpdate;
import flaxbeard.thaumicexploration.wand.WandRodTransmutative;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.wands.StaffRod;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.blocks.BlockCandleItem;
import thaumcraft.common.config.ConfigBlocks;

@Mod(
        modid = ThaumicExploration.MODID,
        name = "Thaumic Exploration",
        version = "GRADLETOKEN_VERSION",
        dependencies = "required-after:Thaumcraft;after:ThaumicTinkerer")
public class ThaumicExploration {

    @Instance("ThaumicExploration")
    public static ThaumicExploration instance;

    public static FMLEventChannel channel;
    public static final String MODID = "ThaumicExploration";

    public static Item pureZombieBrain;
    public static Item blankSeal;
    public static Item chestSeal;
    public static Item chestSealLinked;
    public static Item jarSeal;
    public static Item jarSealLinked;
    public static Item transmutationCore;
    public static Item transmutationStaffCore;
    public static Item amberCore;
    public static Item amberStaffCore;
    public static Item necroStaffCore;
    public static Item breadCore;
    public static Item sojournerCap;
    public static Item sojournerCapUncharged;
    public static Item mechanistCap;
    public static Item mechanistCapUncharged;
    public static Item theCandle;

    // public static EnumArmorMaterial armorMaterialCrystal;
    public static Item maskEvil;
    public static Item focusNecromancy;

    public static Item bootsMeteor;
    public static Item bootsComet;

    public static Item charmNoTaint;

    public static Item charmTaint;

    public static Item talismanFood;

    public static Item tentacleRing;
    public static Item stabilizerBelt;
    public static Item discountRing;

    public static Item enhancedHelmetRunic;
    public static Item enhancedChestRunic;
    public static Item enhancedLegsRunic;
    public static Item enhancedBootsRunic;
    public static Item enhancedHelmetRunic2;
    public static Item enhancedChestRunic2;
    public static Item enhancedLegsRunic2;
    public static Item enhancedBootsRunic2;
    public static int enhancedHelmetRunicID;
    public static int enhancedChestRunicID;
    public static int enhancedLegsRunicID;
    public static int enhancedBootsRunicID;
    public static int enhancedHelmetRunic2ID;
    public static int enhancedChestRunic2ID;
    public static int enhancedLegsRunic2ID;
    public static int enhancedBootsRunic2ID;

    public static Item taintBerry;

    public static Item itemAltar;

    public static Block boundChest;
    public static Block boundJar;
    public static Block thinkTankJar;
    public static Block everfullUrn;
    public static Block everburnUrn;
    public static Block soulBrazier;
    // public static Block autoSorter;
    // public static Block autoCrafter;

    public static Block trashJar;

    public static Block necroPedestal;
    public static Block necroFire;

    public static Block crucibleSouls;
    public static Block taintBerryCrop;
    public static Block meltyIce;
    public static Block replicator;
    public static Block skullCandle;
    public static Block floatCandle;

    public static WandRod WAND_ROD_CRYSTAL;
    public static WandRod STAFF_ROD_CRYSTAL;
    public static WandRod WAND_ROD_AMBER;
    public static WandRod WAND_ROD_NECRO;
    public static WandRod WAND_ROD_BREAD;
    public static StaffRod STAFF_ROD_AMBER;

    public static WandCap WAND_CAP_SOJOURNER;
    public static WandCap WAND_CAP_MECHANIST;

    public static StaffRod STAFF_ROD_NECRO;

    public static int everfullUrnRenderID;
    public static int soulBrazierRenderID;
    public static int crucibleSoulsRenderID;
    public static int replicatorRenderID;
    public static int candleSkullRenderID;
    public static int necroPedestalRenderID;
    public static int floatCandleRenderID;
    public static int trashJarRenderID;
    public static CreativeTabs tab;

    public static Enchantment enchantmentBinding;
    public static Enchantment enchantmentNightVision;
    public static Enchantment enchantmentDisarm;

    public static Potion potionBinding;
    public static Potion potionTaintWithdrawl;

    @SidedProxy(
            clientSide = "flaxbeard.thaumicexploration.client.ClientProxy",
            serverSide = "flaxbeard.thaumicexploration.common.CommonProxy")
    public static CommonProxy proxy;

    private TXTickHandler tickHandler;
    private TXBootsEventHandler entityEventHandler;
    // private TXArmorEventHandler entityEventHandler2;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        // GameRegistry.registerWorldGenerator(this.worldGen = new WorldGenTX());

        SoulBrazierQueue aBrazierQueue = new SoulBrazierQueue();
        MinecraftForge.EVENT_BUS.register(aBrazierQueue);
        FMLCommonHandler.instance().bus().register(aBrazierQueue);

        Potion[] potionTypes = null;

        for (Field f : Potion.class.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
                    Field modfield = Field.class.getDeclaredField("modifiers");
                    modfield.setAccessible(true);
                    modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);

                    potionTypes = (Potion[]) f.get(null);
                    final Potion[] newPotionTypes = new Potion[256];
                    System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
                    f.set(null, newPotionTypes);
                }
            } catch (Exception e) {}
        }

        ConfigTX.loadConfig(event);

        tab = new TXTab(CreativeTabs.getNextID(), "thaumicExploration");

        thinkTankJar = new BlockThinkTank().setBlockName("thaumicexploration:thinkTankJar").setCreativeTab(tab)
                .setBlockTextureName("thaumicExploration:blankTexture");
        everfullUrn = new BlockEverfullUrn().setHardness(2.0F).setBlockName("thaumicexploration:everfullUrn")
                .setCreativeTab(tab).setBlockTextureName("thaumicExploration:everfullUrn");
        everburnUrn = new BlockEverburnUrn().setHardness(2.0F).setBlockName("thaumicexploration:everburnUrn")
                .setCreativeTab(tab).setBlockTextureName("thaumicExploration:everfullUrn");
        soulBrazier = new BlockSoulBrazier().setHardness(2.0F).setBlockName("thaumicexploration:soulBrazier")
                .setCreativeTab(tab).setBlockTextureName("thaumicExploration:soulBrazier");
        crucibleSouls = new BlockCrucibleSouls().setHardness(2.0F).setBlockName("thaumicexploration:crucibleSouls")
                .setCreativeTab(tab).setBlockTextureName("thaumicExploration:crucible3");
        replicator = new BlockReplicator().setHardness(4.0F).setBlockName("thaumicexploration:replicator")
                .setCreativeTab(tab).setBlockTextureName("thaumicexploration:replicatorBottom");
        meltyIce = new BlockBootsIce().setBlockName("thaumicexploration:meltyIce").setHardness(0.5F).setLightOpacity(3)
                .setStepSound(Block.soundTypeGlass).setBlockName("ice").setBlockTextureName("ice");
        // taintBerryCrop = new
        // BlockTaintBerries(taintBerryCropID).setBlockName("thaumicexploration:taintBerryCrop").setBlockTextureName("thaumicExploration:berries");
        boundChest = new BlockBoundChest().setHardness(2.5F).setStepSound(Block.soundTypeWood)
                .setBlockName("boundChest");
        boundJar = new BlockBoundJar().setBlockName("boundJar");

        // autoSorter = new
        // BlockAutoSorter().setHardness(4.0F).setBlockName("thaumicexploration:autoSorter").setCreativeTab(tab).setBlockTextureName("thaumicexploration:replicatorBottom");
        // autoCrafter = new
        // BlockAutoCrafter().setHardness(4.0F).setBlockName("thaumicexploration:autoCrafter").setCreativeTab(tab).setBlockTextureName("thaumicexploration:replicatorBottom");
        floatCandle = new BlockFloatyCandle().setBlockName("thaumicexploration:floatCandle").setCreativeTab(tab);
        trashJar = new BlockTrashJar().setBlockName("thaumicexploration:trashJar");

        // GameRegistry.registerBlock(autoSorter, "autoSorter");
        // GameRegistry.registerBlock(autoCrafter, "autoCrafter");
        GameRegistry.registerBlock(trashJar, "trashJar");
        GameRegistry.registerBlock(boundChest, "boundChest");
        // GameRegistry.registerBlock(taintBerryCrop, "taintBerryCrop");
        GameRegistry.registerBlock(floatCandle, BlockCandleItem.class, "floatCandle");
        GameRegistry.registerBlock(meltyIce, "meltyIce");
        GameRegistry.registerBlock(boundJar, "boundJar");
        GameRegistry.registerBlock(thinkTankJar, "thinkTankJar");
        GameRegistry.registerBlock(everfullUrn, "everfullUrn");
        GameRegistry.registerBlock(everburnUrn, "everburnUrn");
        GameRegistry.registerBlock(soulBrazier, "soulBrazier");
        GameRegistry.registerBlock(crucibleSouls, "crucibleSouls");
        GameRegistry.registerBlock(replicator, "replicator");

        // Items
        transmutationCore = new Item().setUnlocalizedName("thaumicexploration:transmutationCore").setCreativeTab(tab)
                .setTextureName("thaumicexploration:rodTransmutation");
        GameRegistry.registerItem(transmutationCore, "transmutationCore");
        transmutationStaffCore = new Item().setUnlocalizedName("thaumicexploration:transmutationStaffCore")
                .setCreativeTab(tab).setTextureName("thaumicexploration:rodTransmutation_staff");
        GameRegistry.registerItem(transmutationStaffCore, "transmutationStaffCore");
        talismanFood = (new ItemFoodTalisman()).setUnlocalizedName("thaumicexploration:talismanFood")
                .setCreativeTab(tab).setTextureName("thaumicexploration:talismanFood");
        GameRegistry.registerItem(talismanFood, "talismanFood");
        amberCore = new Item().setUnlocalizedName("thaumicexploration:amberCore").setCreativeTab(tab)
                .setTextureName("thaumicexploration:rodAmber");
        GameRegistry.registerItem(amberCore, "amberCore");
        amberStaffCore = new Item().setUnlocalizedName("thaumicexploration:amberStaffCore").setCreativeTab(tab)
                .setTextureName("thaumicexploration:rodAmber_staff");
        GameRegistry.registerItem(amberStaffCore, "amberStaffCore");
        necroStaffCore = new Item().setUnlocalizedName("thaumicexploration:necroStaffCore").setCreativeTab(tab)
                .setTextureName("thaumicexploration:rodNecro_staff");
        GameRegistry.registerItem(necroStaffCore, "necroStaffCore");
        if (ConfigTX.breadWand) {
            breadCore = new Item().setUnlocalizedName("thaumicexploration:breadCore").setCreativeTab(tab)
                    .setTextureName("thaumicexploration:rodBread");
            GameRegistry.registerItem(breadCore, "breadCore");
        }
        sojournerCap = new Item().setUnlocalizedName("thaumicexploration:capSojourner").setCreativeTab(tab)
                .setTextureName("thaumicexploration:capSojournerCharged");
        GameRegistry.registerItem(sojournerCap, "sojournerCap");
        sojournerCapUncharged = new Item().setUnlocalizedName("thaumicexploration:capSojournerInert")
                .setCreativeTab(tab).setTextureName("thaumicexploration:capSojourner");
        GameRegistry.registerItem(sojournerCapUncharged, "sojournerCapUncharged");

        mechanistCap = new Item().setUnlocalizedName("thaumicexploration:capMechanist").setCreativeTab(tab)
                .setTextureName("thaumicexploration:capMechanistCharged");
        GameRegistry.registerItem(mechanistCap, "mechanistCap");
        mechanistCapUncharged = new Item().setUnlocalizedName("thaumicexploration:capMechanistInert")
                .setCreativeTab(tab).setTextureName("thaumicexploration:capMechanist");
        GameRegistry.registerItem(mechanistCapUncharged, "mechanistCapUncharged");

        pureZombieBrain = (new ItemBrain()).setUnlocalizedName("thaumicexploration:pureZombieBrain").setCreativeTab(tab)
                .setTextureName("thaumicexploration:pureZombieBrain");
        GameRegistry.registerItem(pureZombieBrain, "pureZombieBrain");
        blankSeal = (new ItemBlankSeal().setCreativeTab(tab).setTextureName("thaumicexploration:sealBlank"));
        GameRegistry.registerItem(blankSeal, "blankSeal");
        chestSeal = (new ItemChestSeal().setCreativeTab(tab).setTextureName("thaumicexploration:sealChest")
                .setUnlocalizedName("thaumicexploration:chestSeal"));
        GameRegistry.registerItem(chestSeal, "chestSeal");
        chestSealLinked = (new ItemChestSealLinked().setTextureName("thaumicexploration:sealChest")
                .setUnlocalizedName("thaumicexploration:chestSeal"));
        GameRegistry.registerItem(chestSealLinked, "chestSealLinked");
        jarSeal = (new ItemJarSeal().setCreativeTab(tab).setTextureName("thaumicexploration:sealJar")
                .setUnlocalizedName("thaumicexploration:jarSeal"));
        GameRegistry.registerItem(jarSeal, "jarSeal");
        charmNoTaint = new Item().setUnlocalizedName("thaumicexploration:dreamcatcher").setCreativeTab(tab)
                .setTextureName("thaumicexploration:dreamcatcher");
        GameRegistry.registerItem(charmNoTaint, "charmNoTaint");

        // charmTaint = new
        // Item().setUnlocalizedName("thaumicexploration:ringTaint").setCreativeTab(tab).setTextureName("thaumicexploration:taintRing");
        // GameRegistry.registerItem(charmTaint, "charmTaint");

        // maskEvil = new ItemTXArmorSpecialDiscount(maskEvilID, ThaumcraftApi.armorMatSpecial, 2,
        // 0).setUnlocalizedName("thaumicexploration:maskEvil").setCreativeTab(tab).setTextureName("thaumicexploration:maskEvil");
        // GameRegistry.registerItem(maskEvil, "maskEvil");

        bootsMeteor = new ItemTXArmorSpecial(ThaumcraftApi.armorMatSpecial, 4, 3)
                .setUnlocalizedName("thaumicexploration:bootsMeteor").setCreativeTab(tab)
                .setTextureName("thaumicexploration:bootsMeteor");
        GameRegistry.registerItem(bootsMeteor, "bootsMeteor");
        bootsComet = new ItemTXArmorSpecial(ThaumcraftApi.armorMatSpecial, 4, 3)
                .setUnlocalizedName("thaumicexploration:bootsComet").setCreativeTab(tab)
                .setTextureName("thaumicexploration:bootsComet");
        GameRegistry.registerItem(bootsComet, "bootsComet");

        taintBerry = new ItemTaintSeedFood(1, 0.3F, Blocks.tnt, ConfigBlocks.blockTaint).setCreativeTab(tab)
                .setUnlocalizedName("thaumicexploration:taintBerry").setTextureName("thaumicExploration:taintBerry");
        GameRegistry.registerItem(taintBerry, "taintBerry");

        tentacleRing = new ItemBauble(BaubleType.RING).setCreativeTab(tab)
                .setUnlocalizedName("thaumicexploration:tentacleRing")
                .setTextureName("thaumicExploration:taintaclering");
        GameRegistry.registerItem(tentacleRing, "tentacleRing");

        stabilizerBelt = new ItemStabilizerBelt().setCreativeTab(tab)
                .setUnlocalizedName("thaumicexploration:stabilizerBelt")
                .setTextureName("thaumicExploration:stabilizerBelt");
        GameRegistry.registerItem(stabilizerBelt, "stabilizerBelt");

        discountRing = new ItemBaubleDiscountRing().setCreativeTab(tab)
                .setUnlocalizedName("thaumicexploration:discountRing")
                .setTextureName("thaumicExploration:discountRing");
        GameRegistry.registerItem(discountRing, "discountRing");
        ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ChunkLoaderCallback());
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAlterRate());
        event.registerServerCommand(new CommandCheckWarp());
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("tExploration");
        // fakeAspectNecro = new FauxAspect("Necromantic Energy", 0x870404, null, new
        // ResourceLocation("thaumicexploration", "textures/tabs/necroAspect.png"), 771);
        this.tickHandler = new TXTickHandler();
        MinecraftForge.EVENT_BUS.register(this.tickHandler);

        this.entityEventHandler = new TXBootsEventHandler();
        MinecraftForge.EVENT_BUS.register(this.entityEventHandler);

        // this.entityEventHandler2 = new TXArmorEventHandler();
        // inecraftForge.EVENT_BUS.register(this.entityEventHandler2);

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new TXGuiHandler());

        everfullUrnRenderID = RenderingRegistry.getNextAvailableRenderId();
        crucibleSoulsRenderID = RenderingRegistry.getNextAvailableRenderId();
        replicatorRenderID = RenderingRegistry.getNextAvailableRenderId();
        candleSkullRenderID = RenderingRegistry.getNextAvailableRenderId();
        necroPedestalRenderID = RenderingRegistry.getNextAvailableRenderId();
        floatCandleRenderID = RenderingRegistry.getNextAvailableRenderId();
        trashJarRenderID = RenderingRegistry.getNextAvailableRenderId();
        soulBrazierRenderID = RenderingRegistry.getNextAvailableRenderId();
        // Creative Tab

        // EventHandler
        MinecraftForge.EVENT_BUS.register(new TXEventHandler());
        FMLCommonHandler.instance().bus().register(new TXEventHandler());

        // Tiles
        GameRegistry.registerTileEntity(TileEntityFloatyCandle.class, "tileEntityFloatyCandle");
        GameRegistry.registerTileEntity(TileEntityAutoSorter.class, "tileEntityAutoSorter");
        GameRegistry.registerTileEntity(TileEntityBoundChest.class, "tileEntityBoundChest");
        GameRegistry.registerTileEntity(TileEntityBoundJar.class, "tileEntityBoundJar");
        GameRegistry.registerTileEntity(TileEntityThinkTank.class, "tileEntityThinkTank");
        GameRegistry.registerTileEntity(TileEntityEverfullUrn.class, "tileEntityEverfullUrn");
        GameRegistry.registerTileEntity(TileEntityEverburnUrn.class, "tileEntityEverburnUrn");
        GameRegistry.registerTileEntity(TileEntitySoulBrazier.class, "tileEntitySoulBrazier");
        GameRegistry.registerTileEntity(TileEntityCrucibleSouls.class, "tileEntityCrucibleSouls");
        GameRegistry.registerTileEntity(TileEntityReplicator.class, "tileEntityReplicator");
        GameRegistry.registerTileEntity(TileEntityTrashJar.class, "tileEntityTrashJar");
        // GameRegistry.registerTileEntity(TileEntityNecroPedestal.class, "tileEntityNecroPedestal");
        // GameRegistry.registerTileEntity(TileEntityNecroFire.class, "tileEntityNecroFire");
        // Blocks

        // Wands
        STAFF_ROD_AMBER = new StaffRod(
                "AMBER",
                25,
                new ItemStack(ThaumicExploration.amberStaffCore),
                18,
                new WandRodAmberOnUpdate(),
                new ResourceLocation("thaumicexploration:textures/models/rodAmber.png"));
        WAND_ROD_AMBER = new WandRod(
                "AMBER",
                10,
                new ItemStack(ThaumicExploration.amberCore),
                8,
                new WandRodAmberOnUpdate(),
                new ResourceLocation("thaumicexploration:textures/models/rodAmber.png"));
        WAND_ROD_CRYSTAL = new WandRodTransmutative(
                "TRANSMUTATION",
                75,
                new ItemStack(ThaumicExploration.transmutationCore),
                6,
                new WandRodTransmutationOnUpdate(),
                new ResourceLocation("thaumicexploration:textures/models/0.png"));
        STAFF_ROD_CRYSTAL = new StaffRodTransmutative(
                "TRANSMUTATION",
                175,
                new ItemStack(ThaumicExploration.transmutationStaffCore),
                14,
                new WandRodTransmutationOnUpdate(),
                new ResourceLocation("thaumicexploration:textures/models/0.png"));
        STAFF_ROD_NECRO = new StaffRod(
                "NECROMANCER",
                200,
                new ItemStack(ThaumicExploration.necroStaffCore),
                15,
                new WandRodNecromancerOnUpdate(),
                new ResourceLocation("thaumicexploration:textures/models/rodNecro.png"));
        // STAFF_ROD_NECRO.setRunes(true);
        if (ConfigTX.breadWand) {
            WAND_ROD_BREAD = new WandRod(
                    "BREAD",
                    39,
                    new ItemStack(ThaumicExploration.breadCore),
                    8,
                    new WandRodBreadOnUpdate(),
                    new ResourceLocation("thaumicexploration:textures/models/rodBread.png"));
        }
        WAND_CAP_SOJOURNER = new WandCap("SOJOURNER", 0.95F, new ItemStack(ThaumicExploration.sojournerCap), 6);
        WAND_CAP_SOJOURNER.setTexture(new ResourceLocation("thaumicexploration:textures/models/capSojourner.png"));

        WAND_CAP_MECHANIST = new WandCap("MECHANIST", 0.95F, new ItemStack(ThaumicExploration.mechanistCap), 6);
        WAND_CAP_MECHANIST.setTexture(new ResourceLocation("thaumicexploration:textures/models/capMechanist.png"));

        // WandRod.rods.put("transmutation1", WAND_ROD_CRYSTAL1);

        if (ConfigTX.enchantmentBindingEnable) {
            enchantmentBinding = new EnchantmentBinding(ConfigTX.enchantmentBindingID, 1);
        }
        if (ConfigTX.enchantmentNVEnable) {
            enchantmentNightVision = new EnchantmentNightVision(ConfigTX.enchantmentNightVisionID, 1);
        }
        if (ConfigTX.enchantmentDisarmEnable) {
            enchantmentDisarm = new EnchantmentDisarm(ConfigTX.enchantmentDisarmID, 1);
        }

        if (Loader.isModLoaded("ThaumicTinkerer")) {
            TTIntegration.registerEnchants();
        }
        if (Loader.isModLoaded("Waila")) {
            FMLInterModComms.sendMessage(
                    "Waila",
                    "register",
                    "flaxbeard.thaumicexploration.interop.WailaConfig.callbackRegister");
        }
        EntityRegistry.registerModEntity(
                EntityTaintacleMinion.class,
                "TaintacleMinion",
                0,
                ThaumicExploration.instance,
                64,
                3,
                false);
        // enhancedHelmetRunic = new ItemEnhancedRunicArmor(1, enhancedHelmetRunicID, ThaumcraftApi.armorMatSpecial, 0,
        // 0).setUnlocalizedName("thaumicexploration:enhancedHelmetRunic").setCreativeTab(tab);
        // enhancedChestRunic = new ItemEnhancedRunicArmor(1,enhancedChestRunicID, ThaumcraftApi.armorMatSpecial, 0,
        // 1).setUnlocalizedName("thaumicexploration:enhancedChestplateRunic").setCreativeTab(tab);
        // enhancedLegsRunic = new ItemEnhancedRunicArmor(1,enhancedLegsRunicID, ThaumcraftApi.armorMatSpecial, 0,
        // 2).setUnlocalizedName("thaumicexploration:enhancedLeggingsRunic").setCreativeTab(tab);
        // enhancedBootsRunic = new ItemEnhancedRunicArmor(1,enhancedBootsRunicID, ThaumcraftApi.armorMatSpecial, 0,
        // 3).setUnlocalizedName("thaumicexploration:enhancedBootsRunic").setCreativeTab(tab);
        // enhancedHelmetRunic2 = new ItemEnhancedRunicArmor(2, enhancedHelmetRunic2ID, ThaumcraftApi.armorMatSpecial,
        // 0, 0).setUnlocalizedName("thaumicexploration:enhancedHelmetRunic");
        // enhancedChestRunic2 = new ItemEnhancedRunicArmor(2,enhancedChestRunic2ID, ThaumcraftApi.armorMatSpecial, 0,
        // 1).setUnlocalizedName("thaumicexploration:enhancedChestplateRunic");
        // enhancedLegsRunic2 = new ItemEnhancedRunicArmor(2,enhancedLegsRunic2ID, ThaumcraftApi.armorMatSpecial, 0,
        // 2).setUnlocalizedName("thaumicexploration:enhancedLeggingsRunic");
        // enhancedBootsRunic2 = new ItemEnhancedRunicArmor(2,enhancedBootsRunic2ID, ThaumcraftApi.armorMatSpecial, 0,
        // 3).setUnlocalizedName("thaumicexploration:enhancedBootsRunic");

        potionBinding = (new TXPotion(ConfigTX.potionBindingID, false, 0)).setIconIndex(0, 0)
                .setPotionName("potion.binding");
        potionTaintWithdrawl = (new TXTaintPotion(ConfigTX.potionTaintWithdrawlID, true, 0))
                .setPotionName("potion.taintWithdrawl");

        proxy.registerRenderers();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // Researches, Thaumcraft Recipes
        ModRecipes.initRecipes();
        ModResearch.initResearch();
        // NecromanticAltarAPI.initNecromanticRecipes();
        proxy.setUnicode();
    }

    public void addRecipes() {}

    public void registerEntity(Class<? extends Entity> entityClass, String entityName, int bkEggColor, int fgEggColor) {
        int id = EntityRegistry.findGlobalUniqueEntityId();

        EntityRegistry.registerGlobalEntityID(entityClass, entityName, id);
        EntityList.entityEggs.put(Integer.valueOf(id), new EntityEggInfo(id, bkEggColor, fgEggColor));
    }

    public void addSpawn(Class<? extends EntityLiving> entityClass, int spawnProb, int min, int max,
            BiomeGenBase[] biomes) {
        if (spawnProb > 0) {
            EntityRegistry.addSpawn(entityClass, spawnProb, min, max, EnumCreatureType.monster, biomes);
        }
    }

    private void addAchievementName(String ach, String name) {
        LanguageRegistry.instance().addStringLocalization("achievement." + ach, "en_US", name);
    }

    private void addAchievementDesc(String ach, String desc) {
        LanguageRegistry.instance().addStringLocalization("achievement." + ach + ".desc", "en_US", desc);
    }

    private class TXTab extends CreativeTabs {

        public TXTab(int par1, String par2Str) {
            super(par1, par2Str);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {

            return Item.getItemFromBlock(ThaumicExploration.thinkTankJar);
        }
    }
}
