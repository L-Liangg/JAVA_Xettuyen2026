package com.xettuyen.service.impl;

import com.xettuyen.entity.NganhToHop;
import com.xettuyen.repository.NganhToHopRepository;
import java.util.List;

import static com.xettuyen.config.AppConstants.PAGE_SIZE;

public class NganhToHopService {
    private final NganhToHopRepository repository = new NganhToHopRepository();

    public List<NganhToHop> getPage(int page) {
        return repository.findAll(page, PAGE_SIZE);
    }

    public NganhToHop findByTbKeys(String tbKeys) {
        if (tbKeys == null || tbKeys.isBlank()) return null;
        return repository.findByTbKeys(tbKeys.trim());
    }

    public int getTotalPages() {
        return repository.getTotalPages();
    }

    public List<NganhToHop> searchAnd(String manganh, String matohop, int page) {
        String m = (manganh == null) ? "" : manganh.trim();
        String t = (matohop == null) ? "" : matohop.trim();
        if (m.isBlank() && t.isBlank()) return repository.findAll(page, PAGE_SIZE);
        return repository.searchAnd(m, t, page, PAGE_SIZE);
    }

    public int getTotalPagesAnd(String manganh, String matohop) {
        String m = (manganh == null) ? "" : manganh.trim();
        String t = (matohop == null) ? "" : matohop.trim();
        long count = (m.isBlank() && t.isBlank())
                ? repository.countAll()
                : repository.countSearchAnd(m, t);
        return (int) Math.max(1, Math.ceil((double) count / PAGE_SIZE));
    }

    public void update(NganhToHop nganhToHop) {
        repository.update(nganhToHop);
    }

    public void save(NganhToHop nganhToHop) {
        repository.save(nganhToHop);
    }

    public void delete(NganhToHop nganhToHop) {
        repository.delete(nganhToHop);
    }
}
