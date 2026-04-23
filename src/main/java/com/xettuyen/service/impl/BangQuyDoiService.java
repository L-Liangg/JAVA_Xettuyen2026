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

    public int getTotalPages() {
        return repository.getTotalPages();
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