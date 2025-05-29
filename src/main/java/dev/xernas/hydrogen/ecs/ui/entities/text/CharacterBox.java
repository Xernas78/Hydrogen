package dev.xernas.hydrogen.ecs.ui.entities.text;

import dev.xernas.format.ttf.GlyphTable;
import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.ui.Fonts;
import dev.xernas.hydrogen.ecs.ui.SceneEntityUI;
import dev.xernas.hydrogen.rendering.material.ColorMaterial;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.shader.Material;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.function.IntSupplier;

public class CharacterBox implements SceneEntityUI {

    private final Character character;
    private final Fonts.Font font;
    private final IntSupplier x;
    private final IntSupplier y;

    public CharacterBox(Character character, IntSupplier x, IntSupplier y, Fonts.Font font) throws PhotonException {
        if (character == null) {
            throw new PhotonException("Character cannot be null");
        }
        if (font == null) {
            throw new PhotonException("Font cannot be null");
        }
        this.character = character;
        this.font = font;
        this.x = x;
        this.y = y;
    }

    @Override
    public Material getMaterial() {
        return new ColorMaterial(Color.WHITE);
    }

    @Override
    public IntSupplier getX() {
        return x;
    }

    @Override
    public IntSupplier getY() {
        return y;
    }

    @Override
    public IntSupplier getWidth() {
        GlyphTable.Glyph glyph = font.format().glyphForChar(character);
        return () -> (int) ((glyph.xMax() - glyph.xMin()) * (font.size() / 100f));
    }

    @Override
    public IntSupplier getHeight() {
        GlyphTable.Glyph glyph = font.format().glyphForChar(character);
        return () -> (int) ((glyph.yMax() - glyph.yMin()) * (font.size() / 100f));
    }

    @Override
    public @NotNull List<Behavior> getBehaviors() {
        return List.of();
    }
}
