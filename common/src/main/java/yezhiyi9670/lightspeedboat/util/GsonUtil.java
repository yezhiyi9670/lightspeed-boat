package yezhiyi9670.lightspeedboat.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

public class GsonUtil {
    public static String simpleObjToJson(Object obj) {
        if (Objects.isNull(obj)) return "";
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(obj);
        } catch (Exception e) {
            throw e;
        }
    }
    public static <T> T simpleJsonToObj(String json, Class<T> cls) {
        Gson gson = new Gson();
        if (Objects.isNull(json)) return null;
        T obj = gson.fromJson(json, cls);
        if (Objects.isNull(obj)) {
            return null;
        } else {
            return obj;
        }
    }
}
