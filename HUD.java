package com.dentax.client.mixin;

import com.dentax.client.DentaxClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(at = @At("TAIL"), method = "render")
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TitleScreen screen = (TitleScreen) (Object) this;
        String watermark = "✦ " + DentaxClient.MOD_NAME + " v" + DentaxClient.MOD_VERSION + " | 1.21.11";
        context.drawTextWithShadow(
                screen.getTextRenderer(),
                net.minecraft.text.Text.literal(watermark),
                2,
                2,
                0xFF00D4FF
        );
    }
}
