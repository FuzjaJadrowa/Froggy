package pl.fuzjajadrowa.froggy.client;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import pl.fuzjajadrowa.froggy.block.PlayerPaintingBlock;
import pl.fuzjajadrowa.froggy.block.entity.PlayerPaintingBlockEntity;

import java.util.UUID;

//? if >=1.21.1 {
import net.minecraft.client.resources.PlayerSkin;
//?}

public class PlayerPaintingBlockEntityRenderer implements BlockEntityRenderer<PlayerPaintingBlockEntity> {
    private static final ResourceLocation FRONT_BACKGROUND_TEXTURE = 
        //? if >=1.21.1 {
        ResourceLocation.fromNamespaceAndPath("froggy", "textures/block/player_painting_background.png");
        //?} else {
        /* new ResourceLocation("froggy", "textures/block/player_painting_background.png"); */
        //?}

    private static final ResourceLocation BACK_BACKGROUND_TEXTURE = 
        //? if >=1.21.1 {
        ResourceLocation.fromNamespaceAndPath("minecraft", "textures/painting/back.png");
        //?} else {
        /* new ResourceLocation("minecraft", "textures/painting/back.png"); */
        //?}

    public PlayerPaintingBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PlayerPaintingBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof PlayerPaintingBlock)) {
            return;
        }

        GameProfile profile = blockEntity.getOwnerProfile();
        if (profile == null) {
            profile = new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000000"), "");
        }

        ResourceLocation skinTexture;
        //? if >=1.21.1 {
        PlayerSkin skin = Minecraft.getInstance().getSkinManager().getInsecureSkin(profile);
        skinTexture = skin != null ? skin.texture() : DefaultPlayerSkin.get(profile.getId()).texture();
        //?} else {
        /* ResourceLocation loc = Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(profile);
        skinTexture = loc != null ? loc : DefaultPlayerSkin.getDefaultSkin(profile.getId()); */
        //?}

        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);

        float rot = -state.getValue(PlayerPaintingBlock.FACING).toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));

        poseStack.translate(0.0, 0.0, -0.495);

        Matrix4f matrix = poseStack.last().pose();

        VertexConsumer frontBgConsumer = bufferSource.getBuffer(RenderType.entitySolid(FRONT_BACKGROUND_TEXTURE));
        drawPart(frontBgConsumer, matrix, -1.5f, -1.5f, 1.5f, 1.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, packedLight);
        VertexConsumer backBgConsumer = bufferSource.getBuffer(RenderType.entitySolid(BACK_BACKGROUND_TEXTURE));
        drawPartBack(backBgConsumer, matrix, -1.5f, -1.5f, 1.5f, 1.5f, 0.0f, 0.0f, 1.0f, 1.0f, -0.0625f, packedLight);

        if (blockEntity.getOwnerProfile() != null) {
            VertexConsumer skinConsumer = bufferSource.getBuffer(RenderType.entityCutout(skinTexture));

            float baseZ = 0.005f;
            drawPart(skinConsumer, matrix, -0.3f, 0.6f, 0.3f, 1.2f, 8/64f, 8/64f, 16/64f, 16/64f, baseZ, packedLight);
            drawPart(skinConsumer, matrix, -0.3f, -0.3f, 0.3f, 0.6f, 20/64f, 20/64f, 28/64f, 32/64f, baseZ, packedLight);
            drawPart(skinConsumer, matrix, -0.6f, -0.3f, -0.3f, 0.6f, 44/64f, 20/64f, 48/64f, 32/64f, baseZ, packedLight);
            drawPart(skinConsumer, matrix, 0.3f, -0.3f, 0.6f, 0.6f, 36/64f, 52/64f, 40/64f, 64/64f, baseZ, packedLight);
            drawPart(skinConsumer, matrix, -0.3f, -1.2f, 0.0f, -0.3f, 4/64f, 20/64f, 8/64f, 32/64f, baseZ, packedLight);
            drawPart(skinConsumer, matrix, 0.0f, -1.2f, 0.3f, -0.3f, 20/64f, 52/64f, 24/64f, 64/64f, baseZ, packedLight);

            float outerZ = 0.01f;
            drawPart(skinConsumer, matrix, -0.3f, 0.6f, 0.3f, 1.2f, 40/64f, 8/64f, 48/64f, 16/64f, outerZ, packedLight);
            drawPart(skinConsumer, matrix, -0.3f, -0.3f, 0.3f, 0.6f, 20/64f, 36/64f, 28/64f, 48/64f, outerZ, packedLight);
            drawPart(skinConsumer, matrix, -0.6f, -0.3f, -0.3f, 0.6f, 44/64f, 36/64f, 48/64f, 48/64f, outerZ, packedLight);
            drawPart(skinConsumer, matrix, 0.3f, -0.3f, 0.6f, 0.6f, 52/64f, 52/64f, 56/64f, 64/64f, outerZ, packedLight);
            drawPart(skinConsumer, matrix, -0.3f, -1.2f, 0.0f, -0.3f, 4/64f, 36/64f, 8/64f, 48/64f, outerZ, packedLight);
            drawPart(skinConsumer, matrix, 0.0f, -1.2f, 0.3f, -0.3f, 4/64f, 52/64f, 8/64f, 64/64f, outerZ, packedLight);
        }

        poseStack.popPose();
    }

    private void drawPart(VertexConsumer consumer, Matrix4f matrix, float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2, float zOffset, int light) {
        vertex(consumer, matrix, x1, y1, zOffset, u1, v2, light);
        vertex(consumer, matrix, x2, y1, zOffset, u2, v2, light);
        vertex(consumer, matrix, x2, y2, zOffset, u2, v1, light);
        vertex(consumer, matrix, x1, y2, zOffset, u1, v1, light);
    }

    private void drawPartBack(VertexConsumer consumer, Matrix4f matrix, float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2, float zOffset, int light) {
        vertexBack(consumer, matrix, x1, y2, zOffset, u1, v1, light);
        vertexBack(consumer, matrix, x2, y2, zOffset, u2, v1, light);
        vertexBack(consumer, matrix, x2, y1, zOffset, u2, v2, light);
        vertexBack(consumer, matrix, x1, y1, zOffset, u1, v2, light);
    }

//? if >=1.21.1 {
    private static void vertex(VertexConsumer consumer, Matrix4f mat, float x, float y, float z, float u, float v, int light) {
        consumer.addVertex(mat, x, y, z).setColor(255, 255, 255, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
    }

    private static void vertexBack(VertexConsumer consumer, Matrix4f mat, float x, float y, float z, float u, float v, int light) {
        consumer.addVertex(mat, x, y, z).setColor(255, 255, 255, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0f, 0.0f, -1.0f);
    }
//?} else {
/*    private static void vertex(VertexConsumer consumer, Matrix4f mat, float x, float y, float z, float u, float v, int light) {
        consumer.vertex(mat, x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0f, 0.0f, 1.0f).endVertex();
    }

    private static void vertexBack(VertexConsumer consumer, Matrix4f mat, float x, float y, float z, float u, float v, int light) {
        consumer.vertex(mat, x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0f, 0.0f, -1.0f).endVertex();
    }
*/
//?}
}