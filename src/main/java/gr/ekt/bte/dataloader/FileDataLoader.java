package gr.ekt.bte.dataloader;

import gr.ekt.bte.core.DataLoader;

public abstract class FileDataLoader implements DataLoader {
    protected String filename;

    public FileDataLoader(String filename) {
        this.filename = filename;
    }
}
