package org.adaschool.tdd;

import org.adaschool.tdd.controller.weather.dto.NearByWeatherReportsQueryDto;
import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class WeatherReportControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private WeatherReportRepository repository;

    @Test
    public void createWeatherReport(){
        GeoLocation location = new GeoLocation(4.7110,74.0721);
        WeatherReportDto weatherReportDto = new WeatherReportDto(location, 35f, 22f, "tester", new Date());
        this.restTemplate.postForEntity("http://localhost:" + port + "/v1/weather", weatherReportDto, WeatherReport.class);
        verify(repository).save(any(WeatherReport.class));
    }

    @Test
    public void getSpecificWeatherReport(){
        String weatherReportId = "awae-asd45-1dsad";
        GeoLocation location = new GeoLocation(4.7110,74.0721);
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );
        when(repository.findById(weatherReportId)).thenReturn(Optional.of(weatherReport));
        WeatherReport foundWeatherReport = this.restTemplate.getForEntity("http://localhost:" + port + "/v1/weather/" + weatherReportId, WeatherReport.class).getBody();
        Assertions.assertEquals(weatherReport.getTemperature(), foundWeatherReport.getTemperature());
    }

    @Test
    public void findNearByReportsTest() {
        NearByWeatherReportsQueryDto queryDto = new NearByWeatherReportsQueryDto(new GeoLocation(4.7110, 74.0721), 2500);
        WeatherReport weatherReportNear = new WeatherReport(new GeoLocation(4.7111, 74.0722), 35f, 22f, "tester", new Date());
        WeatherReport weatherReportFar = new WeatherReport(new GeoLocation(92.79,95.90), 35f, 22f, "tester", new Date());
        List<WeatherReport> list = new ArrayList<>();list.add(weatherReportNear); list.add(weatherReportFar);
        when(repository.findAll()).thenReturn(list);
        LinkedHashMap<String, String> foundWeatherReport = (LinkedHashMap<String, String>) this.restTemplate.postForEntity("http://localhost:" + port + "/v1/weather/nearby", queryDto, List.class).getBody().get(0);
        Assertions.assertEquals(weatherReportNear.getReporter(), foundWeatherReport.get("reporter"));
    }

    @Test
    public void findByReporterIdTest(){
        WeatherReport weatherReportNear = new WeatherReport(new GeoLocation(4.7111, 74.0722), 35f, 22f, "tester", new Date());
        List<WeatherReport> list = new ArrayList(); list.add(weatherReportNear);
        when(repository.findByReporter("tester")).thenReturn(list);
        LinkedHashMap<String, String> result = (LinkedHashMap<String, String>) this.restTemplate.getForEntity("http://localhost:" + port + "/v1/weather/reporter/" + "tester", List.class).getBody().get(0);
        Assertions.assertEquals("tester", result.get("reporter"));
    }
}
