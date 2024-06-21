package FSPackage;

abstract class FSObject {
    private String name;
    private int size; // Size in bytes
    private FSObject parent;
    private boolean isSystem;
    private boolean write, delete, read;

    public FSObject(FSObject parent, String name){
        this.name = name;
        this.size = 0;
        this.parent = parent;
        if ( parent != null) {
            this.isSystem = parent.isSystemFD();
            this.delete = parent.canDelete();
            this.write = parent.canWrite();
            this.read = parent.canRead();
        } else {
            this.isSystem = false;
            this.delete = true;
            this.write = true;
            this.read = true;
        }
    }

    public FSObject(FSObject parent, String name, int size){
        this.name = name;
        this.size = size;
        this.parent = parent;
        if ( parent != null) {
            this.isSystem = parent.isSystemFD();
            this.delete = parent.canDelete();
            this.write = parent.canWrite();
            this.read = parent.canRead();
        } else {
            this.isSystem = false;
            this.delete = true;
            this.write = true;
            this.read = true;
        }

        updateParentSize(this.size); // Add size to the parent
    }

    public void setSize(int size) { this.size = size; }
    public int getSize() { return size; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setParent(FSObject parent) { this.parent = parent; }
    public FSObject getParent() { return parent; }

    public void setDelete(boolean delete) { this.delete = delete;}
    public void setWrite(boolean write) { this.write = write; }
    public void setRead(boolean read) { this.read = read; }
    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean canDelete() {
        return delete;
    }
    public boolean canWrite() {
        return write;
    }

    public boolean canRead() {
        return read;
    }

    public boolean isSystemFD() {
        return isSystem;
    }

    public void updateSize(int sizeChange) {
        this.size += sizeChange;
        updateParentSize(sizeChange);
    }

    private void updateParentSize(int sizeChange) {
        if (this.parent != null) {
            this.parent.updateSize(sizeChange);
        }
    }

    String getPath() {
        if (getParent() == null) {
            return this.getName();
        } else {
            return getParent().getPath() + "/" + getName();
        }
    }
}
