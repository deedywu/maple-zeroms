package server.life;

import server.maps.AbstractAnimatedMapleMapObject;

/**
 *
 * @author zjj
 */
public abstract class AbstractLoadedMapleLife extends AbstractAnimatedMapleMapObject {

    private final int id;
    private int f;
    private boolean hide;
    private int fh, originFh;
    private int cy;
    private int rx0;
    private int rx1;
    private String ctype;
    private int mtime;

    /**
     *
     * @param id
     */
    public AbstractLoadedMapleLife(int id) {
        this.id = id;
    }

    /**
     *
     * @param life
     */
    public AbstractLoadedMapleLife(AbstractLoadedMapleLife life) {
        this(life.getId());
        this.f = life.f;
        this.hide = life.hide;
        this.fh = life.fh;
        this.originFh = life.fh;
        this.cy = life.cy;
        this.rx0 = life.rx0;
        this.rx1 = life.rx1;
        this.ctype = life.ctype;
        this.mtime = life.mtime;
    }

    /**
     *
     * @return
     */
    public int getF() {
        return f;
    }

    /**
     *
     * @param f
     */
    public void setF(int f) {
        this.f = f;
    }

    /**
     *
     * @return
     */
    public boolean isHidden() {
        return hide;
    }

    /**
     *
     * @param hide
     */
    public void setHide(boolean hide) {
        this.hide = hide;
    }

    /**
     *
     * @return
     */
    public int originFh() {
        return originFh;
    }

    /**
     *
     * @return
     */
    public int getFh() {
        return fh;
    }

    /**
     *
     * @param fh
     */
    public void setFh(int fh) {
        this.fh = fh;
    }

    /**
     *
     * @return
     */
    public int getCy() {
        return cy;
    }

    /**
     *
     * @param cy
     */
    public void setCy(int cy) {
        this.cy = cy;
    }

    /**
     *
     * @return
     */
    public int getRx0() {
        return rx0;
    }

    /**
     *
     * @param rx0
     */
    public void setRx0(int rx0) {
        this.rx0 = rx0;
    }

    /**
     *
     * @return
     */
    public int getRx1() {
        return rx1;
    }

    /**
     *
     * @param rx1
     */
    public void setRx1(int rx1) {
        this.rx1 = rx1;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public int getMTime() {
        return mtime;
    }

    /**
     *
     * @param mtime
     */
    public void setMTime(int mtime) {
        this.mtime = mtime;
    }

    /**
     *
     * @return
     */
    public String getCType() {
        return ctype;
    }

    /**
     *
     * @param type
     */
    public void setCType(String type) {
        this.ctype = type;
    }
}
