package com.harrydev.ticket_management_backend.mapper;

import com.harrydev.ticket_management_backend.dto.request.CreateUserRequestDTO;
import com.harrydev.ticket_management_backend.dto.request.UpdateUserRequestDTO;
import com.harrydev.ticket_management_backend.dto.response.UserResponse;
import com.harrydev.ticket_management_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toUser(CreateUserRequestDTO request); // Nhận request kiểu DTO và trả về kiểu User, tương ứng việc ta truyền
                                               // từng thuộc tính từ request vào cho entity User để lưu vào DB.

    @Mapping(target = "id", ignore = true)
    void updateUser(@MappingTarget User user, UpdateUserRequestDTO request);

    @Mapping(target = "id", ignore = true)
    UserResponse toUserResponse(User user); // Dùng để cấu hình bên controller => Controller không trả về User nữa mà
                                            // trả về các trường cần thiết
}
