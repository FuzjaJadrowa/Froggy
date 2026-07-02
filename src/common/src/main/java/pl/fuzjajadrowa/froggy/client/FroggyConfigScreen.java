package pl.fuzjajadrowa.froggy.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import pl.fuzjajadrowa.froggy.config.FroggyConfig;

public class FroggyConfigScreen extends Screen {
    private final Screen parent;

    public FroggyConfigScreen(Screen parent) {
        super(Component.translatable("gui.froggy.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        int xOffset1 = this.width / 2 - 155;
        int xOffset2 = this.width / 2 + 5;
        int startY = this.height / 2 - 80;

        // 1
        this.addRenderableWidget(Button.builder(getToggleText("gui.froggy.config.stalker", FroggyConfig.spawnStalker), b -> {
            FroggyConfig.spawnStalker = !FroggyConfig.spawnStalker;
            b.setMessage(getToggleText("gui.froggy.config.stalker", FroggyConfig.spawnStalker));
        }).bounds(xOffset1, startY, 150, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> {
            FroggyConfig.weightStalker = Math.max(0, FroggyConfig.weightStalker - 5);
        }).bounds(xOffset2, startY, 25, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> {
            FroggyConfig.weightStalker = Math.min(1000, FroggyConfig.weightStalker + 5);
        }).bounds(xOffset2 + 125, startY, 25, 20).build());


        // 2
        this.addRenderableWidget(Button.builder(getToggleText("gui.froggy.config.jumpscare", FroggyConfig.spawnJumpscare), b -> {
            FroggyConfig.spawnJumpscare = !FroggyConfig.spawnJumpscare;
            b.setMessage(getToggleText("gui.froggy.config.jumpscare", FroggyConfig.spawnJumpscare));
        }).bounds(xOffset1, startY + 25, 150, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> {
            FroggyConfig.weightJumpscare = Math.max(0, FroggyConfig.weightJumpscare - 5);
        }).bounds(xOffset2, startY + 25, 25, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> {
            FroggyConfig.weightJumpscare = Math.min(1000, FroggyConfig.weightJumpscare + 5);
        }).bounds(xOffset2 + 125, startY + 25, 25, 20).build());


        // 3
        this.addRenderableWidget(Button.builder(getToggleText("gui.froggy.config.bored", FroggyConfig.spawnBored), b -> {
            FroggyConfig.spawnBored = !FroggyConfig.spawnBored;
            b.setMessage(getToggleText("gui.froggy.config.bored", FroggyConfig.spawnBored));
        }).bounds(xOffset1, startY + 50, 150, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> {
            FroggyConfig.weightBored = Math.max(0, FroggyConfig.weightBored - 5);
        }).bounds(xOffset2, startY + 50, 25, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> {
            FroggyConfig.weightBored = Math.min(1000, FroggyConfig.weightBored + 5);
        }).bounds(xOffset2 + 125, startY + 50, 25, 20).build());


        // 4
        this.addRenderableWidget(Button.builder(getToggleText("gui.froggy.config.sleeping", FroggyConfig.spawnSleeping), b -> {
            FroggyConfig.spawnSleeping = !FroggyConfig.spawnSleeping;
            b.setMessage(getToggleText("gui.froggy.config.sleeping", FroggyConfig.spawnSleeping));
        }).bounds(xOffset1, startY + 75, 150, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> {
            FroggyConfig.sleepingSpawnChance = Math.max(0.0, Math.round((FroggyConfig.sleepingSpawnChance - 0.01) * 100.0) / 100.0);
        }).bounds(xOffset2, startY + 75, 25, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> {
            FroggyConfig.sleepingSpawnChance = Math.min(1.0, Math.round((FroggyConfig.sleepingSpawnChance + 0.01) * 100.0) / 100.0);
        }).bounds(xOffset2 + 125, startY + 75, 25, 20).build());


        // 5
        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> {
            FroggyConfig.minSpawnRate = Math.max(20, FroggyConfig.minSpawnRate - 200);
        }).bounds(xOffset1, startY + 100, 25, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> {
            FroggyConfig.minSpawnRate = Math.min(1200000, FroggyConfig.minSpawnRate + 200);
        }).bounds(xOffset1 + 125, startY + 100, 25, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> {
            FroggyConfig.maxRandomAdded = Math.max(0, FroggyConfig.maxRandomAdded - 200);
        }).bounds(xOffset2, startY + 100, 25, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> {
            FroggyConfig.maxRandomAdded = Math.min(1200000, FroggyConfig.maxRandomAdded + 200);
        }).bounds(xOffset2 + 125, startY + 100, 25, 20).build());


        // 6
        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> {
            FroggyConfig.sleepingCheckInterval = Math.max(20, FroggyConfig.sleepingCheckInterval - 100);
        }).bounds(xOffset1, startY + 125, 25, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> {
            FroggyConfig.sleepingCheckInterval = Math.min(120000, FroggyConfig.sleepingCheckInterval + 100);
        }).bounds(xOffset1 + 125, startY + 125, 25, 20).build());


        // 7
        this.addRenderableWidget(Button.builder(Component.translatable("gui.froggy.config.save"), b -> {
            FroggyConfig.save();
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 75, startY + 155, 150, 20).build());
    }

    private Component getToggleText(String translationKey, boolean value) {
        return Component.translatable(translationKey).append(": ").append(
            Component.translatable(value ? "gui.yes" : "gui.no")
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);

        int xOffset1 = this.width / 2 - 155;
        int xOffset2 = this.width / 2 + 5;
        int startY = this.height / 2 - 80;

        guiGraphics.drawCenteredString(this.font, Component.literal("Weight: " + FroggyConfig.weightStalker), xOffset2 + 75, startY + 6, 16777215);
        guiGraphics.drawCenteredString(this.font, Component.literal("Weight: " + FroggyConfig.weightJumpscare), xOffset2 + 75, startY + 31, 16777215);
        guiGraphics.drawCenteredString(this.font, Component.literal("Weight: " + FroggyConfig.weightBored), xOffset2 + 75, startY + 56, 16777215);
        guiGraphics.drawCenteredString(this.font, Component.literal("Chance: " + (int)(FroggyConfig.sleepingSpawnChance * 100) + "%"), xOffset2 + 75, startY + 81, 16777215);

        guiGraphics.drawCenteredString(this.font, Component.literal("Min: " + (FroggyConfig.minSpawnRate / 20) + "s"), xOffset1 + 75, startY + 106, 16777215);
        guiGraphics.drawCenteredString(this.font, Component.literal("Rand: " + (FroggyConfig.maxRandomAdded / 20) + "s"), xOffset2 + 75, startY + 106, 16777215);
        
        guiGraphics.drawCenteredString(this.font, Component.literal("Check: " + (FroggyConfig.sleepingCheckInterval / 20) + "s"), xOffset1 + 75, startY + 131, 16777215);
    }

    @Override
    public void onClose() {
        FroggyConfig.save();
        this.minecraft.setScreen(this.parent);
    }
}