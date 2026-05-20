package com.xettuyen.service;

import com.xettuyen.repository.LoginRepository;

public class LoginService {
    private static LoginRepository repository = new LoginRepository();

    public LoginRepository.LoginResponse login(String username, String password) {
        return repository.login(username, password);
    }
}
