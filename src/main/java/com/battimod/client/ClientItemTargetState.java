package com.battimod.client;

import net.minecraft.item.Item;

public class ClientItemTargetState {
    private static Item currentTarget;

    public static void setTarget(Item item) {
        currentTarget = item;
    }

    public static Item getTarget() {
        return currentTarget;
    }
}
