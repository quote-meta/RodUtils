package quote.fsrod.common.structure;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BasicStrucure implements IStructure {

    public static final String NBT_DATA_SIZE_X = "sizeX";
    public static final String NBT_DATA_SIZE_Y = "sizeY";
    public static final String NBT_DATA_SIZE_Z = "sizeZ";
    public static final String NBT_DATA_STATES = "states";
    public static final String NBT_DATA_STATE_NUMS = "stateNums";

    private int sizeX;
    private int sizeY;
    private int sizeZ;
    private List<IBlockState> states;
    private int[] stateNums;

    private ResourceLocation path;

    public BasicStrucure(NBTTagCompound nbt, ResourceLocation path) {
        deserializeNBT(nbt);
        this.path = path;
    }

    public BasicStrucure(World world, BlockPos pos1, BlockPos pos2) {
        fromSpaceIncludes2Positions(world, pos1, pos2);
        this.path = new ResourceLocation("");
    }

    private void fromSpaceIncludes2Positions(World world, BlockPos pos1, BlockPos pos2) {
        BlockPos blockDistance2Pos = pos1.subtract(pos2);
        sizeX = Math.abs(blockDistance2Pos.getX()) + 1;
        sizeY = Math.abs(blockDistance2Pos.getY()) + 1;
        sizeZ = Math.abs(blockDistance2Pos.getZ()) + 1;

        states = new ArrayList<>();
        states.add(Blocks.AIR.getDefaultState());
        List<Integer> stateNumsList = new ArrayList<>();

        for (BlockPos src : BlockPos.getAllInBox(pos1, pos2)) {
            if (world.isAirBlock(src)) {
                stateNumsList.add(0);
            } else {
                IBlockState blockState = world.getBlockState(src);

                int stateNum = states.indexOf(blockState);
                if (stateNum == -1) {
                    states.add(blockState);
                    stateNum = states.indexOf(blockState);
                }

                stateNumsList.add(stateNum);
            }
        }

        stateNums = stateNumsList.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public int getSizeX() {
        return sizeX;
    }

    @Override
    public int getSizeY() {
        return sizeY;
    }

    @Override
    public int getSizeZ() {
        return sizeZ;
    }

    @Override
    public List<IBlockState> getStates() {
        return states;
    }

    @Override
    public int[] getSteteNums() {
        return stateNums;
    }

    @Override
    public IBlockState getStateAt(int x, int y, int z, Rotation rotation) {
        int index = x + y * sizeX + z * sizeX * sizeY;
        if(index < 0 || index >= stateNums.length) return Blocks.AIR.getDefaultState();
        int stateNum = stateNums[index];
        IBlockState state = states.get(stateNum);
        return state != null ? state : Blocks.AIR.getDefaultState();
    }

    @Override
    public IBlockState getStateAt(BlockPos pos, Rotation rotation) {
        return getStateAt(pos.getX(), pos.getY(), pos.getZ(), rotation);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();

        NBTTagList nbtStates = new NBTTagList();
        NBTTagList nbtStateNums = new NBTTagList();

        for (IBlockState state : states) {
            NBTTagCompound nbtState = new NBTTagCompound();
            NBTUtil.writeBlockState(nbtState, state);
            nbtStates.appendTag(nbtState);
        }

        for (int stateNum : stateNums) {
            nbtStateNums.appendTag(new NBTTagInt(stateNum));
        }

        nbt.setInteger(NBT_DATA_SIZE_X, sizeX);
        nbt.setInteger(NBT_DATA_SIZE_Y, sizeY);
        nbt.setInteger(NBT_DATA_SIZE_Z, sizeZ);
        nbt.setTag(NBT_DATA_STATES, nbtStates);
        nbt.setTag(NBT_DATA_STATE_NUMS, nbtStateNums);

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        sizeX = nbt.getInteger(NBT_DATA_SIZE_X);
        sizeY = nbt.getInteger(NBT_DATA_SIZE_Y);
        sizeZ = nbt.getInteger(NBT_DATA_SIZE_Z);
        NBTTagList nbtStates = nbt.getTagList(NBT_DATA_STATES, 10);
        NBTTagList nbtStateNums = nbt.getTagList(NBT_DATA_STATE_NUMS, 3);

        states = new ArrayList<>();

        int stateMax = nbtStates.tagCount();
        for (int num = 0; num < stateMax; num++) {
            NBTBase tagState = nbtStates.get(num);
            if (tagState instanceof NBTTagCompound) {
                IBlockState state = NBTUtil.readBlockState((NBTTagCompound) tagState);
                states.add(state);
            } else {
                states.add(Blocks.AIR.getDefaultState());
            }
        }

        List<Integer> stateNumsList = new ArrayList<>();

        int stateNumMax = nbtStateNums.tagCount();
        for (int stateIndex = 0; stateIndex < stateNumMax; stateIndex++) {
            stateNumsList.add(nbtStateNums.getIntAt(stateIndex));
        }

        stateNums = stateNumsList.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public ResourceLocation getFilePath() {
        return path;
    }
}