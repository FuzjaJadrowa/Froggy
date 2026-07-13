package pl.fuzjajadrowa.froggy.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import pl.fuzjajadrowa.froggy.registry.FroggyBlockEntities;

import java.util.UUID;

//? if >=1.21.1 {
import net.minecraft.core.HolderLookup;
//?}

public class PlayerPaintingBlockEntity extends BlockEntity {
    private UUID ownerUUID;
    private String ownerName = "";

    public PlayerPaintingBlockEntity(BlockPos pos, BlockState state) {
        super(FroggyBlockEntities.PLAYER_PAINTING.get(), pos, state);
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.ownerUUID == null && level.getServer() != null) {
            var players = level.getServer().getPlayerList().getPlayers();
            if (!players.isEmpty()) {
                var firstPlayer = players.get(0);
                this.ownerUUID = firstPlayer.getUUID();
                this.ownerName = firstPlayer.getGameProfile().getName();
                this.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

//? if >=1.21.1 {
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID("OwnerUUID")) {
            this.ownerUUID = tag.getUUID("OwnerUUID");
        }
        if (tag.contains("OwnerName")) {
            this.ownerName = tag.getString("OwnerName");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.ownerUUID != null) {
            tag.putUUID("OwnerUUID", this.ownerUUID);
        }
        if (this.ownerName != null && !this.ownerName.isEmpty()) {
            tag.putString("OwnerName", this.ownerName);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }
//?} else {
/*    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("OwnerUUID")) {
            this.ownerUUID = tag.getUUID("OwnerUUID");
        }
        if (tag.contains("OwnerName")) {
            this.ownerName = tag.getString("OwnerName");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.ownerUUID != null) {
            tag.putUUID("OwnerUUID", this.ownerUUID);
        }
        if (this.ownerName != null && !this.ownerName.isEmpty()) {
            tag.putString("OwnerName", this.ownerName);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }
*/
//?}

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}