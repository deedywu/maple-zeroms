package provider.WzXML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataEntity;
import provider.MapleDataEntry;
import provider.MapleDataFileEntry;

/**
 *
 * @author zjj
 */
public class WZDirectoryEntry extends WZEntry implements MapleDataDirectoryEntry {

    private List<MapleDataDirectoryEntry> subdirs = new ArrayList<>();
    private List<MapleDataFileEntry> files = new ArrayList<>();
    private Map<String, MapleDataEntry> entries = new HashMap<>();

    /**
     *
     * @param name
     * @param size
     * @param checksum
     * @param parent
     */
    public WZDirectoryEntry(String name, int size, int checksum, MapleDataEntity parent) {
        super(name, size, checksum, parent);
    }

    /**
     *
     */
    public WZDirectoryEntry() {
        super(null, 0, 0, null);
    }

    /**
     *
     * @param dir
     */
    public void addDirectory(MapleDataDirectoryEntry dir) {
        subdirs.add(dir);
        entries.put(dir.getName(), dir);
    }

    /**
     *
     * @param fileEntry
     */
    public void addFile(MapleDataFileEntry fileEntry) {
        files.add(fileEntry);
        entries.put(fileEntry.getName(), fileEntry);
    }

    /**
     *
     * @return
     */
    @Override
    public List<MapleDataDirectoryEntry> getSubdirectories() {
        return Collections.unmodifiableList(subdirs);
    }

    /**
     *
     * @return
     */
    @Override
    public List<MapleDataFileEntry> getFiles() {
        return Collections.unmodifiableList(files);
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public MapleDataEntry getEntry(String name) {
        return entries.get(name);
    }
}
