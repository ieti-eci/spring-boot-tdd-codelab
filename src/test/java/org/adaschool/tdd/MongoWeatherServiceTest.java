package org.adaschool.tdd;

import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.adaschool.tdd.service.MongoWeatherService;
import org.adaschool.tdd.service.WeatherService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
class MongoWeatherServiceTest
{
    WeatherService weatherService;

    @Mock
    WeatherReportRepository repository;

    @BeforeEach()
    public void setup()
    {
        weatherService = new MongoWeatherService( repository );
    }

    @Test
    void createWeatherReportCallsSaveOnRepository()
    {
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReportDto weatherReportDto = new WeatherReportDto( location, 35f, 22f, "tester", new Date() );
        weatherService.report( weatherReportDto );
        verify( repository ).save( any( WeatherReport.class ) );
    }

    @Test
    void weatherReportIdFoundTest()
    {
        String weatherReportId = "awae-asd45-1dsad";
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.of( weatherReport ) );
        WeatherReport foundWeatherReport = weatherService.findById( weatherReportId );
        Assertions.assertEquals( weatherReport, foundWeatherReport );
    }

    @Test
    void weatherReportIdNotFoundTest()
    {
        String weatherReportId = "dsawe1fasdasdoooq123";
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.empty() );
        Assertions.assertThrows( WeatherReportNotFoundException.class, () -> {
            weatherService.findById( weatherReportId );
        } );
    }

    @Test
    void weatherReportFindNearLocation(){
        GeoLocation geoLocation = new GeoLocation(2.78,5.89);
        WeatherReport near = new WeatherReport(new GeoLocation(2.79,5.90),35f , 22f,"tester", new Date());
        WeatherReport far = new WeatherReport(new GeoLocation(92.79,95.90),35f , 22f,"tester", new Date());
        List<WeatherReport> list = new ArrayList<>();
        list.add(near); list.add(far);
        when(repository.findAll()).thenReturn(list);
        List<WeatherReport> result = weatherService.findNearLocation(geoLocation, 2000);
        Assertions.assertArrayEquals(new WeatherReport[]{near},result.toArray());
    }

    @Test
    void weatherReportFindWeatherReportsByName(){
        WeatherReport near = new WeatherReport(new GeoLocation(2.79,5.90),35f , 22f,"tester", new Date());
        when(repository.findByReporter("tester")).thenReturn(new ArrayList<>(Collections.singletonList(near)));
        List<WeatherReport> res = weatherService.findWeatherReportsByName("tester");
        Assertions.assertArrayEquals(new WeatherReport[]{near}, res.toArray());
    }

}
