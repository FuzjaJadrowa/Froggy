package pl.fuzjajadrowa.froggy.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import pl.fuzjajadrowa.froggy.Froggy;
import pl.fuzjajadrowa.froggy.network.FroggyPacketSender;

public class CoughSyrupScreen extends Screen {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Froggy.MOD_ID, "textures/gui/cough_question.png");
    private final int entityId;
    private boolean isRendering = false;

    public CoughSyrupScreen(int entityId) {
        super(Component.translatable("gui.froggy.cough_syrup.title"));
        this.entityId = entityId;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - 176) / 2;
        int y = (this.height - 100) / 2;

        this.addRenderableWidget(Button.builder(Component.translatable("gui.froggy.cough_syrup.dry"), button -> {
            submitChoice();
        }).bounds(x + 15, y + 55, 70, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.froggy.cough_syrup.wet"), button -> {
            submitChoice();
        }).bounds(x + 91, y + 55, 70, 20).build());
    }

    private void submitChoice() {
        boolean isCorrect = this.minecraft.level.random.nextBoolean();
        FroggyPacketSender.sendCoughSyrupChoice(entityId, isCorrect);
        this.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.isRendering = true;
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.isRendering = false;
        
        int x = (this.width - 176) / 2;
        int y = (this.height - 100) / 2;
        
        guiGraphics.blit(GUI_TEXTURE, x, y, 0.0F, 0.0F, 176, 100, 176, 100);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        Component questionText = Component.translatable("gui.froggy.cough_syrup.question");
        int textWidth = this.font.width(questionText);
        guiGraphics.drawString(this.font, questionText, this.width / 2 - textWidth / 2, y + 25, 4210752, false);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isRendering) {
            super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}