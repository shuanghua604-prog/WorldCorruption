package com.corruption;

public class CorruptionColors {
    public static final int[][] FLICKER = {
        {0x004499, 0x0066CC, 0x0088FF}, // BLUE
        {0x000000, 0x111111, 0x222222}, // BLACK
        {0x990000, 0xCC0000, 0xFF3333}, // RED
        {0x994400, 0xCC6600, 0xFF8800}, // ORANGE
        {0x666666, 0x888888, 0xAAAAAA}, // GREY
    };

    public static final String[] NAMES = {"BLUE", "BLACK", "RED", "ORANGE", "GREY"};

    public static int getFlickerColor(int type, long worldTime, int chunkX, int chunkZ) {
        if (type < 0 || type >= FLICKER.length) return -1;
        int phase = (int)((worldTime + chunkX * 31 + chunkZ * 17) % 3);
        if (phase < 0) phase += 3;
        return FLICKER[type][phase];
    }

    public static float[] toFloatRGB(int rgb) {
        return new float[] {
            ((rgb >> 16) & 0xFF) / 255f,
            ((rgb >> 8) & 0xFF) / 255f,
            (rgb & 0xFF) / 255f
        };
    }
}