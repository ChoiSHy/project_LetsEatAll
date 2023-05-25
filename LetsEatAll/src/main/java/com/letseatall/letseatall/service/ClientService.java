package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.Entity.Client;
import com.letseatall.letseatall.data.Entity.Login;
import com.letseatall.letseatall.data.dto.Client.ClientDto;
import com.letseatall.letseatall.data.dto.Client.LoginRequestDto;

public interface ClientService {
    Client getClient(Long id);
    Client saveClient(ClientDto clientDto);
    Client tryLogin(LoginRequestDto loginRequestDto);
}
