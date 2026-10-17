package com.registro.CruzMarket.servicio.interfaces;

import com.registro.CruzMarket.dto.LoginRequest;
import com.registro.CruzMarket.dto.LoginResponse;

public interface IAuthServicio {
    LoginResponse authenticate(LoginRequest loginRequest);
}