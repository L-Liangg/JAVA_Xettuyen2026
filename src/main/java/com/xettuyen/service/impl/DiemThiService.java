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

    public int getTotalPages() {
        return repository.getTotalPages();
    }

    public void update(DiemThi diemThi) {
        repository.update(diemThi);
    }

    public void save(DiemThi diemThi) {
        repository.save(diemThi);
    }
}