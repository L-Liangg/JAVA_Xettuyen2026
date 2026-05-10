package com.xettuyen.service.impl;

import com.xettuyen.entity.ThiSinh;
import com.xettuyen.repository.ThiSinhRepository;

import java.time.LocalDate;

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

    public List<ThiSinh> searchAnd(String cccd, String sobaodanh, int page) {
        String c = (cccd == null) ? "" : cccd.trim();
        String s = (sobaodanh == null) ? "" : sobaodanh.trim();
        if (c.isBlank() && s.isBlank())
            return repository.findAll(page, PAGE_SIZE);
        return repository.searchAnd(c, s, page, PAGE_SIZE);
    }

    public int getTotalPages(String keyword) {
        long count = (keyword == null || keyword.isBlank())
                ? repository.countAll()
                : repository.countSearch(keyword.trim());
        return (int) Math.ceil((double) count / PAGE_SIZE);
    }

    public int getTotalPagesAnd(String cccd, String sobaodanh) {
        String c = (cccd == null) ? "" : cccd.trim();
        String s = (sobaodanh == null) ? "" : sobaodanh.trim();
        long count = (c.isBlank() && s.isBlank())
                ? repository.countAll()
                : repository.countSearchAnd(c, s);
        return (int) Math.max(1, Math.ceil((double) count / PAGE_SIZE));
    }

    public ThiSinh findByCccd(String cccd) {
        return repository.findByCccd(cccd);
    }

    public void save(ThiSinh thiSinh) {
        if (thiSinh == null) return;
        thiSinh.setUpdated_at(LocalDate.now());
        repository.save(thiSinh);
    }

    public void update(ThiSinh thiSinh) {
        if (thiSinh == null) return;
        thiSinh.setUpdated_at(LocalDate.now());
        repository.update(thiSinh);
    }

    public void delete(ThiSinh thiSinh) {
        if (thiSinh == null) return;
        repository.delete(thiSinh);
    }
}
