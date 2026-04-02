# Varta OBJ - OBJ Model Vehicle

Полностью функциональный вехикл Varta с OBJ моделью вместо стандартной Bedrock модели.

## Что добавлено

### Файлы энтити
- `tech/vvp/vvp/entity/vehicle/VartaObjEntity.java` - класс энтити
- `tech/vvp/vvp/client/renderer/entity/vehicle/VartaObjRenderer.java` - рендерер

### Система OBJ рендеринга
- `tech/vvp/vvp/client/renderer/obj/ObjVehicleRenderer.java` - базовый рендерер
- `tech/vvp/vvp/client/model/obj/ObjVehicleModel.java` - модель-обертка

### Ресурсы
- `assets/vvp/models/obj/varta2.obj` - OBJ модель
- `assets/vvp/textures/entity/varta_obj.png` - текстура модели
- `assets/vvp/models/item/varta_obj.json` - модель айтема
- `assets/vvp/textures/item/varta_obj.png` - текстура айтема
- `assets/vvp/lang/en_us.json` - локализация

### Регистрация
- `ModEntities.java` - добавлен `VARTA_OBJ`
- `ModEntityRenderers.java` - зарегистрирован рендерер
- `ModItems.java` - добавлен спавн айтем

## Как использовать

### В игре
1. Получите айтем `/give @s vvp:varta_obj`
2. ПКМ по блоку чтобы заспавнить вехикл
3. Вехикл работает как обычный Varta со всеми функциями SuperBWarfare

### Особенности OBJ модели
- Двусторонний рендеринг (видно изнутри)
- Не исчезает когда камера внутри
- Поддерживает анимации через систему SuperBWarfare
- Автоматический масштаб 1/16 для Minecraft единиц
- Минимальное мерцание текстур (Z-fighting fix)

## Как добавить свой OBJ вехикл

### 1. Создайте энтити класс
```java
public class YourVehicleObjEntity extends VehicleEntity {
    public YourVehicleObjEntity(EntityType<YourVehicleObjEntity> type, Level world) {
        super(type, world);
    }
}
```

### 2. Создайте рендерер
```java
public class YourVehicleObjRenderer extends ObjVehicleRenderer<YourVehicleObjEntity> {
    private static final String MODEL_PATH = "vvp:models/obj/your_model.obj";
    private static final ResourceLocation TEXTURE = 
        new ResourceLocation("vvp", "textures/entity/your_texture.png");
    
    public YourVehicleObjRenderer(EntityRendererProvider.Context context) {
        super(context, MODEL_PATH, TEXTURE);
    }
    
    @Override
    protected void applyVehicleTransforms(YourVehicleObjEntity entity, PoseStack poseStack, 
                                         float entityYaw, float partialTicks) {
        poseStack.translate(0, 0.5, 0);
    }
}
```

### 3. Зарегистрируйте
- В `ModEntities.java` добавьте энтити
- В `ModEntityRenderers.java` зарегистрируйте рендерер
- В `ModItems.java` добавьте спавн айтем
- Создайте JSON файлы и локализацию

### 4. Добавьте ресурсы
- OBJ модель в `assets/vvp/models/obj/`
- Текстуру в `assets/vvp/textures/entity/`
- Модель айтема в `assets/vvp/models/item/`
- Текстуру айтема в `assets/vvp/textures/item/`

## Требования к OBJ модели

### Экспорт из Blender
- ✅ Include Normals
- ✅ Include UVs
- ✅ Triangulate Faces (опционально)
- ✅ Apply Modifiers
- ✅ Selection Only (если нужно)

### Масштаб
Модель автоматически масштабируется с коэффициентом 0.0625 (1/16).
Если нужен другой масштаб, измените `SCALE_FACTOR` в `ObjVehicleModel.java`.

### Нормали
Убедитесь что нормали направлены правильно:
- В Blender: Mesh → Normals → Recalculate Outside (Shift+N)
- Проверьте Face Orientation (Alt+Z в Edit Mode)

## Troubleshooting

### Модель не загружается
- Проверьте путь в `MODEL_PATH`
- Убедитесь что OBJ файл в правильной директории
- Проверьте логи на ошибки парсинга

### Модель слишком большая/маленькая
- Измените `SCALE_FACTOR` в `ObjVehicleModel.java`
- Или добавьте `poseStack.scale()` в `applyVehicleTransforms()`

### Видны дыры в модели
- Проверьте нормали в Blender (Shift+N)
- Убедитесь что все грани имеют правильное направление

### Мерцание текстур
- Увеличьте `BACKFACE_OFFSET` в `ObjCube.java` (0.001f → 0.005f)
- Это баланс между мерцанием и видимыми швами

## Интеграция с SuperBWarfare

Varta OBJ полностью совместим с SuperBWarfare:
- ✅ Система сидений
- ✅ Система оружия
- ✅ Система повреждений
- ✅ Физика вехикла
- ✅ Инвентарь
- ✅ Топливо
- ✅ Все остальные функции

## Производительность

OBJ рендеринг оптимизирован:
- Модели кешируются при загрузке
- Используется эффективная триангуляция
- Минимальные вычисления на кадр
- Нет дополнительной нагрузки по сравнению с Bedrock моделями

## Поддержка

Для вопросов и багов создавайте issue в репозитории проекта.
