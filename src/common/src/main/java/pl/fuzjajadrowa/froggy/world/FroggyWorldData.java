package pl.fuzjajadrowa.froggy.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

//? if >=1.21.1 {
import net.minecraft.core.HolderLookup;
//?}

public class FroggyWorldData extends SavedData {
    private boolean generated = false;
    private int houseX;
    private int houseY;
    private int houseZ;

    public FroggyWorldData() {
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
        this.setDirty();
    }

    public int getHouseX() {
        return houseX;
    }

    public int getHouseY() {
        return houseY;
    }

    public int getHouseZ() {
        return houseZ;
    }

    public void setHousePos(int x, int y, int z) {
        this.houseX = x;
        this.houseY = y;
        this.houseZ = z;
        this.setDirty();
    }

//? if >=1.21.1 {
    public static FroggyWorldData load(CompoundTag tag, HolderLookup.Provider registries) {
        FroggyWorldData data = new FroggyWorldData();
        data.generated = tag.getBoolean("Generated");
        data.houseX = tag.getInt("HouseX");
        data.houseY = tag.getInt("HouseY");
        data.houseZ = tag.getInt("HouseZ");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putBoolean("Generated", this.generated);
        tag.putInt("HouseX", this.houseX);
        tag.putInt("HouseY", this.houseY);
        tag.putInt("HouseZ", this.houseZ);
        return tag;
    }

    public static FroggyWorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(
            FroggyWorldData::new,
            FroggyWorldData::load,
            null
        ), "froggy_world_data");
    }
//?} else {
/*    public static FroggyWorldData load(CompoundTag tag) {
        FroggyWorldData data = new FroggyWorldData();
        data.generated = tag.getBoolean("Generated");
        data.houseX = tag.getInt("HouseX");
        data.houseY = tag.getInt("HouseY");
        data.houseZ = tag.getInt("HouseZ");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("Generated", this.generated);
        tag.putInt("HouseX", this.houseX);
        tag.putInt("HouseY", this.houseY);
        tag.putInt("HouseZ", this.houseZ);
        return tag;
    }

    public static FroggyWorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            FroggyWorldData::load,
            FroggyWorldData::new,
            "froggy_world_data"
        );
    }
*/
//?}
}