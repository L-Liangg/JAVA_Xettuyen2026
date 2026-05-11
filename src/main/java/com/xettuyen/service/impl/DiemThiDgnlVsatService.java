package com.xettuyen.service.impl;

import com.xettuyen.entity.DiemThiDgnlVsat;
import com.xettuyen.repository.DiemThiDgnlVsatRepository;
import java.util.List;

import static com.xettuyen.config.AppConstants.PAGE_SIZE;

public class DiemThiDgnlVsatService {
    private final DiemThiDgnlVsatRepository repository = new DiemThiDgnlVsatRepository();

    public List<DiemThiDgnlVsat> getPage(int page) {
        return repository.findAll(page, PAGE_SIZE);
    }

    public List<DiemThiDgnlVsat> search(String keyword, int page) {
        if (keyword == null || keyword.isBlank()) {
            return repository.findAll(page, PAGE_SIZE);
        }
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

    public List<DiemThiDgnlVsat> searchAnd(String cccd, String maMon, int page) {
        String c = (cccd == null) ? "" : cccd.trim();
        String m = (maMon == null) ? "" : maMon.trim();
        if (c.isBlank() && m.isBlank()) return repository.findAll(page, PAGE_SIZE);
        return repository.searchAnd(c, m, page, PAGE_SIZE);
    }

    public int getTotalPagesAnd(String cccd, String maMon) {
        String c = (cccd == null) ? "" : cccd.trim();
        String m = (maMon == null) ? "" : maMon.trim();
        long count = (c.isBlank() && m.isBlank())
                ? repository.countAll()
                : repository.countSearchAnd(c, m);
        return (int) Math.max(1, Math.ceil((double) count / PAGE_SIZE));
    }

    public List<DiemThiDgnlVsat> getAllByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return repository.findAll();
        return repository.searchAll(keyword.trim());
    }

    public DiemThiDgnlVsat findByCccd(String cccd) {
        if (cccd == null || cccd.isBlank()) return null;
        return repository.findByCccd(cccd.trim());
    }

    public DiemThiDgnlVsat findByDvKeys(String dvKeys) {
        if (dvKeys == null || dvKeys.isBlank()) return null;
        return repository.findByDvKeys(dvKeys);
    }

    public void update(DiemThiDgnlVsat dt) {
        repository.update(dt);
    }

    public void save(DiemThiDgnlVsat dt) {
        repository.save(dt);
    }

    public void delete(DiemThiDgnlVsat dt) {
        repository.delete(dt);
    }
}