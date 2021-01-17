package quote.fsrod.common.structure;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

public interface IStructure extends INBTSerializable<CompoundNBT>{
    
    public int getSizeX();
    public int getSizeY();
    public int getSizeZ();
    public List<BlockState> getStates();
    public int[] getSteteNums();

    public BlockState getStateAt(int x, int y, int z, Rotation rotation);
    public BlockState getStateAt(BlockPos pos, Rotation rotation);

    public default ResourceLocation getFilePath() {return new ResourceLocation("");}
}