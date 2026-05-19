package com.xettuyen.service.impl;

import com.xettuyen.entity.DiemCong;
import com.xettuyen.repository.DiemCongRepository;

import java.util.List;

import static com.xettuyen.config.AppConstants.PAGE_SIZE;

public class DiemCongService {
    private final DiemCongRepository repository = new DiemCongRepository();

    public List<DiemCong> getPage(int page) {
        return repository.findAll(page, PAGE_SIZE);
    }

    public List<DiemCong> search(String keyword, int page) {
        if (keyword == null || keyword.isBlank())
            return repository.findAll(page, PAGE_SIZE);
        return repository.search(keyword.trim(), page, PAGE_SIZE);
    }

    public int getTotalPages() {
        return repository.getTotalPages();
    }

    public int getTotalPages(String keyword) {
        long count = (keyword == null || keyword.isBlank())
                ? repository.countAll()
                : repository.countSearch(keyword.trim());
        return (int) Math.max(1, Math.ceil((double) count / PAGE_SIZE));
    }

    public List<DiemCong> searchAnd(String cccd, String manganh, int page) {
        String c = (cccd == null) ? "" : cccd.trim();
        String m = (manganh == null) ? "" : manganh.trim();
        if (c.isBlank() && m.isBlank()) return repository.findAll(page, PAGE_SIZE);
        return repository.searchAnd(c, m, page, PAGE_SIZE);
    }

    public int getTotalPagesAnd(String cccd, String manganh) {
        String c = (cccd == null) ? "" : cccd.trim();
        String m = (manganh == null) ? "" : manganh.trim();
        long count = (c.isBlank() && m.isBlank())
                ? repository.countAll()
                : repository.countSearchAnd(c, m);
        return (int) Math.max(1, Math.ceil((double) count / PAGE_SIZE));
    }

    public DiemCong findByDcKeys(String dcKeys) {
        if (dcKeys == null || dcKeys.isBlank()) return null;
        return repository.findByDcKeys(dcKeys.trim());
    }

    public void update(DiemCong diemCong) {
        repository.update(diemCong);
    }

    public void save(DiemCong diemCong) {
        repository.save(diemCong);
    }

    public void delete(DiemCong diemCong) {
        repository.delete(diemCong);
    }
}