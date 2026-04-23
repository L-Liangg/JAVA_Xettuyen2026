package com.xettuyen.service.impl;

import com.xettuyen.entity.ThiSinh;
import com.xettuyen.repository.ThiSinhRepository;

import java.util.List;

import static com.xettuyen.config.AppConstants.PAGE_SIZE;

public class ThiSinhService {
    private final ThiSinhRepository repository = new ThiSinhRepository();

    public List<ThiSinh> getPage(int page) {
        return repository.findAll(page, PAGE_SIZE);
    }

    public List<ThiSinh> search(String keyword, int page) {
        if (keyword == null || keyword.isBlank())
            return repository.findAll(page, PAGE_SIZE);
        return repository.search(keyword.trim(), page, PAGE_SIZE);
    }

    public int getTotalPages(String keyword) {
        long count = (keyword == null || keyword.isBlank())
                ? repository.countAll()
                : repository.countSearch(keyword.trim());
        return (int) Math.ceil((double) count / PAGE_SIZE);
    }

    public ThiSinh findByCccd(String cccd) {
        return repository.findByCccd(cccd);
    }

    public void update(ThiSinh thiSinh) {
        repository.update(thiSinh);
    }
}