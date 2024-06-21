package FSPackage;
import FSPackage.FSExceptions.*;

public class FileSystem {
    private FSDirectory root;
    private FSDirectory current;
    private int capacity;

    public FileSystem(int capacity) {
        this.capacity = capacity;
        this.root = new FSDirectory("root");
        this.current = root;
    }

    public int getFreeSpace() {
        return capacity - root.getSize();
    }

    public String getCurrentDirectoryPath(){
        return current.getPath();
    }

    public void listObjectsFromCurrentDir(){
        current.listChildren();
    }

    public void changeDirectory(String path) {
        if (path.equals("..")) {
            if (current.getParent() != null) {
                current = (FSDirectory) current.getParent();
            }
        } else if (path.startsWith("../")) {
            String[] parts = path.split("/");
            FSDirectory temp = current;

            for (String part : parts) {
                if (part.equals("..")) {
                    if (temp.getParent() != null) {
                        temp = (FSDirectory) temp.getParent();
                    }
                } else if (!part.isEmpty()) {
                    FSObject next = temp.getChild(part);
                    if (next != null && next instanceof FSDirectory) {
                        temp = (FSDirectory) next;
                    } else {
                        System.out.println("Invalid path: " + path);
                        return;
                    }
                }
            }

            current = temp;
        } else {
            FSObject next = current.getChild(path);
            if (next != null && next instanceof FSDirectory) {
                current = (FSDirectory) next;
            } else {
                System.out.println("Invalid path: " + path);
            }
        }
    }

    public void findInCurrentDirectory(String nameToFind) {
        current.findRecursive(nameToFind);
    }

    public void showObjectStat(String name){
        current.showObjectInfo(name);
    }

    public void makeDir(String name, boolean admin){
        current.makeDirectory(name, admin);
    }

    public void makeF(String fullNameWithExtension, boolean admin) throws NoExtensionException{
        current.makeFile(fullNameWithExtension, admin);
    }

    public void makeFWithContent(String fullNameWithExtension, String content, boolean admin) throws NoExtensionException, InsufficientStorageException{
        if(content.length() > getFreeSpace()){
            throw new InsufficientStorageException("Not enough free space !");
        } else {
            current.makeFileWithContent(fullNameWithExtension, content, admin);
        }
    }

    public void removeDir(String name, boolean admin) throws DirNotEmptyException {
        current.removeDirectory(name, admin);
    }

    public void copy(String name, String destination) throws InsufficientStorageException {
        FSObject sourceObject = current.getChild(name);
        FSObject destinationObject = current.getChild(destination);

        if (sourceObject == null) {
            System.out.println("Source object does not exist: " + name);
            return;
        }

        if (destinationObject == null || !(destinationObject instanceof FSDirectory)) {
            System.out.println("Destination directory does not exist or is not a directory: " + destination);
            return;
        }

        if(sourceObject.getSize() > getFreeSpace()){
            throw new InsufficientStorageException("Not enough free space !");
        }

        try {
            if (sourceObject instanceof FSDirectory) {
                ((FSDirectory) sourceObject).copyDir((FSDirectory) destinationObject, false);
                System.out.println("Directory copied successfully.");
            } else if (sourceObject instanceof FSFile) {
                current.copyFile(name, (FSDirectory) destinationObject, false);
            } else {
                System.out.println("Source is neither a directory nor a file: " + name);
            }
        } catch (NoExtensionException e) {
            System.out.println("Error copying object: " + e.getMessage());
        }
    }

    public void move(String name, String destination){
        FSObject sourceObject = current.getChild(name);
        FSObject destinationObject = current.getChild(destination);

        if (sourceObject == null) {
            System.out.println("Source object does not exist: " + name);
            return;
        }

        if (destinationObject == null || !(destinationObject instanceof FSDirectory)) {
            System.out.println("Destination directory does not exist or is not a directory: " + destination);
            return;
        }

        try {
            if (sourceObject instanceof FSDirectory) {
                current.moveDir((FSDirectory) sourceObject, (FSDirectory) destinationObject, false);
                System.out.println("Directory moved successfully.");
            } else if (sourceObject instanceof FSFile) {
                current.moveFile(name, (FSDirectory) destinationObject, false);
            } else {
                System.out.println("Source is neither a directory nor a file: " + name);
            }
        } catch (NoExtensionException e) {
            System.out.println("Error copying object: " + e.getMessage());
        } catch (PermissionException e) {
            System.out.println("Error moving object: " + e.getMessage());
        }
    }


    public void chmod(String name, String permissions, boolean admin) throws UnsupportedValueException {
        String writeStr = permissions.substring(0,1);
        String deleteStr = permissions.substring(1,2);
        String readStr = permissions.substring(2,3);

        boolean write = customParse(writeStr);
        boolean delete = customParse(deleteStr);
        boolean read = customParse(readStr);

        current.changePermissions(name, write, delete, read, admin);
    }

    private boolean customParse(String stringValue) throws UnsupportedValueException{
        if(stringValue.equals("0")) { return false; }
        else if(stringValue.equals("1")) { return true; }
        else { throw new UnsupportedValueException("Value can only be 0 or 1 !"); }
    }

    public void cat(String nameWithExtension, boolean admin) throws PermissionException{
        current.showFileData(nameWithExtension, admin);
    }

    public void removeF(String nameWithExtension, boolean admin) throws PermissionException {
        current.removeFile(nameWithExtension, admin);
    }

    public void pwd(){
        System.out.println("/" + current.getPath() + "/");
    }
}