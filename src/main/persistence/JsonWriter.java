package persistence;

import model.Board;
import model.Operative;
import model.Spymaster;
import org.json.JSONObject;


import java.io.*;
import java.util.Iterator;

// Structure for the persistence functionality is inspired by the UBC: CPSC 210 Software Construction WorkRoomApp

// Represents a writer that writes JSON representation of workroom to file
public class JsonWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String destination;

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file cannot be opened for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // EFFECTS: returns JSON representation of board
    public JSONObject write(Board bd) {
        JSONObject json = bd.toJson();
        return json;
    }

    // EFFECTS: returns JSON representation of spymaster
    public JSONObject write(Spymaster sm) {
        JSONObject json = sm.toJson();
        return json;
    }

    // EFFECTS: returns JSON representation of operative
    public JSONObject write(Operative op) {
        JSONObject json = op.toJson();
        return json;
    }

    // EFFECTS: saves the JSON object to file
    public void write(JSONObject jo) {
        saveToFile(jo.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }

    // Inspired by -
    // SOURCE: https://stackoverflow.com/questions/19566081/what-is-the-best-way-to-combine-merge-2-jsonobjects
    // EFFECTS: Merges two JSONObjects so there is only one top-level object
    private JSONObject mergeJson(JSONObject obj1, JSONObject obj2) {
        JSONObject mergedObject = new JSONObject();

        Iterator<String> iter1 = obj1.keys();
        Iterator<String> iter2 = obj2.keys();


        while (iter1.hasNext()) {
            String key = iter1.next();
            mergedObject.put(key, obj1.get(key));
        }
        while (iter2.hasNext()) {
            String key = iter2.next();
            mergedObject.put(key, obj2.get(key));
        }

        return mergedObject;
    }

    // EFFECTS: Returns a singular merged JSONObject from all the inputs
    public JSONObject getMergedObject(JSONObject gameBoard, JSONObject redSpymaster, JSONObject blueSpymaster,
                                      JSONObject redOperative, JSONObject blueOperative) {
        JSONObject mergedObject = new JSONObject();
        mergedObject = mergeJson(gameBoard, redSpymaster);
        mergedObject = mergeJson(mergedObject, blueSpymaster);
        mergedObject = mergeJson(mergedObject, redOperative);
        mergedObject = mergeJson(mergedObject, blueOperative);

        return mergedObject;
    }
}
