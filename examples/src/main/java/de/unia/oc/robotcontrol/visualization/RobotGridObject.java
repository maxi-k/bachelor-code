/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import de.unia.oc.robotcontrol.data.RobotDrivingCommand;
import de.unia.oc.robotcontrol.util.Tuple;

import java.awt.*;

public class RobotGridObject extends GridObject {

    private RobotDrivingCommand command;
    private GridDirection rotation;

    public RobotGridObject() {
        rotation = GridDirection.UP;
        command = RobotDrivingCommand.STOP;
    }

    @Override
    public void draw(Graphics g, GridObjectDrawContext context) {
        Color c = g.getColor();
        g.setColor(getColor());

        int padding = g.getFontMetrics().getHeight();
        int radius = context.getWidth() - padding;

        // Draw a robot body
        g.fillOval(
                context.getXMin(getX()) + padding,
                context.getYMin(getY()) + padding,
                context.getWidth() - padding * 2,
                context.getHeight() - padding * 2
        );

        g.setColor(Color.black);
        // Draw the current command in the center
        int centerx = context.getXMin(getX()) + context.getWidth() / 2;
        int centery = context.getYMin(getY()) + context.getHeight() / 2;
        g.drawString(String.valueOf(command.getIdentifier()), centerx, centery);

        // Draw a rectangle where the front of the robot is
        drawFront(g, context, centerx, centery, padding);

        g.setColor(c);
    }

    private void drawFront(Graphics g, GridObjectDrawContext c, int centerx, int centery, int padding) {
        int offsetx = 0, offsety = 0;
        switch(this.rotation) {
            case UP:
                offsety = -(c.getHeight() / 2 - padding);
                break;
            case RIGHT:
                offsetx = c.getWidth() / 2 - padding;
                break;
            case DOWN:
                offsety = c.getHeight() / 2 - padding;
                break;
            case LEFT:
                offsetx = -(c.getWidth() / 2 - padding);
                break;
        }

        int size = padding / 2;
        g.fillRect(
                centerx + offsetx - size/2,
                centery + offsety - size/2,
                size,
                size
        );
    }

    public synchronized void setRotation(GridDirection rotation) {
        this.rotation = rotation;
    }

    public synchronized GridDirection getRotation() {
        return rotation;
    }

    public synchronized void setCommand(RobotDrivingCommand cmd) {
        this.command = cmd;
    }

    public synchronized RobotDrivingCommand getCommand() {
        return command;
    }

    public void updateFromCommand(RobotDrivingCommand cmd) {
        setCommand(cmd);
        setRotation(commandToRotation(cmd));
    }

    private GridDirection commandToRotation(RobotDrivingCommand cmd) {
        switch(cmd) {
            case ROTATE:
                return getRotation().cycle();
            default:
                return getRotation();
        }
    }


    public Tuple<Integer, Integer> getNextXY() {
        if (command == RobotDrivingCommand.ROTATE || command == RobotDrivingCommand.STOP) {
            return Tuple.create(getX(), getY());
        }
        switch(foldCommandDirection()) {
            case UP: return Tuple.create(getX(), getY() - 1);
            case RIGHT: return Tuple.create(getX() + 1, getY());
            case DOWN: return Tuple.create(getX(), getY() + 1);
            case LEFT: return Tuple.create(getX() - 1, getY());
            default: return Tuple.create(getX(), getY());
        }
    }

    private GridDirection foldCommandDirection() {
        switch(this.command) {
            case RIGHT:
                return this.getRotation().cycle();
            case LEFT:
                return this.getRotation().cycleCC();
            case FRONT:
            default:
                return this.getRotation();
        }
    }

    @Override
    protected Color getColor() {
        return Color.CYAN.brighter();
    }

}
