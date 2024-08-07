package com.gtk.warehouse.api.controllers;

import com.gtk.warehouse.api.services.SolaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SolaceController {

    @Autowired
    private SolaceService solaceService;

    @GetMapping(value = "/api/v1/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getLiveEvents(){
        return solaceService.addEmitter();
    }
}
