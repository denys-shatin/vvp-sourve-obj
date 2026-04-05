package tech.vvp.vvp.init;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.*;
import tech.vvp.vvp.entity.vehicle.*;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VVP.MOD_ID);

    public static final RegistryObject<EntityType<Btr4Entity>> BTR_4 = register("btr_4",
            EntityType.Builder.of(Btr4Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<Btr3Entity>> BTR_3 = register("btr_3",
            EntityType.Builder.of(Btr3Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<BradleyEntity>> BRADLEY = register("bradley",
            EntityType.Builder.of(BradleyEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<BrmEntity>> BRM = register("brm",
            EntityType.Builder.of(BrmEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<Bmp3Entity>> BMP_3 = register("bmp_3",
            EntityType.Builder.of(Bmp3Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<Bmp2Entity>> BMP_2 = register("bmp_2",
            EntityType.Builder.of(Bmp2Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3f));

    public static final RegistryObject<EntityType<Bmp2BakhchaEntity>> BMP_2_BAKHCHA = register("bmp_2_bakhcha",
            EntityType.Builder.of(Bmp2BakhchaEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3f));

    public static final RegistryObject<EntityType<Bmp2MEntity>> BMP_2M = register("bmp_2m",
            EntityType.Builder.of(Bmp2MEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3f));

    public static final RegistryObject<EntityType<ChryzantemaEntity>> CHRYZANTEMA = register("chryzantema",
            EntityType.Builder.of(ChryzantemaEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<StrykerEntity>> STRYKER = register("stryker",
            EntityType.Builder.of(StrykerEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<Stryker_M1296Entity>> STRYKER_M1296 = register("stryker_m1296",
            EntityType.Builder.of(Stryker_M1296Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<TerminatorEntity>> TERMINATOR = register("terminator",
            EntityType.Builder.of(TerminatorEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<BMPT3KEntity>> BMPT_3K = register("bmpt_3k",
            EntityType.Builder.of(BMPT3KEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<T90MEntity>> T90_M = register("t90_m",
            EntityType.Builder.of(T90MEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3.5f));

    public static final RegistryObject<EntityType<M1A2Entity>> M1A2 = register("m1a2",
            EntityType.Builder.of(M1A2Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(5f, 5f));

    public static final RegistryObject<EntityType<M1A2SepEntity>> M1A2_SEP = register("m1a2_sep",
            EntityType.Builder.of(M1A2SepEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(5f, 5f));

    public static final RegistryObject<EntityType<PumaEntity>> PUMA = register("puma",
            EntityType.Builder.of(PumaEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<FMTVEntity>> FMTV = register("fmtv",
            EntityType.Builder.of(FMTVEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Mi28Entity>> MI_28 = register("mi_28",
            EntityType.Builder.of(Mi28Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Mi24Entity>> MI_24 = register("mi_24",
            EntityType.Builder.of(Mi24Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Leopard2A7VEntity>> LEOPARD_2A7V = register("leopard_2a7v",
            EntityType.Builder.of(Leopard2A7VEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Leopard2A4Entity>> LEOPARD_2A4 = register("leopard_2a4",
            EntityType.Builder.of(Leopard2A4Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Ah64Entity>> AH_64 = register("ah_64",
            EntityType.Builder.of(Ah64Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<GazTigrEntity>> GAZ_TIGR = register("gaz_tigr",
            EntityType.Builder.of(GazTigrEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f, 2f));

    public static final RegistryObject<EntityType<ChallengerEntity>> CHALLENGER = register("challenger",
            EntityType.Builder.of(ChallengerEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<T72B3MEntity>> T72_B3M = register("t72_b3m",
            EntityType.Builder.of(T72B3MEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<UralEntity>> URAL = register("ural",
            EntityType.Builder.of(UralEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 5f));

    public static final RegistryObject<EntityType<VartaEntity>> VARTA = register("varta",
            EntityType.Builder.of(VartaEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f, 3.5f));

    public static final RegistryObject<EntityType<VartaObjEntity>> VARTA_OBJ = register("varta_obj",
            EntityType.Builder.of(VartaObjEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f, 3.5f));

    public static final RegistryObject<EntityType<VartaPTRKEntity>> VARTA_PTRK = register("varta_ptrk",
            EntityType.Builder.of(VartaPTRKEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3f, 3.5f));

    public static final RegistryObject<EntityType<PantsirS1Entity>> PANTSIR_S1 = register("pantsir_s1",
            EntityType.Builder.of(PantsirS1Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Ags30Entity>> AGS_30 = register("ags_30",
            EntityType.Builder.of(Ags30Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(1f, 1.5f));

    public static final RegistryObject<EntityType<KornetEntity>> KORNET = register("kornet",
            EntityType.Builder.of(KornetEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(1f, 2f));

    public static final RegistryObject<EntityType<CobraEntity>> COBRA = register("cobra",
            EntityType.Builder.of(CobraEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<CentauroEntity>> CENTAURO = register("centauro",
            EntityType.Builder.of(CentauroEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<PantsirMissileEntity>> PANTSIR_MISSILE = register("pantsir_missile",
            EntityType.Builder.<PantsirMissileEntity>of(PantsirMissileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).setTrackingRange(256).setUpdateInterval(1).noSave().fireImmune().sized(0.5f, 0.5f));

    public static final RegistryObject<EntityType<AjaxEntity>> AJAX = register("ajax",
            EntityType.Builder.of(AjaxEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(3.9f, 3f));

    public static final RegistryObject<EntityType<Mi8Entity>> MI_8 = register("mi_8",
            EntityType.Builder.of(Mi8Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<D30Entity>> D30 = register("d30",
            EntityType.Builder.of(D30Entity::new, MobCategory.MISC).setTrackingRange(256).setUpdateInterval(1).fireImmune().sized(3f, 2f));

    public static final RegistryObject<EntityType<Nh90Entity>> NH_90 = register("nh_90",
            EntityType.Builder.of(Nh90Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Uh1Entity>> UH_1 = register("uh_1",
            EntityType.Builder.of(Uh1Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<Ka52Entity>> KA_52 = register("ka_52",
            EntityType.Builder.of(Ka52Entity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<OplotEntity>> OPLOT = register("oplot",
            EntityType.Builder.of(OplotEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));

    public static final RegistryObject<EntityType<PautinaEntity>> PAUTINA = register("pautina",
            EntityType.Builder.of(PautinaEntity::new, MobCategory.MISC).setTrackingRange(512).setUpdateInterval(1).fireImmune().sized(4f, 4f));


    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return ENTITY_TYPES.register(name, () -> entityTypeBuilder.build(name));
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
