package com.xettuyen.service.impl;

import com.xettuyen.entity.Nganh;
import com.xettuyen.repository.NganhRepository;

import java.util.List;

import static com.xettuyen.config.AppConstants.PAGE_SIZE;

public class NganhService {
    private final NganhRepository repository = new NganhRepository();

    public List<Nganh> getPage(int page) {
        return repository.findAll(page, PAGE_SIZE);
    }

    public int getTotalPages() {
        return repository.getTotalPages();
    }

    public void save(Nganh nganh) {
        repository.save(nganh);
    }

    public void update(Nganh nganh) {
        repository.update(nganh);
    }

    public void delete(Nganh nganh) {
        repository.delete(nganh);
    }
}
