package user.controllers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import user.controllers.dto.UserAddressDetailDto;
import user.controllers.dto.UserAddressDto;
import user.controllers.dto.UserCardDetailDto;
import user.controllers.dto.UserCardDto;
import user.controllers.dto.UserDetailDto;
import user.controllers.dto.UserDto;
import user.model.User;
import user.model.UserAddress;
import user.model.UserCard;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between model objects and API DTOs.
 */
@Mapper(componentModel = "jsr330")
public abstract class DtoMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "version")
    @Mapping(ignore = true, target = "cards")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    public abstract User fromUserDto(UserDto userDto);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "version")
    @Mapping(ignore = true, target = "user")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    public abstract UserAddress fromUserAddressDto(UserAddressDto userAddressDto);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "version")
    @Mapping(ignore = true, target = "cards")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    public abstract void copyFromUserDto(@MappingTarget User user, UserDto userDto);

    public abstract UserAddressDetailDto toUserAddressDetailDto(UserAddress userAddress);

    @Mapping(ignore = true, target = "user")
    public abstract UserAddressDetailDto toUserAddressDetailDtoNoUser(UserAddress userAddress);

    public abstract UserCardDetailDto toUserCardDetailDto(UserCard userCard);

    @Mapping(ignore = true, target = "user")
    public abstract UserCardDetailDto toUserCardDetailDtoNoUser(UserCard userCard);

    @Mapping(ignore = true, target = "addresses")
    @Mapping(ignore = true, target = "cards")
    public abstract UserDetailDto toSimpleUserDetailDto(User user);

    public UserCard fromUserCardDto(UserCardDto userCardDto) {
        final String last4Digits = userCardDto.getLongNum().substring(userCardDto.getLongNum().length() - 4);
        final String redactedNumber = "xxxxxxxxxxxx" + last4Digits;

        return new UserCard(
                last4Digits,
                redactedNumber,
                userCardDto.getExpires()
        );
    }

    public UserDetailDto toUserDetailDto(User user, boolean includeAddresses, boolean includeCards) {
        if (user == null) {
            return null;
        }
        final UserDetailDto userDetailDto = toSimpleUserDetailDto(user);
        if (includeAddresses) {
            userDetailDto.setAddresses(toUserAddressDetailDtos(user.getAddresses()));
        }
        if (includeCards) {
            userDetailDto.setCards(toUserCardsDetailDtos(user.getCards()));
        }
        return userDetailDto;
    }

    public List<UserAddressDetailDto> toUserAddressDetailDtos(List<UserAddress> userAddresses) {
        if (userAddresses == null) {
            return null;
        }
        return userAddresses.stream()
                .map(this::toUserAddressDetailDtoNoUser)
                .collect(Collectors.toList());
    }

    public List<UserCardDetailDto> toUserCardsDetailDtos(List<UserCard> userCards) {
        if (userCards == null) {
            return null;
        }
        return userCards.stream()
                .map(this::toUserCardDetailDtoNoUser)
                .collect(Collectors.toList());
    }

}
