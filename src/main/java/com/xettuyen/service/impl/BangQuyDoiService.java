package com.xettuyen.service.impl;

import com.xettuyen.entity.BangQuyDoi;
import com.xettuyen.repository.BangQuyDoiRepository;

import java.util.List;

import static com.xettuyen.config.AppConstants.PAGE_SIZE;

public class BangQuyDoiService {
    private final BangQuyDoiRepository repository = new BangQuyDoiRepository();

    public List<BangQuyDoi> getPage(int page) {
        return repository.findAll(page, PAGE_SIZE);
    }

    public List<BangQuyDoi> search(String keyword, int page) {
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

    public List<BangQuyDoi> searchAnd(String phuongthuc, String tohop, int page) {
        String p = (phuongthuc == null) ? "" : phuongthuc.trim();
        String t = (tohop == null) ? "" : tohop.trim();
        if (p.isBlank() && t.isBlank()) return repository.findAll(page, PAGE_SIZE);
        return repository.searchAnd(p, t, page, PAGE_SIZE);
    }

    public int getTotalPagesAnd(String phuongthuc, String tohop) {
        String p = (phuongthuc == null) ? "" : phuongthuc.trim();
        String t = (tohop == null) ? "" : tohop.trim();
        long count = (p.isBlank() && t.isBlank())
                ? repository.countAll()
                : repository.countSearchAnd(p, t);
        return (int) Math.max(1, Math.ceil((double) count / PAGE_SIZE));
    }

    public BangQuyDoi findByMaquydoi(String maquydoi) {
        if (maquydoi == null || maquydoi.isBlank()) return null;
        return repository.findByMaquydoi(maquydoi.trim());
    }

    public void update(BangQuyDoi bangQuyDoi) {
        repository.update(bangQuyDoi);
    }

    public void save(BangQuyDoi bangQuyDoi) {
        repository.save(bangQuyDoi);
    }

    public void delete(BangQuyDoi bangQuyDoi) {
        repository.delete(bangQuyDoi);
    }
}
