package gitlet;



import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The object folder to keep all files */
    public static final File OBJ_DIR = join(GITLET_DIR, "objects");

    /** The dir to save head pointer of all branches */
    public static final File HEAD_DIR = join(GITLET_DIR, "heads");

    /** Head file includes head pointer reference of branch */
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    /** Stage. Stage is actually a index file contains reference of relation between files and blobs */
    public static final File INDEX = join(GITLET_DIR, "INDEX");

    public static final File GLOBAL_LOGS = join(GITLET_DIR, "logs");



    public static void init(){
        // create new .gitlet folder(check if it's the first time)
        if(GITLET_DIR.exists()){
            printandExit("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        OBJ_DIR.mkdir();
        HEAD_DIR.mkdir();
        // set up objects saving folders
        for(int i = 0; i <= 15; i++){
            for(int j = 0; j <= 15; j++){
                String string = Integer.toHexString(i) + Integer.toHexString(j);
                File f = join(OBJ_DIR, string);
                f.mkdir();
            }
        }
        // create the default commit
        Commit master = new Commit();
        String sha1Value = master.initialCommit();
        // write head file
        File masterFile = Utils.join(HEAD_DIR, "master");
        writeContents(masterFile, sha1Value);
        writeContents(HEAD_FILE, "master");
    }


    public static void add(String fileName){
        //1. check whether init had been executed
        initializedCheck();
        //2. check whether the file exists
        if(!join(CWD, fileName).exists()){
            printandExit("File does not exist.");
        }
        //3. add to stage
        //3.1 get index file
        Stage stage = getIndex();
        //3.2 write reference to index file and create blob to save file
        stage.add(fileName);
    }

    public static Stage getIndex(){
        if(!INDEX.exists()){
            return new Stage();
        }
        return readObject(INDEX, Stage.class);
    }

    public static Commit getHeadCommitByHash(){
        File headFileOfBranch = join(HEAD_DIR, readContentsAsString(HEAD_FILE));
        String string = readContentsAsString(headFileOfBranch);
        File fileById = getFileById(string);
        return readObject(fileById, Commit.class);
    }

    public static Commit getCommitById(String id){
        Commit commit = readObject(getFileById(id), Commit.class);
        return commit;
    }


    public static File getHeadFileOfBranch(){
        String string = readContentsAsString(HEAD_FILE);
        File file = join(HEAD_DIR, string);
        return file;
    }


    public static void commit(String msg) {
        initializedCheck();
        if (msg == null || msg.length() == 0){
            printandExit("Please enter a commit message.");
        }
        //get parent
        Commit parent = getHeadCommitByHash();
        //create new commit and save object
        String sha1 = new Commit().commit(msg, parent);
        //renew head file
        File headFileOfBranch = getHeadFileOfBranch();
        writeContents(headFileOfBranch, sha1);
    }

    public static void remove(String fileName){
        initializedCheck();
        File file = join(CWD, fileName);
        Stage stage = getIndex();
        if(stage.remove(file)){
            stage.save();
        }else {
            printandExit("No reason to remove the file.");
        }
    }

    public static void log() {
        initializedCheck();
        Commit headCommit = getHeadCommitByHash();
        headCommit.getLog();
    }

    public static void globalLog() {
        initializedCheck();
        GlobalLogs globalLogs = new GlobalLogs();
        globalLogs.getGlobalLogs();
    }

    public static void find(String commitMsg) {
        initializedCheck();
        GlobalLogs globalLogs = new GlobalLogs();
        globalLogs.find(commitMsg);
    }

    public static void status() {
        initializedCheck();
        branchStatus();
        Stage stage = getIndex();
        stage.status();
    }

    private static void branchStatus() {
        File[] files = HEAD_DIR.listFiles();
        String headName = readContentsAsString(HEAD_FILE);
        Arrays.sort(files);
        System.out.println("=== Branches ===");
        for (File f : files) {
            String s = f.getName();
            if (s.equals(headName)) {
                System.out.println("*" + s);
            } else {
                System.out.println(s);
            }
        }
        System.out.println();
    }

    public static void checkOutFile(String fName) {
        initializedCheck();
        if (!getHeadCommitByHash().checkOutFileName(fName)) {
            printandExit("File does not exist in that commit.");
        }
    }


    public static void changeBranch(String branchName) {
        initializedCheck();
        File branchFile = findBranch(branchName);
        if (branchFile == null){
            printandExit("No such branch exists.");
        }
        if (branchName.equals(getHeadFileOfBranch().getName())){
            System.out.println("No need to checkout the current branch.");
        }
        //1.find all current file
        HashMap<String, String> currentFiles = findAllCurrentFiles();
        //2. check whether they are untracked
        List<String> untracked = findFilesUntracked(currentFiles);
        String branchId = readContentsAsString(branchFile);
        Commit targetCommit = readObject(getFileById(branchId), Commit.class);
        Map<String, String> targetCommitFile = targetCommit.getStoredFile();
        for (String s : untracked){
            String untrackedblobId = currentFiles.get(s);
            if (!untrackedblobId.equals(targetCommitFile.get(s))) {
                //same name with different id means sth changed unsaved
                printandExit("There is an "
                        + "untracked file in the way; delete it, or add and commit it first.");
            }
        }
        deleteCurrentFiles();
        targetCommit.putFilesToCWD();
        writeContents(HEAD_FILE, branchName);
        INDEX.delete();
    }

    private static void deleteCurrentFiles() {
        File[] files = CWD.listFiles(File::isFile);
        for (File file : files){
            restrictedDelete(file);
        }
    }

    public static void checkOutFileFromCommit(String id, String fName) {
        initializedCheck();
        File commitFile = getFileFromShortId(id);
        if (commitFile == null){
            System.out.println("No commit with that id exists.");
        }
        Commit commit = readObject(commitFile, Commit.class);
        if (commit == null){
            System.out.println("No commit with that id exists.");
        }
        if (!getHeadCommitByHash().checkOutFileName(fName)) {
            printandExit("File does not exist in that commit.");
        }
    }

    private static HashMap<String, String> findAllCurrentFiles() {
        //file name as key and blobId as value
        HashMap<String, String> currentFileMap = new HashMap<>();
        File[] currentFiles = CWD.listFiles(File::isFile);
        for (File f :currentFiles){
            Blob blob = new Blob(f);
            currentFileMap.put(f.getName(), blob.getBlobId());
        }
        return currentFileMap;
    }

    private static List<String> findFilesUntracked(HashMap<String, String> currentFiles) {
        //contains the file name of untracked files
        ArrayList<String> untracked = new ArrayList<>();
        Map<String, String> tracked = getHeadCommitByHash().getStoredFile();
        Stage stage = getIndex();
        HashMap<String, String> added = stage.getAdded();
        HashSet<String> removed = stage.getRemoved();
        for (String s : currentFiles.keySet()) {
            if (tracked.containsKey(s)) {
                if (removed.contains(s)) {
                    untracked.add(s);
                }
            } else if (!added.containsKey(s)) {
                untracked.add(s);
            }
        }
        return untracked;
    }

    public static void createBranch(String branchName){
        initializedCheck();
        File newBranchFile = join(HEAD_DIR, branchName);
        if (newBranchFile.exists()){
            printandExit("A branch with that name already exists.");
        }
        writeContents(newBranchFile, readContents(getHeadFileOfBranch()));
    }

    public static void rmBranch(String branchName) {
        File branchHeadFile = getBranchHeadFile(branchName);
        if (!branchHeadFile.exists()){
            printandExit("A branch with that name does not exist.");
        }
        String currentBranch = readContentsAsString(getHeadFileOfBranch());
        if (currentBranch.equals(branchName)){
            printandExit("Cannot remove the current branch.");
        }
        branchHeadFile.delete();
    }

    public static File getBranchHeadFile(String branchName){
        return join(HEAD_DIR, branchName);
    }
}
