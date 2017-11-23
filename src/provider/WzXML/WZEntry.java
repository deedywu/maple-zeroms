package provider.WzXML;

import provider.MapleDataEntity;
import provider.MapleDataEntry;

/**
 *
 * @author zjj
 */
public class WZEntry implements MapleDataEntry {

    private String name;
    private int size;
    private int checksum;
    private int offset;
    private MapleDataEntity parent;

    /**
     *
     * @param name
     * @param size
     * @param checksum
     * @param parent
     */
    public WZEntry(String name, int size, int checksum, MapleDataEntity parent) {
        super();
        this.name = name;
        this.size = size;
        this.checksum = checksum;
        this.parent = parent;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     *
     * @return
     */
    @Override
    public int getChecksum() {
        return checksum;
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
     * @return
     */
    @Override
    public MapleDataEntity getParent() {
        return parent;
    }
}
