package com.sinthoras.visualprospecting.database;

import api.visualprospecting.VPOreGenCallbackHandler;
import com.sinthoras.visualprospecting.database.veintypes.VPVeinTypeCaching;
import net.minecraft.world.World;

public class VPOreGenCallback implements VPOreGenCallbackHandler {
    @Override
    public void prospectPotentialNewVein(String oreMixName, World aWorld, int aX, int aZ) {
        VPCacheWorld.putVeinType(aWorld.provider.dimensionId, aX, aZ, VPVeinTypeCaching.getVeinType(oreMixName));
    }
}
