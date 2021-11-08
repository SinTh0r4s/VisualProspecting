package com.sinthoras.visualprospecting.mixins.xaeromap;

import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;
import com.sinthoras.visualprospecting.gui.xaeromap.Buttons;
import com.sinthoras.visualprospecting.gui.xaeromap.RenderStepManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xaero.map.MapProcessor;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiMap;
import xaero.map.gui.GuiTexturedButton;
import xaero.map.gui.ScreenBase;
import xaero.map.misc.Misc;

@Mixin(value = GuiMap.class, remap = false)
public abstract class GuiMapMixin extends ScreenBase {

	@Unique private int oldMouseX = 0;
    @Unique private int oldMouseY = 0;
	@Unique private long timeLastClick = 0;

	protected GuiMapMixin(GuiScreen parent, GuiScreen escape) {
		super(parent, escape);
	}

	@Shadow private double cameraX;

	@Shadow private double cameraZ;

	@Shadow private double scale;

	@Shadow public abstract void addGuiButton(GuiButton b);

	@Shadow private int screenScale;

	@Inject(method = "<init>",
			at = @At("RETURN")
	)
	private void injectConstruct(GuiScreen parent, GuiScreen escape, MapProcessor mapProcessor, EntityPlayer player, CallbackInfo ci) {
		VeinTypeCaching.recalculateNEISearch();
	}

	// method = "drawScreen"
	@Inject(method = "func_73863_a",
			at = @At(value = "INVOKE",
					target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V",
					ordinal = 1
			),
			locals = LocalCapture.CAPTURE_FAILEXCEPTION
	)
	// why is this method so long. this isnt even 1/5 of the way through and look at how many locals there are already
	private void injectPreRender(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci, Minecraft mc, long startTime, long passed, double passedScrolls,
								  int direction, Object var12, boolean mapLoaded, boolean noWorldMapEffect, int mouseXPos, int mouseYPos, double scaleMultiplier,
								  double oldMousePosZ, double preScale, double fboScale, double secondaryScale, double mousePosX, double mousePosZ, int mouseFromCentreX,
								  int mouseFromCentreY, double oldMousePosX, int textureLevel, int leveledRegionShift) {
		// snap the camera to whole pixel values. works around a rendering issue
		cameraX = Math.round(cameraX * scale) / scale;
		cameraZ = Math.round(cameraZ * scale) / scale;

		RenderStepManager.updateHovered(mousePosX, mousePosZ, cameraX, cameraZ, scale);
	}

	// method = "drawScreen"
	@Inject(method = "func_73863_a",
			at = @At(value = "INVOKE",
					target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
					ordinal = 1,
					shift = At.Shift.AFTER
			), slice = @Slice(
					from = @At(value = "INVOKE",
							target = "Lorg/lwjgl/opengl/GL14;glBlendFuncSeparate(IIII)V"
					),
					to = @At(value = "INVOKE",
							target = "Lxaero/map/mods/SupportXaeroMinimap;renderWaypoints(Lnet/minecraft/client/gui/GuiScreen;DDIIDDDDLjava/util/regex/Pattern;Ljava/util/regex/Pattern;FLxaero/map/mods/gui/Waypoint;Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/ScaledResolution;)Lxaero/map/mods/gui/Waypoint;"
					)
			)
	)
	private void injectDraw(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
		RenderStepManager.render(this, cameraX, cameraZ, scale);
	}

	// method = drawScreen
	@Inject(method = "func_73863_a",
			at = @At(value = "INVOKE",
					target = "Lorg/lwjgl/opengl/GL11;glTranslated(DDD)V"
			),
			slice = @Slice(
					from = @At(value = "FIELD",
							// target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;"
							target = "Lnet/minecraft/client/Minecraft;field_71462_r:Lnet/minecraft/client/gui/GuiScreen;",
							opcode = Opcodes.GETFIELD
					),
					to = @At(value = "INVOKE",
							target = "Lxaero/map/gui/CursorBox;drawBox(IIII)V"
					)
			)
	)
	private void injectDrawTooltip(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
		RenderStepManager.drawTooltip(this, cameraX, cameraZ, scale, screenScale);
	}

	// method = "initGui"
	@Inject(method = "func_73866_w_",
			at = @At(value = "INVOKE",
					target = "Lorg/lwjgl/input/Keyboard;enableRepeatEvents(Z)V"
			)
	)
	private void injectInitButtons(CallbackInfo ci) {
		Buttons.oreVeinButton = new GuiTexturedButton(0, height - 20, 20, 20, 0, 0, 16, 16,
				Buttons.xTextures, Buttons::onOreVeinButton, new CursorBox("visualprospecting.button.orevein"));
		addGuiButton(Buttons.oreVeinButton);
		Buttons.undergroundFluidButton = new GuiTexturedButton(0, height - 40, 20, 20, 16, 0, 16, 16,
				Buttons.xTextures, Buttons::onUndergroundFluidButton, new CursorBox("visualprospecting.button.undergroundfluid"));
		addGuiButton(Buttons.undergroundFluidButton);
		if (Utils.isTCNodeTrackerInstalled()) {
			Buttons.thaumcraftNodeButton = new GuiTexturedButton(0, height - 60, 20, 20, 32, 0, 16, 16,
					Buttons.xTextures, Buttons::onThaumcraftNodeButton, new CursorBox("visualprospecting.button.nodes"));
			addGuiButton(Buttons.thaumcraftNodeButton);
		}
	}

	@Inject(method = "onInputPress",
			at = @At("TAIL")
	)
	private void injectListenKeypress(boolean mouse, int code, CallbackInfoReturnable<Boolean> cir) {
		if (Misc.inputMatchesKeyBinding(mouse, code, VP.keyAction)) {
			RenderStepManager.doActionKeyPress();
		}
	}

	@Inject(method = "mapClicked",
			at = @At("TAIL")
	)
	private void injectListenClick(int button, int x, int y, CallbackInfo ci) {
		if (button == 0) {
			final long timestamp = System.currentTimeMillis();
			final boolean isDoubleClick = x == oldMouseX && y == oldMouseY && timestamp - timeLastClick < 500;
			oldMouseX = x;
			oldMouseY = y;
			timeLastClick = isDoubleClick ? 0 : timestamp;

			if (isDoubleClick) {
				RenderStepManager.doDoubleClick();
			}
		}
	}
}
