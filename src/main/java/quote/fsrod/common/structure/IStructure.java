package quote.fsrod.common.structure;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;

public interface IStructure extends INBTSerializable<CompoundTag>{
    public int getSizeX();
    public int getSizeY();
    public int getSizeZ();
    public List<BlockState> getStates();
    public int[] getSteteNums();

    public BlockState getStateAt(int x, int y, int z, Rotation rotation);
    public BlockState getStateAt(BlockPos pos, Rotation rotation);

    public default ResourceLocation getFilePath() {return new ResourceLocation("");}
}
