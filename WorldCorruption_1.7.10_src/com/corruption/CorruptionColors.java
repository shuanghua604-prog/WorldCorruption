package com.corruption;

public class CorruptionColors {

    // 6种腐化类型的闪烁三色 (RGB整数)
    public static final int[][] FLICKER = {
        {0x004499, 0x0066CC, 0x0088FF}, // 0 - 蓝色侵蚀 BLUE
        {0x000000, 0x111111, 0x222222}, // 1 - 黑色虚空 BLACK
        {0x990000, 0xCC0000, 0xFF3333}, // 2 - 红色腐化 RED (实体专用)
        {0x994400, 0xCC6600, 0xFF8800}, // 3 - 橙色地热 ORANGE
        {0x666666, 0x888888, 0xAAAAAA}, // 4 - 灰色死寂 GREY
    };

    // 类型名称
    public static final String[] NAMES = {
        "BLUE", "BLACK", "RED", "ORANGE", "GREY"
    };

    // 获取当前tick的闪烁颜色
    public static int getFlickerColor(int type, long worldTime, int chunkX, int chunkZ) {
        if (type < 0 || type >= FLICKER.length) return -1;
        // 每个区块有独立相位，基于坐标哈希
        int phase = (int)((worldTime + chunkX * 31 + chunkZ * 17) % 3);
        if (phase < 0) phase += 3;
        return FLICKER[type][phase];
    }

    // 将RGB整数拆分为float[3]
    public static float[] toFloatRGB(int rgb) {
        return new float[] {
            ((rgb >> 16) & 0xFF) / 255f,
            ((rgb >> 8) & 0xFF) / 255f,
            (rgb & 0xFF) / 255f
        };
    }
}