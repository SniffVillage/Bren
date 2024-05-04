package nl.sniffiandros.bren.common.registry;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class KeyBindingReg {
    public static final String KEY_RELOAD = "key.bren.reload";

    public static KeyBinding reloadKey;

    public static void registerKeyInputs() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            while (reloadKey.wasPressed()) {
                ClientPlayNetworking.send(NetworkReg.RELOAD_PACKET_ID, PacketByteBufs.empty());
            }
        });
    }

    public static void reg() {
        reloadKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_RELOAD,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KeyBinding.GAMEPLAY_CATEGORY
        ));

        registerKeyInputs();
    }
}
