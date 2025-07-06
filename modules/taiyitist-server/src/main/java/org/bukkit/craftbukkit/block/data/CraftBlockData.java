package org.bukkit.craftbukkit.block.data;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.BaseCoralFanBlock;
import net.minecraft.world.level.block.BaseCoralPlantBlock;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.BigDripleafStemBlock;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CalibratedSculkSensorBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.CaveVinesPlantBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.ConduitBlock;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.CoralWallFanBlock;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.DriedGhastBlock;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.HangingMossBlock;
import net.minecraft.world.level.block.HangingRootsBlock;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.HeavyCoreBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.InfestedRotatedPillarBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LeafLitterBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LoomBlock;
import net.minecraft.world.level.block.MangroveLeavesBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.MangroveRootsBlock;
import net.minecraft.world.level.block.MossyCarpetBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MyceliumBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.PiglinWallSkullBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.PlayerHeadBlock;
import net.minecraft.world.level.block.PlayerWallHeadBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.PotatoBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SculkCatalystBlock;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SculkVeinBlock;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.SnifferEggBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.TestBlock;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.TorchflowerCropBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.TwistingVinesBlock;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.WeatheringCopperBulbBlock;
import net.minecraft.world.level.block.WeatheringCopperDoorBlock;
import net.minecraft.world.level.block.WeatheringCopperGrateBlock;
import net.minecraft.world.level.block.WeatheringCopperSlabBlock;
import net.minecraft.world.level.block.WeatheringCopperStairBlock;
import net.minecraft.world.level.block.WeatheringCopperTrapDoorBlock;
import net.minecraft.world.level.block.WeepingVinesBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.WitherWallSkullBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockSupport;
import org.bukkit.block.BlockType;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.craftbukkit.block.impl.CraftBrushable;
import org.bukkit.craftbukkit.block.impl.CraftRotatable;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftSoundGroup;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.CraftBlockSupport;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.block.impl.CraftAmethystCluster;
import org.bukkit.craftbukkit.block.impl.CraftAnvil;
import org.bukkit.craftbukkit.block.impl.CraftBamboo;
import org.bukkit.craftbukkit.block.impl.CraftBanner;
import org.bukkit.craftbukkit.block.impl.CraftBannerWall;
import org.bukkit.craftbukkit.block.impl.CraftBarrel;
import org.bukkit.craftbukkit.block.impl.CraftBarrier;
import org.bukkit.craftbukkit.block.impl.CraftBed;
import org.bukkit.craftbukkit.block.impl.CraftBeehive;
import org.bukkit.craftbukkit.block.impl.CraftBeetroot;
import org.bukkit.craftbukkit.block.impl.CraftBell;
import org.bukkit.craftbukkit.block.impl.CraftBigDripleaf;
import org.bukkit.craftbukkit.block.impl.CraftBigDripleafStem;
import org.bukkit.craftbukkit.block.impl.CraftBlastFurnace;
import org.bukkit.craftbukkit.block.impl.CraftBrewingStand;
import org.bukkit.craftbukkit.block.impl.CraftBubbleColumn;
import org.bukkit.craftbukkit.block.impl.CraftButtonAbstract;
import org.bukkit.craftbukkit.block.impl.CraftCactus;
import org.bukkit.craftbukkit.block.impl.CraftCake;
import org.bukkit.craftbukkit.block.impl.CraftCalibratedSculkSensor;
import org.bukkit.craftbukkit.block.impl.CraftCampfire;
import org.bukkit.craftbukkit.block.impl.CraftCandle;
import org.bukkit.craftbukkit.block.impl.CraftCandleCake;
import org.bukkit.craftbukkit.block.impl.CraftCarrots;
import org.bukkit.craftbukkit.block.impl.CraftCaveVines;
import org.bukkit.craftbukkit.block.impl.CraftCaveVinesPlant;
import org.bukkit.craftbukkit.block.impl.CraftCeilingHangingSign;
import org.bukkit.craftbukkit.block.impl.CraftChain;
import org.bukkit.craftbukkit.block.impl.CraftChest;
import org.bukkit.craftbukkit.block.impl.CraftChestTrapped;
import org.bukkit.craftbukkit.block.impl.CraftChiseledBookShelf;
import org.bukkit.craftbukkit.block.impl.CraftChorusFlower;
import org.bukkit.craftbukkit.block.impl.CraftChorusFruit;
import org.bukkit.craftbukkit.block.impl.CraftCobbleWall;
import org.bukkit.craftbukkit.block.impl.CraftCocoa;
import org.bukkit.craftbukkit.block.impl.CraftCommand;
import org.bukkit.craftbukkit.block.impl.CraftComposter;
import org.bukkit.craftbukkit.block.impl.CraftConduit;
import org.bukkit.craftbukkit.block.impl.CraftCopperBulb;
import org.bukkit.craftbukkit.block.impl.CraftCoralDead;
import org.bukkit.craftbukkit.block.impl.CraftCoralFan;
import org.bukkit.craftbukkit.block.impl.CraftCoralFanAbstract;
import org.bukkit.craftbukkit.block.impl.CraftCoralFanWall;
import org.bukkit.craftbukkit.block.impl.CraftCoralFanWallAbstract;
import org.bukkit.craftbukkit.block.impl.CraftCoralPlant;
import org.bukkit.craftbukkit.block.impl.CraftCrafter;
import org.bukkit.craftbukkit.block.impl.CraftCreakingHeart;
import org.bukkit.craftbukkit.block.impl.CraftCrops;
import org.bukkit.craftbukkit.block.impl.CraftDaylightDetector;
import org.bukkit.craftbukkit.block.impl.CraftDecoratedPot;
import org.bukkit.craftbukkit.block.impl.CraftDirtSnow;
import org.bukkit.craftbukkit.block.impl.CraftDispenser;
import org.bukkit.craftbukkit.block.impl.CraftDoor;
import org.bukkit.craftbukkit.block.impl.CraftDriedGhast;
import org.bukkit.craftbukkit.block.impl.CraftDropper;
import org.bukkit.craftbukkit.block.impl.CraftEndRod;
import org.bukkit.craftbukkit.block.impl.CraftEnderChest;
import org.bukkit.craftbukkit.block.impl.CraftEnderPortalFrame;
import org.bukkit.craftbukkit.block.impl.CraftFence;
import org.bukkit.craftbukkit.block.impl.CraftFenceGate;
import org.bukkit.craftbukkit.block.impl.CraftFire;
import org.bukkit.craftbukkit.block.impl.CraftFloorSign;
import org.bukkit.craftbukkit.block.impl.CraftFlowerBed;
import org.bukkit.craftbukkit.block.impl.CraftFluids;
import org.bukkit.craftbukkit.block.impl.CraftFurnaceFurace;
import org.bukkit.craftbukkit.block.impl.CraftGlazedTerracotta;
import org.bukkit.craftbukkit.block.impl.CraftGlowLichen;
import org.bukkit.craftbukkit.block.impl.CraftGrass;
import org.bukkit.craftbukkit.block.impl.CraftGrindstone;
import org.bukkit.craftbukkit.block.impl.CraftHangingMoss;
import org.bukkit.craftbukkit.block.impl.CraftHangingRoots;
import org.bukkit.craftbukkit.block.impl.CraftHay;
import org.bukkit.craftbukkit.block.impl.CraftHeavyCore;
import org.bukkit.craftbukkit.block.impl.CraftHopper;
import org.bukkit.craftbukkit.block.impl.CraftHugeMushroom;
import org.bukkit.craftbukkit.block.impl.CraftIceFrost;
import org.bukkit.craftbukkit.block.impl.CraftInfestedRotatedPillar;
import org.bukkit.craftbukkit.block.impl.CraftIronBars;
import org.bukkit.craftbukkit.block.impl.CraftJigsaw;
import org.bukkit.craftbukkit.block.impl.CraftJukeBox;
import org.bukkit.craftbukkit.block.impl.CraftKelp;
import org.bukkit.craftbukkit.block.impl.CraftLadder;
import org.bukkit.craftbukkit.block.impl.CraftLantern;
import org.bukkit.craftbukkit.block.impl.CraftLayeredCauldron;
import org.bukkit.craftbukkit.block.impl.CraftLeafLitter;
import org.bukkit.craftbukkit.block.impl.CraftLectern;
import org.bukkit.craftbukkit.block.impl.CraftLever;
import org.bukkit.craftbukkit.block.impl.CraftLight;
import org.bukkit.craftbukkit.block.impl.CraftLightningRod;
import org.bukkit.craftbukkit.block.impl.CraftLoom;
import org.bukkit.craftbukkit.block.impl.CraftMangroveLeaves;
import org.bukkit.craftbukkit.block.impl.CraftMangrovePropagule;
import org.bukkit.craftbukkit.block.impl.CraftMangroveRoots;
import org.bukkit.craftbukkit.block.impl.CraftMinecartDetector;
import org.bukkit.craftbukkit.block.impl.CraftMinecartTrack;
import org.bukkit.craftbukkit.block.impl.CraftMossyCarpet;
import org.bukkit.craftbukkit.block.impl.CraftMultiface;
import org.bukkit.craftbukkit.block.impl.CraftMycel;
import org.bukkit.craftbukkit.block.impl.CraftNetherWart;
import org.bukkit.craftbukkit.block.impl.CraftNote;
import org.bukkit.craftbukkit.block.impl.CraftObserver;
import org.bukkit.craftbukkit.block.impl.CraftPiglinWallSkull;
import org.bukkit.craftbukkit.block.impl.CraftPiston;
import org.bukkit.craftbukkit.block.impl.CraftPistonExtension;
import org.bukkit.craftbukkit.block.impl.CraftPistonMoving;
import org.bukkit.craftbukkit.block.impl.CraftPitcherCrop;
import org.bukkit.craftbukkit.block.impl.CraftPointedDripstone;
import org.bukkit.craftbukkit.block.impl.CraftPortal;
import org.bukkit.craftbukkit.block.impl.CraftPotatoes;
import org.bukkit.craftbukkit.block.impl.CraftPoweredRail;
import org.bukkit.craftbukkit.block.impl.CraftPressurePlateBinary;
import org.bukkit.craftbukkit.block.impl.CraftPressurePlateWeighted;
import org.bukkit.craftbukkit.block.impl.CraftPumpkinCarved;
import org.bukkit.craftbukkit.block.impl.CraftRedstoneComparator;
import org.bukkit.craftbukkit.block.impl.CraftRedstoneLamp;
import org.bukkit.craftbukkit.block.impl.CraftRedstoneOre;
import org.bukkit.craftbukkit.block.impl.CraftRedstoneTorch;
import org.bukkit.craftbukkit.block.impl.CraftRedstoneTorchWall;
import org.bukkit.craftbukkit.block.impl.CraftRedstoneWire;
import org.bukkit.craftbukkit.block.impl.CraftReed;
import org.bukkit.craftbukkit.block.impl.CraftRepeater;
import org.bukkit.craftbukkit.block.impl.CraftRespawnAnchor;
import org.bukkit.craftbukkit.block.impl.CraftSapling;
import org.bukkit.craftbukkit.block.impl.CraftScaffolding;
import org.bukkit.craftbukkit.block.impl.CraftSculkCatalyst;
import org.bukkit.craftbukkit.block.impl.CraftSculkSensor;
import org.bukkit.craftbukkit.block.impl.CraftSculkShrieker;
import org.bukkit.craftbukkit.block.impl.CraftSculkVein;
import org.bukkit.craftbukkit.block.impl.CraftSeaPickle;
import org.bukkit.craftbukkit.block.impl.CraftShulkerBox;
import org.bukkit.craftbukkit.block.impl.CraftSkull;
import org.bukkit.craftbukkit.block.impl.CraftSkullPlayer;
import org.bukkit.craftbukkit.block.impl.CraftSkullPlayerWall;
import org.bukkit.craftbukkit.block.impl.CraftSkullWall;
import org.bukkit.craftbukkit.block.impl.CraftSmallDripleaf;
import org.bukkit.craftbukkit.block.impl.CraftSmoker;
import org.bukkit.craftbukkit.block.impl.CraftSnifferEgg;
import org.bukkit.craftbukkit.block.impl.CraftSnow;
import org.bukkit.craftbukkit.block.impl.CraftSoil;
import org.bukkit.craftbukkit.block.impl.CraftStainedGlassPane;
import org.bukkit.craftbukkit.block.impl.CraftStairs;
import org.bukkit.craftbukkit.block.impl.CraftStem;
import org.bukkit.craftbukkit.block.impl.CraftStemAttached;
import org.bukkit.craftbukkit.block.impl.CraftStepAbstract;
import org.bukkit.craftbukkit.block.impl.CraftStonecutter;
import org.bukkit.craftbukkit.block.impl.CraftStructure;
import org.bukkit.craftbukkit.block.impl.CraftSweetBerryBush;
import org.bukkit.craftbukkit.block.impl.CraftTNT;
import org.bukkit.craftbukkit.block.impl.CraftTallPlant;
import org.bukkit.craftbukkit.block.impl.CraftTallPlantFlower;
import org.bukkit.craftbukkit.block.impl.CraftTallSeagrass;
import org.bukkit.craftbukkit.block.impl.CraftTarget;
import org.bukkit.craftbukkit.block.impl.CraftTest;
import org.bukkit.craftbukkit.block.impl.CraftTintedParticleLeaves;
import org.bukkit.craftbukkit.block.impl.CraftTorchWall;
import org.bukkit.craftbukkit.block.impl.CraftTorchflowerCrop;
import org.bukkit.craftbukkit.block.impl.CraftTrapdoor;
import org.bukkit.craftbukkit.block.impl.CraftTrialSpawner;
import org.bukkit.craftbukkit.block.impl.CraftTripwire;
import org.bukkit.craftbukkit.block.impl.CraftTripwireHook;
import org.bukkit.craftbukkit.block.impl.CraftTurtleEgg;
import org.bukkit.craftbukkit.block.impl.CraftTwistingVines;
import org.bukkit.craftbukkit.block.impl.CraftUntintedParticleLeaves;
import org.bukkit.craftbukkit.block.impl.CraftVault;
import org.bukkit.craftbukkit.block.impl.CraftVine;
import org.bukkit.craftbukkit.block.impl.CraftWallHangingSign;
import org.bukkit.craftbukkit.block.impl.CraftWallSign;
import org.bukkit.craftbukkit.block.impl.CraftWaterloggedTransparent;
import org.bukkit.craftbukkit.block.impl.CraftWeatheringCopperBulb;
import org.bukkit.craftbukkit.block.impl.CraftWeatheringCopperDoor;
import org.bukkit.craftbukkit.block.impl.CraftWeatheringCopperGrate;
import org.bukkit.craftbukkit.block.impl.CraftWeatheringCopperSlab;
import org.bukkit.craftbukkit.block.impl.CraftWeatheringCopperStair;
import org.bukkit.craftbukkit.block.impl.CraftWeatheringCopperTrapDoor;
import org.bukkit.craftbukkit.block.impl.CraftWeepingVines;
import org.bukkit.craftbukkit.block.impl.CraftWitherSkull;
import org.bukkit.craftbukkit.block.impl.CraftWitherSkullWall;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CraftBlockData implements BlockData {
   private BlockState state;
   private Map<Property<?>, Comparable<?>> parsedStates;
   private static final Map<Class<? extends Block>, Function<BlockState, CraftBlockData>> MAP = new HashMap();

   protected CraftBlockData() {
      throw new AssertionError("Template Constructor");
   }

   protected CraftBlockData(BlockState state) {
      this.state = state;
   }

   public Material getMaterial() {
      return CraftBlockType.minecraftToBukkit(this.state.getBlock());
   }

   public BlockState getState() {
      return this.state;
   }

   protected <N extends Enum<N> & StringRepresentable, B extends Enum<B>> B get(CraftBlockStateEnum<N, B> nms) {
      return nms.toBukkit((Enum)this.state.getValue(nms.nms()));
   }

   protected <N extends Enum<N> & StringRepresentable, B extends Enum<B>> Set<B> getValues(CraftBlockStateEnum<N, B> nms) {
      return nms.getValues();
   }

   protected <N extends Enum<N> & StringRepresentable, B extends Enum<B>> void set(CraftBlockStateEnum<N, B> nms, B bukkit) {
      this.parsedStates = null;
      this.state = (BlockState)this.state.setValue(nms.nms(), nms.toNMS(bukkit));
   }

   public BlockData merge(BlockData data) {
      CraftBlockData craft = (CraftBlockData)data;
      Preconditions.checkArgument(craft.parsedStates != null, "Data not created via string parsing");
      Preconditions.checkArgument(this.state.getBlock() == craft.state.getBlock(), "States have different types (got %s, expected %s)", data, this);
      CraftBlockData clone = (CraftBlockData)this.clone();
      clone.parsedStates = null;

      Property parsed;
      for(Iterator var4 = craft.parsedStates.keySet().iterator(); var4.hasNext(); clone.state = (BlockState)clone.state.setValue(parsed, craft.state.getValue(parsed))) {
         parsed = (Property)var4.next();
      }

      return clone;
   }

   public boolean matches(BlockData data) {
      if (data == null) {
         return false;
      } else if (!(data instanceof CraftBlockData)) {
         return false;
      } else {
         CraftBlockData craft = (CraftBlockData)data;
         if (this.state.getBlock() != craft.state.getBlock()) {
            return false;
         } else {
            boolean exactMatch = this.equals(data);
            return !exactMatch && craft.parsedStates != null ? this.merge(data).equals(this) : exactMatch;
         }
      }
   }

   protected <T extends Comparable<T>> T get(Property<T> ibs) {
      return this.state.getValue(ibs);
   }

   public <T extends Comparable<T>, V extends T> void set(Property<T> ibs, V v) {
      this.parsedStates = null;
      this.state = (BlockState)this.state.setValue(ibs, v);
   }

   public String getAsString() {
      return this.toString(this.state.getValues());
   }

   public String getAsString(boolean hideUnspecified) {
      return hideUnspecified && this.parsedStates != null ? this.toString(this.parsedStates) : this.getAsString();
   }

   public BlockData clone() {
      try {
         return (BlockData)super.clone();
      } catch (CloneNotSupportedException var2) {
         CloneNotSupportedException ex = var2;
         throw new AssertionError("Clone not supported", ex);
      }
   }

   public String toString() {
      return "CraftBlockData{" + this.getAsString() + "}";
   }

   public String toString(Map<Property<?>, Comparable<?>> states) {
      StringBuilder stateString = new StringBuilder(BuiltInRegistries.BLOCK.getKey(this.state.getBlock()).toString());
      if (!states.isEmpty()) {
         stateString.append('[');
         stateString.append((String)states.entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
         stateString.append(']');
      }

      return stateString.toString();
   }

   public Map<String, String> toStates(boolean hideUnspecified) {
      return hideUnspecified && this.parsedStates != null ? toStates(this.parsedStates) : toStates(this.state.getValues());
   }

   private static Map<String, String> toStates(Map<Property<?>, Comparable<?>> states) {
      Map<String, String> compound = new HashMap();
      Iterator var2 = states.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<Property<?>, Comparable<?>> entry = (Map.Entry)var2.next();
         Property iblockstate = (Property)entry.getKey();
         compound.put(iblockstate.getName(), iblockstate.getName((Comparable)entry.getValue()));
      }

      return compound;
   }

   public boolean equals(Object obj) {
      return obj instanceof CraftBlockData && this.state.equals(((CraftBlockData)obj).state);
   }

   public int hashCode() {
      return this.state.hashCode();
   }

   protected static BooleanProperty getBoolean(String name) {
      throw new AssertionError("Template Method");
   }

   protected static BooleanProperty getBoolean(String name, boolean optional) {
      throw new AssertionError("Template Method");
   }

   protected static <B extends Enum<B>> CraftBlockStateEnum<?, B> getEnum(String name, Class<? extends Enum<B>> bukkit) {
      throw new AssertionError("Template Method");
   }

   protected static IntegerProperty getInteger(String name) {
      throw new AssertionError("Template Method");
   }

   protected static BooleanProperty getBoolean(Class<? extends Block> block, String name) {
      return (BooleanProperty)getState(block, name, false);
   }

   protected static BooleanProperty getBoolean(Class<? extends Block> block, String name, boolean optional) {
      return (BooleanProperty)getState(block, name, optional);
   }

   protected static <B extends Enum<B>> CraftBlockStateEnum<?, B> getEnum(Class<? extends Block> block, String name, Class<B> bukkit) {
      return new CraftBlockStateEnum((EnumProperty)getState(block, name, false), bukkit);
   }

   protected static IntegerProperty getInteger(Class<? extends Block> block, String name) {
      return (IntegerProperty)getState(block, name, false);
   }

   private static Property<?> getState(Class<? extends Block> block, String name, boolean optional) {
      Property<?> state = null;
      Iterator var4 = BuiltInRegistries.BLOCK.iterator();

      while(var4.hasNext()) {
         Block instance = (Block)var4.next();
         if (instance.getClass() == block) {
            if (state == null) {
               state = instance.getStateDefinition().getProperty(name);
            } else {
               Property<?> newState = instance.getStateDefinition().getProperty(name);
               Preconditions.checkState(state == newState, "State mistmatch %s,%s", state, newState);
            }
         }
      }

      Preconditions.checkState(optional || state != null, "Null state for %s,%s", block, name);
      return state;
   }

   protected static int getMin(IntegerProperty state) {
      return state.min;
   }

   protected static int getMax(IntegerProperty state) {
      return state.max;
   }

   private static void register(Class<? extends Block> nms, Function<BlockState, CraftBlockData> bukkit) {
      Preconditions.checkState(MAP.put(nms, bukkit) == null, "Duplicate mapping %s->%s", nms, bukkit);
   }

   public static CraftBlockData newData(BlockType blockType, String data) {
      Block block = blockType == null ? null : (Block)((CraftBlockType)blockType).getHandle();
      Map<Property<?>, Comparable<?>> parsed = null;
      BlockState blockData;
      if (data != null) {
         try {
            if (block != null) {
               String var10000 = String.valueOf(BuiltInRegistries.BLOCK.getKey(block));
               data = var10000 + data;
            }

            StringReader reader = new StringReader(data);
            BlockStateParser.BlockResult arg = BlockStateParser.parseForBlock(CraftRegistry.getMinecraftRegistry(Registries.BLOCK), reader, false);
            Preconditions.checkArgument(!reader.canRead(), "Spurious trailing data: " + data);
            blockData = arg.blockState();
            parsed = arg.properties();
         } catch (CommandSyntaxException var7) {
            CommandSyntaxException ex = var7;
            throw new IllegalArgumentException("Could not parse data: " + data, ex);
         }
      } else {
         blockData = block.defaultBlockState();
      }

      CraftBlockData craft = fromData(blockData);
      craft.parsedStates = parsed;
      return craft;
   }

   public static CraftBlockData fromData(BlockState data) {
      return (CraftBlockData)((Function)MAP.getOrDefault(data.getBlock().getClass(), CraftBlockData::new)).apply(data);
   }

   public SoundGroup getSoundGroup() {
      return CraftSoundGroup.getSoundGroup(this.state.getSoundType());
   }

   public int getLightEmission() {
      return this.state.getLightEmission();
   }

   public boolean isOccluding() {
      return this.state.canOcclude();
   }

   public boolean requiresCorrectToolForDrops() {
      return this.state.requiresCorrectToolForDrops();
   }

   public boolean isPreferredTool(ItemStack tool) {
      Preconditions.checkArgument(tool != null, "tool must not be null");
      net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(tool);
      return isPreferredTool(this.state, nms);
   }

   public static boolean isPreferredTool(BlockState iblockdata, net.minecraft.world.item.ItemStack nmsItem) {
      return !iblockdata.requiresCorrectToolForDrops() || nmsItem.isCorrectToolForDrops(iblockdata);
   }

   public PistonMoveReaction getPistonMoveReaction() {
      return PistonMoveReaction.getById(this.state.getPistonPushReaction().ordinal());
   }

   public boolean isSupported(org.bukkit.block.Block block) {
      Preconditions.checkArgument(block != null, "block must not be null");
      CraftBlock craftBlock = (CraftBlock)block;
      return this.state.canSurvive(craftBlock.getCraftWorld().getHandle(), craftBlock.getPosition());
   }

   public boolean isSupported(Location location) {
      Preconditions.checkArgument(location != null, "location must not be null");
      CraftWorld world = (CraftWorld)location.getWorld();
      Preconditions.checkArgument(world != null, "location must not have a null world");
      BlockPos position = CraftLocation.toBlockPosition(location);
      return this.state.canSurvive(world.getHandle(), position);
   }

   public boolean isFaceSturdy(BlockFace face, BlockSupport support) {
      Preconditions.checkArgument(face != null, "face must not be null");
      Preconditions.checkArgument(support != null, "support must not be null");
      return this.state.isFaceSturdy(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CraftBlock.blockFaceToNotch(face), CraftBlockSupport.toNMS(support));
   }

   public Color getMapColor() {
      return Color.fromRGB(this.state.getMapColor((BlockGetter)null, (BlockPos)null).col);
   }

   public Material getPlacementMaterial() {
      return CraftItemType.minecraftToBukkit(this.state.getBlock().asItem());
   }

   public void rotate(StructureRotation rotation) {
      this.state = this.state.rotate(Rotation.valueOf(rotation.name()));
   }

   public void mirror(Mirror mirror) {
      this.state = this.state.mirror(net.minecraft.world.level.block.Mirror.valueOf(mirror.name()));
   }

   public void copyTo(BlockData blockData) {
      CraftBlockData other = (CraftBlockData)blockData;
      BlockState nms = other.state;
      Iterator var4 = this.state.getBlock().getStateDefinition().getProperties().iterator();

      while(var4.hasNext()) {
         Property<?> property = (Property)var4.next();
         if (nms.hasProperty(property)) {
            nms = this.copyProperty(this.state, nms, property);
         }
      }

      other.state = nms;
   }

   private <T extends Comparable<T>> BlockState copyProperty(BlockState source, BlockState target, Property<T> property) {
      return (BlockState)target.setValue(property, source.getValue(property));
   }

   @NotNull
   public org.bukkit.block.BlockState createBlockState() {
      return CraftBlockStates.getBlockState((BlockState)this.state, (CompoundTag)null);
   }

   static {
      register(AmethystClusterBlock.class, CraftAmethystCluster::new);
      register(BigDripleafBlock.class, CraftBigDripleaf::new);
      register(BigDripleafStemBlock.class, CraftBigDripleafStem::new);
      register(AnvilBlock.class, CraftAnvil::new);
      register(BambooStalkBlock.class, CraftBamboo::new);
      register(BannerBlock.class, CraftBanner::new);
      register(WallBannerBlock.class, CraftBannerWall::new);
      register(BarrelBlock.class, CraftBarrel::new);
      register(BarrierBlock.class, CraftBarrier::new);
      register(BedBlock.class, CraftBed::new);
      register(BeehiveBlock.class, CraftBeehive::new);
      register(BeetrootBlock.class, CraftBeetroot::new);
      register(BellBlock.class, CraftBell::new);
      register(BlastFurnaceBlock.class, CraftBlastFurnace::new);
      register(BrewingStandBlock.class, CraftBrewingStand::new);
      register(BubbleColumnBlock.class, CraftBubbleColumn::new);
      register(ButtonBlock.class, CraftButtonAbstract::new);
      register(CactusBlock.class, CraftCactus::new);
      register(CakeBlock.class, CraftCake::new);
      register(CampfireBlock.class, CraftCampfire::new);
      register(CarrotBlock.class, CraftCarrots::new);
      register(ChainBlock.class, CraftChain::new);
      register(ChestBlock.class, CraftChest::new);
      register(TrappedChestBlock.class, CraftChestTrapped::new);
      register(ChorusFlowerBlock.class, CraftChorusFlower::new);
      register(ChorusPlantBlock.class, CraftChorusFruit::new);
      register(WallBlock.class, CraftCobbleWall::new);
      register(CocoaBlock.class, CraftCocoa::new);
      register(CommandBlock.class, CraftCommand::new);
      register(ComposterBlock.class, CraftComposter::new);
      register(ConduitBlock.class, CraftConduit::new);
      register(BaseCoralPlantBlock.class, CraftCoralDead::new);
      register(CoralFanBlock.class, CraftCoralFan::new);
      register(BaseCoralFanBlock.class, CraftCoralFanAbstract::new);
      register(CoralWallFanBlock.class, CraftCoralFanWall::new);
      register(BaseCoralWallFanBlock.class, CraftCoralFanWallAbstract::new);
      register(CoralPlantBlock.class, CraftCoralPlant::new);
      register(CropBlock.class, CraftCrops::new);
      register(DaylightDetectorBlock.class, CraftDaylightDetector::new);
      register(SnowyDirtBlock.class, CraftDirtSnow::new);
      register(DispenserBlock.class, CraftDispenser::new);
      register(DoorBlock.class, CraftDoor::new);
      register(DropperBlock.class, CraftDropper::new);
      register(EndRodBlock.class, CraftEndRod::new);
      register(EnderChestBlock.class, CraftEnderChest::new);
      register(EndPortalFrameBlock.class, CraftEnderPortalFrame::new);
      register(FenceBlock.class, CraftFence::new);
      register(FenceGateBlock.class, CraftFenceGate::new);
      register(FireBlock.class, CraftFire::new);
      register(StandingSignBlock.class, CraftFloorSign::new);
      register(LiquidBlock.class, CraftFluids::new);
      register(FurnaceBlock.class, CraftFurnaceFurace::new);
      register(GlazedTerracottaBlock.class, CraftGlazedTerracotta::new);
      register(GrassBlock.class, CraftGrass::new);
      register(GrindstoneBlock.class, CraftGrindstone::new);
      register(HayBlock.class, CraftHay::new);
      register(HopperBlock.class, CraftHopper::new);
      register(HugeMushroomBlock.class, CraftHugeMushroom::new);
      register(FrostedIceBlock.class, CraftIceFrost::new);
      register(IronBarsBlock.class, CraftIronBars::new);
      register(JigsawBlock.class, CraftJigsaw::new);
      register(JukeboxBlock.class, CraftJukeBox::new);
      register(KelpBlock.class, CraftKelp::new);
      register(LadderBlock.class, CraftLadder::new);
      register(LanternBlock.class, CraftLantern::new);
      register(LecternBlock.class, CraftLectern::new);
      register(LeverBlock.class, CraftLever::new);
      register(LoomBlock.class, CraftLoom::new);
      register(DetectorRailBlock.class, CraftMinecartDetector::new);
      register(RailBlock.class, CraftMinecartTrack::new);
      register(MyceliumBlock.class, CraftMycel::new);
      register(NetherWartBlock.class, CraftNetherWart::new);
      register(NoteBlock.class, CraftNote::new);
      register(ObserverBlock.class, CraftObserver::new);
      register(NetherPortalBlock.class, CraftPortal::new);
      register(PotatoBlock.class, CraftPotatoes::new);
      register(PoweredRailBlock.class, CraftPoweredRail::new);
      register(PressurePlateBlock.class, CraftPressurePlateBinary::new);
      register(WeightedPressurePlateBlock.class, CraftPressurePlateWeighted::new);
      register(CarvedPumpkinBlock.class, CraftPumpkinCarved::new);
      register(ComparatorBlock.class, CraftRedstoneComparator::new);
      register(RedstoneLampBlock.class, CraftRedstoneLamp::new);
      register(RedStoneOreBlock.class, CraftRedstoneOre::new);
      register(RedstoneTorchBlock.class, CraftRedstoneTorch::new);
      register(RedstoneWallTorchBlock.class, CraftRedstoneTorchWall::new);
      register(RedStoneWireBlock.class, CraftRedstoneWire::new);
      register(SugarCaneBlock.class, CraftReed::new);
      register(RepeaterBlock.class, CraftRepeater::new);
      register(RespawnAnchorBlock.class, CraftRespawnAnchor::new);
      register(RotatedPillarBlock.class, CraftRotatable::new);
      register(SaplingBlock.class, CraftSapling::new);
      register(ScaffoldingBlock.class, CraftScaffolding::new);
      register(SeaPickleBlock.class, CraftSeaPickle::new);
      register(ShulkerBoxBlock.class, CraftShulkerBox::new);
      register(SkullBlock.class, CraftSkull::new);
      register(PlayerHeadBlock.class, CraftSkullPlayer::new);
      register(PlayerWallHeadBlock.class, CraftSkullPlayerWall::new);
      register(WallSkullBlock.class, CraftSkullWall::new);
      register(SmokerBlock.class, CraftSmoker::new);
      register(SnowLayerBlock.class, CraftSnow::new);
      register(FarmBlock.class, CraftSoil::new);
      register(StainedGlassPaneBlock.class, CraftStainedGlassPane::new);
      register(StairBlock.class, CraftStairs::new);
      register(StemBlock.class, CraftStem::new);
      register(AttachedStemBlock.class, CraftStemAttached::new);
      register(SlabBlock.class, CraftStepAbstract::new);
      register(StonecutterBlock.class, CraftStonecutter::new);
      register(StructureBlock.class, CraftStructure::new);
      register(SweetBerryBushBlock.class, CraftSweetBerryBush::new);
      register(TntBlock.class, CraftTNT::new);
      register(DoublePlantBlock.class, CraftTallPlant::new);
      register(TallFlowerBlock.class, CraftTallPlantFlower::new);
      register(TargetBlock.class, CraftTarget::new);
      register(WallTorchBlock.class, CraftTorchWall::new);
      register(TrapDoorBlock.class, CraftTrapdoor::new);
      register(TripWireBlock.class, CraftTripwire::new);
      register(TripWireHookBlock.class, CraftTripwireHook::new);
      register(TurtleEggBlock.class, CraftTurtleEgg::new);
      register(TwistingVinesBlock.class, CraftTwistingVines::new);
      register(VineBlock.class, CraftVine::new);
      register(WallSignBlock.class, CraftWallSign::new);
      register(WeepingVinesBlock.class, CraftWeepingVines::new);
      register(WitherSkullBlock.class, CraftWitherSkull::new);
      register(WitherWallSkullBlock.class, CraftWitherSkullWall::new);
      register(BrushableBlock.class, CraftBrushable::new);
      register(CalibratedSculkSensorBlock.class, CraftCalibratedSculkSensor::new);
      register(CandleBlock.class, CraftCandle::new);
      register(CandleCakeBlock.class, CraftCandleCake::new);
      register(CaveVinesBlock.class, CraftCaveVines::new);
      register(CaveVinesPlantBlock.class, CraftCaveVinesPlant::new);
      register(CeilingHangingSignBlock.class, CraftCeilingHangingSign::new);
      register(ChiseledBookShelfBlock.class, CraftChiseledBookShelf::new);
      register(CopperBulbBlock.class, CraftCopperBulb::new);
      register(CrafterBlock.class, CraftCrafter::new);
      register(CreakingHeartBlock.class, CraftCreakingHeart::new);
      register(DecoratedPotBlock.class, CraftDecoratedPot::new);
      register(DriedGhastBlock.class, CraftDriedGhast::new);
      register(FlowerBedBlock.class, CraftFlowerBed::new);
      register(GlowLichenBlock.class, CraftGlowLichen::new);
      register(HangingMossBlock.class, CraftHangingMoss::new);
      register(HangingRootsBlock.class, CraftHangingRoots::new);
      register(HeavyCoreBlock.class, CraftHeavyCore::new);
      register(InfestedRotatedPillarBlock.class, CraftInfestedRotatedPillar::new);
      register(LayeredCauldronBlock.class, CraftLayeredCauldron::new);
      register(LeafLitterBlock.class, CraftLeafLitter::new);
      register(LightBlock.class, CraftLight::new);
      register(LightningRodBlock.class, CraftLightningRod::new);
      register(MangroveLeavesBlock.class, CraftMangroveLeaves::new);
      register(MangrovePropaguleBlock.class, CraftMangrovePropagule::new);
      register(MangroveRootsBlock.class, CraftMangroveRoots::new);
      register(MossyCarpetBlock.class, CraftMossyCarpet::new);
      register(MultifaceBlock.class, CraftMultiface::new);
      register(PiglinWallSkullBlock.class, CraftPiglinWallSkull::new);
      register(PitcherCropBlock.class, CraftPitcherCrop::new);
      register(PointedDripstoneBlock.class, CraftPointedDripstone::new);
      register(SculkCatalystBlock.class, CraftSculkCatalyst::new);
      register(SculkSensorBlock.class, CraftSculkSensor::new);
      register(SculkShriekerBlock.class, CraftSculkShrieker::new);
      register(SculkVeinBlock.class, CraftSculkVein::new);
      register(SmallDripleafBlock.class, CraftSmallDripleaf::new);
      register(SnifferEggBlock.class, CraftSnifferEgg::new);
      register(TallSeagrassBlock.class, CraftTallSeagrass::new);
      register(TestBlock.class, CraftTest::new);
      register(TintedParticleLeavesBlock.class, CraftTintedParticleLeaves::new);
      register(TorchflowerCropBlock.class, CraftTorchflowerCrop::new);
      register(TrialSpawnerBlock.class, CraftTrialSpawner::new);
      register(UntintedParticleLeavesBlock.class, CraftUntintedParticleLeaves::new);
      register(VaultBlock.class, CraftVault::new);
      register(WallHangingSignBlock.class, CraftWallHangingSign::new);
      register(WaterloggedTransparentBlock.class, CraftWaterloggedTransparent::new);
      register(WeatheringCopperBulbBlock.class, CraftWeatheringCopperBulb::new);
      register(WeatheringCopperDoorBlock.class, CraftWeatheringCopperDoor::new);
      register(WeatheringCopperGrateBlock.class, CraftWeatheringCopperGrate::new);
      register(WeatheringCopperSlabBlock.class, CraftWeatheringCopperSlab::new);
      register(WeatheringCopperStairBlock.class, CraftWeatheringCopperStair::new);
      register(WeatheringCopperTrapDoorBlock.class, CraftWeatheringCopperTrapDoor::new);
      register(PistonBaseBlock.class, CraftPiston::new);
      register(PistonHeadBlock.class, CraftPistonExtension::new);
      register(MovingPistonBlock.class, CraftPistonMoving::new);
   }
}
