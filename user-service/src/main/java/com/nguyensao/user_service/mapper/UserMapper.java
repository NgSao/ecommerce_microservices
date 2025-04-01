package com.nguyensao.user_service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nguyensao.user_service.dto.AddressDto;
import com.nguyensao.user_service.dto.UserDto;
import com.nguyensao.user_service.dto.request.UserRegisterRequest;
import com.nguyensao.user_service.dto.request.UserUpdateRequest;
import com.nguyensao.user_service.model.Address;
import com.nguyensao.user_service.model.User;

@Component
public class UserMapper {

    public User toUserUpdatedRequest(UserUpdateRequest request) {
        return User.builder()
                .fullname(request.getFullname())
                .birthday(request.getBirthday())
                .profileImageUrl(request.getProfileImageUrl())
                .gender(request.getGender())
                .build();
    }

    public User toUserRegisterRequest(UserRegisterRequest request) {
        return User.builder()
                .fullname(request.getFullname())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .status(request.getStatus())
                .build();
    }

    public UserDto userToDto(User user) {
        if (user == null)
            return null;
        return UserDto.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User userToEntity(UserDto dto) {
        if (dto == null)
            return null;
        return User.builder()
                .fullname(dto.getFullname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .birthday(dto.getBirthday())
                .gender(dto.getGender())
                .profileImageUrl(dto.getProfileImageUrl() != null ? dto.getProfileImageUrl() : "khachhang.png")
                .lastLoginDate(dto.getLastLoginDate())
                .role(dto.getRole())
                .status(dto.getStatus())
                .build();
    }

    public List<UserDto> userToDtoList(List<User> users) {
        return users.stream().map(this::userToDto).collect(Collectors.toList());
    }

    // --------------------
    public AddressDto addressToDto(Address address) {
        if (address == null)
            return null;
        return AddressDto.builder()
                .id(address.getId())
                .fullname(address.getFullname())
                .phone(address.getPhone())
                .city(address.getCity())
                .district(address.getDistrict())
                .street(address.getStreet())
                .addressDetail(address.getAddressDetail())
                .active(address.getActive())
                .email(address.getUser() != null ? address.getUser().getEmail() : null)
                .build();
    }

    public Address addressToEntity(AddressDto dto) {
        if (dto == null)
            return null;

        return Address.builder()
                .id(dto.getId())
                .fullname(dto.getFullname())
                .phone(dto.getPhone())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .street(dto.getStreet())
                .addressDetail(dto.getAddressDetail())
                .active(dto.getActive())
                .build();
    }

    public List<AddressDto> addressToDtoList(List<Address> addresses) {
        return addresses.stream().map(this::addressToDto).collect(Collectors.toList());
    }

}
