package dev.kofe.ikmhdemo.repo;

import dev.kofe.ikmhdemo.model.ConfirmationToken;
import dev.kofe.ikmhdemo.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, String> {

    ConfirmationToken findByConfirmationToken(String confirmationToken);

    void deleteAllByUser(User user);

    List<ConfirmationToken> findAllByUser(User user);

}
