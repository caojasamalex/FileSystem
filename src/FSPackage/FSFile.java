package FSPackage;
import FSPackage.FSExceptions.*;

public class FSFile extends FSObject {
    private String extension; // Extension
    private byte[] content;

    public FSFile(String name, String extension) throws NoExtensionException {
        this(null, name, extension, new byte[0]);
    }

    public FSFile(FSObject parent, String name, String extension) throws NoExtensionException {
        this(parent, name, extension, new byte[0]);
    }

    public FSFile(FSObject parent, String name, String extension, byte[] content) throws NoExtensionException {
        super(parent, name, content.length);
        this.extension = extension;
        this.content = content;
    }

    String getExtension() {
        return extension;
    }

    byte[] getContent() {
        return content;
    }

    String getStringContent(){
        return new String(content);
    }
}
