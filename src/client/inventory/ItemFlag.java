package client.inventory;

public enum ItemFlag {

    LOCK(0x01),//锁
    SPIKES(0x02),//防滑
    COLD(0x04),
    UNTRADEABLE(0x08),//不可交易
    KARMA_EQ(0x10),
    KARMA_USE(0x02),
    装备时获得魅力(0x20),
    ANDROID_ACTIVATED(0x40),
    CRAFTED(0x80),
    防爆卷轴(0x100), //防爆卷轴 - 保护物品的魔法盾。在装备物品上使用，可以在使用卷轴失败时防止装备物品损坏，#c仅限1次#。但是使用卷轴成功时，防御效果也会消失，#c强化12星以上的物品无法使用。# \n可以和#c安全之盾、复原之盾#一起使用。
    幸运卷轴(0x200), //幸运日卷轴 - 使接下去使用的卷轴的成功率提高10%。潜能附加卷轴、强化卷轴无效
    宿命剪刀(0x400),
    只能拥有一个(0x800),
    转存吊牌(0x1000),
    保护卷轴(0x2000), //保护卷轴 - 保护物品的魔法盾。在装备物品上使用，可以在使用卷轴失败时防止装备物品#c可升级次数#减少，#c仅限1次#。但是使用卷轴成功时，防御效果也会消失。\n可以和#c安全之盾、复原之盾#一起使用。
    防护卷轴(0x4000), //卷轴防护卷轴 - 卷轴使用失败时，可以保护卷轴不消失的魔法防护卷轴. \n使用在装备道具上时 #c添加一次保护机会#，如果卷轴使用失败时#c使用的卷轴不会消失#。但是,卷轴使用成功时也会消耗保护效果。\n可以和#c保护卷轴,防爆卷轴#一起使用。
    UNK(0x8000);
    private final int i;

    private ItemFlag(int i) {
        this.i = i;
    }

    public final int getValue() {
        return i;
    }

    public final boolean check(int flag) {
        return (flag & i) == i;
    }
}
