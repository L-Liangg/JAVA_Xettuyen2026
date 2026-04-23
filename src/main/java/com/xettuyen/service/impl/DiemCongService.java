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

    public int getTotalPages() {
        return repository.getTotalPages();
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