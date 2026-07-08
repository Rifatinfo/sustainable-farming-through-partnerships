package util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import model.Admin;
import model.Investor;
import model.Monitor;
import model.User;
import model.UserRole;

import java.lang.reflect.Type;

public class UserDeserializer implements JsonDeserializer<User> {

    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        JsonElement roleElem = obj.get("role");
        if (roleElem == null) {
            throw new JsonParseException("Missing 'role' field in user JSON");
        }
        String roleStr = roleElem.getAsString();
        UserRole role;
        try {
            role = UserRole.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Unknown role: " + roleStr);
        }
        switch (role) {
            case ADMIN:
                return context.deserialize(json, Admin.class);
            case MONITOR:
                return context.deserialize(json, Monitor.class);
            case INVESTOR:
                return context.deserialize(json, Investor.class);
            default:
                throw new JsonParseException("Unhandled role: " + role);
        }
    }
}
