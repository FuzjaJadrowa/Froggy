package pl.fuzjajadrowa.froggy.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import pl.fuzjajadrowa.froggy.network.FroggyPacketSender;

public class CoughSyrupScreen extends Screen {
    private final int entityId;

    public CoughSyrupScreen(int entityId) {
        super(Component.translatable("gui.froggy.cough_syrup.title"));
        this.entityId = entityId;
    }

    @Override
    protected void init() {
        super.init();
        int y = this.height / 2;

        this.addRenderableWidget(Button.builder(Component.translatable("gui.froggy.cough_syrup.dry"), button -> {
            submitChoice();
        }).bounds(this.width / 2 - 105, y, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.froggy.cough_syrup.wet"), button -> {
            submitChoice();
        }).bounds(this.width / 2 + 5, y, 100, 20).build());
    }

    private void submitChoice() {
        boolean isCorrect = this.minecraft.level.random.nextBoolean();
        FroggyPacketSender.sendCoughSyrupChoice(entityId, isCorrect);
        this.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        guiGraphics.drawCenteredString(this.font, Component.translatable("gui.froggy.cough_syrup.question"), this.width / 2, this.height / 2 - 40, 16777215);
    }
}