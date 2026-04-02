package tech.vvp.vvp.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.renderer.entity.PantsirMissileRenderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.*;

@Mod.EventBusSubscriber(modid = VVP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BTR_4.get(), btr4Renderer::new);
        event.registerEntityRenderer(ModEntities.BRADLEY.get(), BradleyRenderer::new);
        event.registerEntityRenderer(ModEntities.TERMINATOR.get(), TerminatorRenderer::new);
        event.registerEntityRenderer(ModEntities.T90_M.get(), T90MRenderer::new);
        event.registerEntityRenderer(ModEntities.BRM.get(), BrmRenderer::new);
        event.registerEntityRenderer(ModEntities.BMP_3.get(), Bmp3Renderer::new);
        event.registerEntityRenderer(ModEntities.CHRYZANTEMA.get(), ChryzantemaRenderer::new);
        event.registerEntityRenderer(ModEntities.M1A2.get(), M1A2Renderer::new);
        event.registerEntityRenderer(ModEntities.PUMA.get(), PumaRenderer::new);
        event.registerEntityRenderer(ModEntities.M1A2_SEP.get(), M1A2SepRenderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER.get(), StrykerRenderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER_M1296.get(), Stryker_M1296Renderer::new);
        event.registerEntityRenderer(ModEntities.FMTV.get(), FMTVRenderer::new);
        event.registerEntityRenderer(ModEntities.GAZ_TIGR.get(), GazTigrRenderer::new);
        event.registerEntityRenderer(ModEntities.MI_28.get(), Mi28Renderer::new);
        event.registerEntityRenderer(ModEntities.MI_24.get(), Mi24Renderer::new);
        event.registerEntityRenderer(ModEntities.LEOPARD_2A7V.get(), Leopard2A7VRenderer::new);
        event.registerEntityRenderer(ModEntities.LEOPARD_2A4.get(), Leopard2A4Renderer::new);
        event.registerEntityRenderer(ModEntities.AH_64.get(), Ah64Renderer::new);
        event.registerEntityRenderer(ModEntities.CHALLENGER.get(), ChallengerRenderer::new);
        event.registerEntityRenderer(ModEntities.BMP_2.get(), Bmp2Renderer::new);
        event.registerEntityRenderer(ModEntities.T72_B3M.get(), T72B3MRenderer::new);
        event.registerEntityRenderer(ModEntities.BMP_2M.get(), Bmp2MRenderer::new);
        event.registerEntityRenderer(ModEntities.URAL.get(), UralRenderer::new);
        event.registerEntityRenderer(ModEntities.VARTA.get(), VartaRenderer::new);
        event.registerEntityRenderer(ModEntities.VARTA_OBJ.get(), VartaObjRenderer::new);
        event.registerEntityRenderer(ModEntities.PANTSIR_S1.get(), PantsirS1Renderer::new);
        event.registerEntityRenderer(ModEntities.KORNET.get(), KornetRenderer::new);
        event.registerEntityRenderer(ModEntities.AGS_30.get(), Ags30Renderer::new);
        event.registerEntityRenderer(ModEntities.COBRA.get(), CobraRenderer::new);
        event.registerEntityRenderer(ModEntities.CENTAURO.get(), CentauroRenderer::new);
        event.registerEntityRenderer(ModEntities.BMPT_3K.get(), BMPT3KRenderer::new);
        event.registerEntityRenderer(ModEntities.AJAX.get(), AjaxRenderer::new);
        event.registerEntityRenderer(ModEntities.OPLOT.get(), OplotRenderer::new);
        event.registerEntityRenderer(ModEntities.BTR_3.get(), Btr3Renderer::new);
        event.registerEntityRenderer(ModEntities.PAUTINA.get(), PautinaRenderer::new);

        event.registerEntityRenderer(ModEntities.VARTA_PTRK.get(), VartaPTRKRenderer::new);
        event.registerEntityRenderer(ModEntities.BMP_2_BAKHCHA.get(), Bmp2BakhcaRenderer::new);

        event.registerEntityRenderer(ModEntities.PANTSIR_MISSILE.get(), PantsirMissileRenderer::new);

        // Mi-8 and D-30
        event.registerEntityRenderer(ModEntities.MI_8.get(), Mi8Renderer::new);
        event.registerEntityRenderer(ModEntities.D30.get(), D30Renderer::new);

        // NH-90
        event.registerEntityRenderer(ModEntities.NH_90.get(), Nh90Renderer::new);

    }

    /**
     * Регистрация всех рендереров сущностей
     * @deprecated Используйте метод registerEntityRenderers с аннотацией @SubscribeEvent
     */
    @Deprecated
    public static void register() {
        // Эта функция больше не используется
        // Все регистрации перенесены в метод registerEntityRenderers
    }
}

