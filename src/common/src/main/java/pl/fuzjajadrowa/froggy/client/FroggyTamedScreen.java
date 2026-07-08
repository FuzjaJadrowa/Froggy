package pl.fuzjajadrowa.froggy.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import pl.fuzjajadrowa.froggy.menu.FroggyTamedMenu;
import pl.fuzjajadrowa.froggy.network.FroggyPacketSender;

public class FroggyTamedScreen extends AbstractContainerScreen<FroggyTamedMenu> {
//? if >=1.21.1 {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(pl.fuzjajadrowa.froggy.Froggy.MOD_ID, "textures/gui/tamed_froggy.png");
//?} else {
/*    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(pl.fuzjajadrowa.froggy.Froggy.MOD_ID, "textures/gui/tamed_froggy.png");
*/
//?}

    private Button btnFollow;
    private Button btnStay;
    private Button btnPatrol;

    public FroggyTamedScreen(FroggyTamedMenu menu, net.minecraft.world.entity.player.Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 290;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        this.imageWidth = 290;
        this.imageHeight = 166;
        super.init();

        int x = this.leftPos;
        int y = this.topPos;

        this.btnFollow = this.addRenderableWidget(Button.builder(Component.literal("Follow"), button -> {
            FroggyPacketSender.sendTamedStateChange(this.menu.getFroggy().getId(), 0);
        }).bounds(x + 10, y + 86, 50, 20).build());

        this.btnStay = this.addRenderableWidget(Button.builder(Component.literal("Stay"), button -> {
            FroggyPacketSender.sendTamedStateChange(this.menu.getFroggy().getId(), 1);
        }).bounds(x + 10, y + 108, 50, 20).build());

        this.btnPatrol = this.addRenderableWidget(Button.builder(Component.literal("Patrol"), button -> {
            FroggyPacketSender.sendTamedStateChange(this.menu.getFroggy().getId(), 2);
        }).bounds(x + 10, y + 130, 50, 20).build());

        updateButtonStates();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtonStates();
    }

    private void updateButtonStates() {
        if (this.menu.getFroggy() != null) {
            int state = this.menu.getFroggy().getTamedState();
            this.btnFollow.active = (state != 0);
            this.btnStay.active = (state != 1);
            this.btnPatrol.active = (state != 2);
        }
    }

//? if >=1.21.1 {
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
//?} else {
/*    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
*/
//?}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 290, 166);

        if (this.menu.getFroggy() != null) {
            int invSize = this.menu.getFroggy().getInventorySize();
            for (int i = invSize; i < 27; i++) {
                int col = i % 9;
                int row = i / 9;
                int slotX = x + 121 + col * 18;
                int slotY = y + 19 + row * 18;
                guiGraphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0x80FF0000);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, Component.literal("Tamed Froggy"), 10, 8, 4210752, false);

        if (this.menu.getFroggy() != null) {
            int invSize = this.menu.getFroggy().getInventorySize();
            int invLvl = invSize == 3 ? 0 : (invSize == 9 ? 1 : (invSize == 18 ? 2 : 3));
            guiGraphics.drawString(this.font, Component.literal("Inventory Level: " + invLvl + "/3"), 10, 24, 4210752, false);

            int screamDmg = this.menu.getFroggy().getScreamDamage();
            int screamLvl = screamDmg == 5 ? 0 : (screamDmg == 8 ? 1 : (screamDmg == 13 ? 2 : 3));
            guiGraphics.drawString(this.font, Component.literal("Scream Level: " + screamLvl + "/3"), 10, 40, 4210752, false);
        }

        guiGraphics.drawString(this.font, this.playerInventoryTitle, 66, 75, 4210752, false);
    }
}