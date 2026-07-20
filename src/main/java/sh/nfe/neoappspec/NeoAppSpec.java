package sh.nfe.neoappspec;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(NeoAppSpec.MOD_ID)
public class NeoAppSpec {
    public static final String MOD_ID = "neo_app_spec";
    public static final Logger LOGGER = LoggerFactory.getLogger(NeoAppSpec.class);

    public NeoAppSpec(IEventBus modEventBus, ModContainer modContainer) {
        ModRegistry.Items.register(modEventBus);
    }
}
