package org.example;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

class GsonHelper {

    public static final JsonDeserializer<ZonedDateTime> ZDT_DESERIALIZER = new JsonDeserializer<ZonedDateTime>() {

        @Override
        public ZonedDateTime deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                // if provided as String - '2011-12-03T10:15:30+01:00[Europe/Paris]'
                if(jsonPrimitive.isString()){
                    return ZonedDateTime.parse(jsonPrimitive.getAsString(),
                            DateTimeFormatter.ofPattern(format.toPattern()).withZone(ZoneId.systemDefault()));
                }

                // if provided as Long
                if(jsonPrimitive.isNumber()){
                    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(jsonPrimitive.getAsLong()), ZoneId.systemDefault());
                }

            } catch(RuntimeException e){
                throw new JsonParseException("Unable to parse ZonedDateTime", e);
            }
            throw new JsonParseException("Unable to parse ZonedDateTime");
        }
    };

}
