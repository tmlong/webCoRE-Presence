package com.longfocus.webcorepresence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
import com.longfocus.webcorepresence.smartapp.response.StatusCode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

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

    @Nullable
    public static <T> T fromJson(@Nullable final String json, @NonNull final Class<T> tClass) {
        return getGson().fromJson(json, tClass);
    }

    @NonNull
    public static <T> String toJson(@Nullable final T source) {
        return getGson().toJson(source);
    }

    @NonNull
    public static String jsonCallback(@NonNull final String callback, @NonNull final String response) {
        if (response.length() < callback.length()) {
            return response;
        }

        return response.substring(callback.length() + 1, response.length() - 1);
    }

    @Nullable
    public static String getData(@NonNull final Response response) throws IOException {
        final ResponseBody body = response.body();
        final BufferedSource source = body != null ? body.source() : null;

        if (source != null) {
            source.request(Integer.MAX_VALUE);
            return source.buffer().snapshot().utf8();
        }

        return null;
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
