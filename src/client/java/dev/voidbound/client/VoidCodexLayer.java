package dev.voidbound.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.voidbound.Voidbound;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public final class VoidCodexLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final Identifier BOOK_TEXTURE = Identifier.fromNamespaceAndPath(
            Voidbound.MOD_ID, "textures/entity/void_codex.png");
    private static final RenderType GLOW_RENDER_TYPE = RenderTypes.entityTranslucentEmissive(
            Identifier.fromNamespaceAndPath(Voidbound.MOD_ID, "textures/entity/void_codex_glow.png"));

    private final BookModel book;

    public VoidCodexLayer(AvatarRenderer<?> renderer, EntityRendererProvider.Context context) {
        super(renderer);
        this.book = new BookModel(context.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords,
                       AvatarRenderState state, float yRot, float xRot) {
        if (state.isInvisible || state.isSpectator
                || (state.leftHandItemStack.getItem() != Voidbound.VOID_CODEX
                && state.rightHandItemStack.getItem() != Voidbound.VOID_CODEX)) {
            return;
        }

        float time = state.ageInTicks;
        float sweep = VoidCodexMotion.pageSweep(time);
        float flutter = (float) Math.sin(time * 0.13F) * 0.009F;
        float firstPage = 0.022F + flutter + 0.94F * sweep;
        float secondPage = 0.978F - flutter - 0.16F * sweep;
        BookModel.State bookState = BookModel.State.forAnimation(time, firstPage, secondPage, 0.78F);

        poseStack.pushPose();
        poseStack.translate(
                VoidCodexMotion.SIDE,
                VoidCodexMotion.HEIGHT - VoidCodexMotion.bob(time)
                        + (state.isCrouching ? VoidCodexMotion.CROUCH_DROP : 0.0F),
                VoidCodexMotion.DEPTH);
        poseStack.rotateDegrees(Axis.YP, 16.0F + (float) Math.sin(time * 0.041F) * 8.0F);
        poseStack.rotateDegrees(Axis.ZP, 78.0F + (float) Math.sin(time * 0.052F) * 2.5F);
        poseStack.rotateDegrees(Axis.XP, -8.0F + (float) Math.sin(time * 0.038F) * 3.0F);
        poseStack.scale(0.78F, 0.78F, 0.78F);

        collector.submitModel(book, bookState, poseStack, BOOK_TEXTURE, lightCoords, OverlayTexture.NO_OVERLAY, 0);
        collector.submitModel(book, bookState, poseStack, GLOW_RENDER_TYPE, lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}