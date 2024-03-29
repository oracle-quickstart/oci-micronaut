package user.controllers;

import io.micronaut.http.annotation.Post;
import user.controllers.dto.UserDetailDto;

/**
 * User login operations.
 */
public interface LoginOperations {

    /**
     * Log-in user.
     *
     * @param username The username
     * @param password The password
     * @return the user object if login is successful
     */
    @Post("/login")
    UserDetailDto login(String username, String password);

}
