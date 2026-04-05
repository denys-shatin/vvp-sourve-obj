package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.lang.reflect.Field;

public class Ka52Entity extends CamoVehicleBase {
    private static final ResourceLocation[] CAMO_TEXTURES = {
        new ResourceLocation("vvp", "textures/entity/ka52_camo1.png"),
        new ResourceLocation("vvp", "textures/entity/ka52_camo2.png")
    };

    private static final String[] CAMO_NAMES = {"Default", "Alt"};

    private static Field propellerRotField;
    private static Field propellerRotOField;

    static {
        try {
            Class<?> vehicleClass = Class.forName("com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity");
            propellerRotField = vehicleClass.getDeclaredField("propellerRot");
            propellerRotField.setAccessible(true);
            propellerRotOField = vehicleClass.getDeclaredField("propellerRotO");
            propellerRotOField.setAccessible(true);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Ka52Entity(EntityType<Ka52Entity> type, Level world) {
        super(type, world);
    }

    @Override
    public ResourceLocation[] getCamoTextures() {
        return CAMO_TEXTURES;
    }

    @Override
    public String[] getCamoNames() {
        return CAMO_NAMES;
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
            .custom((source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

    public float getPropellerRot() {
        try {
            return propellerRotField != null ? (float) propellerRotField.get(this) : 0.0f;
        } catch (Exception exception) {
            return 0.0f;
        }
    }

    public float getPropellerRotO() {
        try {
            return propellerRotOField != null ? (float) propellerRotOField.get(this) : 0.0f;
        } catch (Exception exception) {
            return 0.0f;
        }
    }
}
