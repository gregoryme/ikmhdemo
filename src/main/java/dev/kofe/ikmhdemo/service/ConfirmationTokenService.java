package dev.kofe.ikmhdemo.service;

import dev.kofe.ikmhdemo.model.User;
import dev.kofe.ikmhdemo.repo.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConfirmationTokenService {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    public void deleteTokensByUser (User user) {
            confirmationTokenRepository.deleteAllByUser(user);
    }

}
