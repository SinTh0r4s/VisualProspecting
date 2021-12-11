package com.sinthoras.visualprospecting.integration.voxelmap;

import java.lang.reflect.Method;

import com.thevoxelbox.voxelmap.interfaces.IWaypointManager;

public class IWaypointManagerReflection {

	private static Method getCurrentSubworldDescriptor;

	public static String getCurrentSubworldDescriptor(IWaypointManager obj, boolean arg) {
		try {
			return (String) getCurrentSubworldDescriptor.invoke(obj, arg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	static {
		try {
			getCurrentSubworldDescriptor = IWaypointManager.class.getMethod("if", boolean.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
