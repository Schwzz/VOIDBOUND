package dev.voidbound.client.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import dev.voidbound.client.VoidboundControls;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Inject(method = "click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V", at = @At("TAIL"))
    private static void voidbound$discardDebugClick(InputConstants.Key key, CallbackInfo ci) {
        VoidboundControls.discardDebugClick(key);
    }
}