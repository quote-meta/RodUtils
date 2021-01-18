package quote.fsrod.common.structure;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
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
    private List<BlockState> states;
    private int[] stateNums;

    private ResourceLocation path;

    public BasicStrucure(CompoundNBT nbt, ResourceLocation path) {
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

        BlockPos.getAllInBox(pos1, pos2).forEach( src -> {
            if (world.isAirBlock(src)) {
                stateNumsList.add(0);
            } else {
                BlockState blockState = world.getBlockState(src);

                int stateNum = states.indexOf(blockState);
                if (stateNum == -1) {
                    states.add(blockState);
                    stateNum = states.indexOf(blockState);
                }

                stateNumsList.add(stateNum);
            }
        });

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
    public List<BlockState> getStates() {
        return states;
    }

    @Override
    public int[] getSteteNums() {
        return stateNums;
    }

    @Override
    public BlockState getStateAt(int x, int y, int z, Rotation rotation) {
        int index = x + y * sizeX + z * sizeX * sizeY;
        if(index < 0 || index >= stateNums.length) return Blocks.AIR.getDefaultState();
        int stateNum = stateNums[index];
        BlockState state = states.get(stateNum);
        return state != null ? state : Blocks.AIR.getDefaultState();
    }

    @Override
    public BlockState getStateAt(BlockPos pos, Rotation rotation) {
        return getStateAt(pos.getX(), pos.getY(), pos.getZ(), rotation);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        ListNBT nbtStates = new ListNBT();
        ListNBT nbtStateNums = new ListNBT();

        for (BlockState state : states) {
            CompoundNBT nbtState = NBTUtil.writeBlockState(state);
            nbtStates.add(nbtState);
        }

        for (int stateNum : stateNums) {
            nbtStateNums.add(new IntNBT(stateNum));
        }

        nbt.putInt(NBT_DATA_SIZE_X, sizeX);
        nbt.putInt(NBT_DATA_SIZE_Y, sizeY);
        nbt.putInt(NBT_DATA_SIZE_Z, sizeZ);
        nbt.put(NBT_DATA_STATES, nbtStates);
        nbt.put(NBT_DATA_STATE_NUMS, nbtStateNums);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        sizeX = nbt.getInt(NBT_DATA_SIZE_X);
        sizeY = nbt.getInt(NBT_DATA_SIZE_Y);
        sizeZ = nbt.getInt(NBT_DATA_SIZE_Z);
        ListNBT nbtStates = nbt.getList(NBT_DATA_STATES, 10);
        ListNBT nbtStateNums = nbt.getList(NBT_DATA_STATE_NUMS, 3);

        states = new ArrayList<>();

        int stateMax = nbtStates.size();
        for (int num = 0; num < stateMax; num++) {
            INBT tagState = nbtStates.get(num);
            if (tagState instanceof CompoundNBT) {
                BlockState state = NBTUtil.readBlockState((CompoundNBT) tagState);
                states.add(state);
            } else {
                states.add(Blocks.AIR.getDefaultState());
            }
        }

        List<Integer> stateNumsList = new ArrayList<>();

        int stateNumMax = nbtStateNums.size();
        for (int stateIndex = 0; stateIndex < stateNumMax; stateIndex++) {
            stateNumsList.add(nbtStateNums.getInt(stateIndex));
        }

        stateNums = stateNumsList.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public ResourceLocation getFilePath() {
        return path;
    }
}