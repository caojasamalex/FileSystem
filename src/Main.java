import java.util.Arrays;
import java.util.Scanner;
import FSPackage.*;

public class Main {
    public static void main(String[] args) {
        FileSystem fs = new FileSystem(10000);
        Scanner scanner = new Scanner(System.in);
        boolean adminPermission;

        while (true) {
            System.out.print("/" + fs.getCurrentDirectoryPath() + "/$ ");
            String commandLine = scanner.nextLine();
            String[] commandParts = commandLine.split(" ");
            String command = commandParts[0];
            adminPermission = false;

            if(command.isEmpty()){
                continue;
            }

            if (command.equals("sudo")) {
                if (commandParts.length < 2) {
                    System.out.println("Usage: sudo <command>");
                    continue;
                }
                command = commandParts[1];
                adminPermission = true;
            }

            try {
                switch (command) {
                    case "help":
                        if(commandParts.length < (adminPermission ? 2 : 1)){ System.out.println("Usage: help"); }
                        else { printHelp(); }
                        break;

                    case "man":
                        if(commandParts.length < (adminPermission ? 3 : 2)){ System.out.println("Usage: man <command>"); }
                        else { printHelpForCommand(adminPermission ? commandParts[2] : commandParts[1]); }
                        break;

                    case "ls":
                        if(commandParts.length < (adminPermission ? 2 : 1)) { System.out.println("Usage: ls"); }
                        else { fs.listObjectsFromCurrentDir(); }
                        break;

                    case "cd":
                        if(commandParts.length < (adminPermission ? 3 : 2)) { System.out.println("Usage: cd <path>"); }
                        else { fs.changeDirectory(adminPermission ? commandParts[2] : commandParts[1]); }
                        break;

                    case "pwd":
                        if (commandParts.length < (adminPermission ? 2 : 1)) { System.out.println("Usage: pwd"); }
                        else { fs.pwd(); }
                        break;

                    case "find":
                        if(commandParts.length < (adminPermission ? 3 : 2)) { System.out.println("Usage: find <file/directory name>"); }
                        else { fs.findInCurrentDirectory(adminPermission ? commandParts[2] : commandParts[1]); }
                        break;

                    case "stat":
                        if(commandParts.length < (adminPermission ? 3 : 2)){ System.out.println("Usage: stat <file/directory name>"); }
                        else { fs.showObjectStat(adminPermission ? commandParts[2] : commandParts[1]); }
                        break;

                    case "cp":
                        if(commandParts.length < (adminPermission ? 4 : 3)){ System.out.println("Usage: cp <source> <destination absolute/ralative path>"); }
                        else { fs.copy((adminPermission ? commandParts[2] : commandParts[1]) , (adminPermission ? commandParts[3] : commandParts[2])); }
                        break;

                    case "mv":
                        if(commandParts.length < (adminPermission ? 3 : 2)){ System.out.println("Usage: mv <source> <destination absolute/relative path>"); }
                        else { fs.move(adminPermission ? commandParts[2] : commandParts[1], adminPermission ? commandParts[3] : commandParts[2]); }
                        break;

                    case "mkdir":
                        if(commandParts.length < (adminPermission ? 3 : 2)){ System.out.println("Usage: mkdir <directory name>"); }
                        else { fs.makeDir(adminPermission ? commandParts[2] : commandParts[1], adminPermission); }
                        break;

                    case "rmdir":
                        if(commandParts.length < (adminPermission ? 3 : 2)){ System.out.println("Usage: rmdir <directory name>"); }
                        else { fs.removeDir(adminPermission ? commandParts[2] : commandParts[1], adminPermission); }
                        break;

                    case "touch":
                        if(commandParts.length < (adminPermission ? 3 : 2)){
                            System.out.println("Usage:\ntouch <file name with extension>");
                            System.out.println("touch <filename with extension> <content>");
                        } else {
                            String fileName = adminPermission ? commandParts[2] : commandParts[1];
                            String content = "";
                            if (commandParts.length > (adminPermission ? 3 : 2)) {
                                content = String.join(" ", Arrays.copyOfRange(commandParts, adminPermission ? 3 : 2, commandParts.length));
                                fs.makeFWithContent(fileName, content, adminPermission);
                            } else {
                                fs.makeF(fileName, adminPermission);
                            }
                        }
                        break;

                    case "rm":
                        if(commandParts.length < (adminPermission ? 3 : 2)){ System.out.println("Usage: rm <file name with extension>"); }
                        else { fs.removeF(adminPermission ? commandParts[2] : commandParts[1], adminPermission); }
                        break;

                    case "cat":
                        if(commandParts.length < (adminPermission ? 3 : 2)){ System.out.println("Usage: cat <file name with extension>"); }
                        else { fs.cat(adminPermission ? commandParts[2] : commandParts[1], adminPermission); }
                        break;

                    //case "edit":
                        //if(commandParts.length < (adminPermission ? 3 : 2)){ System.out.println("Usage: edit <file name with extension>"); }
                        //else {  }
                        //break;

                    case "chmod":
                        if(commandParts.length < (adminPermission ? 3 : 2)){ System.out.println("Usage: chmod <permissions> <file name with extension / directory name>"); }
                        else { fs.chmod((adminPermission ? commandParts[3] : commandParts[2]), (adminPermission ? commandParts[2] : commandParts[1]), adminPermission); }
                        break;

                    case "exit":
                        scanner.close();
                        return;

                    case "clear":
                        clearScreen();
                        break;

                    default:
                        System.out.println("Unknown command: " + command);
                        System.out.println("Type 'help' for a list of available commands.");
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());

            }
        }
    }

    public static void clearScreen() {
        for (int i = 0; i < 50; i++){
            System.out.println("\n");
        }
    }

    public static void printHelp(){
        System.out.println("===============================================================================================");
        System.out.println("List of commands - Type keyword sudo before a command to execute a command as an administrator");
        System.out.println("===============================================================================================");
        System.out.println("\thelp - print following message");
        System.out.println("\tman <command> - show command usage example");
        System.out.println("\tls - list directory content");
        System.out.println("\tcd - change directory");
        System.out.println("\tpwd - print working directory");
        System.out.println("\tfind - find a file/directory path by name");
        System.out.println("\tstat - display information about file/directory that's in the current directory");
        System.out.println("\tcp - copy a file/directory");
        System.out.println("\tmv - move a file/directory");
        System.out.println("\tmkdir - make a directory");
        System.out.println("\trmdir - remove a directory");
        System.out.println("\ttouch - create a file");
        System.out.println("\trm - remove a file");
        System.out.println("\tcat - file's content display");
        //System.out.println("\tedit - edit file's content");
        System.out.println("\tchmod - change file permissions");
        System.out.println("\texit - exit program");
    }

    public static void printHelpForCommand(String command) {
        switch (command) {
            case "ls":
                System.out.println("ls - lists directory contents.");
                System.out.println("Usage: ls");
                break;
            case "cd":
                System.out.println("cd - changes the current directory.");
                System.out.println("Usage: cd <path>");
                break;
            case "pwd":
              System.out.println("pwd - prints the current working directory path.");
              System.out.println("Usage: pwd");
              break;
            case "find":
                System.out.println("find - finds a file or directory by name.");
                System.out.println("Usage: find <file/directory name>");
                break;
            case "stat":
                System.out.println("stat - displays information about a file or directory within current directory.");
                System.out.println("Usage: stat <file/directory name>");
                break;
            case "cp":
                System.out.println("cp - copies a file or directory.");
                System.out.println("Usage: cp <source> <destination absolute/relative path>");
                break;
            case "mv":
                System.out.println("mv - moves or renames a file or directory.");
                System.out.println("Usage: mv <source> <destination absolute/relative path>");
                break;
            case "mkdir":
                System.out.println("mkdir - creates a new directory.");
                System.out.println("Usage: mkdir <directory name>");
                break;
            case "rmdir":
                System.out.println("rmdir - removes an empty directory.");
                System.out.println("Usage: rmdir <directory name>");
                break;
            case "touch":
                System.out.println("touch - creates a new file.");
                System.out.println("Usage: touch <file name>");
                System.out.println("Usage: touch <file name> [content]");
                break;
            case "rm":
                System.out.println("rm - removes a file.");
                System.out.println("Usage: rm <file name>");
                break;
            case "cat":
                System.out.println("cat - displays file content.");
                System.out.println("Usage: cat <file name>");
                break;
            //case "edit":
                //System.out.println("edit - edits file content.);
                //System.out.println("Usage: edit <file name>");
                //break;
            case "chmod":
                System.out.println("chmod - changes file permissions.");
                System.out.println("Usage: chmod <permissions> <file name with extension / directory name>");
                System.out.println("[permissions]:");
                System.out.println("000 - Not writeable; Not deleteable; Not readable");
                System.out.println("001 - Not writeable; Not deleteable; Readable");
                System.out.println("010 - Not writeable; Deleteable; Not readable");
                System.out.println("011 - Not writeable; Deleteable; Readable");
                System.out.println("100 - Writeable; Not deleteable; Not readable");
                System.out.println("101 - Writeable; Not deleteable; Readable");
                System.out.println("110 - Writeable; Deleteable; Not readable");
                System.out.println("111 - Writeable; Deleteable; Readable");
                break;
            case "exit":
                System.out.println("exit - exits the program.");
                System.out.println("Usage: exit");
                break;
            default:
                System.out.println("Unknown command: " + command);
                System.out.println("Type 'help' for a list of available commands.");
        }
    }
}