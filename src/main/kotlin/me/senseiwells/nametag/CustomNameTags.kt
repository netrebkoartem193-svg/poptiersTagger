package me.senseiwells.nametag;

import net.fabricmc.api.ClientModInitializer;

public class CustomNameTags implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("PopTiers initialized!");
    }
}
