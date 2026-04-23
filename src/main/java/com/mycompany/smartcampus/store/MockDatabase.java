/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus.store;

/**
 *
 * @author Sanuthmee
 */
import com.mycompany.smartcampus.model.SensorReading;
import com.mycompany.smartcampus.model.Sensor;

import com.mycompany.smartcampus.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockDatabase {
    public static final Map<String, Room> ROOMS = new HashMap<>();
    public static final Map<String, Sensor> SENSORS = new HashMap<>();
    public static final Map<String, List<SensorReading>> READINGS = new HashMap<>();
    static {
        Room room1 = new Room();
        room1.setId("LIB-301");
        room1.setName("Library Quiet Study");
        room1.setCapacity(40);

        Room room2 = new Room();
        room2.setId("ENG-201");
        room2.setName("Engineering Lab");
        room2.setCapacity(30);

        ROOMS.put(room1.getId(), room1);
        ROOMS.put(room2.getId(), room2);

        Sensor sensor1 = new Sensor();
        sensor1.setId("TEMP-001");
        sensor1.setType("Temperature");
        sensor1.setStatus("ACTIVE");
        sensor1.setCurrentValue(22.5);
        sensor1.setRoomId("LIB-301");

        Sensor sensor2 = new Sensor();
        sensor2.setId("CO2-001");
        sensor2.setType("CO2");
        sensor2.setStatus("ACTIVE");
        sensor2.setCurrentValue(450.0);
        sensor2.setRoomId("ENG-201");

        SENSORS.put(sensor1.getId(), sensor1);
        SENSORS.put(sensor2.getId(), sensor2);

        ROOMS.get("LIB-301").getSensorIds().add(sensor1.getId());
        ROOMS.get("ENG-201").getSensorIds().add(sensor2.getId());

        READINGS.put(sensor1.getId(), new ArrayList<>());
        READINGS.put(sensor2.getId(), new ArrayList<>());
    }
}
