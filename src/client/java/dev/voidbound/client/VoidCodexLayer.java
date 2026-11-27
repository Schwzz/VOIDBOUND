package dev.voidbound.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.voidbound.Voidbound;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
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
        this.book = new CodexBookModel(context.bakeLayer(ModelLayers.BOOK));
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
        float turn = VoidCodexMotion.pageTurn(time);
        float idlePage = 0.02F + (float) Math.sin(time * 0.065F) * 0.002F;
        float openness = 0.975F + (float) Math.sin(time * 0.032F) * 0.018F;
        BookModel.State bookState = new BookModel.State(openness, turn, idlePage);

        poseStack.pushPose();
        poseStack.translate(
                VoidCodexMotion.SIDE,
                VoidCodexMotion.HEIGHT - VoidCodexMotion.bob(time)
                        + (state.isCrouching ? VoidCodexMotion.CROUCH_DROP : 0.0F),
                VoidCodexMotion.DEPTH);
        poseStack.rotateDegrees(Axis.YP, 165.0F + (float) Math.sin(time * 0.041F) * 4.0F);
        poseStack.rotateDegrees(Axis.ZP, (float) Math.sin(time * 0.052F) * 1.5F);
        poseStack.rotateDegrees(Axis.XP, -2.0F + (float) Math.sin(time * 0.038F) * 1.5F);
        poseStack.scale(0.78F, 0.78F, 0.78F);

        collector.submitModel(book, bookState, poseStack, BOOK_TEXTURE, lightCoords, OverlayTexture.NO_OVERLAY, 0);
        collector.submitModel(book, bookState, poseStack, GLOW_RENDER_TYPE, lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    private static final class CodexBookModel extends BookModel {
        private final ModelPart turningPage;
        private final ModelPart idlePage;

        private CodexBookModel(ModelPart root) {
            super(root);
            this.turningPage = root.getChild("flip_page1");
            this.idlePage = root.getChild("flip_page2");
        }

        @Override
        public void setupAnim(BookModel.State state) {
            super.setupAnim(state);
            // Reset the turning page while hidden instead of snapping it back across the book.
            this.turningPage.visible = state.pageFlip1() >= 0.0F;
            this.idlePage.visible = !this.turningPage.visible;
        }
    }
}