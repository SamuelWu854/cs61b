package gitlet;

import static gitlet.Utils.printandExit;
import static gitlet.Utils.validateNumArgs;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Samuel
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0 || args == null) {
            printandExit("Must have at least one argument");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(firstArg, args, 1);
                Repository.init();
                break;
            case "add":
                validateNumArgs(firstArg, args, 2);
                Repository.add(args[2]);
                break;
            case "commit":
                validateNumArgs(firstArg, args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                validateNumArgs(firstArg,args, 2);
                Repository.remove(args[1]);
                break;
            case "log":
                validateNumArgs(firstArg, args, 1);
                Repository.log();
                break;
            case "global-log":
                validateNumArgs(firstArg, args, 1);
                Repository.globalLog();
                break;
            case "find":
                validateNumArgs(firstArg, args, 2);
                Repository.find(args[1]);
                break;
            // TODO: FILL THE REST IN
        }
    }
}
