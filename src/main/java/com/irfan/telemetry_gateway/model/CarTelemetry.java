package com.irfan.telemetry_gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarTelemetry {
    private float speed;
    private float gas;
    private float brake;
    private float gear;
    private float engineRPM;
    private float steerAngle;
    private float tireSlipFL;
    private float tireSlipFR;
    private float tireSlipRL;
    private float tireSlipRR;
    private float gHorizontal;
    private float gVertical;
    private float gLongitudinal;
}
