package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.Entity.Client;
import com.letseatall.letseatall.data.Entity.Login;
import com.letseatall.letseatall.data.dto.Client.ClientDto;
import com.letseatall.letseatall.data.dto.Client.ClientResponseDto;
import com.letseatall.letseatall.data.dto.Client.LoginRequestDto;

public interface ClientService {
    ClientResponseDto getClient(Long id);
    ClientResponseDto saveClient(ClientDto clientDto);
    ClientResponseDto tryLogin(LoginRequestDto loginRequestDto);
    String deleteClient(Long id);
}
