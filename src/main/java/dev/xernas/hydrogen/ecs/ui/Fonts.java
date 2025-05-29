package dev.xernas.hydrogen.ecs.ui;

import dev.xernas.format.ttf.TTFFormat;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Fonts {

    private static final Map<String, TTFFormat> fonts = new HashMap<>();

    public static void fillFonts(Map<String, TTFFormat> fonts) {
        Fonts.fonts.putAll(fonts);
    }

    public static Font getFont(String name, int size) {
        return new Font(name, size, fonts.get(name));
    }

    public static Map<String, TTFFormat> getFonts() {
        return fonts;
    }

    public record Font(String name, int size, TTFFormat format) {}
}
