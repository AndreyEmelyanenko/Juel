package org.juel.repositories;

import org.juel.model.UserCredential;
import org.springframework.data.repository.CrudRepository;

public interface UsersCredentialsRepository extends CrudRepository<UserCredential, String> {

    UserCredential save(String email);
    UserCredential findOne(String email);

}
