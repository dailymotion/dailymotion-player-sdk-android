package com.dailymotion.sdk.httprequest;

import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

public abstract class JsonRequest<T> extends HttpRequest<T> {
    protected Gson mGson;
    private final Type mClazz;
    /**
     * Translate from java identifier to json identifier
     *
     *   tile$event --> tile.event
     */
    public static final FieldNamingStrategy sNamingStrategy = new FieldNamingStrategy() {
        /**
         * A custom field naming policy because sometimes API will return fields with a '.' inside
         *
         * owner$avatar_240_url -> owner.avatar_240_url
         */
        @Override
        public String translateName(Field f) {
            return javaToJson(f.getName());
        }
    };

    public JsonRequest(int method, String endpoint, Type clazz) {
        super(method, endpoint);
        mClazz = clazz;
        mGson = new GsonBuilder().setFieldNamingStrategy(sNamingStrategy).create();
    }

    @Override
    protected T parseResponse(byte[] data, Map<String, String> headers) throws Exception {
        String json = new String(
                data, HttpHeaderParser.parseCharset(headers));
        Object result = mGson.fromJson(json, mClazz);
        return (T)result;
    }

    public static String javaToJson(String name) {
        StringBuilder translation = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char character = name.charAt(i);
            if (character == '$') {
                character = '.';
            }
            translation.append(character);
        }

        return translation.toString().toLowerCase();
    }
}
