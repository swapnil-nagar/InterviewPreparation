package LLD.FileSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class ImMemoryFileSystem {
    private final FileComponent root;

    // private classes
    private abstract class FileComponent {
        protected final String name;

        private FileComponent(String name) {
            this.name = name;
        }

        protected abstract List<String> getList();

        protected String getContent() {
            throw new UnsupportedOperationException();
        }

        protected FileComponent getChild(String s) {
            throw new UnsupportedOperationException();
        }

        protected FileComponent add(String s, FileComponent f) {
            throw new UnsupportedOperationException();
        }

        protected void addContent(String data) {
            throw new UnsupportedOperationException();
        }
    }

    private class Folder extends FileComponent {
        private Map<String, FileComponent> dirs;

        private Folder(String name) {
            super(name);
            dirs = new TreeMap<>();
        }

        protected List<String> getList() {
            return new ArrayList<>(dirs.keySet());
        }

        @Override
        protected FileComponent getChild(String s) {
            return dirs.get(s);
        }

        @Override
        protected FileComponent add(String s, FileComponent f) {
            dirs.put(s, f);
            return f;
        }
    }

    private class File extends FileComponent {
        private final StringBuilder content;

        private File(String name) {
            super(name);
            this.content = new StringBuilder();
        }

        protected List<String> getList() {
            return List.of(name);
        }

        @Override
        protected String getContent() {
            return content.toString();
        }

        @Override
        protected void addContent(String data) {
            content.append(data);
        }
    }

    // constructor
    public ImMemoryFileSystem() {
        root = new Folder("");
    }

    // public methods...
    public List<String> ls(String path) {
        return getComponent(path).getList();
    }

    public void mkdir(String path) {
        setComponents(path, false);
    }

    public void addContentToFile(String filePath, String content) {
        setComponents(filePath, true).addContent(content);
    }

    public String readContentFromFile(String filePath) {
        FileComponent node = getComponent(filePath);
        if (node == null) throw new IllegalArgumentException("invalid filepath");
        return node.getContent();
    }

    // private methods...
    private FileComponent getComponent(String path) {
        String[] nodes = path.split("/");
        FileComponent f = root;
        for (int i = 1; i < nodes.length && f != null; i++)
            f = f.getChild(nodes[i]);
        return f;
    }

    private FileComponent setComponents(String path, boolean isFilePath) {
        String[] nodes = path.split("/");
        FileComponent f = root, next;
        for (int i = 1; i < nodes.length; i++, f = next) {
            next = f.getChild(nodes[i]);
            if (next == null) next = f.add(nodes[i], (i == nodes.length-1 && isFilePath) ? new File(nodes[i]) : new Folder(nodes[i]));
        }
        return f;
    }
}
