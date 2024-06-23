package FSPackage;

import FSPackage.FSExceptions.DirNotEmptyException;
import FSPackage.FSExceptions.NoExtensionException;
import FSPackage.FSExceptions.PermissionException;

import java.util.ArrayList;
import java.util.List;

public class FSDirectory extends FSObject {
    private List<FSObject> children;
    public FSDirectory(String name) {
        super(null, name);
        this.children = new ArrayList<>();
    }

    public FSDirectory(FSObject parent, String name) {
        super(parent, name);
        this.children = new ArrayList<>();
    }

    public List<FSObject> getChildren() {
        return children;
    }

    public FSObject getChild(String name) {
        String temp;
        for (FSObject child : children) {
            temp = child.getName();
            if (child instanceof FSFile) {
                temp += ((FSFile) child).getExtension();
            }

            if(temp.equals(name)) {
                return child;
            }
        }
        return null;
    }

    void listChildren(){
        List<FSDirectory> childrenDirectories = new ArrayList<>();
        List<FSFile> childrenFiles = new ArrayList<>();

        for(FSObject child : this.children){
            if(child instanceof FSDirectory){
                childrenDirectories.add((FSDirectory) child);
            } else if (child instanceof FSFile){
                childrenFiles.add((FSFile) child);
            }
        }

        for(FSDirectory child : childrenDirectories){
            System.out.println(child.getName() + "/\t(" + child.getSize() + "bytes)");
        }

        for(FSFile child : childrenFiles){
            System.out.println(child.getName() + child.getExtension() + "\t(" + child.getSize() + "bytes)");
        }
    }

    void findRecursive(String nameToFind) {
        findRecursiveHelper(this, nameToFind);
    }

    private void findRecursiveHelper(FSDirectory currentDir, String nameToFind) {
        for (FSObject child : currentDir.getChildren()) {
            if (child.getName().equals(nameToFind)) {
                String addition = child instanceof FSDirectory ? "/" : "";
                System.out.println(currentDir.getPath() + "/" + child.getName() + addition);
            }
            if (child instanceof FSDirectory) {
                findRecursiveHelper((FSDirectory) child, nameToFind);
            }
        }
    }

    void showObjectInfo(String name){
        String temp;

        for (FSObject child : getChildren()) {
            temp = child.getName();
            if(child instanceof FSFile){
                temp += ((FSFile) child).getExtension();
            }
            if (temp.equals(name)) {
                System.out.println("Information about " + "'" + temp + "'");
                System.out.println("Full path: /" + child.getPath() + (child instanceof FSDirectory ? "/" : ""));
                System.out.println("Total size: " + child.getSize());
                String type;
                if(child instanceof FSDirectory) { type = "Directory"; }
                else if(child instanceof FSFile) { type = "File"; }
                else { type = "Unknown"; }
                System.out.println("Type: " + type);
                System.out.println("System File: " + (child.isSystemFD() ? "Yes" : "No"));
                System.out.println("Permissions");
                System.out.println("Writeable - " + (child.canWrite() ? "Yes" : "No"));
                System.out.println("Deletable - " + (child.canRead() ? "Yes" : "No"));
                System.out.println("Readable - " + (child.canRead() ? "Yes" : "No"));
            }
        }
    }

    void makeDirectory(String name, boolean admin){
        int addition = 1;
        String newName = name;
        while(childWithNameExists(newName)){
            newName = name + "-" + addition;
            addition++;
        }

        FSDirectory newChild = new FSDirectory(this, newName);
        newChild.setSystem(admin);
        children.add(newChild);
    }

    private boolean childWithNameExists(String name) {
        String temp;

        for (FSObject child : children) {
            temp = child.getName();
            if(child instanceof FSFile){ temp += ((FSFile) child).getExtension(); }
            if (temp.equals(name)) {
                return true;
            }
        }
        return false;
    }

    void makeFile(String name, boolean admin) throws NoExtensionException {
        String startName = name;

        String baseName = startName;
        String extension = "";
        int lastDotIndex = startName.lastIndexOf('.');

        if (lastDotIndex != -1) {
            baseName = startName.substring(0, lastDotIndex);
            extension = startName.substring(lastDotIndex);
        } else {
            throw new NoExtensionException("Extension has not been provided !");
        }

        String fullName = baseName;
        int addition = 1;
        while (fileWithNameAndExtension(fullName, extension)) {
            fullName = baseName + "-" + addition;
            addition++;
        }

        FSFile newChild = new FSFile(this, fullName, extension);
        newChild.setSystem(admin);
        children.add(newChild);
        updateSize(newChild.getSize());
    }

    void makeFileWithContent(String name, String content, boolean admin) throws NoExtensionException {
        String startName = name;

        String baseName = startName;
        String extension = "";
        int lastDotIndex = startName.lastIndexOf('.');

        if (lastDotIndex != -1) {
            baseName = startName.substring(0, lastDotIndex);
            extension = startName.substring(lastDotIndex);
        } else {
            throw new NoExtensionException("Extension has not been provided !");
        }

        String fullName = baseName;
        int addition = 1;
        while (fileWithNameAndExtension(fullName, extension)) {
            fullName = baseName + "-" + addition;
            addition++;
        }

        FSFile newChild = new FSFile(this, fullName, extension, content.getBytes());
        newChild.setSystem(admin);
        children.add(newChild);
    }

    private boolean fileWithNameAndExtension(String name, String extension) {
        for (FSObject child : children) {
            if (child instanceof FSFile) {
                String childNE = child.getName() + ((FSFile) child).getExtension();
                if(childNE.equals(name + extension)) { return true; }
            }
        }
        return false;
    }

    void removeDirectory(String name, boolean admin) throws DirNotEmptyException {
        for(FSObject child : children){
            if(child instanceof FSDirectory && child.getName().equals(name)){
                if(admin || !child.isSystemFD()){
                    if (((FSDirectory) child).getChildren().isEmpty() || admin) {
                        updateSize(-child.getSize());
                        this.children.remove(child);
                    } else {
                        throw new DirNotEmptyException("Directory '" + name + "' is not empty ! - Try using higher privileges.");
                    }
                }
            }
        }
    }

    void changePermissions(String name, boolean write, boolean delete, boolean read, boolean admin){
        String temp;
        for(FSObject child : children){
            temp = child.getName();
            if(child instanceof FSFile) { temp += ((FSFile) child).getExtension(); }
            if(temp.equals(name)){
                if(child.isSystemFD() && admin || !child.isSystemFD()){
                    child.setWrite(write);
                    child.setDelete(delete);
                    child.setRead(read);
                }
            }
        }
    }

    void showFileData(String nameWithExtension, boolean admin) throws PermissionException{
        String temp;
        for(FSObject child : children){
            if(child instanceof FSFile){
                temp = child.getName() + ((FSFile) child).getExtension();
                if(temp.equals(nameWithExtension)){
                    if(child.canRead() || admin ){
                        System.out.println(((FSFile) child).getStringContent());
                    } else {
                        throw new PermissionException("File is not readable or is a system file !");
                    }
                }
            }
        }
    }

    public void copyDir(FSDirectory destination, boolean admin) throws NoExtensionException {
        FSDirectory copy = new FSDirectory(this.getName());
        copy.setSystem(admin);
        destination.addChild(copy);

        for (FSObject child : children) {
            if (child instanceof FSDirectory) {
                ((FSDirectory) child).copyDir(copy, admin);
            } else if (child instanceof FSFile) {
                FSFile file = (FSFile) child;
                copy.addChild(new FSFile(copy, file.getName(), file.getExtension(), file.getContent()));
            }
        }
    }

    public void copyFile(String sourceName, FSDirectory destination, boolean admin) throws NoExtensionException {
        for (FSObject child : children) {
            if (child instanceof FSFile) {
                FSFile file = (FSFile) child;
                String fileName = file.getName() + file.getExtension();
                if (fileName.equals(sourceName)) {
                    FSFile copy = new FSFile(destination, file.getName(), file.getExtension(), file.getContent());
                    copy.setSystem(admin);
                    destination.addChild(copy);
                    System.out.println("File copied successfully.");
                    return;
                }
            }
        }
        System.out.println("File not found: " + sourceName);
    }

    public void moveDir(FSDirectory source, FSDirectory destination, boolean admin) throws NoExtensionException {
        for (FSObject child : children) {
            if (child instanceof FSDirectory) {
                if(child.getName().equals(destination.getName())){
                    if(!child.isSystemFD()||admin) {
                        this.updateSize(-source.getSize());
                        source.setParent(destination);
                        destination.updateSize(source.getSize());
                        destination.addChild(source);
                        this.children.remove(source);
                        return;
                    } else {
                        throw new NoExtensionException("This directory cannot be moved with these privileges !");
                    }
                }
            }
        }
    }

    public void moveFile(String sourceName, FSDirectory destination, boolean admin) throws PermissionException{
        for (FSObject child : children) {
            if (child instanceof FSFile) {
                FSFile file = (FSFile) child;
                String fileName = file.getName() + file.getExtension();
                if (fileName.equals(sourceName)) {
                    if(!file.isSystemFD() || admin) {
                        this.updateSize(-file.getSize());
                        file.setParent(destination);
                        destination.updateSize(file.getSize());
                        destination.addChild(file);
                        this.children.remove(file);
                        return;
                    } else {
                        throw new PermissionException("This file cannot be moved with these privileges !");
                    }
                }
            }
        }
        System.out.println("File not found: " + sourceName);
    }

    private void addChild(FSObject child) {
        children.add(child);
        child.setParent(this);
    }

    void removeFile(String nameWithExtension, boolean admin) throws PermissionException {
        String temp;

        for(FSObject child : children){
            if(child instanceof FSFile){
                if(((FSFile) child).getContent().length == 0 || admin) {
                    temp = ((FSFile) child).getName() + ((FSFile) child).getExtension();
                    if(temp.equals(nameWithExtension)){
                        if((child.canDelete() && !child.isSystemFD()) || admin) {
                            updateSize(-child.getSize());
                            children.remove(child);
                            System.out.println("File '" + child.getName()+ ((FSFile) child).getExtension() + "' deleted successfully !");
                        } else {
                            throw new PermissionException("File is not deleteable or is a system file");
                        }
                    }
                } else {
                    throw new PermissionException("File is not empty. Try using higher privileges to remove it !");
                }
            }
        }
    }

    void changeName(String source, String newName, boolean admin){
        String temp;

        for(FSObject child : children){
            temp = child.getName();
            if(child instanceof FSFile){
                temp += ((FSFile) child).getExtension();
            }

            if(temp.equals(source)){
                if(!child.isSystemFD() || admin){
                    child.setName(newName);
                    System.out.println("Name has been successfully changed !");
                }
            }
        }
    }
}