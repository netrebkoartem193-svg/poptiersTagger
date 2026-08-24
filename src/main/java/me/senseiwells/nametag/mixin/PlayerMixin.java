package me.senseiwells.nametag.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    private static final Map<String, String> TIERS = new HashMap<>();
    private static long lastCheck = 0;

    // В 1.21.4 имя над головой берется через getDisplayName()
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Text> cir) {
        long now = System.currentTimeMillis();
        
        // Фоновое обновление тиров с Rentry каждые 60 секунд
        if (now - lastCheck > 60000) {
            lastCheck = now;
            new Thread(() -> {
                try {
                    URL url = new URL("https://rentry.co/твой_хеш/raw");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String line;
                    Map<String, String> tempMap = new HashMap<>();
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(":");
                        if (parts.length == 2) {
                            tempMap.put(parts[0].trim().toLowerCase(), parts[1].trim());
                        }
                    }
                    reader.close();
                    TIERS.clear();
                    TIERS.putAll(tempMap);
                } catch (Exception ignored) {}
            }).start();
        }

        PlayerEntity player = (PlayerEntity) (Object) this;
        String name = player.getGameProfile().getName().toLowerCase();

        // Если ник есть в базе Rentry — к нему добавляется тир
        if (TIERS.containsKey(name)) {
            String tier = TIERS.get(name);
            Text originalText = cir.getReturnValue();
            
            // Форматирование: [HT1] ИмяИгрока
            Text modifiedText = Text.literal("§7[" + tier + "] ").append(originalText);
            cir.setReturnValue(modifiedText);
        }
    }
}
