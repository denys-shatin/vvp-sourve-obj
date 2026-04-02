package tech.vvp.vvp.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.*;


public class ModItems {
    public static final DeferredRegister<Item> REGISTRY = 
            DeferredRegister.create(ForgeRegistries.ITEMS, VVP.MOD_ID);

    public static final RegistryObject<Item> NATO_TAB_ICON = REGISTRY.register("nato_tab_icon",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RU_TAB_ICON = REGISTRY.register("ru_tab_icon",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ARMOR_ICON = REGISTRY.register("armor_icon",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MULTICAM_HELMET = REGISTRY.register("multicamhelmet",
            () -> new multicamhelmet());

    public static final RegistryObject<Item> MULTICAM_CHEST = REGISTRY.register("multicamchest",
            () -> new multicamchest());

    public static final RegistryObject<Item> MI_28_HELMET = REGISTRY.register("mi28helmet",
            () -> new mi28helmet());

    public static final RegistryObject<Item> MI_28_CHEST = REGISTRY.register("mi28chest",
            () -> new mi28chest());


    public static final RegistryObject<Item> GMLRS_M31 = REGISTRY.register("gmlrs_m31", 
            () -> new Item(new Item.Properties().stacksTo(6)));

    public static final RegistryObject<Item> AGS_30_ITEM = REGISTRY.register("ags_30_item",
            () -> new tech.vvp.vvp.item.VehicleSpawnItem(ModEntities.AGS_30::get, new Item.Properties().stacksTo(64)));
    
    public static final RegistryObject<Item> KORNET_ITEM = REGISTRY.register("kornet_item",
            () -> new tech.vvp.vvp.item.VehicleSpawnItem(ModEntities.KORNET::get, new Item.Properties().stacksTo(64)));


    public static final RegistryObject<Item> CREW_HELMET = REGISTRY.register("crewhelmet",
            () -> new crewhelmet());
    public static final RegistryObject<Item> PANAMA = REGISTRY.register("panama",
            () -> new panama());
    public static final RegistryObject<Item> KEPKA = REGISTRY.register("kepka",
            () -> new kepka());
    public static final RegistryObject<Item> BERETA = REGISTRY.register("bereta",
            () -> new bereta());

    public static final RegistryObject<Item> SPRAY = REGISTRY.register("spray",
            () -> new tech.vvp.vvp.item.varies.SprayItem());

    public static final RegistryObject<Item> ITEM_40_MM = REGISTRY.register("item_40_mm",
            () -> new Item(new Item.Properties().stacksTo(2)));

    // RUS ARMOR
    public static final RegistryObject<Item> RUS_ARMOR = REGISTRY.register("rus_armor",
            () -> new rus_armor());
    public static final RegistryObject<Item> RUS_ARMOR_2 = REGISTRY.register("rus_armor_2",
            () -> new rus_armor_2());
    public static final RegistryObject<Item> RUS_ARMOR_3 = REGISTRY.register("rus_armor_3",
            () -> new rus_armor_3());
    public static final RegistryObject<Item> RUS_HELMET = REGISTRY.register("rus_helmet",
            () -> new rus_helmet());
    public static final RegistryObject<Item> RUS_HELMET_2 = REGISTRY.register("rus_helmet_2",
            () -> new rus_helmet_2());
    public static final RegistryObject<Item> RUS_HELMET_3 = REGISTRY.register("rus_helmet_3",
            () -> new rus_helmet_3());

    // UKR_ARMOR

    public static final RegistryObject<Item> UKR_CHEST = REGISTRY.register("ukr_chest",
            () -> new ukr_chest());
    public static final RegistryObject<Item> UKR_HELMET = REGISTRY.register("ukr_helmet",
            () -> new ukr_helmet());
    public static final RegistryObject<Item> UKR_V2_CHEST = REGISTRY.register("ukr_v2_chest",
            () -> new ukr_v2_chest());
    public static final RegistryObject<Item> UKR_V2_HELMET = REGISTRY.register("ukr_v2_helmet",
            () -> new ukr_v2_helmet());

    // PMC ARMOR
    public static final RegistryObject<Item> PMC_HELMET = REGISTRY.register("pmc_helmet",
            () -> new pmc_helmet());
    public static final RegistryObject<Item> PMC_CHEST = REGISTRY.register("pmc_chest",
            () -> new pmc_chest());
    public static final RegistryObject<Item> PMC_V2_CHEST = REGISTRY.register("pmc_v2_chest",
            () -> new pmc_v2_chest());


    // AMMO
    public static final RegistryObject<Item> ITEM_30MM = REGISTRY.register("item_30mm",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> ITEM_AP_SHELL = REGISTRY.register("ap_shell",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> ITEM_HE_SHELL = REGISTRY.register("he_shell",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> ITEM_7_62MM = REGISTRY.register("item_7_62mm",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> ITEM_12_7MM = REGISTRY.register("item_12_7mm",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> AT4 = REGISTRY.register("at4",
            () -> new tech.vvp.vvp.item.gun.At4Item());

    public static final RegistryObject<Item> AGM = REGISTRY.register("agm",
            () -> new Item(new Item.Properties().stacksTo(4)));

    public static final RegistryObject<Item> AAM = REGISTRY.register("aam",
            () -> new Item(new Item.Properties().stacksTo(4)));

    // Mi-8 and D-30
    public static final RegistryObject<Item> MI_8 = REGISTRY.register("mi_8",
            () -> new tech.vvp.vvp.item.VehicleSpawnItem(ModEntities.MI_8::get, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> D30_ITEM = REGISTRY.register("d30_item",
            () -> new tech.vvp.vvp.item.VehicleSpawnItem(ModEntities.D30::get, new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> SHELL_122MM = REGISTRY.register("shell_122mm",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> SHELL_122MM_CASING = REGISTRY.register("shell_122mm_casing",
            () -> new tech.vvp.vvp.item.Shell122mmCasingItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> NH_90 = REGISTRY.register("nh_90",
            () -> new tech.vvp.vvp.item.VehicleSpawnItem(ModEntities.NH_90::get, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> VARTA_OBJ = REGISTRY.register("varta_obj",
            () -> new tech.vvp.vvp.item.VehicleSpawnItem(ModEntities.VARTA_OBJ::get, new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
