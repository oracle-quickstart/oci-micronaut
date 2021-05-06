package user.api.controllers;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.transaction.annotation.ReadOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.api.DtoMapper;
import user.api.dto.UserDto;
import user.api.UserOperations;
import user.api.dto.UserAddressDetailDto;
import user.api.dto.UserAddressDto;
import user.api.dto.UserCardDetailDto;
import user.api.dto.UserCardDto;
import user.api.dto.UserDetailDto;
import user.model.User;
import user.model.UserAddress;
import user.model.UserAddressRepository;
import user.model.UserCard;
import user.model.UserCardRepository;
import user.model.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * User API controller.
 */
@Controller
public class UserOperationsController implements UserOperations {

    private static final Logger LOG = LoggerFactory.getLogger(UserOperationsController.class);

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserCardRepository userCardRepository;
    private final DtoMapper dtoMapper;

    public UserOperationsController(UserRepository userRepository, UserAddressRepository userAddressRepository, UserCardRepository userCardRepository, DtoMapper dtoMapper) {
        this.userRepository = userRepository;
        this.userAddressRepository = userAddressRepository;
        this.userCardRepository = userCardRepository;
        this.dtoMapper = dtoMapper;
    }

    @Timed("user.create")
    @Transactional
    @Override
    public UserDetailDto createUser(UserDto userDto) {
        LOG.debug("Create {}", userDto);
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            LOG.warn("User exists {}", userDto);
            throw new HttpStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        User user = dtoMapper.fromUserDto(userDto);
        userRepository.save(user);
        LOG.debug("created {} - {}", userDto, user.getId());
        if (user.getAddresses() != null) {
            user.getAddresses().forEach(userAddress -> userAddress.setUser(user));
            userAddressRepository.saveAll(user.getAddresses());
        }
        return dtoMapper.toSimpleUserDetailDto(user);
    }

    @Timed("user.list")
    @ReadOnly
    @Transactional
    @Override
    public List<UserDetailDto> getUsers() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(userRepository.findAll().iterator(), Spliterator.ORDERED), false)
                .map(dtoMapper::toSimpleUserDetailDto)
                .collect(Collectors.toList());
    }

    @Timed("user.get")
    @ReadOnly
    @Transactional
    @Override
    public UserDetailDto getUser(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> dtoMapper.toUserDetailDto(user, true, true))
                .orElseThrow(() -> userNotFoundException(userId));
    }

    @Timed("user.get_by_username")
    @ReadOnly
    @Transactional
    @Override
    public UserDetailDto getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(dtoMapper::toSimpleUserDetailDto)
                .orElseThrow(() -> userNotFoundException(username));
    }

    @Timed("user.update")
    @Transactional
    @Override
    public void updateUser(UUID userId, UserDto userDto) {
        LOG.debug("User[{}] update {}", userId, userDto);
        User user = getUserOrThrow(userId);
        dtoMapper.copyFromUserDto(user, userDto);
        userRepository.update(user);
    }

    @Timed("user.address.add")
    @Transactional
    @Override
    public UserAddressDetailDto addUserAddress(UUID userId, UserAddressDto userAddressDto) {
        LOG.debug("User[{}] create {}", userId, userAddressDto);
        UserAddress userAddress = dtoMapper.fromUserAddressDto(userAddressDto);
        userAddress.setUser(getUserOrThrow(userId));
        userAddressRepository.save(userAddress);
        return dtoMapper.toUserAddressDetailDto(userAddress);
    }

    @Timed("user.address.list")
    @ReadOnly
    @Transactional
    @Override
    public List<UserAddressDetailDto> getUserAddresses(UUID userId) {
        return userAddressRepository.findByUserId(userId)
                .stream()
                .map(dtoMapper::toUserAddressDetailDto)
                .collect(Collectors.toList());
    }

    @Timed("user.address.get")
    @ReadOnly
    @Transactional
    @Override
    public UserAddressDetailDto getUserAddress(UUID userId, UUID addressId) {
        LOG.debug("User[{}] get address {}", userId, addressId);
        UserAddressDetailDto userAddressDetailDto = userAddressRepository.findByIdAndUserId(addressId, userId)
                .map(dtoMapper::toUserAddressDetailDto)
                .orElseThrow(() -> userAddressNotFoundException(userId, addressId));
        LOG.debug("Found {}", userAddressDetailDto);
        return userAddressDetailDto;
    }

    @Timed("user.card.add")
    @Transactional
    @Override
    public UserCardDetailDto addUserCard(UUID userId, UserCardDto userCardDto) {
        LOG.debug("User[{}] create {}", userId, userCardDto);
        UserCard userCard = dtoMapper.fromUserCardDto(userCardDto);
        userCard.setUser(getUserOrThrow(userId));
        userCardRepository.save(userCard);
        return dtoMapper.toUserCardDetailDto(userCard);
    }

    @Timed("user.card.get")
    @ReadOnly
    @Transactional
    @Override
    public UserCardDetailDto getUserCard(UUID userId, UUID cardId) {
        return userCardRepository.findByIdAndUserId(cardId, userId)
                .map(dtoMapper::toUserCardDetailDto)
                .orElseThrow(() -> userCardNotFoundException(userId, cardId));
    }

    @Timed("user.card.list")
    @ReadOnly
    @Transactional
    @Override
    public List<UserCardDetailDto> getUserCards(UUID userId) {
        return userCardRepository.findByUserId(userId)
                .stream()
                .map(dtoMapper::toUserCardDetailDto)
                .collect(Collectors.toList());
    }

    @Timed("user.delete")
    @Transactional
    @Override
    public void deleteUser(UUID userId) {
        User user = getUserOrThrow(userId);
        if (user.getAddresses() != null) {
            user.getAddresses().forEach(userAddressRepository::delete);
        }
        if (user.getCards() != null) {
            user.getCards().forEach(userCardRepository::delete);
        }
        userRepository.delete(user);
    }

    @Timed("user.address.delete")
    @Transactional
    @Override
    public void deleteUserAddress(UUID userId, UUID addressId) {
        UserAddress userAddress = userAddressRepository.findByIdAndUserId(addressId, userId).orElseThrow(() -> userAddressNotFoundException(userId, addressId));
        userAddressRepository.delete(userAddress);
    }

    @Timed("user.card.delete")
    @Transactional
    @Override
    public void deleteUserCard(UUID userId, UUID cardId) {
        UserCard userCard = userCardRepository.findByIdAndUserId(cardId, userId).orElseThrow(() -> userCardNotFoundException(userId, cardId));
        userCardRepository.delete(userCard);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> userNotFoundException(userId));
    }

    @Counted("user.not_found")
    protected HttpStatusException userNotFoundException(UUID userId) {
        throw new HttpStatusException(HttpStatus.NOT_FOUND, "User with id: " + userId + " not found!");
    }

    @Counted("user.not_found")
    protected HttpStatusException userNotFoundException(String username) {
        throw new HttpStatusException(HttpStatus.NOT_FOUND, "User with username: " + username + " not found!");
    }

    @Counted("user.card.not_found")
    protected HttpStatusException userCardNotFoundException(UUID userId, UUID cardId) {
        throw new HttpStatusException(HttpStatus.NOT_FOUND, "User card with id: " + cardId + " not found for the user with id: " + userId + "!");
    }

    @Counted("user.address.not_found")
    protected HttpStatusException userAddressNotFoundException(UUID userId, UUID addressId) {
        throw new HttpStatusException(HttpStatus.NOT_FOUND, "User address with id: " + addressId + " not found for the user with id: " + userId + "!");
    }
}
