package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VartaObjEntity extends VehicleEntity implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // ... твои переменные ...
    public float prevWheelRotation;
    public float wheelRotation;

    // Новые переменные для поворота влево/вправо
    public float prevSteeringAngle;
    public float steeringAngle;

    public VartaObjEntity(EntityType<VartaObjEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            this.prevWheelRotation = this.wheelRotation;
            this.prevSteeringAngle = this.steeringAngle;

            // 1. ВРАЩЕНИЕ (Кручение колес вперед/назад)
            double dx = this.getX() - this.xo;
            double dz = this.getZ() - this.zo;
            float distance = (float) Math.sqrt(dx * dx + dz * dz);

            // Направление движения
            float rotRad = this.getYRot() * 0.017453292F;
            float lookX = (float) -Math.sin(rotRad);
            float lookZ = (float) Math.cos(rotRad);
            int direction = (dx * lookX + dz * lookZ) >= 0 ? 1 : -1;

            if (distance > 0.001f) {
                this.wheelRotation += distance * 140.0f * direction;
            }

            // 2. ПОВОРОТ (Влево/Вправо)
            // Считаем разницу поворота корпуса между тиками
            float yawDelta = this.getYRot() - this.yRotO;

            // Исправляем резкий переход через 180 градусов
            if (yawDelta > 180) yawDelta -= 360;
            if (yawDelta < -180) yawDelta += 360;

            // Умножаем на 15.0f, чтобы получить угол поворота колес (макс 45 градусов)
            // ИНВЕРТИРОВАНО (минус перед yawDelta), чтобы колеса крутились в ту же сторону, куда поворачивает корпус
            float targetSteer = net.minecraft.util.Mth.clamp(-yawDelta * 15.0f, -45.0f, 45.0f);

            // Если машина стоит, руль плавно возвращается в центр
            if (distance < 0.01f) targetSteer = 0;

            // Плавное изменение поворота колес
            this.steeringAngle = net.minecraft.util.Mth.approach(this.steeringAngle, targetSteer, 5.0f);

            this.wheelRotation %= 360.0f;
        }
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return tickCount;
    }
}