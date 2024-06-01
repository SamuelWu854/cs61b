package gitlet;



import java.io.File;
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
}
