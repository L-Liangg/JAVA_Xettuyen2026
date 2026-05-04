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

    public List<ToHopMon> getAll() {
        return repository.findAll();
    }

    public int getTotalPages() {
        return repository.getTotalPages();
    }

    public List<ToHopMon> search(String keyword, int page) {
        if (keyword == null || keyword.isBlank()) {
            return getPage(page);
        }
        return repository.search(keyword, page, PAGE_SIZE);
    }

    public int getTotalPages(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getTotalPages();
        }
        long total = repository.countSearch(keyword);
        return (int) Math.max(1, Math.ceil((double) total / PAGE_SIZE));
    }

    public ToHopMon findByMatohop(String matohop) {
        if (matohop == null || matohop.isBlank()) return null;
        return repository.findByMatohop(matohop);
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