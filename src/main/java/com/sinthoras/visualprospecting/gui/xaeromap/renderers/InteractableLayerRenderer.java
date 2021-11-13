package com.sinthoras.visualprospecting.gui.xaeromap.renderers;

import com.sinthoras.visualprospecting.gui.model.layers.WaypointProviderManager;
import com.sinthoras.visualprospecting.gui.model.locations.ILocationProvider;
import com.sinthoras.visualprospecting.gui.xaeromap.rendersteps.InteractableRenderStep;
import com.sinthoras.visualprospecting.gui.xaeromap.rendersteps.RenderStep;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public abstract class InteractableLayerRenderer extends LayerRenderer {
	private double mouseXForRender;
	private double mouseYForRender;
	protected WaypointProviderManager manager;
	protected InteractableRenderStep hovered;

	public InteractableLayerRenderer(WaypointProviderManager manager) {
		super(manager);
		this.manager = manager;
		hovered = null;
	}

	@Override
	protected abstract List<? extends InteractableRenderStep> generateRenderSteps(List<? extends ILocationProvider> visibleElements);

	public void updateHovered(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
		mouseXForRender = mouseX;
		mouseYForRender = mouseY;
		for (RenderStep step : renderSteps) {
			if (step instanceof InteractableRenderStep && ((InteractableRenderStep) step).isMouseOver(mouseX, mouseY, cameraX, cameraZ, scale)) {
				hovered = (InteractableRenderStep) step;
				return;
			}
		}
		hovered = null;
	}

	public void drawTooltip(GuiScreen gui, double cameraX, double cameraZ, double scale, int scaleAdj) {
		if (hovered != null) {
			hovered.drawTooltip(gui, mouseXForRender, mouseYForRender, cameraX, cameraZ, scale, scaleAdj);
		}
	}

	public void doActionKeyPress() {
		if (manager.isLayerActive() && hovered != null) {
			hovered.onActionButton();
			manager.forceRefresh();
		}
	}

	public void doDoubleClick() {
		if (hovered != null) {
			hovered.onDoubleClick();
		}
	}
}
