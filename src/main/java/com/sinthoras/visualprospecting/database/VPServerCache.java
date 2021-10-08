package com.sinthoras.visualprospecting.database;

import com.sinthoras.visualprospecting.VPTags;
import com.sinthoras.visualprospecting.VPUtils;

import java.io.File;

public class VPServerCache extends VPWorldCache{

    protected File getStorageDirectory() {
        return VPUtils.getSubDirectory(VPTags.SERVER_DIR);
    }
}
