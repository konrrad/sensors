package com.example.sensors;

import com.example.sensors.domain.sensor.Sensor;
import com.example.sensors.domain.sensor.SensorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SensorControllerTest {
    @MockBean
    SensorRepository sensorRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getSensorById() throws Exception {
        //given
        Sensor sensor = new Sensor(1L, "addr", "own");
        when(sensorRepository.findById(1L)).thenReturn(java.util.Optional.of(sensor));

        //when
        mockMvc.perform(get("/sensor/{id}", 1))
                //then
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("address", is("addr")))
                .andExpect(jsonPath("owner", is("own")));
    }

    @Test
    public void getSensorNotFound() throws Exception {
        //given
        when(sensorRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        mockMvc.perform(get("/sensor/{id}", 1))
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllSensors() throws Exception {
        //given
        Sensor sensor1 = new Sensor(1L, "addr", "own");
        Sensor sensor2 = new Sensor(2L, "addr", "own");

        when(sensorRepository.findAll()).thenReturn(Arrays.asList(sensor1, sensor2));

        //when
        mockMvc.perform(get("/sensor"))
                //then
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*]", hasSize(2)));
    }

    @Test
    public void getSensorsEmpty() throws Exception {
        when(sensorRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        mockMvc.perform(get("/sensor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(0)));
    }

    @Test
    public void deleteByIdNull() throws Exception {
        mockMvc.perform(delete("/sensor/null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteById() throws Exception {
        mockMvc.perform(delete("/sensor/{id}", 1))
                .andExpect(status().isOk());
        verify(sensorRepository, times(1)).deleteById(1L);
    }

    @Test
    public void updateAddress() throws Exception {
        //given
        Sensor oldSensor = new Sensor(1L, "old address", "old owner");
        String addressUpdate = "{\"address\":\"new address\"}";
        when(sensorRepository.existsById(1L)).thenReturn(true);
        when(sensorRepository.findById(1L)).thenReturn(Optional.of(oldSensor));

        //when
        mockMvc.perform(put("/sensor/{id}", 1L).content(addressUpdate).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk());
        assertTrue(sensorRepository.findById(1L).isPresent());
        assertEquals(sensorRepository.findById(1L).get().getAddress(), "new address");
    }


    @Test
    public void updateOwner() throws Exception {
        //given
        Sensor oldSensor = new Sensor(1L, "old address", "old owner");
        String addressUpdate = "{\"owner\":\"new owner\"}";
        when(sensorRepository.existsById(1L)).thenReturn(true);
        when(sensorRepository.findById(1L)).thenReturn(Optional.of(oldSensor));

        //when
        mockMvc.perform(put("/sensor/{id}", 1L).content(addressUpdate).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk());
        assertTrue(sensorRepository.findById(1L).isPresent());
        assertEquals(sensorRepository.findById(1L).get().getOwner(), "new owner");
    }

    @Test
    public void updateNull() throws Exception {

        //given
        Sensor oldSensor = new Sensor(1L, "old address", "old owner");
        String nullUpdate = "{}";
        when(sensorRepository.existsById(1L)).thenReturn(true);
        when(sensorRepository.findById(1L)).thenReturn(Optional.of(oldSensor));
        //when
        mockMvc.perform(put("/sensor/{id}", 1L).content(nullUpdate).contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isBadRequest());
        verify(sensorRepository, times(0)).save(any(Sensor.class));
    }

    @Test
    public void postSensor() throws Exception {
        String sensorJSON = "{\"address\":\"address\", \"owner\":\"owner\"}";
        mockMvc.perform(post("/sensor").contentType(MediaType.APPLICATION_JSON).content(sensorJSON))
                .andExpect(status().isOk());
        verify(sensorRepository,times(1)).save(any(Sensor.class));
    }


}
