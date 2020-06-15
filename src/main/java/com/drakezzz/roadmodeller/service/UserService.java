package com.drakezzz.roadmodeller.service;

import com.drakezzz.roadmodeller.persistence.entity.User;
import com.drakezzz.roadmodeller.web.dto.StatusResult;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<StatusResult> registerUser(User user);

}
