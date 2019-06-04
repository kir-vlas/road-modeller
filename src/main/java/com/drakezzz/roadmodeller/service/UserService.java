package com.drakezzz.roadmodeller.service;

import com.drakezzz.roadmodeller.persistence.entity.User;
import com.drakezzz.roadmodeller.web.dto.StatusResult;

public interface UserService {

    StatusResult registerUser(User user);

}
