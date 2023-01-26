package quote.fsrod.common.core.collision;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface ICollision {
    public abstract <T extends Entity> List<T> checkCollisionWithEntities(Level level, List<T> list);
    public abstract List<Entity> checkCollisionWithEntities(Level level, @Nullable Entity entity, @Nullable Predicate <? super Entity> predicate);
}