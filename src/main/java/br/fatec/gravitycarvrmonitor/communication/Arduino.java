package br.fatec.gravitycarvrmonitor.communication;

import br.fatec.gravitycarvrmonitor.models.ControlPackage;
import br.fatec.gravitycarvrmonitor.models.GravityCarPackage;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.json.JSONObject;

public final class Arduino implements SerialPortDataListener {

    private static final int QUEUE_SIZE = 64;

    private static final String JSON_LEFT_BRAKE = "LB";
    private static final String JSON_RIGHT_BRAKE = "RB";
    private static final String JSON_STEERING_ANGLE = "SA";

    private static final String JSON_LEFT_MOTOR = "LM";
    private static final String JSON_RIGHT_MOTOR = "RM";
    private static final String JSON_FAN_SPEED = "FA";

    private SerialPort mSerialPort = null;

    private volatile StringBuilder mBuffer = new StringBuilder();

    private BlockingQueue<GravityCarPackage> mReceiveQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);

    private boolean isConnected;

    private OutputStream mOutputStream;

    public boolean isConnected() {
        return isConnected;
    }

    public boolean connect(String port, int baudRate) {

        mSerialPort = SerialPort.getCommPort(port);
        mSerialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
        mSerialPort.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);

        if (mSerialPort.openPort()) {
            mSerialPort.addDataListener(this);
            mOutputStream = mSerialPort.getOutputStream();
            isConnected = true;
        } else {
            disconnect();
            isConnected = false;
        }

        return isConnected;
    }

    public synchronized void disconnect() {
        if (mSerialPort != null) {
            mSerialPort.removeDataListener();
            mSerialPort.closePort();
        }
    }

    public synchronized boolean isEmptyGravityCarPackage() {
        return mReceiveQueue.isEmpty();
    }

    public GravityCarPackage removeGravityCarPackage() throws InterruptedException {
        return mReceiveQueue.take();
    }

    public synchronized GravityCarPackage fromJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        int leftBrake = jsonObject.getInt(JSON_LEFT_BRAKE);
        int rightBrake = jsonObject.getInt(JSON_RIGHT_BRAKE);
        int steeringAngle = jsonObject.getInt(JSON_STEERING_ANGLE);

        return new GravityCarPackage(leftBrake, rightBrake, steeringAngle);
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {

        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
            return;
        }

        byte[] newData = new byte[mSerialPort.bytesAvailable()];
        mSerialPort.readBytes(newData, newData.length);

        String part = new String(newData);
        mBuffer.append(part);

        checkCommandBuffer();
    }

    private void checkCommandBuffer() {
        String commands = mBuffer.toString();
        if (commands.contains("}") && commands.contains("{") && (commands.lastIndexOf("{") < commands.lastIndexOf("}"))) {

            int startIndex = commands.lastIndexOf("{");
            int endIndex = commands.lastIndexOf("}") + 1;

            String command = commands.substring(startIndex, endIndex);
            mBuffer.delete(startIndex, endIndex);

            GravityCarPackage gravityCar = fromJson(command);
            if (gravityCar != null) {
                mReceiveQueue.add(gravityCar);
            }
        }
    }

    public synchronized void write(ControlPackage controlPackage) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(JSON_LEFT_MOTOR, controlPackage.getLeftVibrationMotor());
        jsonObject.put(JSON_RIGHT_MOTOR, controlPackage.getRightVibrationMotor());
        jsonObject.put(JSON_FAN_SPEED, controlPackage.getFanSpeed());

        if (!isConnected())
            return;
        
        try {
            mOutputStream.write(jsonObject.toString().getBytes());
            mOutputStream.flush();
        } catch (IOException ex) {
            System.out.println("[WRITE]" + ex.getMessage());
        }
    }
}
