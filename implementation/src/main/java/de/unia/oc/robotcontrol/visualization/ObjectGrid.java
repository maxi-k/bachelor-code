/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectGrid implements Visualization<Component> {

    private static final Color GRID_LINES_COLOR = Color.LIGHT_GRAY;

    private final int cellsX;
    private final int cellsY;

    private final Table<Integer, Integer, GridObject> objects;

    public ObjectGrid(int cellsX, int cellsY) {
        this.cellsX = cellsX;
        this.cellsY = cellsY;

        this.objects = HashBasedTable.create();
    }

    @Override
    public void draw(Graphics g, Component c) {
        final int width = c.getWidth();
        final int height = c.getHeight();

        // GridObjectDrawContext context = new C

        float scaleX = width / cellsX;
        float scaleY = height / cellsY;
        drawGrid(g, scaleX, scaleY, height, width);


        GridObjectDrawContext context = new GridObjectDrawContext(scaleX, scaleY);
        synchronized(this.objects) {
            for (GridObject o : objects.values()) {
                o.draw(g, context);
            }
        }
    }

    private void drawGrid(Graphics g, float scaleX, float scaleY, int height, int width) {
        Color c = g.getColor();
        g.setColor(GRID_LINES_COLOR);

        for (int i = 0; i < cellsX; ++i) {
            int x = (int) scaleX * i;
            g.drawLine(x, 0, x, height);
        }

        for (int i = 0; i < cellsY; ++i) {
            int y = (int) scaleY * i;
            g.drawLine(0, y, width, y);
        }

        g.setColor(c);
    }

    public int gridSize() {
        return cellsX * cellsY;
    }

    public int gridSizeX() {
        return cellsX;
    }

    public int gridSizeY() {
        return cellsY;
    }

    public synchronized boolean putAt(int x, int y, GridObject o) {
        checkInRange(x, y);
        checkGridHasSpace(1);
        o.setXY(x, y);
        return this.objects.put(x, y, o) != null;
    }

    public synchronized boolean remove(int x, int y) {
        checkInRange(x, y);
        return this.objects.remove(x, y) != null;
    }

    public synchronized boolean move(int x, int y, int newX, int newY) {
        checkInRange(x, y);
        checkInRange(newX, newY);
        if (!objects.contains(x, y) || objects.contains(newX, newY)) {
            return false;
        }
        GridObject o = this.objects.remove(x, y);
        o.setXY(newX, newY);
        this.objects.put(newX, newY, o);
        return true;
    }

    public synchronized boolean putRandomly(GridObject o) {
        checkGridHasSpace(1);
        int x, y;
        do {
            x = (int) (Math.random() * (this.cellsX - 1));
            y = (int) (Math.random() * (this.cellsY - 1));
        } while (this.objects.contains(x, y));
        return this.putAt(x, y, o);
    }

    public synchronized Collection<GridObject> fillRandomly(int amount, Function<Integer, GridObject> factory) {
        checkGridHasSpace(amount);
        Set<GridObject> result = new HashSet<>(amount);
        for (int i = 0; i < amount; ++i) {
            GridObject o = factory.apply(i);
            putRandomly(o);
            result.add(o);
        }
        return result;
    }

    public synchronized Collection<GridObject> fillPercentage(float percentage, Function<Integer, GridObject> factory) {
        int amount = (int) (percentage * gridSize());
        checkGridHasSpace(amount);
        return fillRandomly(amount, factory);
    }

    private void checkInRange(int x, int y) throws IllegalArgumentException {
        if (x >= cellsX || y >= cellsY) {
            throw new IllegalArgumentException("Tried access or put GridObject outside of grid bounds!");
        }
    }

    private void checkGridHasSpace(int amount) throws IllegalArgumentException {
        if (this.objects.values().size() + amount > gridSize()) {
            throw new IllegalArgumentException("Grid is too small for this many objects!");
        }
    }

}
