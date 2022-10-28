package user.controllers;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import user.controllers.dto.UserAddressDetailDto;
import user.controllers.dto.UserAddressDto;
import user.controllers.dto.UserCardDetailDto;
import user.controllers.dto.UserCardDto;
import user.controllers.dto.UserDetailDto;
import user.controllers.dto.UserDto;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * User operations.
 */
public interface UserOperations {

    /**
     * User creation / (registration)
     *
     * @param userDto The create dto
     */
    @Post("/register")
    UserDetailDto createUser(@Valid @Body UserDto userDto);

    /**
     * Get users
     *
     * @return addresses
     */
    @Get("/customers")
    List<UserDetailDto> getUsers();

    /**
     * User getter
     *
     * @param userId The user id
     */
    @Get("/customers/{userId}")
    UserDetailDto getUser(UUID userId);

    /**
     * User getter
     *
     * @param username The user username
     */
    @Get("/customers")
    UserDetailDto getUserByUsername(String username);

    /**
     * User replace
     *
     * @param userId  The user id
     * @param userDto The replace dto
     */
    @Put("/customers/{userId}")
    void updateUser(UUID userId, @Valid @Body UserDto userDto);

    /**
     * Add address
     *
     * @param userId         The user id
     * @param userAddressDto The add address dto
     */
    @Post("/customers/{userId}/addresses")
    UserAddressDetailDto addUserAddress(UUID userId, @Valid @Body UserAddressDto userAddressDto);

    /**
     * Get user address
     *
     * @param userId The user id
     * @return addresses
     */
    @Get("/customers/{userId}/addresses")
    List<UserAddressDetailDto> getUserAddresses(UUID userId);

    /**
     * Get user address
     *
     * @param userId    The user id
     * @param addressId The address id
     * @return addresses
     */
    @Get("/customers/{userId}/addresses/{addressId}")
    UserAddressDetailDto getUserAddress(UUID userId, UUID addressId);

    /**
     * Add card
     *
     * @param userId      The user id
     * @param userCardDto The add card dto
     */
    @Post("/customers/{userId}/cards")
    UserCardDetailDto addUserCard(UUID userId, @Valid @Body UserCardDto userCardDto);

    /**
     * Get user card
     *
     * @param userId The user id
     * @param cardId The card id
     * @return cards
     */
    @Get("/customers/{userId}/cards/{cardId}")
    UserCardDetailDto getUserCard(UUID userId, UUID cardId);

    /**
     * Get user cards
     *
     * @param userId The user id
     * @return cards
     */
    @Get("/customers/{userId}/cards")
    List<UserCardDetailDto> getUserCards(UUID userId);

    /**
     * Delete user
     *
     * @param userId The user id
     */
    @Delete("/customers/{userId}")
    void deleteUser(UUID userId);

    /**
     * Delete user address
     *
     * @param userId    The user id
     * @param addressId The address id
     */
    @Delete("/customers/{userId}/addresses/{addressId}")
    void deleteUserAddress(UUID userId, UUID addressId);

    /**
     * Delete user card
     *
     * @param userId The user id
     * @param cardId The card id
     */
    @Delete("/customers/{userId}/cards/{cardId}")
    void deleteUserCard(UUID userId, UUID cardId);

}
