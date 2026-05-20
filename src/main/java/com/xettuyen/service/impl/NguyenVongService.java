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

    public List<NguyenVong> search(String keyword, int page) {
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

    public List<NguyenVong> searchAnd(String cccd, String manganh, String ketqua, int page) {
        String c = (cccd == null) ? "" : cccd.trim();
        String m = (manganh == null) ? "" : manganh.trim();
        String k = (ketqua == null) ? "" : ketqua.trim();

        if (c.isBlank() && m.isBlank() && k.isBlank())
            return repository.findAll(page, PAGE_SIZE);
        return repository.searchAnd(c, m, k, page, PAGE_SIZE);
    }

    public int getTotalPagesAnd(String cccd, String manganh, String ketqua) {
        String c = (cccd == null) ? "" : cccd.trim();
        String m = (manganh == null) ? "" : manganh.trim();
        String k = (ketqua == null) ? "" : ketqua.trim();

        long count = (c.isBlank() && m.isBlank() && k.isBlank())
                ? repository.countAll()
                : repository.countSearchAnd(c, m, k);
        return (int) Math.max(1, Math.ceil((double) count / PAGE_SIZE));
    }

    public NguyenVong findByNvKeys(String nvKeys) {
        if (nvKeys == null || nvKeys.isBlank()) return null;
        return repository.findByNvKeys(nvKeys.trim());
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

    public List<String> recalculateThxtAll() {
        return new ThxtCalculationService().recalculateAll();
    }

    public List<String> recalculateThxtAll(ThxtCalculationService.ProgressListener listener) {
        return new ThxtCalculationService().recalculateAll(listener);
    }

    public XetTuyenService.Result runXetTuyenAll() {
        return new XetTuyenService().runXetTuyenAll();
    }

    public XetTuyenService.Result runXetTuyenAll(XetTuyenService.ProgressListener listener) {
        return new XetTuyenService().runXetTuyenAll(listener);
    }
}

