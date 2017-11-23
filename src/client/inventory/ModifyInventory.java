package client.inventory;

import constants.GameConstants;

/**
 *
 * @author zjj
 */
public class ModifyInventory {

    /**
     *
     */
    public static class Types {

        /**
         *
         */
        public static final int ADD = 0;

        /**
         *
         */
        public static final int UPDATE = 1;

        /**
         *
         */
        public static final int MOVE = 2;

        /**
         *
         */
        public static final int REMOVE = 3;
    }

    private final int mode;
    private IItem item;
    private short oldPos;

    /**
     *
     * @param mode
     * @param item
     */
    public ModifyInventory(final int mode, final IItem item) {
        this.mode = mode;
        this.item = item.copy();
    }

    /**
     *
     * @param mode
     * @param item
     * @param oldPos
     */
    public ModifyInventory(final int mode, final IItem item, final short oldPos) {
        this.mode = mode;
        this.item = item.copy();
        this.oldPos = oldPos;
    }

    /**
     *
     * @return
     */
    public final int getMode() {
        return mode;
    }

    /**
     *
     * @return
     */
    public final int getInventoryType() {
        return GameConstants.getInventoryType(item.getItemId()).getType();
    }

    /**
     *
     * @return
     */
    public final short getPosition() {
        return item.getPosition();
    }

    /**
     *
     * @return
     */
    public final short getOldPosition() {
        return oldPos;
    }

    /**
     *
     * @return
     */
    public final short getQuantity() {
        return item.getQuantity();
    }

    /**
     *
     * @return
     */
    public final IItem getItem() {
        return item;
    }

    /**
     *
     */
    public final void clear() {
        this.item = null;
    }
}
