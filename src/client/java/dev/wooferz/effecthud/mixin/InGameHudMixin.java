package dev.wooferz.effecthud.mixin;

import dev.wooferz.effecthud.StatusEffectHUDClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(at = @At(value = "HEAD"), method = "renderStatusEffectOverlay", cancellable = true)
    private void renderStatusEffectOverlay(CallbackInfo ci)
    {
        if (StatusEffectHUDClient.disableVanillaGui) {
            ci.cancel();
        }
    }
}
