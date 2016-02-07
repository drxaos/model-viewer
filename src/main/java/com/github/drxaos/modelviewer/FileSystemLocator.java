package com.github.drxaos.modelviewer;

import com.jme3.asset.*;

import java.io.*;

public class FileSystemLocator implements AssetLocator {

    File dir;

    public void setRootPath(String rootPath) {
    }

    public AssetInfo locate(AssetManager manager, AssetKey key) {
        String name = key.getName();
        name = name.replace("\\", "/");
        File file = new File(name);
        if (!file.exists() || !file.isFile()) {
            file = new File(dir, name);
        }
        if (file.exists() && file.isFile()) {
            try {
                String ex = file.getCanonicalPath();
                String absolute = file.getAbsolutePath();
                if (dir == null) {
                    dir = file.getParentFile();
                }
                if (!ex.endsWith(absolute)) {
                    throw new AssetNotFoundException("Asset name doesn\'t match requirements.\n\"" + ex + "\" doesn\'t match \"" + absolute + "\"");
                }
            } catch (IOException var7) {
                throw new AssetLoadException("Failed to get file canonical path " + file, var7);
            }

            return new FileSystemLocator.AssetInfoFile(manager, key, file);
        } else {
            return null;
        }
    }

    private static class AssetInfoFile extends AssetInfo {
        private File file;

        public AssetInfoFile(AssetManager manager, AssetKey key, File file) {
            super(manager, key);
            this.file = file;
        }

        public InputStream openStream() {
            try {
                return new FileInputStream(this.file);
            } catch (FileNotFoundException var2) {
                throw new AssetLoadException("Failed to open file: " + this.file, var2);
            }
        }
    }
}
