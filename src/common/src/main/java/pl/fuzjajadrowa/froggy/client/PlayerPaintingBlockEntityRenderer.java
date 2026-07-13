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
    private static final ResourceLocation BACKGROUND_TEXTURE = 
        //? if >=1.21.1 {
        ResourceLocation.fromNamespaceAndPath("froggy", "textures/block/player_painting_background.png");
        //?} else {
        /* new ResourceLocation("froggy", "textures/block/player_painting_background.png"); */
        //?}

    public PlayerPaintingBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PlayerPaintingBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof PlayerPaintingBlock)) {
            return;
        }

        UUID uuid = blockEntity.getOwnerUUID();
        String name = blockEntity.getOwnerName();
        if (uuid == null) {
            // Draw background only if no owner yet
            uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
            name = "";
        }

        // Get player skin texture
        ResourceLocation skinTexture;
        //? if >=1.21.1 {
        PlayerSkin skin = Minecraft.getInstance().getSkinManager().getInsecureSkin(new GameProfile(uuid, name));
        skinTexture = skin != null ? skin.texture() : DefaultPlayerSkin.get(uuid).texture();
        //?} else {
        /* ResourceLocation loc = Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(new GameProfile(uuid, name));
        skinTexture = loc != null ? loc : DefaultPlayerSkin.getDefaultSkin(uuid); */
        //?}

        poseStack.pushPose();
        
        // Center of the block
        poseStack.translate(0.5, 0.5, 0.5);
        
        // Rotate based on facing
        float rot = -state.getValue(PlayerPaintingBlock.FACING).toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));
        
        // Push slightly in front of the wall
        poseStack.translate(0.0, 0.0, -0.495);

        Matrix4f matrix = poseStack.last().pose();

        // 1. Render Background
        VertexConsumer bgConsumer = bufferSource.getBuffer(RenderType.entitySolid(BACKGROUND_TEXTURE));
        drawPart(bgConsumer, matrix, -1.5f, -1.5f, 1.5f, 1.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, packedLight);

        // 2. Render Player Skin (only if we have a valid owner)
        if (blockEntity.getOwnerUUID() != null) {
            VertexConsumer skinConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(skinTexture));
            
            // Draw base layers (zOffset = 0.005f)
            float baseZ = 0.005f;
            // Head (X: -0.3 to 0.3, Y: 0.6 to 1.2)
            drawPart(skinConsumer, matrix, -0.3f, 0.6f, 0.3f, 1.2f, 8/64f, 8/64f, 16/64f, 16/64f, baseZ, packedLight);
            // Torso (X: -0.3 to 0.3, Y: -0.3 to 0.6)
            drawPart(skinConsumer, matrix, -0.3f, -0.3f, 0.3f, 0.6f, 20/64f, 20/64f, 28/64f, 32/64f, baseZ, packedLight);
            // Right Arm (X: -0.6 to -0.3, Y: -0.3 to 0.6)
            drawPart(skinConsumer, matrix, -0.6f, -0.3f, -0.3f, 0.6f, 44/64f, 20/64f, 48/64f, 32/64f, baseZ, packedLight);
            // Left Arm (X: 0.3 to 0.6, Y: -0.3 to 0.6)
            drawPart(skinConsumer, matrix, 0.3f, -0.3f, 0.6f, 0.6f, 36/64f, 52/64f, 40/64f, 64/64f, baseZ, packedLight);
            // Right Leg (X: -0.3 to 0.0, Y: -1.2 to -0.3)
            drawPart(skinConsumer, matrix, -0.3f, -1.2f, 0.0f, -0.3f, 4/64f, 20/64f, 8/64f, 32/64f, baseZ, packedLight);
            // Left Leg (X: 0.0 to 0.3, Y: -1.2 to -0.3)
            drawPart(skinConsumer, matrix, 0.0f, -1.2f, 0.3f, -0.3f, 20/64f, 52/64f, 24/64f, 64/64f, baseZ, packedLight);

            // Draw outer layers (zOffset = 0.01f)
            float outerZ = 0.01f;
            // Hat (Outer Head)
            drawPart(skinConsumer, matrix, -0.3f, 0.6f, 0.3f, 1.2f, 40/64f, 8/64f, 48/64f, 16/64f, outerZ, packedLight);
            // Jacket (Outer Torso)
            drawPart(skinConsumer, matrix, -0.3f, -0.3f, 0.3f, 0.6f, 20/64f, 36/64f, 28/64f, 48/64f, outerZ, packedLight);
            // Sleeve Right (Outer Right Arm)
            drawPart(skinConsumer, matrix, -0.6f, -0.3f, -0.3f, 0.6f, 44/64f, 36/64f, 48/64f, 48/64f, outerZ, packedLight);
            // Sleeve Left (Outer Left Arm)
            drawPart(skinConsumer, matrix, 0.3f, -0.3f, 0.6f, 0.6f, 52/64f, 52/64f, 56/64f, 64/64f, outerZ, packedLight);
            // Pants Right (Outer Right Leg)
            drawPart(skinConsumer, matrix, -0.3f, -1.2f, 0.0f, -0.3f, 4/64f, 36/64f, 8/64f, 48/64f, outerZ, packedLight);
            // Pants Left (Outer Left Leg)
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

//? if >=1.21.1 {
    private static void vertex(VertexConsumer consumer, Matrix4f mat, float x, float y, float z, float u, float v, int light) {
        consumer.addVertex(mat, x, y, z).setColor(255, 255, 255, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0f, 0.0f, 1.0f);
    }
//?} else {
/*    private static void vertex(VertexConsumer consumer, Matrix4f mat, float x, float y, float z, float u, float v, int light) {
        consumer.vertex(mat, x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0f, 0.0f, 1.0f).endVertex();
    }
*/
//?}
}