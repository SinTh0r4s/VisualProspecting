package com.sinthoras.visualprospecting.mixins.journeymap;

import com.sinthoras.visualprospecting.gui.journeymap.ResetClientCacheConfirmation;
import journeymap.client.ui.UIManager;
import journeymap.client.ui.component.Button;
import journeymap.client.ui.component.ButtonList;
import journeymap.client.ui.component.JmUI;
import journeymap.client.ui.dialog.FullscreenActions;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FullscreenActions.class)
public abstract class FullscreenActionsMixin extends JmUI {

    @Shadow(remap = false)
    Button buttonAbout;

    private Button resetVisualProspectingCacheButton;

    public FullscreenActionsMixin() {
        super("");
    }

    @Inject(method = "func_73866_w_",
            at = @At(value = "INVOKE",
                    target = "Ljourneymap/client/ui/component/Button;setDrawFrame(Z)V"),
            remap = false,
            require = 1)
    private void onInitGui(CallbackInfo callbackInfo) {
        resetVisualProspectingCacheButton = new Button(I18n.format("visualprospecting.button.resetprogress"));
        resetVisualProspectingCacheButton.setTooltip(I18n.format("visualprospecting.button.resetprogress.tooltip"));
        buttonList.add(resetVisualProspectingCacheButton);
    }

    @Inject(method = "layoutButtons", at = @At("RETURN"), remap = false, require = 1)
    private void onLayoutButtons(CallbackInfo callbackInfo) {
        final ButtonList row = new ButtonList(buttonAbout, resetVisualProspectingCacheButton);
        row.layoutCenteredHorizontal(width / 2, height / 4, true, 4);
    }

    @Inject(method = "func_146284_a", at = @At("HEAD"), remap = false, cancellable = true, require = 1, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onButtonClicked(GuiButton guibutton, CallbackInfo callbackInfo) {
        if(guibutton == resetVisualProspectingCacheButton) {
            UIManager.getInstance().open(ResetClientCacheConfirmation.class);
            callbackInfo.cancel();
        }
    }
}
