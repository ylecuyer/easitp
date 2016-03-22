package fr.ylecuyer.easitp;

import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Rest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

//@Rest(rootUrl = "http://www.easitp.tk", converters = { MappingJackson2HttpMessageConverter.class })
@Rest(rootUrl = "http://10.0.2.2:4567", converters = { MappingJackson2HttpMessageConverter.class })
public interface MyRestClient {

    @Get("/tullave/{latitude}/{longitude}")
    TuLlaveList getPuntos(@Path double latitude, @Path double longitude);

    @Get("/sitp/{latitude_start}/{longitude_start}/{latitude_destino}/{longitude_destino}")
    SitpList getLines(@Path double latitude_start, @Path double longitude_start, @Path double latitude_destino, @Path double longitude_destino);

    @Get("/line/{id}")
    StationList getStations(@Path double id);
}