package server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dto.HitDto;
import server.model.Hit;
import server.model.HitMapper;
import server.repository.HitRepository;

@Service
@Slf4j
public class HitService {
    private final HitRepository repository;

    @Autowired
    HitService(HitRepository repository) {
        this.repository = repository;
    }

    public HitDto create(HitDto dto) {
        Hit hit = HitMapper.toHit(dto);
        hit = repository.save(hit);
        return HitMapper.toHitDto(hit);
    }
}
