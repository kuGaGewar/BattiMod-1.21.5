package com.battimod.client;

import com.battimod.game.GameManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Formatting;
import net.minecraft.client.gui.DrawContext;


public class GameHUDOverlay {

    public static void register() {
        HudRenderCallback.EVENT.register((DrawContext context, RenderTickCounter  tickDelta) -> {
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

                drawCenteredText(context, client, msg, width / 2, height / 2 - 20, color, 5.0f);

            }

            // SPIELTIMER anzeigen
            if (GameManager.isGameRunning()) {
                String time = GameManager.getFormattedTimer();
                drawCenteredText(context, client, "Spielzeit: " + time, width / 2, 20, Formatting.GRAY, 1.5f);

                if (GameManager.isGamePaused()) {
                    drawCenteredText(context, client, "⏸ PAUSIERT", width / 2, height / 2 + 40, Formatting.YELLOW, 3.0f);
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
