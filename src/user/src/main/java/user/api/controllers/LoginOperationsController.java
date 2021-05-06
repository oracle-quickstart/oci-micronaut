package user.api.controllers;

import io.micrometer.core.annotation.Counted;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.transaction.annotation.ReadOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.PasswordUtils;
import user.api.DtoMapper;
import user.api.LoginOperations;
import user.api.dto.UserDetailDto;
import user.model.User;
import user.model.UserRepository;

import javax.transaction.Transactional;

/**
 * User login API controller.
 */
@Controller
public class LoginOperationsController implements LoginOperations {

    private static final Logger LOG = LoggerFactory.getLogger(LoginOperationsController.class);

    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;

    public LoginOperationsController(UserRepository userRepository, DtoMapper dtoMapper) {
        this.userRepository = userRepository;
        this.dtoMapper = dtoMapper;
    }

    @Override
    @ReadOnly
    @Transactional
    public UserDetailDto login(String username, String password) {
        LOG.debug("Requests login: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() -> userNotFoundException(username));
        if (PasswordUtils.isExpectedPassword(password, user.getSalt(), user.getPassword())) {
            return loginSuccessful(user);
        }
        return loginInvalidPassword(username);
    }

    @Counted("login.invalid_password")
    protected UserDetailDto loginInvalidPassword(String username) {
        LOG.debug("Invalid password: {}", username);
        throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User with username: " + username + " doesn't match provided password");
    }

    @Counted("login.success")
    protected UserDetailDto loginSuccessful(User user) {
        LOG.debug("Success: {}", user.getUsername());
        return dtoMapper.toSimpleUserDetailDto(user);
    }

    @Counted("login.invalid_username")
    protected HttpStatusException userNotFoundException(String username) {
        LOG.debug("User not found: {}", username);
        throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "User with username: " + username + " not found");
    }

}
