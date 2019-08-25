package testUtils;

import org.json.JSONObject;
import org.junit.Assert;

public class JSONTestUtils {
    public static <T> void assertJSON(JSONObject object, T expected, String key) {
        @SuppressWarnings("unchecked")
        T value = (T) object.get(key);
        Assert.assertEquals("JSON contains wrong value for attribute:" + key,
                expected, value);
    }
}
