package com.xettuyen.service.impl;

import com.xettuyen.entity.NguyenVong;
import com.xettuyen.repository.NguyenVongRepository;
import java.util.List;

import static com.xettuyen.config.AppConstants.PAGE_SIZE;

public class NguyenVongService {
    private final NguyenVongRepository repository = new NguyenVongRepository();

    public List<NguyenVong> getPage(int page) {
        return repository.findAll(page, PAGE_SIZE);
    }

    public int getTotalPages() {
        return repository.getTotalPages();
    }

    public void update(NguyenVong nguyenVong) {
        repository.update(nguyenVong);
    }

    public void save(NguyenVong nguyenVong) {
        repository.save(nguyenVong);
    }

    public void delete(NguyenVong nguyenVong) {
        repository.delete(nguyenVong);
    }
}