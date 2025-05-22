package com.battimod.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Random;

public class ItemGiver {
    private static final Item[] ITEMS = {
            Items.DIAMOND_SWORD,
            Items.BOW,
            Items.CROSSBOW,
            Items.TRIDENT
    };

    public static void giveRandomItem(ServerPlayerEntity player) {
        ItemStack stack = new ItemStack(ITEMS[new Random().nextInt(ITEMS.length)]);
        player.getInventory().insertStack(stack);
    }
}
