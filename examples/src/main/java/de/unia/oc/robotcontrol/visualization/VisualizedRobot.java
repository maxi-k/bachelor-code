/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.visualization;

import de.unia.oc.robotcontrol.data.RobotDrivingCommand;
import de.unia.oc.robotcontrol.util.Tuple;

import java.awt.*;

public class VisualizedRobot extends GridObject {

    private RobotDrivingCommand command;
    private int rotation;

    public VisualizedRobot() {
        rotation = 0;
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
            case 0:
                offsety = -(c.getHeight() / 2 - padding);
                break;
            case 1:
                offsetx = c.getWidth() / 2 - padding;
                break;
            case 2:
                offsety = c.getHeight() / 2 - padding;
                break;
            case 3:
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

    public void setRotation(int rotation) {
        this.rotation = Math.abs(rotation % 4);
    }

    public int getRotation() {
        return rotation;
    }

    public void setCommand(RobotDrivingCommand cmd) {
        this.command = cmd;
    }

    public RobotDrivingCommand getCommand() {
        return command;
    }

    public Tuple<Integer, Integer> getNextXY() {
        switch(this.command) {
            case FRONT:
                switch(this.rotation) {
                    case 0: return Tuple.create(getX(), getY() - 1);
                    case 1: return Tuple.create(getX() + 1, getY());
                    case 2: return Tuple.create(getX(), getY() + 1);
                    case 3: return Tuple.create(getX() - 1, getY());
                }
            case LEFT:
                switch(this.rotation) {
                    case 0: return Tuple.create(getX() - 1, getY());
                    case 1: return Tuple.create(getX(), getY() - 1);
                    case 2: return Tuple.create(getX() + 1, getY());
                    case 3: return Tuple.create(getX(), getY() + 1);
                }
            case RIGHT:
                switch(this.rotation) {
                    case 0: return Tuple.create(getX() + 1, getY());
                    case 1: return Tuple.create(getX(), getY() + 1);
                    case 2: return Tuple.create(getX() - 1, getY());
                    case 3: return Tuple.create(getX(), getY() - 1);
                }
            default:
                return Tuple.create(getX(), getY()) ;
        }
    }

    @Override
    protected Color getColor() {
        return Color.CYAN.brighter();
    }

}
