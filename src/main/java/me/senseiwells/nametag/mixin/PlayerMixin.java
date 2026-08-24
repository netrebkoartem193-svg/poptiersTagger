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
public abstract class PlayerMixin {

    private static final Map<String, String> TIERS = new HashMap<>();
    private static long lastCheck = 0;

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Text> cir) {
        long now = System.currentTimeMillis();

        if (now - lastCheck > 60000) {
            lastCheck = now;
            new Thread(() -> {
                try {
                    URL url = new URL("https://rentry.co/poptier123/raw");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String line;
                    Map<String, String> tempMap = new HashMap<>();
                    while ((line = reader.readLine()) != null) {
                        String trimmed = line.trim();
                        if (trimmed.startsWith("#") || trimmed.isEmpty()) continue;
                        String[] parts = trimmed.split(":");
                        if (parts.length == 2) {
                            tempMap.put(parts[0].trim().toLowerCase(), parts[1].trim().toLowerCase());
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

        if (TIERS.containsKey(name)) {
            String formattedTier = TIERS.get(name).replace("&", "§");
            Text originalText = cir.getReturnValue();
            Text modifiedText = Text.literal("§7[" + formattedTier + "§7] ").append(originalText);
            cir.setReturnValue(modifiedText);
        }
    }
}
