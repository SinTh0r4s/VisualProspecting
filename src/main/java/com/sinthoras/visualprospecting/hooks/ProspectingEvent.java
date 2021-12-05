package com.sinthoras.visualprospecting.hooks;

import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;

import cpw.mods.fml.common.eventhandler.Event;

public class ProspectingEvent extends Event {
	
	@Override
	public boolean isCancelable() {
		return true;
	}
	
	public static class OreVein extends ProspectingEvent {
		
		private final OreVeinPosition position;
		
		public OreVein(OreVeinPosition position) {
			this.position = position;
		}
		
		public OreVeinPosition getPosition() {
			return this.position;
		}
		
	}
	
	public static class UndergroundFluid extends ProspectingEvent {
		
		private final UndergroundFluidPosition position;
		
		public UndergroundFluid(UndergroundFluidPosition position) {
			this.position = position;
		}
		
		public UndergroundFluidPosition getPosition() {
			return this.position;
		}
		
	}

}
