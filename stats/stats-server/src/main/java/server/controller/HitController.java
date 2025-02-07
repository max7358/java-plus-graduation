package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import dto.HitDto;
import server.service.HitService;

@RestController
@RequestMapping(path = "/hit")
public class HitController {
    private final HitService hitService;

    @Autowired
    HitController(HitService hitService) {
        this.hitService = hitService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto create(@RequestBody HitDto dto) {
        return hitService.create(dto);
    }
}
