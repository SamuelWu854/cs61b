package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import static gitlet.Repository.*;
import static gitlet.Utils.join;
import static gitlet.Utils.writeObject;

public class Stage implements Serializable {

    // file path as key and Sha1 as value
    private HashMap<String, String> tracked;

    // added file, file path as key and Sha1 as value
    private HashMap<String, String> added;

    // file path as key
    private HashSet<String> removed;

    public HashMap<String, String> getAdded() {
        return added;
    }

    public HashSet<String> getRemoved() {
        return removed;
    }

    public void add(String fileName){
        File file = join(CWD, fileName);
        String path = file.getPath();

        Blob blob = new Blob(file);
        String blobId = blob.getBlobId();

        // if the file had been saved in commit and had not changed
        if (getHeadCommitByHash().getStoredFile().get(path).equals(blobId)) {
            // added area and removed area should remove it
            if (added.containsKey(path)) {
                added.remove(path);
            }
            removeAdd(path);
            writeObject(Repository.INDEX, this);
        } else {
            added.put(path, blobId);
            removeAdd(path);
            writeObject(Repository.INDEX, this);
        }
    }

    /**
     * return true if the staging area has changed
     * @param file
     * @return
     */
    public boolean remove(File file){
        // get filePath as key
        String path = file.getPath();
        //check both added and tracked
        String blobId = added.remove(path);
        if(blobId != null){
            // staging area has been changed
            return true;
        }
        // tracked
        if (tracked.get(blobId) != null){
            if(file.exists())
                file.delete();
            return removed.add(path);
        }
        return false;
    }



    public void removeAdd(String filePath){
        if(removed.contains(filePath)){
            removed.remove(filePath);
        }
    }

    public boolean isClear(){
        return added.isEmpty() && removed.isEmpty();
    }

    public void clear(){
        added.clear();
        removed.clear();
    }

    public HashMap commit(){
        tracked.putAll(added);
        for(String filePath : removed){
            tracked.remove(filePath);
        }
        return tracked;
    }

    public void save(){
        writeObject(INDEX, this);
    }

}
