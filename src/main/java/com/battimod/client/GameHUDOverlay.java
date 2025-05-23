package com.battimod.client;

import com.battimod.game.GameManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.util.Formatting;

public class GameHUDOverlay {

    public static void register() {
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            // COUNTDOWN
            if (GameManager.isCountdownRunning()) {
                int seconds = GameManager.getCountdownSeconds();
                String msg = seconds > 0 ? String.valueOf(seconds) : "LOS!";
                Formatting color = Formatting.WHITE;

                if (seconds <= 3) {
                    String team = GameManager.getPlayerTeam(client.player);
                    if ("rot".equalsIgnoreCase(team)) color = Formatting.RED;
                    else if ("blau".equalsIgnoreCase(team)) color = Formatting.BLUE;
                }

                float scale = "LOS!".equals(msg) ? 6.0f : 5.0f;
                drawCenteredText(context, client, msg, width / 2, height / 2 - 30, color, scale);
            }

            // SPIELTIMER + PAUSE + ZIEL-ITEM
            if (GameManager.isGameRunning()) {
                String time = GameManager.getFormattedTimer();
                int y = height / 2 - 100;

                // Timer
                drawCenteredText(context, client, "Spielzeit: " + time, width / 2, y, Formatting.LIGHT_PURPLE, 1.2f);

                // Pause-Anzeige
                if (GameManager.isGamePaused()) {
                    drawCenteredText(context, client, "⏸ PAUSIERT", width / 2, y - 30, Formatting.YELLOW, 3.0f);
                }

                // Ziel-Item
                Item item = ClientItemTargetState.getTarget();
                if (item != null) {
                    String itemName = item.getName().getString();
                    drawCenteredText(context, client, "🎯 Ziel: " + itemName, width / 2, y + 20, Formatting.GOLD, 1.4f);
                }
            }
        });
    }

    private static void drawCenteredText(DrawContext context, MinecraftClient client, String text, int x, int y, Formatting color, float scale) {
        int colorCode = color.getColorValue();
        int w = (int) (client.textRenderer.getWidth(text) * scale);
        int h = (int) (client.textRenderer.fontHeight * scale);

        context.getMatrices().push();
        context.getMatrices().translate(x - w / 2f, y - h / 2f, 0);
        context.getMatrices().scale(scale, scale, 1.0f);
        context.drawTextWithShadow(client.textRenderer, text, 0, 0, colorCode);
        context.getMatrices().pop();
    }
}
