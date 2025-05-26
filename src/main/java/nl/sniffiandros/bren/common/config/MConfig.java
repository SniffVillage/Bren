package nl.sniffiandros.bren.common.config;

import com.google.gson.*;
import nl.sniffiandros.bren.common.Bren;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

/*
 * Credits : Khajiitos
 * Git : https://github.com/Khajiitos/ChestedCompanions/blob/master/Common/src/main/java/me/khajiitos/chestedcompanions/common/config/CCConfig.java
 */

public class MConfig {
    private static final File file = new File("config/bren_config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    @Entry(clientOnly = true)
    public static final ConfigHelper.BooleanValue renderGunOnBack = new ConfigHelper.BooleanValue(true,
            "Renders the gun on backs || Default: true";

    @Entry(clientOnly = true)
    public static final ConfigHelper.BooleanValue spawnCasingParticles = new ConfigHelper.BooleanValue(true,
            "Spawns empty casings when a gun is fired || Default: true");

    @Entry(clientOnly = true)
    public static final ConfigHelper.BooleanValue showAmmoGui = new ConfigHelper.BooleanValue(true,
            "Shows the ammo GUI || Default: true");

    @Entry()
    public static final ConfigHelper.BooleanValue bulletsBreakGlass = new ConfigHelper.BooleanValue(true,
            "Breaks glass on bullet impact || Default: true");

    @Entry()
    public static final ConfigHelper.FloatValue recoilMultiplier = new ConfigHelper.FloatValue(1.0f,
            "The recoil multiplier, so 0 is no recoil || Default: 1.0");

    @Entry()
    public static final ConfigHelper.FloatValue machineGunDamage = new ConfigHelper.FloatValue(4.5f,
            "Ranged damage for the Machine Gun || Default: 4.5");

    @Entry()
    public static final ConfigHelper.FloatValue netheriteMachineGunDamage = new ConfigHelper.FloatValue(5.0f,
            "Ranged damage for the Netherite Machine Gun || Default: 5.0");

    @Entry()
    public static final ConfigHelper.FloatValue autoGunDamage = new ConfigHelper.FloatValue(5.5f,
            "Ranged damage for the Auto-Gun || Default: 5.5");

    @Entry()
    public static final ConfigHelper.FloatValue netheriteAutoGunDamage = new ConfigHelper.FloatValue(6.0f,
            "Ranged damage for the Netherite Auto-Gun || Default: 6.0");

    @Entry()
    public static final ConfigHelper.FloatValue rifleDamage = new ConfigHelper.FloatValue(10.0f,
            "Ranged damage for the Rifle || Default: 10.0");

    @Entry()
    public static final ConfigHelper.FloatValue netheriteRifleDamage = new ConfigHelper.FloatValue(11.0f,
            "Ranged damage for the Netherite Rifle || Default: 11.0");

    @Entry()
    public static final ConfigHelper.FloatValue shotgunDamage = new ConfigHelper.FloatValue(4.0f,
            "Damage per shrapnel for the Shotgun || Default: 4.0");

    @Entry()
    public static final ConfigHelper.FloatValue netheriteShotgunDamage = new ConfigHelper.FloatValue(4.5f,
            "Damage per shrapnel for the Netherite Shotgun || Default: 4.5");

    @Entry()
    public static final ConfigHelper.FloatValue revolverDamage = new ConfigHelper.FloatValue(8.0f,
            "Ranged damage for the Revolver || Default: 8.0");

    @Entry()
    public static final ConfigHelper.FloatValue netheriteRevolverDamage = new ConfigHelper.FloatValue(8.5f,
            "Ranged damage for the Netherite Revolver || Default: 8.5");

    public static void init() {
        if (!file.exists()) {
            save();
        } else {
            load();
        }
    }

    public static void save() {
        if (!file.getParentFile().isDirectory() && !file.getParentFile().mkdirs()) {
            Bren.LOGGER.error("Failed to create config directory");
            return;
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", "Config for the Bren mod");

            for (Field field : MConfig.class.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Entry.class)) {
                    continue;
                }

                Object object = field.get(null);

                if (!(object instanceof ConfigHelper.Value<?> configValue)) {
                    continue;
                }

                jsonObject.addProperty(String.format("_comment_%s", field.getName()), configValue.getComment());

                jsonObject.add(field.getName(), configValue.write());
            }

            GSON.toJson(jsonObject, fileWriter);
        } catch (IOException e) {
            Bren.LOGGER.error("Failed to save the Bren config", e);
        } catch (IllegalAccessException e) {
            Bren.LOGGER.error("Error while saving the Bren config", e);
        }
    }

    public static void load() {
        if (!file.exists()) {
            return;
        }

        try (FileReader fileReader = new FileReader(file)) {
            JsonObject jsonObject = GSON.fromJson(fileReader, JsonObject.class);

            for (Field field : MConfig.class.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Entry.class)) {
                    continue;
                }

                String fieldName = field.getName();

                if (!jsonObject.has(fieldName)) {
                    continue;
                }

                Object object = field.get(null);

                if (!(object instanceof ConfigHelper.Value<?> configValue)) {
                    continue;
                }

                JsonElement jsonElement = jsonObject.get(fieldName);
                configValue.setUnchecked(configValue.read(jsonElement));
            }
        } catch (IOException e) {
            Bren.LOGGER.error("Failed to read the Bren config", e);
        } catch (IllegalAccessException e) {
            Bren.LOGGER.error("Error while reading the Bren config", e);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Entry {
        String category() default "general";
        boolean clientOnly() default false;
    }
}
