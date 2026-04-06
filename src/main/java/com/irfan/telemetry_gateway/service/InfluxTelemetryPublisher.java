package com.irfan.telemetry_gateway.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.irfan.telemetry_gateway.model.CarTelemetry;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InfluxTelemetryPublisher {

    @Value("${influxdb.url}")
    private String influxUrl;

    @Value("${influxdb.token}")
    private String influxToken;

    @Value("${influxdb.org}")
    private String influxOrg;

    @Value("${influxdb.bucket}")
    private String influxBucket;

    private InfluxDBClient influxDBClient;
    private WriteApiBlocking writeApi;

    @PostConstruct
    public void init() {
        influxDBClient = InfluxDBClientFactory.create(influxUrl, influxToken.toCharArray(), influxOrg, influxBucket);
        writeApi = influxDBClient.getWriteApiBlocking();
    }

    public void publish(CarTelemetry telemetry) {
        Point point = Point.measurement("car_telemetry")
                .addTag("session_type", "practice")
                .addField("speed", telemetry.getSpeed())
                .addField("gas", telemetry.getGas())
                .addField("brake", telemetry.getBrake())
                .addField("engineRPM", telemetry.getEngineRPM())
                .addField("steerAngle", telemetry.getSteerAngle())
                .addField("gear", telemetry.getGear())
                .addField("tireSlipFL", telemetry.getTireSlipFL())
                .addField("tireSlipFR", telemetry.getTireSlipFR())
                .addField("tireSlipRL", telemetry.getTireSlipRL())
                .addField("tireSlipRR", telemetry.getTireSlipRR())
                .addField("gHorizontal", telemetry.getGHorizontal())
                .addField("gVertical", telemetry.getGVertical())
                .addField("gLongitudinal", telemetry.getGLongitudinal());

        writeApi.writePoint(influxBucket, influxOrg, point);
    }

    @PreDestroy
    public void shutdown() {
        if (influxDBClient != null) {
            influxDBClient.close();
        }
    }
}
