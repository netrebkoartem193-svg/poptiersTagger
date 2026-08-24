package me.senseiwells.nametag.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Text> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        String name = player.getGameProfile().getName();

        // Вызываем логику из твоего CustomNameTags.kt
        String tier = me.senseiwells.nametag.CustomNameTags.INSTANCE.getTier(name);

        if (tier != null) {
            Text original = cir.getReturnValue();
            cir.setReturnValue(Text.literal("§7[" + tier + "] ").append(original));
        }
    }
}
