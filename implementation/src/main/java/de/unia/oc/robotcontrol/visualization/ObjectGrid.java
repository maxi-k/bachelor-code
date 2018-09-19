/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import de.unia.oc.robotcontrol.util.Tuple;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implementation of a 2D-Grid for displaying Objects.
 * Uses a Guava Table of {@link GridObject} instances as the backing table.
 */
public class ObjectGrid implements Visualization<Component> {

    /**
     * The color to draw the grid lines in
     */
    private static final Color GRID_LINES_COLOR = Color.LIGHT_GRAY;

    /**
     * The number of cells in the x (horizontal) direction
     */
    private final int cellsX;
    /**
     * The number of cells in the y (vertical) direction
     */
    private final int cellsY;

    /**
     * Internal Lock for the {@link #objects} Table and
     * related variables.
     */
    private final Object gridLock = new Object();
    /**
     * The table used to represent the current grid state.
     */
    private final Table<Integer, Integer, GridObject> objects;

    /**
     * Create a new instance of {@link ObjectGrid} with the specified
     * size.
     * @param cellsX the number of horizontal cells
     * @param cellsY the number of vertical cells
     */
    public ObjectGrid(int cellsX, int cellsY) {
        this.cellsX = cellsX;
        this.cellsY = cellsY;

        this.objects = HashBasedTable.create();
    }

    /**
     * {@inheritDoc}
     *
     * Draw the grid in the passed component, passing
     * a {@link GridObjectDrawContext} with the current scale
     * to the {@link GridObject#draw(Graphics, GridObjectDrawContext)} method
     * of each {@link GridObject} contained within, so that it may draw itself.
     *
     * @param g the graphics
     * @param c the grid context for each {@link GridObject}
     */
    @Override
    public void draw(Graphics g, Component c) {
        final int width = c.getWidth();
        final int height = c.getHeight();

        float scaleX = (float) width / cellsX;
        float scaleY = (float) height / cellsY;
        drawGrid(g, scaleX, scaleY, height, width);

        GridObjectDrawContext context = new GridObjectDrawContext(scaleX, scaleY);
        synchronized (this.gridLock) {
            Collection<GridObject> values = this.objects.values();
            for (GridObject o : values) {
                o.draw(g, context);
            }
        }
    }

    /**
     * Draw the grid lines with the given scale and given
     * graphics.
     * @param g the graphics to use for drawing
     * @param scaleX the scale factor (width / cells) for the x direction
     * @param scaleY the scale factor (height / cells) for the y direction
     * @param height the overall height of the window to draw in
     * @param width the overall width of the window to draw in
     */
    private void drawGrid(Graphics g, float scaleX, float scaleY, int height, int width) {
        Color c = g.getColor();
        g.setColor(GRID_LINES_COLOR);

        for (int i = 0; i <= cellsX; ++i) {
            int x = (int) scaleX * i;
            g.drawLine(x, 0, x, height);
        }

        for (int i = 0; i <= cellsY; ++i) {
            int y = (int) scaleY * i;
            g.drawLine(0, y, width, y);
        }

        g.setColor(c);
    }

    /**
     * @return the overall number of cells
     */
    public int gridSize() {
        return cellsX * cellsY;
    }

    /**
     * @return the horizontal width of the grid ("x direction"),
     * as the number of cells.
     */
    @Pure
    public @Positive int gridSizeX() {
        return cellsX;
    }

    /**
     * @return the vertical height of the grid ("y direction"),
     * as the number of cells.
     */
    @Pure
    public @Positive int gridSizeY() {
        return cellsY;
    }

    /**
     * Get the {@link GridObject} stored in {@link #objects} at
     * the specified coordinates.
     * @param x the x coordinate to look up
     * @param y the y coordinate to look up
     * @return the {@link GridObject} instance stored at the given
     * coordinates, or {@code null} if there is none
     */
    public @Nullable GridObject getObjectAt(int x, int y) {
        synchronized (gridLock) {
            return objects.get(x, y);
        }
    }

    /**
     * Get the next {@link GridObject} instance that is in the given direction,
     * beginning at (and excluding) the given x and y coordinates.
     *
     * @param x the x coordinate to start the search from
     * @param y the y coordinate to start the search from
     * @param dir the direction to look into
     * @return an instance of {@link GridObject} from {@link #objects}
     */
    public @Nullable GridObject getNextObjectInDirection(int x, int y, GridDirection dir) {
        Tuple<Integer, Integer> nextCoords = Tuple.create(x, y);
        do {
           nextCoords = getNextCoordsFor(nextCoords, dir);
           @Nullable GridObject obj = nextCoords.<@Nullable GridObject>joinWith(this::getObjectAt);
           if (obj != null) return obj;
        } while (nextCoords.joinWith(this::areCoordsInRange));
        return null;
    }

    /**
     * Returns whether the given coordinates are on the edge of the grid in
     * the given direction, that is, moving a {@link GridObject} in that direction
     * would not be possible.
     *
     * @param x the x coordinate to consider
     * @param y the y coordinate to consider
     * @param dir the direction to use for checking whether the grid ends there
     * @return {@code true} if the grid's edge is in the given direction from
     * the passed coordinates' point of view
     */
    @Pure
    public boolean isOnEdge(int x, int y, GridDirection dir) {
        switch(dir) {
            case UP: return y >= gridSizeY();
            case RIGHT: return x >= gridSizeX();
            case DOWN: return y == 0;
            case LEFT: return x == 0;
            default: return false;
        }
    }

    /**
     * If a grid object moved from the given coordinates in the given direction,
     * the resulting value would be its new coordinates.
     * @param xy the current coordinates as a tuple
     * @param dir the direction to move to
     * @return the new coordinates of a {@link GridObject} if it moved
     * in that direction, as a tuple.
     */
    @Pure
    private Tuple<Integer, Integer> getNextCoordsFor(Tuple<Integer, Integer> xy, GridDirection dir) {
        return getNextCoordsFor(xy.first, xy.second, dir);
    }

    /**
     * If a grid object moved from the given coordinates in the given direction,
     * the resulting value would be its new coordinates.
     * @param x the current x coordinate
     * @param y the current y coordinate
     * @param dir the direction to move to
     * @return the new coordinates of a {@link GridObject} if it moved
     * in that direction, as a tuple.
     */
    @Pure
    private Tuple<Integer, Integer> getNextCoordsFor(int x, int y, GridDirection dir) {
        switch(dir) {
            case UP:
                return Tuple.create(x, y - 1);
            case RIGHT:
                return Tuple.create(x + 1, y);
            case DOWN:
                return Tuple.create(x, y + 1);
            case LEFT:
                return Tuple.create(x - 1, y);
            default:
                return Tuple.create(x, y);
        }
    }

    /**
     * Put the given {@link GridObject} at the given coordinates in the grid
     * by storing it in {@link #objects}. Throws an exception if the coordinates
     * are out of range.
     * @param x the x coordinate to put the given object on to
     * @param y the y coordinate to put the given object on to
     * @param o the instance of {@link GridObject} to put into the grid
     * @return the result of {@link Table#put(Object, Object, Object)}
     */
    public boolean putAt(int x, int y, GridObject o) {
        checkInRange(x, y);
        checkGridHasSpace(1);
        o.setXY(x, y);
        synchronized (gridLock) {
            return this.objects.put(x, y, o) != null;
        }
    }

    /**
     * Remove any grid objects on the given coordinates from the grid
     * @param x the x coordinate to remove objects from
     * @param y the y coordinate to remove objects from
     * @return the rsult of {@link Table#remove(Object, Object)}
     */
    public synchronized boolean remove(int x, int y) {
        checkInRange(x, y);
        synchronized (gridLock) {
            return this.objects.remove(x, y) != null;
        }
    }

    /**
     * Move any {@link GridObject} on the given x and y coordinates
     * to the given new coordinates
     * @param x the x coordinate to move a {@link GridObject} from
     * @param y the y coordinate to move a {@link GridObject} from
     * @param newX the x coordinate to move the {@link GridObject} to
     * @param newY the y coordinate to move the {@link GridObject} to
     * @return whether any object was moved
     */
    public boolean move(int x, int y, int newX, int newY) {
        synchronized (gridLock) {
            if (!objects.contains(x, y) || objects.contains(newX, newY)) {
                return false;
            }
            try {
                checkInRange(newX, newY);
                GridObject o = this.objects.remove(x, y);
                if (o == null) return false;
                o.setXY(newX, newY);
                this.objects.put(newX, newY, o);
            } catch (IllegalArgumentException e) {
                return false;
            }
            return true;
        }
    }

    /**
     * Put th given {@link GridObject} on the grid
     * at a random coordinate.
     * @param o the object to put on the grid
     * @return whether the object was put on th egrid
     */
    public boolean putRandomly(GridObject o) {
        synchronized (gridLock) {
            checkGridHasSpace(1);
            int x, y;
            do {
                x = (int) (Math.random() * this.cellsX);
                y = (int) (Math.random() * this.cellsY);
            } while (this.objects.contains(x, y));
            return this.putAt(x, y, o);
        }
    }

    /**
     * Fill the grid randomly with {@link GridObject} instances generated by the given
     * factory.
     * @param amount the amount of objects to fill the grid with
     * @param factory the factory used for generating new grid objects. the current loop index is passed.
     * @return a collection of the generated grid objects.
     */
    public Collection<GridObject> fillRandomly(@Positive int amount, Function<Integer, GridObject> factory) {
        checkGridHasSpace(amount);
        Set<GridObject> result = new HashSet<>(amount);
        for (int i = 0; i < amount; ++i) {
            GridObject o = factory.apply(i);
            putRandomly(o);
            result.add(o);
        }
        return result;
    }

    /**
     * Same as {@link #fillRandomly(int, Function)}, but given a percentage of the
     * overall grid size {@link #gridSize()} instead of an absolute amount.
     *
     * @param percentage the percentage of the grid to fill with objects
     * @param factory the factory used for generating new grid objects
     * @return a collection of the generated grid objects.
     */
    public Collection<GridObject> fillPercentage(@Positive float percentage, Function<Integer, GridObject> factory) {
        int amount = Math.min((int) (percentage * gridSize()), gridSize());
        checkGridHasSpace(amount);
        return fillRandomly(amount, factory);
    }

    /**
     * Throws an exception if the given coordinates are outside of the
     * range of this grid.
     * @param x the x coordinate to check
     * @param y the y coordinate to check
     * @throws IllegalArgumentException if the given coordinates are
     * negative or larger than {@link #cellsX} and {@link #cellsY}, respectively.
     */
    @Pure
    private void checkInRange(int x, int y) throws IllegalArgumentException {
        if (x >= cellsX || y >= cellsY || x < 0 || y < 0) {
            throw new IllegalArgumentException("Tried access or put GridObject outside of grid bounds!");
        }
    }

    /**
     * Throws an exception of the grid has less free spaces than
     * the value given to the function.
     * @param amount the amount of free space to check
     * @throws IllegalArgumentException if the given amount added
     * to the amount of grid objects present is larger than the overall
     * grid size.
     */
    @Pure
    private void checkGridHasSpace(int amount) throws IllegalArgumentException {
        synchronized (gridLock) {
            if (this.objects.values().size() + amount > gridSize()) {
                throw new IllegalArgumentException("Grid is too small for this many objects!");
            }
        }
    }

    /**
     * Calculates whether the given coordinates are in range of the grid.
     * @param x the x coordinate to check
     * @param y the y coordinate to check
     * @return whehter the given coordinates are negative or larger than
     * {@link #cellsX} and {@link #cellsY}, respectively.
     */
    @Pure
    private boolean areCoordsInRange(int x, int y) {
        return x >= 0 && x <= cellsX && y >= 0 && y <= cellsY;
    }

}
