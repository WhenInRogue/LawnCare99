package com.WhenInRogue.LawnCare99.services;

import com.WhenInRogue.LawnCare99.dtos.LoginRequest;
import com.WhenInRogue.LawnCare99.dtos.RegisterRequest;
import com.WhenInRogue.LawnCare99.dtos.Response;
import com.WhenInRogue.LawnCare99.dtos.UserDTO;
import com.WhenInRogue.LawnCare99.models.User;

public interface UserService {
    Response registerUser(RegisterRequest registerRequest);

    Response loginUser(LoginRequest loginRequest);

    Response getAllUsers();

    User getCurrentLoggedInUser();

    Response getUserById(Long id);

    Response updateUser(Long id, UserDTO userDTO);

    Response deleteUser(Long id);

    Response getUserSupplyTransactions(Long id);

}
