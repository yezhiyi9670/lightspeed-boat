package yezhiyi9670.lightspeedboat;

import com.google.common.base.Suppliers;
import yezhiyi9670.lightspeedboat.config.ConfigManager;

import java.util.function.Supplier;

public class ExampleMod {
    public static final String MOD_ID = "lightspeedboat";

    public static void init() {
        ConfigManager.init();
    }
}
