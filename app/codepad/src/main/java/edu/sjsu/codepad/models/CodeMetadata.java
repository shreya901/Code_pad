/**
 * SJSU CS 218 Fall 2019 TEAM-5
 */

package edu.sjsu.codepad.models;

public class CodeMetadata {
    int id;
    String type;
    String filename;

    public CodeMetadata() {
    }

    public CodeMetadata(int id, String type, String filename) {
        this.id = id;
        this.type = type;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
