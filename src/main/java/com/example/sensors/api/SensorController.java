package com.example.sensors.api;

import com.example.sensors.domain.sensor.Sensor;
import com.example.sensors.domain.sensor.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/sensor", produces = "application/json")
@Validated
public class SensorController {
    SensorRepository sensorRepository;

    @Autowired
    public SensorController(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @GetMapping
    public Iterable<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSensorById(@PathVariable @NotNull Long id) {
        return ResponseEntity.of(sensorRepository.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSensor(@PathVariable @NotNull Long id) {
        try
        {
            sensorRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            //when no sensor with such id exists
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public Sensor addSensor(@RequestBody @NotNull Sensor sensor) {
        return this.sensorRepository.save(sensor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sensor> updateSensor(@PathVariable @NotNull Long id, @RequestBody @NotNull Sensor sensor) {

        if (sensorRepository.existsById(id)) {

            var currentSensor = sensorRepository.findById(id).get();
            if (sensor.getAddress() == null && sensor.getOwner() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sensor);
            if (sensor.getAddress() != null) {
                currentSensor.setAddress(sensor.getAddress());
            }
            if (sensor.getOwner() != null) {
                currentSensor.setOwner(sensor.getOwner());
            }
            sensorRepository.save(currentSensor);
            return ResponseEntity.ok(currentSensor);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(sensor);
    }

}
