package com.xettuyen.service.impl;

import com.xettuyen.entity.ThiSinh;
import com.xettuyen.repository.ThiSinhRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


import static com.xettuyen.config.AppConstants.PAGE_SIZE;

public class ThiSinhService {
    private final ThiSinhRepository repository = new ThiSinhRepository();

    public List<ThiSinh> search(String keyword, int page) {
        if (keyword == null || keyword.isBlank())
            return repository.findAll(page, PAGE_SIZE);
        return repository.search(keyword.trim(), page, PAGE_SIZE);
    }

    public long countSearch(String keyword) {
        keyword = (keyword == null) ? "" : keyword.trim();
        return repository.countSearch(keyword);
    }

    public int getTotalPages(String keyword) {
        keyword = (keyword == null) ? "" : keyword.trim();
        long count = repository.countSearch(keyword);
        return (int) Math.max(1, Math.ceil((double) count / PAGE_SIZE));
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

    public Map<String, Long> countByDoiTuong() {
        return repository.countByDoiTuong();
    }

    public Map<String, Long> countByKhuVuc() {
        return repository.countByKhuVuc();
    }
}
