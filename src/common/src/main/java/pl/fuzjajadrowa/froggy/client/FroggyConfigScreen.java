package pl.fuzjajadrowa.froggy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import pl.fuzjajadrowa.froggy.config.FroggyConfig;

import java.util.List;

public class FroggyConfigScreen extends Screen {
    private final Screen parent;
    private ConfigList list;

    public FroggyConfigScreen(Screen parent) {
        super(Component.translatable("gui.froggy.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

//? if >=1.21.1 {
        int listHeight = this.height - 90;
        this.list = new ConfigList(this.minecraft, this.width, listHeight, 50, 25);
//?} else {
/*        this.list = new ConfigList(this.minecraft, this.width, this.height, 50, this.height - 40, 25);
*/
//?}
        this.addRenderableWidget(this.list);

        // 1. Stalker Spawn Toggle
        Button stalkerToggle = Button.builder(getToggleText("gui.froggy.config.stalker", FroggyConfig.spawnStalker), b -> {
            FroggyConfig.spawnStalker = !FroggyConfig.spawnStalker;
            FroggyConfig.save();
            b.setMessage(getToggleText("gui.froggy.config.stalker", FroggyConfig.spawnStalker));
        }).bounds(0, 0, 120, 20).build();
        this.list.addEntry(Component.translatable("gui.froggy.config.stalker"), stalkerToggle);

        // 2. Stalker Weight Slider
        ConfigSlider stalkerWeightSlider = new ConfigSlider(0, 0, 120, 20, 
                Component.translatable("gui.froggy.config.weight_stalker"),
                0.0F, 200.0F, 5.0F, (float) FroggyConfig.weightStalker,
                value -> { FroggyConfig.weightStalker = Math.round(value); FroggyConfig.save(); },
                value -> Integer.toString(Math.round(value)));
        this.list.addEntry(Component.translatable("gui.froggy.config.weight_stalker"), stalkerWeightSlider);

        // 3. Jumpscare Spawn Toggle
        Button jumpscareToggle = Button.builder(getToggleText("gui.froggy.config.jumpscare", FroggyConfig.spawnJumpscare), b -> {
            FroggyConfig.spawnJumpscare = !FroggyConfig.spawnJumpscare;
            FroggyConfig.save();
            b.setMessage(getToggleText("gui.froggy.config.jumpscare", FroggyConfig.spawnJumpscare));
        }).bounds(0, 0, 120, 20).build();
        this.list.addEntry(Component.translatable("gui.froggy.config.jumpscare"), jumpscareToggle);

        // 4. Jumpscare Weight Slider
        ConfigSlider jumpscareWeightSlider = new ConfigSlider(0, 0, 120, 20, 
                Component.translatable("gui.froggy.config.weight_jumpscare"),
                0.0F, 200.0F, 5.0F, (float) FroggyConfig.weightJumpscare,
                value -> { FroggyConfig.weightJumpscare = Math.round(value); FroggyConfig.save(); },
                value -> Integer.toString(Math.round(value)));
        this.list.addEntry(Component.translatable("gui.froggy.config.weight_jumpscare"), jumpscareWeightSlider);

        // 5. Bored Spawn Toggle
        Button boredToggle = Button.builder(getToggleText("gui.froggy.config.bored", FroggyConfig.spawnBored), b -> {
            FroggyConfig.spawnBored = !FroggyConfig.spawnBored;
            FroggyConfig.save();
            b.setMessage(getToggleText("gui.froggy.config.bored", FroggyConfig.spawnBored));
        }).bounds(0, 0, 120, 20).build();
        this.list.addEntry(Component.translatable("gui.froggy.config.bored"), boredToggle);

        // 6. Bored Weight Slider
        ConfigSlider boredWeightSlider = new ConfigSlider(0, 0, 120, 20, 
                Component.translatable("gui.froggy.config.weight_bored"),
                0.0F, 200.0F, 5.0F, (float) FroggyConfig.weightBored,
                value -> { FroggyConfig.weightBored = Math.round(value); FroggyConfig.save(); },
                value -> Integer.toString(Math.round(value)));
        this.list.addEntry(Component.translatable("gui.froggy.config.weight_bored"), boredWeightSlider);

        // 7. Sleeping Spawn Toggle
        Button sleepingToggle = Button.builder(getToggleText("gui.froggy.config.sleeping", FroggyConfig.spawnSleeping), b -> {
            FroggyConfig.spawnSleeping = !FroggyConfig.spawnSleeping;
            FroggyConfig.save();
            b.setMessage(getToggleText("gui.froggy.config.sleeping", FroggyConfig.spawnSleeping));
        }).bounds(0, 0, 120, 20).build();
        this.list.addEntry(Component.translatable("gui.froggy.config.sleeping"), sleepingToggle);

        // 8. Sleeping Chance Slider
        ConfigSlider sleepingChanceSlider = new ConfigSlider(0, 0, 120, 20, 
                Component.translatable("gui.froggy.config.sleep_chance"),
                0.0F, 1.0F, 0.01F, (float) FroggyConfig.sleepingSpawnChance,
                value -> { FroggyConfig.sleepingSpawnChance = Math.round(value * 100.0) / 100.0; FroggyConfig.save(); },
                value -> Math.round(value * 100) + "%");
        this.list.addEntry(Component.translatable("gui.froggy.config.sleep_chance"), sleepingChanceSlider);

        // 9. Min Spawn Cooldown Slider (seconds)
        ConfigSlider minCooldownSlider = new ConfigSlider(0, 0, 120, 20, 
                Component.translatable("gui.froggy.config.min_rate"),
                10.0F, 3600.0F, 10.0F, FroggyConfig.minSpawnRate / 20.0F,
                value -> { FroggyConfig.minSpawnRate = Math.round(value) * 20; FroggyConfig.save(); },
                value -> Math.round(value) + "s");
        this.list.addEntry(Component.translatable("gui.froggy.config.min_rate"), minCooldownSlider);

        // 10. Max Random Added Slider (seconds)
        ConfigSlider maxRandomSlider = new ConfigSlider(0, 0, 120, 20, 
                Component.translatable("gui.froggy.config.max_random"),
                0.0F, 1800.0F, 10.0F, FroggyConfig.maxRandomAdded / 20.0F,
                value -> { FroggyConfig.maxRandomAdded = Math.round(value) * 20; FroggyConfig.save(); },
                value -> Math.round(value) + "s");
        this.list.addEntry(Component.translatable("gui.froggy.config.max_random"), maxRandomSlider);

        // 11. Sleep Check Interval Slider (seconds)
        ConfigSlider sleepCheckSlider = new ConfigSlider(0, 0, 120, 20, 
                Component.translatable("gui.froggy.config.sleep_check"),
                5.0F, 300.0F, 5.0F, FroggyConfig.sleepingCheckInterval / 20.0F,
                value -> { FroggyConfig.sleepingCheckInterval = Math.round(value) * 20; FroggyConfig.save(); },
                value -> Math.round(value) + "s");
        this.list.addEntry(Component.translatable("gui.froggy.config.sleep_check"), sleepCheckSlider);

        // 12. Scream Damage Level 1 EditBox
        EditBox screamLvl1Box = new EditBox(this.font, 0, 0, 120, 20,
                Component.translatable("gui.froggy.config.scream_damage_lvl1"));
        screamLvl1Box.setFilter(s -> s.isEmpty() || s.matches("\\d{1,4}"));
        screamLvl1Box.setValue(String.valueOf(FroggyConfig.screamDamageLvl1));
        screamLvl1Box.setResponder(s -> {
            try { int v = Integer.parseInt(s); if (v > 0) { FroggyConfig.screamDamageLvl1 = v; FroggyConfig.save(); } } catch (NumberFormatException ignored) {}
        });
        this.list.addEntry(Component.translatable("gui.froggy.config.scream_damage_lvl1"), screamLvl1Box);

        // 13. Scream Damage Level 2 EditBox
        EditBox screamLvl2Box = new EditBox(this.font, 0, 0, 120, 20,
                Component.translatable("gui.froggy.config.scream_damage_lvl2"));
        screamLvl2Box.setFilter(s -> s.isEmpty() || s.matches("\\d{1,4}"));
        screamLvl2Box.setValue(String.valueOf(FroggyConfig.screamDamageLvl2));
        screamLvl2Box.setResponder(s -> {
            try { int v = Integer.parseInt(s); if (v > 0) { FroggyConfig.screamDamageLvl2 = v; FroggyConfig.save(); } } catch (NumberFormatException ignored) {}
        });
        this.list.addEntry(Component.translatable("gui.froggy.config.scream_damage_lvl2"), screamLvl2Box);

        // 14. Scream Damage Level 3 EditBox
        EditBox screamLvl3Box = new EditBox(this.font, 0, 0, 120, 20,
                Component.translatable("gui.froggy.config.scream_damage_lvl3"));
        screamLvl3Box.setFilter(s -> s.isEmpty() || s.matches("\\d{1,4}"));
        screamLvl3Box.setValue(String.valueOf(FroggyConfig.screamDamageLvl3));
        screamLvl3Box.setResponder(s -> {
            try { int v = Integer.parseInt(s); if (v > 0) { FroggyConfig.screamDamageLvl3 = v; FroggyConfig.save(); } } catch (NumberFormatException ignored) {}
        });
        this.list.addEntry(Component.translatable("gui.froggy.config.scream_damage_lvl3"), screamLvl3Box);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
                .bounds(this.width / 2 - 75, this.height - 30, 150, 20).build());
    }

    private Component getToggleText(String translationKey, boolean value) {
        return Component.translatable(translationKey).append(": ").append(
            Component.translatable(value ? "gui.yes" : "gui.no")
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//? if >=1.21.1 {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
//?} else {
/*        this.renderBackground(guiGraphics);
*/
//?}
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        FroggyConfig.save();
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    private final class ConfigList extends ContainerObjectSelectionList<ConfigList.Entry> {
//? if >=1.21.1 {
        public ConfigList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
        }
//?} else {
/*        public ConfigList(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
            super(minecraft, width, height, top, bottom, itemHeight);
        }
*/
//?}

        public void clearList() {
            this.clearEntries();
            this.setScrollAmount(0);
        }

        public void addEntry(Component label, AbstractWidget widget) {
            super.addEntry(new Entry(label, widget));
        }

        @Override
        public int getRowWidth() {
            return 340;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width / 2 + 160;
        }

        class Entry extends ContainerObjectSelectionList.Entry<Entry> {
            private final Component label;
            private final AbstractWidget widget;
            private final List<AbstractWidget> children;

            public Entry(Component label, AbstractWidget widget) {
                this.label = label;
                this.widget = widget;
                this.children = com.google.common.collect.ImmutableList.of(widget);
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
                int centerY = top + (height - FroggyConfigScreen.this.font.lineHeight) / 2;
                guiGraphics.drawString(FroggyConfigScreen.this.font, label, FroggyConfigScreen.this.width / 2 - 138, centerY, 0xFFFFFFFF, false);

                widget.setX(FroggyConfigScreen.this.width / 2 + 20);
                widget.setY(top);
                widget.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return this.children;
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return this.children;
            }
        }
    }

    private static final class ConfigSlider extends AbstractSliderButton {
        private final Component label;
        private final float min;
        private final float max;
        private final float step;
        private final java.util.function.Consumer<Float> onChange;
        private final java.util.function.Function<Float, String> valueText;

        private ConfigSlider(
                int x,
                int y,
                int width,
                int height,
                Component label,
                float min,
                float max,
                float step,
                float initial,
                java.util.function.Consumer<Float> onChange,
                java.util.function.Function<Float, String> valueText
        ) {
            super(x, y, width, height, Component.empty(), toNormalized(snap(initial, min, max, step), min, max));
            this.label = label;
            this.min = min;
            this.max = max;
            this.step = step;
            this.onChange = onChange;
            this.valueText = valueText;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            float val = currentValue();
            setMessage(label.copy().append(": ").append(valueText.apply(val)));
        }

        @Override
        protected void applyValue() {
            float snapped = snap(fromNormalized(this.value, min, max), min, max, step);
            this.value = toNormalized(snapped, min, max);
            onChange.accept(snapped);
            updateMessage();
        }

        private float currentValue() {
            return snap(fromNormalized(this.value, min, max), min, max, step);
        }

        private static double toNormalized(float value, float min, float max) {
            return (value - min) / (max - min);
        }

        private static float fromNormalized(double normalized, float min, float max) {
            return (float) (min + (max - min) * normalized);
        }

        private static float snap(float value, float min, float max, float step) {
            float clamped = Math.max(min, Math.min(max, value));
            return Math.round(clamped / step) * step;
        }
    }
}