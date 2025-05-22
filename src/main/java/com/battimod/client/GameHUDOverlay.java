package com.battimod.client;

import com.battimod.game.GameManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Formatting;

public class GameHUDOverlay {

    public static void register() {
        HudRenderCallback.EVENT.register((DrawContext context, RenderTickCounter tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            // COUNTDOWN anzeigen
            if (GameManager.isCountdownRunning()) {
                int seconds = GameManager.getCountdownSeconds();
                String msg = seconds > 0 ? String.valueOf(seconds) : "LOS!";
                Formatting color = Formatting.WHITE;

                if (seconds <= 3) {
                    String team = GameManager.getPlayerTeam(client.player);
                    if ("rot".equalsIgnoreCase(team)) color = Formatting.RED;
                    else if ("blau".equalsIgnoreCase(team)) color = Formatting.BLUE;
                }

                drawCenteredText(context, client, msg, width / 2, height / 2 - 20, color);
            }

            // SPIELTIMER anzeigen
            if (GameManager.isGameRunning()) {
                String time = GameManager.getFormattedTimer();
                drawCenteredText(context, client, "Spielzeit: " + time, width / 2, 20, Formatting.GRAY);
            }
        });
    }

    private static void drawCenteredText(DrawContext context, MinecraftClient client, String text, int x, int y, Formatting color) {
        int colorCode = color.getColorValue();
        int w = client.textRenderer.getWidth(text);
        context.drawTextWithShadow(client.textRenderer, text, x - w / 2, y, colorCode);
    }
}
