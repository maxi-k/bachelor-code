/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino.device;

import de.unia.oc.robotcontrol.coding.Encoding;
import de.unia.oc.robotcontrol.device.LockingDeviceConnector;
import de.unia.oc.robotcontrol.example.arduino.data.RobotDrivingCommand;
import de.unia.oc.robotcontrol.example.arduino.message.ArduinoMessageTypes;
import de.unia.oc.robotcontrol.example.arduino.message.DistanceDataMessage;
import de.unia.oc.robotcontrol.example.arduino.message.SpeedCmdMessage;
import de.unia.oc.robotcontrol.example.arduino.visualization.RobotGridObject;
import de.unia.oc.robotcontrol.example.arduino.visualization.TargetGridObject;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.util.Tuple;
import de.unia.oc.robotcontrol.visualization.*;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static de.unia.oc.robotcontrol.visualization.GridDirection.LEFT;
import static de.unia.oc.robotcontrol.visualization.GridDirection.RIGHT;

/**
 * A device representing a robot simulating itself in a discrete grid.
 * The grid is filled with obstacles and a target which the robot has to
 * find.
 */
public class DiscreteSimulatedRobot extends LockingDeviceConnector<Message, Message> {

    /**
     * The grid in which to simulate the robot
     */
    private final ObjectGrid simulationEnvironment;
    /**
     * The representation of the robot on the grid.
     */
    private final RobotGridObject robot;

    /**
     * The window in which to display the visualization
     */
    private final VisualizingWindow window;

    /**
     * Tracks the overall number of collisions of
     * the robot with obstacles.
     */
    private int collisions = 0;

    /**
     * Callback for passing the current number of collisions the the
     * {@link RuntimeMetrics}
     */
    private final Consumer<Double> collisionMetric;

    public DiscreteSimulatedRobot(Encoding<Message> encoding,
                                  Supplier<Message> updateRequestMessageProvider,
                                  ObjectGrid simulationEnvironment) {
        super(encoding, encoding, updateRequestMessageProvider);
        this.simulationEnvironment = simulationEnvironment;
        this.robot = new RobotGridObject();
        this.window = new VisualizingWindow(simulationEnvironment);
        this.collisionMetric = Metrics.instance().registerCallback("Grid Collisions");

        setupVisualization();
    }

    /**
     * Initialize the visualization by randomly filling the grid with obstacles,
     * a target and the robot
     */
    @RequiresNonNull({"this.simulationEnvironment", "this.robot", "this.window"})
    private void setupVisualization(@UnderInitialization DiscreteSimulatedRobot this) {
        simulationEnvironment.fillPercentage(0.2f, (i) -> new GridObject());
        simulationEnvironment.putRandomly(new TargetGridObject());
        simulationEnvironment.putRandomly(robot);
        SwingUtilities.invokeLater(window::setup);
    }

    /**
     * "Send" a message to the robot. This simulates the data having to be encoded
     * as bytes to be sent to an external device.
     * In reality, this method decodes the bytes right away and acts on the resulting
     * message.
     *
     * @param message the encoded message to act on
     */
    @Override
    protected void pushMessage(byte[] message) {
        Message m = this.inputEncoding.decode(message);
        actOnMessage(m);
        synchronized (this.window) {
            this.window.update();
        }
    }

    /**
     * "Retrieve" resulting bytes from the robot. This simulates the
     * data having to be decoded later, after being sent through a
     * communication protocol to the application.
     *
     * @return a byte array containing an encoded message from the "robot"
     */
    @Override
    protected byte[] retrieveMessage() {
        return this.outputEncoding.encode(getGridDistances());
    }


    /**
     * Act on the given message by updating the robot on the grid.
     * Only reacts if the received message is a {@link SpeedCmdMessage}.
     *
     * @param m the message to act on
     */
    private synchronized void actOnMessage(Message m) {
        if (m.getType() == ArduinoMessageTypes.SPEED_CMD) {
            SpeedCmdMessage cmd = (SpeedCmdMessage) m;
            RobotDrivingCommand command = cmd.getCommand();
            if (command == null) return;
            this.robot.updateFromCommand(command);
            Tuple<Integer, Integer> next = robot.getNextXY();
            try {
                boolean moved = this.simulationEnvironment.move(
                        robot.getX(),
                        robot.getY(),
                        next.first,
                        next.second);
                collisionMetric.accept(moved ? (double) collisions : (double) ++collisions);
            } catch (IllegalArgumentException e) {

                System.err.println("Could not move simulated robot: ");
                e.printStackTrace();
            }
        };
    }

    /**
     * Measures the current distances of the robot to the obstacles on the grid
     * on each side of the robot.
     * @return a {@link DistanceDataMessage} encapsulating the measured distances
     */
    private synchronized DistanceDataMessage getGridDistances() {
        GridDirection robotRot = robot.getRotation();
        return new DistanceDataMessage(
                getDistanceToOther(robotRot),
                getDistanceToOther(robotRot.cycle()),
                getDistanceToOther(robotRot.cycleCC())
        );
    }

    /**
     * Utility function for measuring the distance from the
     * robot to another grid object in the given direction.
     * Handles the edge of the grid like an obstacle.
     *
     * @param dir the direction to measure in
     * @return the distance in grid cells to the next object.
     */
    private int getDistanceToOther(GridDirection dir) {
        int x = robot.getX();
        int y = robot.getY();
        if (simulationEnvironment.isOnEdge(x, y, dir)) return 0;
        GridObject other = simulationEnvironment.getNextObjectInDirection(x, y, dir);
        if (other == null) return Integer.MAX_VALUE;
        return (dir == LEFT || dir == RIGHT) ?
                Math.abs(x - other.getX()) :
                Math.abs(y - other.getY());
    }

    @Override
    public String getDeviceName() {
        return "Discrete Simulated Robot";
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public void terminate() {
        // nothing to terminate
    }
}
