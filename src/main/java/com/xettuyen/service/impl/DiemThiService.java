package com.xettuyen.service.impl;

import com.xettuyen.entity.DiemThi;
import com.xettuyen.repository.DiemThiRepository;
import java.util.List;

import static com.xettuyen.config.AppConstants.PAGE_SIZE;

public class DiemThiService {
    private final DiemThiRepository repository = new DiemThiRepository();

    public List<DiemThi> getPage(int page) {
        return repository.findAll(page, PAGE_SIZE);
    }

    public List<DiemThi> search(String keyword, int page) {
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

    public List<DiemThi> getAllByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return repository.findAll();
        return repository.searchAll(keyword.trim());
    }

    public DiemThi findByCccd(String cccd) {
        if (cccd == null || cccd.isBlank()) return null;
        return repository.findByCccd(cccd.trim());
    }

    public void update(DiemThi diemThi) {
        repository.update(diemThi);
    }

    public void save(DiemThi diemThi) {
        repository.save(diemThi);
    }

    public void delete(DiemThi diemThi) {
        repository.delete(diemThi);
    }
}