package quote.fsRod.common.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public final class ModUtils {

    private ModUtils(){}

    /**
     * scale value
     * example: scale(4, 10, 100, 200) -> 0.4 * (200 - 100) + 100 = 140
     * @param param
     * @param paramMax
     * @param min
     * @param max
     * @return scaled value
     */
    public static float scale(float param, float paramMax, float min, float max){
        return min + (max - min) * param / paramMax;
    }

    public static float radianAngle(float degree){
        return degree * (float)(2*Math.PI/360);
    }

    public static float degreeAngle(float radian){
        return radian / (float)(2*Math.PI/360);
    }

    public static int searchEntityIDByUUIDFromList(UUID uuid, List list){
        for (Object obj : list) {
            if(obj instanceof Entity){
                Entity entity = (Entity)obj;
                if(uuid.equals(entity.getUniqueID())){
                    int id = entity.getEntityId();
                    return id;
                }
            }
        }
        return -1;
    }

    public static int searchIDfromEntityAndPlayerbyUUID(UUID uuid, World world){
        int id = -1;
        if((id=searchEntityIDByUUIDFromList(uuid,world.playerEntities)) != -1)return id;
        if((id=searchEntityIDByUUIDFromList(uuid,world.loadedEntityList)) != -1)return id;
        return -1;
    }

    public static Vec3d calcPosOnUnitBallFromYawPitch(float yawDegree, float pitchDegree){
        double dx = ((double) (-MathHelper.sin(yawDegree / 180.0f * (float) Math.PI)
            * MathHelper.cos(pitchDegree / 180.0f * (float) Math.PI)));
        double dz = ((double) (MathHelper.cos(yawDegree / 180.0f * (float) Math.PI)
            * MathHelper.cos(pitchDegree / 180.0f * (float) Math.PI)));
        double dy = ((double) (-MathHelper.sin(pitchDegree / 180.0f * (float) Math.PI)));
        return new Vec3d(dx, dy, dz);
    }

    @Nonnull
    public static NBTTagCompound getTagThoughAbsent(ItemStack stack){
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null){
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        return tag;
    }

    public static int encodeRGB(int r, int g, int b){
        int color = 0;
        color += (r << 16);
        color += (g << 8);
        color += (b );
        return color;
    }

    // O(range**2)
    // axis 0:yz, 1:xz, 2:xy
    public static List<Vec3i> calcCoordinatesSquareRange(int x, int y, int z, float range, Axis axis){
        List<Vec3i> listCoordinate = new ArrayList<>();
        double[] array0 = {0};
        double[] array = arange(-MathHelper.floor(range), MathHelper.floor(range), 1.0);

        double[] arrayX = array;
        double[] arrayY = array;
        double[] arrayZ = array;

        if(axis == Axis.X){
            arrayX = array0;
        }
        else if(axis == Axis.Y){
            arrayY = array0;
        }
        else if(axis == Axis.Z){
            arrayZ = array0;
        }

        for (double i : arrayX) {
            for (double j : arrayY) {
                for (double k : arrayZ) {
                    listCoordinate.add(new Vec3i(x + i, y + j, z + k));
                }
            }
        }

        return listCoordinate;
    }

    public static List<Vec3i> calcCoordinatesSquareRange(Vec3i vec, float range, Axis axis){
        return calcCoordinatesSquareRange(vec.getX(), vec.getY(), vec.getZ(), range, axis);
    }

    // O(range**2)
    // axis 0:yz, 1:xz, 2:xy
    public static List<Vec3i> calcCoordinatesCircleRange(int x, int y, int z, float range, Axis axis){
        List<Vec3i> listCoordinate = new ArrayList<>();
        double[] array0 = {0};
        double[] array = arange(-MathHelper.floor(range), MathHelper.floor(range), 1.0);

        double[] arrayX = array;
        double[] arrayY = array;
        double[] arrayZ = array;

        if(axis == Axis.X){
            arrayX = array0;
        }
        else if(axis == Axis.Y){
            arrayY = array0;
        }
        else if(axis == Axis.Z){
            arrayZ = array0;
        }

        for (double i : arrayX) {
            for (double j : arrayY) {
                for (double k : arrayZ) {
                    Vec3d cubedCoordinate = new Vec3d(i, j, k);
                    if (cubedCoordinate.lengthVector() <= range){
                        listCoordinate.add(
                            new Vec3i(cubedCoordinate.x + x, cubedCoordinate.y + y, cubedCoordinate.z + z)
                        );
                    }
                }
            }
        }

        return listCoordinate;
    }

    public static List<Vec3i> calcCoordinatesCircleRange(Vec3i vec, float range, Axis axis){
        return calcCoordinatesCircleRange(vec.getX(), vec.getY(), vec.getZ(), range, axis);
    }

    // O(range**2)
    // axis 0:yz, 1:xz, 2:xy
    public static Map<Vec3i, Double> calcCoordinatesWithDistanceCircleRange(int x, int y, int z, float range, Axis axis){
        Map<Vec3i, Double> mapCoordinateDistance = new HashMap<>();
        double[] array0 = {0};
        double[] array = arange(-MathHelper.floor(range), MathHelper.floor(range), 1.0);

        double[] arrayX = array;
        double[] arrayY = array;
        double[] arrayZ = array;

        if(axis == Axis.X){
            arrayX = array0;
        }
        else if(axis == Axis.Y){
            arrayY = array0;
        }
        else if(axis == Axis.Z){
            arrayZ = array0;
        }

        for (double i : arrayX) {
            for (double j : arrayY) {
                for (double k : arrayZ) {
                    Vec3d cubedCoordinate = new Vec3d(i, j, k);
                    double d = cubedCoordinate.lengthVector();
                    if (d <= range){
                        mapCoordinateDistance.put(
                            new Vec3i(cubedCoordinate.x + x, cubedCoordinate.y + y, cubedCoordinate.z + z), d
                        );
                    }
                }
            }
        }

        return mapCoordinateDistance;
    }

    public static Map<Vec3i, Double> calcCoordinatesWithDistanceCircleRange(Vec3i vec, float range, Axis axis){
        return calcCoordinatesWithDistanceCircleRange(vec.getX(), vec.getY(), vec.getZ(), range, axis);
    }

    // O(range**3)
    public static List<Vec3i> calcCoordinatesSphericalRange(int x, int y, int z, float range){
        if(range < 0){
            throw new IllegalArgumentException("range: " + range);
        }
        List<Vec3i> listCoordinate = new ArrayList<>();
        double[] array = arange(-MathHelper.floor(range), MathHelper.floor(range), 1.0);

        for (double i : array) {
            for (double j : array) {
                for (double k : array) {
                    Vec3d cubedCoordinate = new Vec3d(i, j, k);
                    if (cubedCoordinate.lengthVector() <= range){
                        listCoordinate.add(
                            new Vec3i(cubedCoordinate.x + x, cubedCoordinate.y + y, cubedCoordinate.z + z)
                        );
                    }
                }
            }
        }
        return listCoordinate;
    }

    public static List<Vec3i> calcCoordinatesSphericalRange(Vec3i vec, float range){
        return calcCoordinatesSphericalRange(vec.getX(), vec.getY(), vec.getZ(), range);
    }

    // O(range**3)
    public static List<Vec3i> calcCoordinatesCubedRange(int x, int y, int z, float range){
        if(range < 0){
            throw new IllegalArgumentException("range: " + range);
        }
        List<Vec3i> listCoordinate = new ArrayList<>();
        double[] array = arange(-MathHelper.floor(range), MathHelper.floor(range), 1.0);

        for (double i : array) {
            for (double j : array) {
                for (double k : array) {
                    listCoordinate.add(new Vec3i(x + i, y + j, z + k));
                }
            }
        }
        return listCoordinate;
    }

    public static List<Vec3i> calcCoordinatesCubedRange(Vec3i vec, float range){
        return calcCoordinatesCubedRange(vec.getX(), vec.getY(), vec.getZ(), range);
    }

    /**
     * <pre>
     * {@code
     * arange(0, 4, 1) -> [0.0, 1.0, 2.0, 3.0, 4.0]
     * arange(0, 4, 1.5) -> [0.0, 1.5, 3.0]
     * arange(-2, 5, 2) -> [-2.0, 0.0, 2.0, 4.0]
     * }
     * </pre>
     */
    public static double[] arange(double start, double end, double step){
        return IntStream.rangeClosed(0, (int)((end-start)/step)).mapToDouble(x -> x*step + start).toArray();
    }
}