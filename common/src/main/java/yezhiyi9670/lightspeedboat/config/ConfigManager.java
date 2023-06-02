package yezhiyi9670.lightspeedboat.config;

import yezhiyi9670.lightspeedboat.ExampleExpectPlatform;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import yezhiyi9670.lightspeedboat.util.GsonUtil;

public class ConfigManager {
    private static Path configFile;
    private static ConfigData data;

    public static void init() {
        Path configDir = ExampleExpectPlatform.getConfigDirectory();
        configFile = configDir.resolve("lightspeedboat-common.json");
        try {
            byte[] bytes = Files.readAllBytes(configFile);
            String content = new String(bytes, StandardCharsets.UTF_8);
            data = GsonUtil.simpleJsonToObj(content, ConfigData.class);
            if(data == null) {
                throw new Exception("Json load failed");
            }
        } catch(Exception _err) {
            data = new ConfigData();
        }
        try {
            Files.writeString(configFile, GsonUtil.simpleObjToJson(data), StandardCharsets.UTF_8);
        } catch(IOException _err1) {}
    }

    public static ConfigData get() {
        return data;
    }
}
