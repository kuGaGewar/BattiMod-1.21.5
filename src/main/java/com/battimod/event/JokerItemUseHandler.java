package com.battimod.event;

import com.battimod.game.ItemBattleManager;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class JokerItemUseHandler {

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient()) return ActionResult.PASS;

            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() == Items.BARRIER) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

                if (ItemBattleManager.useJoker(player.getUuid())) {
                    // Entferne Joker
                    stack.decrement(1);

                    // Gib das Ziel-Item
                    var item = ItemBattleManager.getCurrentTarget(serverPlayer);
                    serverPlayer.giveItemStack(new ItemStack(item));

                    serverPlayer.sendMessage(net.minecraft.text.Text.literal("🎁 Du hast dein Ziel-Item mit einem Joker erhalten!"), false);
                    return ActionResult.SUCCESS;
                } else {
                    player.sendMessage(net.minecraft.text.Text.literal("❌ Du hast keine Joker mehr."), false);
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });
    }
}
