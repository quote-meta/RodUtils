package quote.fsrod.common.structure;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class BasicStructure implements IStructure {

    public static final String TAG_DATA_SIZE_X = "sizeX";
    public static final String TAG_DATA_SIZE_Y = "sizeY";
    public static final String TAG_DATA_SIZE_Z = "sizeZ";
    public static final String TAG_DATA_STATES = "states";
    public static final String TAG_DATA_STATE_NUMS = "stateNums";

    private int sizeX;
    private int sizeY;
    private int sizeZ;
    private List<BlockState> states;
    private int[] stateNums;

    private ResourceLocation path;

    public BasicStructure(CompoundTag nbt) {
        this(nbt, new ResourceLocation(""));
    }

    public BasicStructure(CompoundTag nbt, ResourceLocation path) {
        deserializeNBT(nbt);
        this.path = path;
    }

    public BasicStructure(Level level, BlockPos pos1, BlockPos pos2) {
        fromSpaceIncludes2Positions(level, pos1, pos2);
        this.path = new ResourceLocation("");
    }

    private void fromSpaceIncludes2Positions(Level level, BlockPos pos1, BlockPos pos2) {
        BlockPos blockDistance2Pos = pos1.subtract(pos2);
        sizeX = Math.abs(blockDistance2Pos.getX()) + 1;
        sizeY = Math.abs(blockDistance2Pos.getY()) + 1;
        sizeZ = Math.abs(blockDistance2Pos.getZ()) + 1;

        states = new ArrayList<>();
        states.add(Blocks.AIR.defaultBlockState());
        List<Integer> stateNumsList = new ArrayList<>();

        BlockPos.betweenClosed(pos1, pos2).forEach(src -> {
            if (level.isEmptyBlock(src)) {
                stateNumsList.add(0);
            } else {
                BlockState blockState = level.getBlockState(src);

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
        if(index < 0 || index >= stateNums.length) return Blocks.AIR.defaultBlockState();
        int stateNum = stateNums[index];
        BlockState state = states.get(stateNum);
        return state != null ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState getStateAt(BlockPos pos, Rotation rotation) {
        return getStateAt(pos.getX(), pos.getY(), pos.getZ(), rotation);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        ListTag nbtStates = new ListTag();
        ListTag nbtStateNums = new ListTag();

        for (BlockState state : states) {
            CompoundTag nbtState = NbtUtils.writeBlockState(state);
            nbtStates.add(nbtState);
        }

        for (int stateNum : stateNums) {
            nbtStateNums.add(IntTag.valueOf(stateNum));
        }

        nbt.putInt(TAG_DATA_SIZE_X, sizeX);
        nbt.putInt(TAG_DATA_SIZE_Y, sizeY);
        nbt.putInt(TAG_DATA_SIZE_Z, sizeZ);
        nbt.put(TAG_DATA_STATES, nbtStates);
        nbt.put(TAG_DATA_STATE_NUMS, nbtStateNums);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        sizeX = nbt.getInt(TAG_DATA_SIZE_X);
        sizeY = nbt.getInt(TAG_DATA_SIZE_Y);
        sizeZ = nbt.getInt(TAG_DATA_SIZE_Z);
        ListTag nbtStates = nbt.getList(TAG_DATA_STATES, 10);
        ListTag nbtStateNums = nbt.getList(TAG_DATA_STATE_NUMS, 3);

        states = new ArrayList<>();

        int stateMax = nbtStates.size();
        for (int num = 0; num < stateMax; num++) {
            Tag tagState = nbtStates.get(num);
            if (tagState instanceof CompoundTag) {
                BlockState state = NbtUtils.readBlockState((CompoundTag) tagState);
                states.add(state);
            } else {
                states.add(Blocks.AIR.defaultBlockState());
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