package flaxbeard.thaumicexploration.interop;

import mcp.mobius.waila.api.IWailaRegistrar;
import flaxbeard.thaumicexploration.block.BlockBoundJar;
import flaxbeard.thaumicexploration.block.BlockEverburnUrn;
import flaxbeard.thaumicexploration.block.BlockSoulBrazier;

/**
 * Created by Katrina on 17/06/2015.
 */
public class WailaConfig {

    public static void callbackRegister(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new BoundJarProvider(), BlockBoundJar.class);
        registrar.registerBodyProvider(new SoulBrazierProvider(), BlockSoulBrazier.class);
        registrar.registerBodyProvider(new EverBurnUrnProvider(), BlockEverburnUrn.class);
    }
}
