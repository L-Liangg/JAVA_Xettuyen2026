package com.xettuyen.service.impl;

import com.xettuyen.entity.ToHopMon;
import com.xettuyen.repository.ToHopMonRepository;
import java.util.List;

import static com.xettuyen.config.AppConstants.PAGE_SIZE;

public class ToHopMonService {
    private final ToHopMonRepository repository = new ToHopMonRepository();

    public List<ToHopMon> getPage(int page) {
        return repository.findAll(page, PAGE_SIZE);
    }

    public int getTotalPages() {
        return repository.getTotalPages();
    }

    public void update(ToHopMon toHopMon) {
        repository.update(toHopMon);
    }

    public void save(ToHopMon toHopMon) {
        repository.save(toHopMon);
    }

    public void delete(ToHopMon toHopMon) {
        repository.delete(toHopMon);
    }
}