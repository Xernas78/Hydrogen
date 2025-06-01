package dev.xernas.hydrogen.ecs.ui.elements;

import dev.xernas.hydrogen.ecs.DrawingBoard;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.utils.Shapes;
import dev.xernas.hydrogen.rendering.material.ColorMaterial;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.shader.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.IntSupplier;

public abstract class CoordinateSystem extends UIEntity {

    private final boolean planeDisplayed;
    private final boolean hasAxis;
    private final boolean hasGrid;

    private final double rangeBeginX;
    private final double rangeEndX;
    private final double rangeBeginY;
    private final double rangeEndY;

    private final Material dotMaterial;
    private final IntSupplier dotRadius;

    private final List<Function<Double, Double>> plots;
    private final List<Dot> dots;

    public CoordinateSystem() {
        this.dots = getDots();
        this.plots = getPlots();
        this.rangeBeginX = getRangeBeginX();
        this.rangeEndX = getRangeEndX();
        this.rangeBeginY = getRangeBeginY();
        this.rangeEndY = getRangeEndY();
        this.dotMaterial = getDotMaterial();
        this.dotRadius = getDotRadius();
        this.planeDisplayed = isPlaneDisplayed();
        this.hasAxis = hasAxis();
        this.hasGrid = hasGrid();

        for (UIEntity child : getPlotsAndDots()) addChildren(child);
    }

    private List<UIEntity> getPlotsAndDots() {
        List<UIEntity> children = new ArrayList<>();
        IntSupplier dotRadius = () -> Math.max(1, this.dotRadius.getAsInt());
        for (Dot dot : dots) {
            UIEntity pointElement = plotDot(dot, dotRadius, dotMaterial);
            children.add(pointElement);
        }
        for (Function<Double, Double> plot : plots) {
            List<UIEntity> dotsOfFunction = plotFunction(plot);
            for (int j = 1; j < dotsOfFunction.size(); j++) {
                SceneEntity lastEntity = dotsOfFunction.get(j - 1);
                SceneEntity currentEntity = dotsOfFunction.get(j);
                if (lastEntity == null || currentEntity == null) continue;
                UIEntity line = DrawingBoard. createLine(lastEntity, currentEntity, () -> dotRadius.getAsInt() * 2, dotsOfFunction.get(j).getMaterial());
                children.add(line);
            }
            children.addAll(dotsOfFunction);
        }

        // Axis lines
        if (hasAxis) {
            UIEntity xAxis = DrawingBoard.createLine(
                    fromCoordinateToScreenX((this::getRangeBeginX)),
                    fromCoordinateToScreenY(() -> 0.0),
                    fromCoordinateToScreenX(this::getRangeEndX),
                    fromCoordinateToScreenY(() -> 0.0),
                    () -> 1,
                    new ColorMaterial(Color.BLACK)
            );
            UIEntity yAxis = DrawingBoard.createLine(
                    fromCoordinateToScreenX(() -> 0.0),
                    fromCoordinateToScreenY(this::getRangeBeginY),
                    fromCoordinateToScreenX(() -> 0.0),
                    fromCoordinateToScreenY(this::getRangeEndY),
                    () -> 1,
                    new ColorMaterial(Color.BLACK)
            );
            children.add(xAxis);
            children.add(yAxis);
        }

        if (hasGrid) {
            for (int i = (int) rangeBeginX; i <= rangeEndX; i++) {
                if (i == 0 && hasAxis) continue; // Skip the axis line
                int finalI = i;
                UIEntity verticalLine = DrawingBoard.createLine(
                        fromCoordinateToScreenX(() -> finalI),
                        fromCoordinateToScreenY(() -> rangeBeginY),
                        fromCoordinateToScreenX(() -> finalI),
                        fromCoordinateToScreenY(() -> rangeEndY),
                        () -> 1,
                        new ColorMaterial(Color.LIGHT_GRAY)
                );
                children.add(verticalLine);
            }
            for (int j = (int) rangeBeginY; j <= rangeEndY; j++) {
                if (j == 0 && hasAxis) continue; // Skip the axis line
                int finalJ = j;
                UIEntity horizontalLine = DrawingBoard.createLine(
                        fromCoordinateToScreenX(() -> rangeBeginX),
                        fromCoordinateToScreenY(() -> finalJ),
                        fromCoordinateToScreenX(() -> rangeEndX),
                        fromCoordinateToScreenY(() -> finalJ),
                        () -> 1,
                        new ColorMaterial(Color.LIGHT_GRAY)
                );
                children.add(horizontalLine);
            }
        }

        return children;
    }

    @Override
    public final String getShader() {
        return planeDisplayed ? super.getShader() : null;
    }

    @Override
    public Material getMaterial() {
        return null;
    }

    public UIEntity plotDot(Dot dot, IntSupplier dotRadius, Material dotMaterial) {
        if (dot.x.getAsDouble() <rangeBeginX || dot.x.getAsDouble() > rangeEndX || dot.y.getAsDouble() < rangeBeginY || dot.y.getAsDouble() > rangeEndY) {
            throw new IllegalArgumentException("Point is out of bounds of the coordinate system.");
        }

        return new DrawingBoard.DrawableEntity(
                Shapes.circle(64),
                dotMaterial != null ? dotMaterial : new ColorMaterial(Color.BLACK),
                () -> fromCoordinateToScreenX(dot.x).getAsInt(),
                () -> fromCoordinateToScreenY(dot.y).getAsInt(),
                dotRadius,
                dotRadius
        );
    }

    public List<UIEntity> plotFunction(Function<Double, Double> function) {
        List<UIEntity> allDotsOfFunction = new ArrayList<>();
        IntSupplier pointRadius = () -> Math.max(1, dotRadius.getAsInt());
        ColorMaterial randomColor = ColorMaterial.randomColor();
        for (double x = rangeBeginX; x <= rangeEndX; x += (rangeEndX - rangeBeginX) / Math.max(1, getPlotResolution())) {
            double y = function.apply(x);
            if (y < rangeBeginY || y > rangeEndY) continue;

            double finalX = x;
            allDotsOfFunction.add(plotDot(new Dot(() -> finalX, () -> y), pointRadius, randomColor));
        }
        return allDotsOfFunction;
    }

    public final IntSupplier fromCoordinateToScreenX(DoubleSupplier coordinateX) {
        double fullRange = rangeEndX - rangeBeginX;
        return () -> (int) (getX().getAsInt() + (coordinateX.getAsDouble() - rangeBeginX) * (getWidth().getAsInt() / fullRange));
    }

    public final IntSupplier fromCoordinateToScreenY(DoubleSupplier coordinateY) {
        double fullRange = rangeEndY - rangeBeginY;
        return () -> (int) (getY().getAsInt() + (-coordinateY.getAsDouble() - rangeBeginY) * (getHeight().getAsInt() / fullRange));
    }

    public boolean isPlaneDisplayed() {
        return false;
    }

    public boolean hasAxis() {
        return true;
    }

    public boolean hasGrid() {
        return false;
    }

    public abstract double getRangeBeginX();
    public abstract double getRangeEndX();

    public abstract double getRangeBeginY();
    public abstract double getRangeEndY();

    public abstract Material getDotMaterial();
    public abstract IntSupplier getDotRadius();

    public abstract int getPlotResolution();
    public abstract List<Function<Double, Double>> getPlots();

    public abstract List<Dot> getDots();

    public record Dot(DoubleSupplier x, DoubleSupplier y) {

    }
}
