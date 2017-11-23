package provider.WzXML;

import provider.MapleDataEntity;
import provider.MapleDataFileEntry;

/**
 *
 * @author zjj
 */
public class WZFileEntry extends WZEntry implements MapleDataFileEntry {

    private int offset;

    /**
     *
     * @param name
     * @param size
     * @param checksum
     * @param parent
     */
    public WZFileEntry(String name, int size, int checksum, MapleDataEntity parent) {
        super(name, size, checksum, parent);
    }

    /**
     *
     * @return
     */
    @Override
    public int getOffset() {
        return offset;
    }

    /**
     *
     * @param offset
     */
    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
