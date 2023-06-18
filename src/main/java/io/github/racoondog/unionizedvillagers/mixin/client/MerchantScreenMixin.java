package io.github.racoondog.unionizedvillagers.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.racoondog.unionizedvillagers.IOshaViolationHolder;
import io.github.racoondog.unionizedvillagers.UnionizedVillagers;
import io.github.racoondog.unionizedvillagers.UnionizedVillagersClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {
    @Unique private boolean oshaViolation = false;

    private MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "drawForeground", at = @At("TAIL"))
    private void drawForeground(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo ci) {
        TradeOfferList tradeOfferList = this.handler.getRecipes();
        if (tradeOfferList.isEmpty()) return;
        for (var offer : tradeOfferList) {
            if (((IOshaViolationHolder) offer).villagerBalancing$getOshaViolationStatus()) {
                oshaViolation = true;
                RenderSystem.setShaderTexture(0, UnionizedVillagersClient.WARNING_TEXTURE);

                MerchantScreen.drawTexture(matrices, backgroundWidth - 24, 8, 0, 0.0f, 0.0f, 16, 16, 16, 16);
                return;
            }
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/MerchantScreen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;II)V"))
    private void renderTooltip(MerchantScreen instance, MatrixStack matrixStack, Text text, int x, int y) {
        instance.renderTooltip(matrixStack, oshaViolation ? Text.translatable("villager.gui.osha-violation") : text, x, y);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderTooltip(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!oshaViolation) return;

        if (isPointWithinBounds(backgroundWidth - 24, 8, 16, 16, mouseX, mouseY)) {
            renderTooltip(matrices, Text.translatable("villager.gui.osha-violation"), mouseX, mouseY);
        }
    }
}
