package quote.fsrod.common.structure;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

public interface IStructure extends INBTSerializable<NBTTagCompound>{
    
    public int getSizeX();
    public int getSizeY();
    public int getSizeZ();
    public List<IBlockState> getStates();
    public int[] getSteteNums();

    public IBlockState getStateAt(int x, int y, int z, Rotation rotation);
    public IBlockState getStateAt(BlockPos pos, Rotation rotation);

    public default ResourceLocation getFilePath() {return new ResourceLocation("");}
}