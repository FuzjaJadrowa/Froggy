package pl.fuzjajadrowa.froggy.block.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import pl.fuzjajadrowa.froggy.registry.FroggyBlockEntities;

//? if >=1.21.1 {
import net.minecraft.core.HolderLookup;
//?}

public class PlayerPaintingBlockEntity extends BlockEntity {
//? if >=1.21.1 {
    private net.minecraft.world.item.component.ResolvableProfile ownerProfile;
//?} else {
/*    private GameProfile ownerProfile; */
//?}

    public PlayerPaintingBlockEntity(BlockPos pos, BlockState state) {
        super(FroggyBlockEntities.PLAYER_PAINTING.get(), pos, state);
    }

//? if >=1.21.1 {
    public GameProfile getOwnerProfile() {
        return ownerProfile != null ? ownerProfile.gameProfile() : null;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.ownerProfile == null && level.getServer() != null) {
            var players = level.getServer().getPlayerList().getPlayers();
            if (!players.isEmpty()) {
                var firstPlayer = players.get(0);
                this.ownerProfile = new net.minecraft.world.item.component.ResolvableProfile(firstPlayer.getGameProfile());
                this.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }
//?} else {
/*    public GameProfile getOwnerProfile() {
        return ownerProfile;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.ownerProfile == null && level.getServer() != null) {
            var players = level.getServer().getPlayerList().getPlayers();
            if (!players.isEmpty()) {
                var firstPlayer = players.get(0);
                this.ownerProfile = firstPlayer.getGameProfile();
                this.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    } */
//?}

//? if >=1.21.1 {
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("OwnerProfile")) {
            this.ownerProfile = net.minecraft.world.item.component.ResolvableProfile.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag.getCompound("OwnerProfile")).result().orElse(null);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.ownerProfile != null) {
            tag.put("OwnerProfile", net.minecraft.world.item.component.ResolvableProfile.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, this.ownerProfile).getOrThrow());
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
        if (tag.contains("OwnerProfile")) {
            this.ownerProfile = net.minecraft.nbt.NbtUtils.readGameProfile(tag.getCompound("OwnerProfile"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.ownerProfile != null) {
            CompoundTag profileTag = new CompoundTag();
            net.minecraft.nbt.NbtUtils.writeGameProfile(profileTag, this.ownerProfile);
            tag.put("OwnerProfile", profileTag);
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