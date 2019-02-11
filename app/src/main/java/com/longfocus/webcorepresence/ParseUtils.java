package com.longfocus.webcorepresence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.longfocus.webcorepresence.smartapp.StatusCode;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParseUtils {

    private static final Gson GSON;

    static {
        GSON = new GsonBuilder()
                .registerTypeAdapterFactory(new ValidatorAdapterFactory())
                .registerTypeAdapter(StatusCode.class, new StatusCodeDeserializer())
                .create();
    }

    public static Gson getGson() {
        return GSON;
    }

    public static <T> T fromJson(final String json, final Class<T> tClass) {
        return getGson().fromJson(json, tClass);
    }

    public static <T> String toJson(final T source) {
        return getGson().toJson(source);
    }

    public static String jsonCallback(final String callback, final String response) {
        if (response.length() < callback.length()) {
            return response;
        }

        return response.substring(callback.length() + 1, response.length() - 1);
    }

    static class ValidatorAdapterFactory implements TypeAdapterFactory {

        @Override
        public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
            // If the type adapter is a reflective type adapter, we want to modify the implementation using reflection. The
            // trick is to replace the Map object used to lookup the property name. Instead of returning null if the
            // property is not found, we throw a Json exception to terminate the deserialization.
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

            // Check if the type adapter is a reflective, cause this solution only work for reflection.
            if (delegate instanceof ReflectiveTypeAdapterFactory.Adapter) {
                try {
                    // Get reference to the existing boundFields.
                    final Field f = delegate.getClass().getDeclaredField("boundFields");
                    f.setAccessible(true);

                    Map boundFields = (Map) f.get(delegate);

                    // Then replace it with our implementation throwing exception if the value is null.
                    boundFields = new LinkedHashMap(boundFields) {

                        @Override
                        public Object get(final Object key) {
                            final Object value = super.get(key);
                            if (value == null) {
                                throw new JsonParseException("invalid property name: " + key);
                            }
                            return value;
                        }
                    };

                    // Finally, push our custom map back using reflection.
                    f.set(delegate, boundFields);
                } catch (Exception e) {
                    // Should never happen if the implementation doesn't change.
                    throw new IllegalStateException(e);
                }
            }

            return delegate;
        }
    }

    static class StatusCodeDeserializer implements JsonDeserializer<StatusCode> {

        @Override
        public StatusCode deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            for (final StatusCode statusCode : StatusCode.values()) {
                if (statusCode.getCode().equals(json.getAsString()))
                    return statusCode;
            }
            return null;
        }
    }
}
