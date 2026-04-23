package com.xettuyen.service.impl;

import com.xettuyen.entity.NganhToHop;
import com.xettuyen.repository.NganhToHopRepository;
import java.util.List;

import static com.xettuyen.config.AppConstants.PAGE_SIZE;

public class NganhToHopService {
    private final NganhToHopRepository repository = new NganhToHopRepository();

    public List<NganhToHop> getPage(int page) {
        return repository.findAll(page, PAGE_SIZE);
    }

    public int getTotalPages() {
        return repository.getTotalPages();
    }

    public void update(NganhToHop nganhToHop) {
        repository.update(nganhToHop);
    }

    public void save(NganhToHop nganhToHop) {
        repository.save(nganhToHop);
    }

    public void delete(NganhToHop nganhToHop) {
        repository.delete(nganhToHop);
    }
}