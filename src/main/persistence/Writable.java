package persistence;

import org.json.JSONObject;

// Structure for the persistence functionality is inspired by the UBC: CPSC 210 Software Construction WorkRoomApp

public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}