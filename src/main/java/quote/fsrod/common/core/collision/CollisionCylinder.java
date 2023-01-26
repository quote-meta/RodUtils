package quote.fsrod.common.core.collision;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CollisionCylinder implements ICollision{
    private Vec3 vecNear;
    private Vec3 vecEnd;
    private float radius;

    public CollisionCylinder(Vec3 vecNear, Vec3 vecEnd, float radius){
        this.vecNear = vecNear;
        this.vecEnd = vecEnd;
        this.radius = radius;
    }

    @Override
    public <T extends Entity> List<T> checkCollisionWithEntities(Level level, List<T> list) {
        List<T> targets = new ArrayList<>();

        for (T target : list) {
            AABB aabb = target.getBoundingBox().inflate(radius, radius, radius);
            Optional<Vec3> result = aabb.clip(vecNear, vecEnd);
            if(result.isPresent()){
                targets.add(target);
            }
        }

        return targets;
    }

    @Override
    public List<Entity> checkCollisionWithEntities(Level level, Entity entity, Predicate<? super Entity> predicate) {
        AABB aabb = new AABB(vecNear.x, vecNear.y, vecNear.z, vecEnd.x, vecEnd.y, vecEnd.z).inflate(radius);
        List<Entity> targetsInAABB = level.getEntities(entity, aabb, predicate);
        return checkCollisionWithEntities(level, targetsInAABB);
    }
}