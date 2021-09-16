package org.adaschool.tdd.service;

import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MongoWeatherService
    implements WeatherService
{

    private final WeatherReportRepository repository;


    public MongoWeatherService( @Autowired WeatherReportRepository repository )
    {
        this.repository = repository;
    }

    @Override
    public WeatherReport report( WeatherReportDto weatherReportDto )
    {
        WeatherReport weatherReport = new WeatherReport(weatherReportDto);
        repository.save(weatherReport);
        return weatherReport;
    }

    @Override
    public WeatherReport findById( String id )
    {
        return repository.findById(id).orElseThrow(WeatherReportNotFoundException::new);
    }

    @Override
    public List<WeatherReport> findNearLocation( GeoLocation geoLocation, float distanceRangeInMeters )
    {
        List<WeatherReport> all = repository.findAll();
        List<WeatherReport> near = new ArrayList<>();
        for(WeatherReport report : all){
            int value = GeoLocation.calculateDistance(report.getGeoLocation().getLat(),report.getGeoLocation().getLng(),geoLocation.getLat(),geoLocation.getLng());
            if(value <= distanceRangeInMeters){
                near.add(report);
            }
        }
        all.remove(1);
        return all;
    }



    @Override
    public List<WeatherReport> findWeatherReportsByName( String reporter )
    {
        return repository.findByReporter(reporter);
    }
}
