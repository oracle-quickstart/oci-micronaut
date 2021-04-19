package user.api;

import user.api.dto.UserAddressDetailDto;
import user.api.dto.UserAddressDto;
import user.api.dto.UserCardDetailDto;
import user.api.dto.UserCardDto;
import user.api.dto.UserDetailDto;
import user.model.User;
import user.model.UserAddress;
import user.model.UserCard;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between model objects and API DTOs.
 */
@Singleton
public class DtoMapper {

    public User fromUserDto(UserDto userDto) {
        return new User(
                userDto.getUsername(),
                userDto.getPassword(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPhone(),
                userDto.getAddresses() == null ? null : userDto.getAddresses().stream().map(this::fromUserAddressDto).collect(Collectors.toList())
        );
    }

    public void copyFromUserDto(User user, UserDto userDto) {
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
    }

    public UserAddress fromUserAddressDto(UserAddressDto userAddressDto) {
        return new UserAddress(
                userAddressDto.getNumber(),
                userAddressDto.getStreet(),
                userAddressDto.getCity(),
                userAddressDto.getCountry(),
                userAddressDto.getPostcode()
        );
    }

    public UserCard fromUserCardDto(UserCardDto userCardDto) {
        final String last4Digits = userCardDto.getLongNum().substring(userCardDto.getLongNum().length() - 4);
        final String redactedNumber = "xxxxxxxxxxxx" + last4Digits;

        return new UserCard(
                last4Digits,
                redactedNumber,
                userCardDto.getExpires()
        );
    }

    public UserDetailDto toUserDetailDto(User user) {
        return toUserDetailDto(user, true, true);
    }

    public UserDetailDto toUserDetailDto(User user, boolean includeAddresses, boolean includeCards) {
        if (user == null) {
            return null;
        }
        return new UserDetailDto(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                includeAddresses ? toUserAddressDetailDtos(user.getAddresses(), false) : null,
                includeCards ? toUserCardsDetailDtos(user.getCards(), false) : null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public List<UserAddressDetailDto> toUserAddressDetailDtos(List<UserAddress> userAddresses, boolean includeUser) {
        if (userAddresses == null) {
            return null;
        }
        return userAddresses.stream()
                .map(userAddress -> toUserAddressDetailDto(userAddress, includeUser))
                .collect(Collectors.toList());
    }

    public List<UserCardDetailDto> toUserCardsDetailDtos(List<UserCard> userCards, boolean includeUser) {
        if (userCards == null) {
            return null;
        }
        return userCards.stream()
                .map(userCard -> toUserCardDetailDto(userCard, includeUser))
                .collect(Collectors.toList());
    }

    public UserAddressDetailDto toUserAddressDetailDto(UserAddress userAddress) {
        return toUserAddressDetailDto(userAddress, true);
    }

    public UserAddressDetailDto toUserAddressDetailDto(UserAddress userAddress, boolean includeUser) {
        return new UserAddressDetailDto(
                userAddress.getId(),
//                userAddress.getUserId(),
                userAddress.getNumber(),
                userAddress.getStreet(),
                userAddress.getCity(),
                userAddress.getCountry(),
                userAddress.getPostcode(),
                userAddress.getCreatedAt(),
                userAddress.getUpdatedAt(),
                includeUser ? toUserDetailDto(userAddress.getUser(), false, false) : null
        );
    }

    public UserCardDetailDto toUserCardDetailDto(UserCard userCard) {
        return toUserCardDetailDto(userCard, true);
    }

    public UserCardDetailDto toUserCardDetailDto(UserCard userCard, boolean includeUser) {
        return new UserCardDetailDto(
                userCard.getId(),
                userCard.getNumber(),
                userCard.getLongNum(),
                userCard.getExpires(),
                userCard.getCreatedAt(),
                userCard.getUpdatedAt(),
                includeUser ? toUserDetailDto(userCard.getUser(), false, false) : null
        );
    }
}
