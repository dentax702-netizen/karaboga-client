package com.dentax.client.gui;

import com.dentax.client.DentaxClient;
import com.dentax.client.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Screen {

    private final List<CategoryPanel> panels = new ArrayList<>();
    private static final int PANEL_WIDTH = 140;
    private static final int PANEL_HEADER_HEIGHT = 24;
    private static final int MODULE_HEIGHT = 22;
    private static final int PANEL_SPACING = 10;

    // Color theme
    private static final int COLOR_BG = 0xE5101010;
    private static final int COLOR_PANEL_HEADER = 0xFF1A1A2E;
    private static final int COLOR_PANEL_ACCENT = 0xFF00D4FF;
    private static final int COLOR_MODULE_BG = 0xFF161625;
    private static final int COLOR_MODULE_HOVER = 0xFF1F1F35;
    private static final int COLOR_ENABLED = 0xFF00D4FF;
    private static final int COLOR_TEXT = 0xFFE0E0FF;
    private static final int COLOR_TEXT_DIM = 0xFF888899;

    private long openTime;

    public ClickGUI() {
        super(Text.literal("Dentax Client"));
    }

    @Override
    protected void init() {
        panels.clear();
        openTime = System.currentTimeMillis();

        Module.Category[] categories = Module.Category.values();
        int startX = 20;

        for (int i = 0; i < categories.length; i++) {
            Module.Category cat = categories[i];
            List<Module> mods = DentaxClient.moduleManager.getModulesByCategory(cat);
            int x = startX + i * (PANEL_WIDTH + PANEL_SPACING);
            int y = 30;
            panels.add(new CategoryPanel(cat, mods, x, y));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        long elapsed = System.currentTimeMillis() - openTime;
        float anim = Math.min(1f, elapsed / 200f);

        // Dark overlay background
        context.fillGradient(0, 0, this.width, this.height,
                0xCC000000, 0xDD050510);

        // Title watermark top-right
        String watermark = "✦ Karaboga Client v1.0";
        int wmWidth = this.textRenderer.getWidth(watermark);
        context.drawTextWithShadow(this.textRenderer, Text.literal(watermark),
                this.width - wmWidth - 8, 8, COLOR_PANEL_ACCENT);

        // Render each panel with animation
        for (int i = 0; i < panels.size(); i++) {
            CategoryPanel panel = panels.get(i);
            float panelAnim = Math.min(1f, (elapsed - i * 30) / 180f);
            if (panelAnim > 0) {
                panel.render(context, mouseX, mouseY, panelAnim);
            }
        }

        // Keybind hint at bottom
        String hint = "[ Right Shift ] to close  •  [ Left Click ] to toggle";
        int hintWidth = this.textRenderer.getWidth(hint);
        context.drawTextWithShadow(this.textRenderer, Text.literal(hint),
                this.width / 2 - hintWidth / 2,
                this.height - 14, COLOR_TEXT_DIM);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (CategoryPanel panel : panels) {
                if (panel.mouseClicked((int) mouseX, (int) mouseY)) return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // ======= Inner class: CategoryPanel =======
    private class CategoryPanel {
        final Module.Category category;
        final List<Module> modules;
        int x, y;
        boolean collapsed = false;

        CategoryPanel(Module.Category category, List<Module> modules, int x, int y) {
            this.category = category;
            this.modules = modules;
            this.x = x;
            this.y = y;
        }

        int getHeight() {
            if (collapsed) return PANEL_HEADER_HEIGHT;
            return PANEL_HEADER_HEIGHT + modules.size() * MODULE_HEIGHT + 4;
        }

        void render(DrawContext ctx, int mouseX, int mouseY, float anim) {
            int drawY = (int) (y - (1f - anim) * 10);
            int alpha = (int) (anim * 255);

            // Panel shadow
            ctx.fill(x + 3, drawY + 3, x + PANEL_WIDTH + 3,
                    drawY + getHeight() + 3, (0x33000000));

            // Panel background
            ctx.fill(x, drawY, x + PANEL_WIDTH, drawY + getHeight(), withAlpha(COLOR_BG, alpha));

            // Header background
            ctx.fill(x, drawY, x + PANEL_WIDTH, drawY + PANEL_HEADER_HEIGHT,
                    withAlpha(COLOR_PANEL_HEADER, alpha));

            // Left accent bar (category color)
            int catColor = category.getColor();
            ctx.fill(x, drawY, x + 3, drawY + PANEL_HEADER_HEIGHT,
                    withAlpha(catColor | 0xFF000000, alpha));

            // Category name
            ctx.drawTextWithShadow(textRenderer,
                    Text.literal(category.getDisplayName()),
                    x + 8, drawY + 8, withAlpha(COLOR_TEXT, alpha));

            // Module count badge
            String countStr = String.valueOf(modules.size());
            int countWidth = textRenderer.getWidth(countStr);
            ctx.fill(x + PANEL_WIDTH - countWidth - 10, drawY + 7,
                    x + PANEL_WIDTH - 5, drawY + 17,
                    withAlpha(0xFF222240, alpha));
            ctx.drawTextWithShadow(textRenderer, Text.literal(countStr),
                    x + PANEL_WIDTH - countWidth - 7, drawY + 8,
                    withAlpha(COLOR_TEXT_DIM, alpha));

            if (!collapsed) {
                // Module list
                for (int i = 0; i < modules.size(); i++) {
                    Module mod = modules.get(i);
                    int modY = drawY + PANEL_HEADER_HEIGHT + i * MODULE_HEIGHT + 2;

                    boolean hovered = mouseX >= x && mouseX <= x + PANEL_WIDTH
                            && mouseY >= modY && mouseY <= modY + MODULE_HEIGHT;

                    // Module background
                    int modBg = hovered ? withAlpha(COLOR_MODULE_HOVER, alpha)
                                        : withAlpha(COLOR_MODULE_BG, alpha);
                    ctx.fill(x + 1, modY, x + PANEL_WIDTH - 1, modY + MODULE_HEIGHT, modBg);

                    // Enabled indicator bar
                    if (mod.isEnabled()) {
                        ctx.fill(x + 1, modY, x + 3, modY + MODULE_HEIGHT,
                                withAlpha(COLOR_ENABLED, alpha));
                    }

                    // Module name
                    int nameColor = mod.isEnabled()
                            ? withAlpha(COLOR_ENABLED, alpha)
                            : withAlpha(COLOR_TEXT, alpha);
                    ctx.drawTextWithShadow(textRenderer,
                            Text.literal(mod.getName()),
                            x + 8, modY + 7, nameColor);

                    // Enabled dot
                    if (mod.isEnabled()) {
                        ctx.drawTextWithShadow(textRenderer,
                                Text.literal("●"),
                                x + PANEL_WIDTH - 14, modY + 7,
                                withAlpha(COLOR_ENABLED, alpha));
                    }
                }
            }

            // Bottom border line
            ctx.fill(x, drawY + getHeight() - 1,
                    x + PANEL_WIDTH, drawY + getHeight(),
                    withAlpha(catColor | 0x66000000, alpha));
        }

        boolean mouseClicked(int mouseX, int mouseY) {
            // Click header = collapse
            if (mouseX >= x && mouseX <= x + PANEL_WIDTH
                    && mouseY >= y && mouseY <= y + PANEL_HEADER_HEIGHT) {
                collapsed = !collapsed;
                return true;
            }
            if (!collapsed) {
                for (int i = 0; i < modules.size(); i++) {
                    int modY = y + PANEL_HEADER_HEIGHT + i * MODULE_HEIGHT + 2;
                    if (mouseX >= x && mouseX <= x + PANEL_WIDTH
                            && mouseY >= modY && mouseY <= modY + MODULE_HEIGHT) {
                        modules.get(i).toggle();
                        return true;
                    }
                }
            }
            return false;
        }

        private int withAlpha(int color, int alpha) {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            int origAlpha = (color >> 24) & 0xFF;
            int finalAlpha = (origAlpha * alpha) / 255;
            return (finalAlpha << 24) | (r << 16) | (g << 8) | b;
        }
    }
}
