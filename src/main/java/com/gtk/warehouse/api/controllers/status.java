package com.gtk.warehouse.api.controllers;

import com.gtk.warehouse.api.model.APIStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class status {

    @GetMapping(value = "/api/v1/status")
    public APIStatus getAPIStatus() {
        return new APIStatus("ok");
    }
}
