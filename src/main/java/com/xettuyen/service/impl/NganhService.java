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

    public List<Nganh> getAll() {
        return repository.findAll();
    }

    public List<Nganh> search(String keyword, int page) {
        if (keyword == null || keyword.isBlank())
            return repository.findAll(page, PAGE_SIZE);
        return repository.search(keyword.trim(), page, PAGE_SIZE);
    }

    public List<Nganh> searchAnd(String manganh, String tennganh, int page) {
        String m = (manganh == null) ? "" : manganh.trim();
        String t = (tennganh == null) ? "" : tennganh.trim();
        if (m.isBlank() && t.isBlank())
            return repository.findAll(page, PAGE_SIZE);
        return repository.searchAnd(m, t, page, PAGE_SIZE);
    }

    public int getTotalPages() {
        return repository.getTotalPages();
    }

    public int getTotalPages(String keyword) {
        long count = (keyword == null || keyword.isBlank())
                ? repository.countAll()
                : repository.countSearch(keyword.trim());
        return (int) Math.ceil((double) count / PAGE_SIZE);
    }

    public int getTotalPagesAnd(String manganh, String tennganh) {
        String m = (manganh == null) ? "" : manganh.trim();
        String t = (tennganh == null) ? "" : tennganh.trim();
        long count = (m.isBlank() && t.isBlank())
                ? repository.countAll()
                : repository.countSearchAnd(m, t);
        return (int) Math.max(1, Math.ceil((double) count / PAGE_SIZE));
    }

    public Nganh findByManganh(String manganh) {
        if (manganh == null || manganh.isBlank()) return null;
        return repository.findByManganh(manganh.trim());
    }

    public void save(Nganh nganh) {
        if (nganh == null) return;
        repository.save(nganh);
    }

    public void update(Nganh nganh) {
        if (nganh == null) return;
        repository.update(nganh);
    }

    public void delete(Nganh nganh) {
        if (nganh == null) return;
        repository.delete(nganh);
    }

    public void updateSlNguyenVong() {
        repository.updateSlNguyenVong();
    }
}
