package server.life;

import java.awt.Point;

/**
 *
 * @author zjj
 */
public class MobAttackInfo {

    private boolean isDeadlyAttack;
    private int mpBurn, mpCon;
    private int diseaseSkill, diseaseLevel;
    public int PADamage,

    /**
     *
     */
    MADamage, 

    /**
     *
     */
    attackAfter, 

    /**
     *
     */
    range = 0;
    public Point lt = null, 

    /**
     *
     */
    rb = null;
    public boolean magic = false, 

    /**
     *
     */
    isElement = false;

    /**
     *
     */
    public MobAttackInfo() {
    }

    /**
     *
     * @param isDeadlyAttack
     */
    public void setDeadlyAttack(boolean isDeadlyAttack) {
        this.isDeadlyAttack = isDeadlyAttack;
    }

    /**
     *
     * @return
     */
    public boolean isDeadlyAttack() {
        return isDeadlyAttack;
    }

    /**
     *
     * @param mpBurn
     */
    public void setMpBurn(int mpBurn) {
        this.mpBurn = mpBurn;
    }

    /**
     *
     * @return
     */
    public int getMpBurn() {
        return mpBurn;
    }

    /**
     *
     * @param diseaseSkill
     */
    public void setDiseaseSkill(int diseaseSkill) {
        this.diseaseSkill = diseaseSkill;
    }

    /**
     *
     * @return
     */
    public int getDiseaseSkill() {
        return diseaseSkill;
    }

    /**
     *
     * @param diseaseLevel
     */
    public void setDiseaseLevel(int diseaseLevel) {
        this.diseaseLevel = diseaseLevel;
    }

    /**
     *
     * @return
     */
    public int getDiseaseLevel() {
        return diseaseLevel;
    }

    /**
     *
     * @param mpCon
     */
    public void setMpCon(int mpCon) {
        this.mpCon = mpCon;
    }

    /**
     *
     * @return
     */
    public int getMpCon() {
        return mpCon;
    }

    /**
     *
     * @return
     */
    public int getRange() {
        int maxX = Math.max(Math.abs(lt == null ? 0 : lt.x), Math.abs(rb == null ? 0 : rb.x));
        int maxY = Math.max(Math.abs(lt == null ? 0 : lt.y), Math.abs(rb == null ? 0 : rb.y));
        return Math.max((maxX * maxX) + (maxY * maxY), range);
    }
}
