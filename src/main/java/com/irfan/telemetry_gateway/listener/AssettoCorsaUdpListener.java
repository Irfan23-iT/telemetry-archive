package com.irfan.telemetry_gateway.listener;

import com.irfan.telemetry_gateway.model.CarTelemetry;
import com.irfan.telemetry_gateway.service.InfluxTelemetryPublisher;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.springframework.stereotype.Service;

@Service
public class AssettoCorsaUdpListener {

    private static final int ASSETTO_CORSA_PORT = 9996;
    private static final int BUFFER_SIZE = 1024;

    private final InfluxTelemetryPublisher publisher;

    private DatagramSocket socket;
    private Thread listenerThread;
    private InetAddress gameAddress;

    public AssettoCorsaUdpListener(InfluxTelemetryPublisher publisher) {
        this.publisher = publisher;
    }

    @PostConstruct
    public void start() {
        try {
            gameAddress = InetAddress.getByName("127.0.0.1");
            socket = new DatagramSocket(null);
            socket.bind(new InetSocketAddress(0));
            socket.setSoTimeout(2000);

            System.out.println("[NETWORK] UDP socket opened on local port " + socket.getLocalPort());
            sendHandshake();

            listenerThread = new Thread(this::listenForever, "assetto-corsa-udp-listener");
            listenerThread.setDaemon(true);
            listenerThread.start();
        } catch (Exception startupException) {
            throw new RuntimeException("Failed to start Assetto Corsa UDP listener", startupException);
        }
    }

    private void listenForever() {
        System.out.println("[NETWORK] Listener loop started");
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                System.out.println("[NETWORK] Waiting for telemetry packet...");
                socket.receive(packet);
                System.out.println("[NETWORK] Packet received. bytes=" + packet.getLength());

                if (packet.getLength() < 96) {
                    System.out.println("[ERROR] Packet too short (" + packet.getLength() + " bytes), skipping.");
                    continue;
                }

                ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), 0, packet.getLength())
                        .order(ByteOrder.LITTLE_ENDIAN);

                float speed = buffer.getFloat(8);
                float gas = buffer.getFloat(56);
                float brake = buffer.getFloat(60);
                float gear = (float) buffer.getInt(76);
                float engineRPM = buffer.getFloat(68);
                float steerAngle = buffer.getFloat(72);
                float gVertical = buffer.getFloat(44);
                float gHorizontal = buffer.getFloat(48);
                float gLongitudinal = buffer.getFloat(52);
                float tireSlipFL = buffer.getFloat(156);
                float tireSlipFR = buffer.getFloat(160);
                float tireSlipRL = buffer.getFloat(164);
                float tireSlipRR = buffer.getFloat(168);

                CarTelemetry telemetry = new CarTelemetry(
                        speed,
                        gas,
                        brake,
                        gear,
                        engineRPM,
                        steerAngle,
                        tireSlipFL,
                        tireSlipFR,
                        tireSlipRL,
                        tireSlipRR,
                        gHorizontal,
                        gVertical,
                        gLongitudinal);

                publisher.publish(telemetry);
                System.out.println("[DATA] Speed: " + speed + " | Gear: " + gear + " | G-Lat: " + gHorizontal);
            } catch (SocketTimeoutException timeoutException) {
                System.out.println("[ERROR] Game silent, retrying...");
                try {
                    sendHandshake();
                } catch (Exception handshakeException) {
                    System.out.println("[ERROR] Handshake resend failed: " + handshakeException.getMessage());
                }
            } catch (SocketException socketException) {
                if (socket == null || socket.isClosed()) {
                    System.out.println("[NETWORK] Socket closed, listener exiting.");
                    return;
                }
                System.out.println("[ERROR] Socket exception: " + socketException.getMessage());
            } catch (Exception receiveException) {
                System.out.println("[ERROR] Listener exception: " + receiveException.getMessage());
                receiveException.printStackTrace();
            }
        }
    }

    private void sendHandshake() throws Exception {
        byte[] handshake = ByteBuffer.allocate(12)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(1)
                .putInt(1)
                .putInt(1)
                .array();

        System.out.println("[NETWORK] Sending handshake...");
        DatagramPacket handshakePacket = new DatagramPacket(handshake, handshake.length, gameAddress, ASSETTO_CORSA_PORT);
        socket.send(handshakePacket);
        System.out.println("[NETWORK] Handshake sent to 127.0.0.1:" + ASSETTO_CORSA_PORT);
    }

    @PreDestroy
    public void stop() {
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
