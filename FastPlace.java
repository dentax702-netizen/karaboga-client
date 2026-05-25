package com.dentax.client;

import com.dentax.client.module.ModuleManager;
import com.dentax.client.gui.ClickGUI;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DentaxClient implements ClientModInitializer {

    public static final String MOD_NAME = "Karaboga Client";
    public static final String MOD_VERSION = "1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger("dentax");

    public static ModuleManager moduleManager;
    public static ClickGUI clickGUI;

    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[Dentax] Starting {} v{} for Minecraft 1.21.11...", MOD_NAME, MOD_VERSION);

        // Initialize module manager
        moduleManager = new ModuleManager();

        // Initialize Click GUI
        clickGUI = new ClickGUI();

        // Register keybinding (Right Shift = open GUI)
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dentax.opengui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.dentax"
        ));

        // Tick event: open GUI when key pressed
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(clickGUI);
                }
            }
            // Tick all modules
            moduleManager.tick();
        });

        LOGGER.info("[Dentax] Successfully loaded {} modules!", moduleManager.getModules().size());
    }
}
