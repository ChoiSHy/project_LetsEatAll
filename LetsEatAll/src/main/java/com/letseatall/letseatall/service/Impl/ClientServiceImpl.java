package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.Client;
import com.letseatall.letseatall.data.Entity.Login;
import com.letseatall.letseatall.data.dto.Client.ClientDto;
import com.letseatall.letseatall.data.dto.Client.LoginRequestDto;
import com.letseatall.letseatall.data.repository.ClientRepository;
import com.letseatall.letseatall.data.repository.LoginRepository;
import com.letseatall.letseatall.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final LoginRepository loginRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, LoginRepository loginRepository) {
        this.clientRepository = clientRepository;
        this.loginRepository = loginRepository;
    }

    @Override
    public Client getClient(Long id) {
        Client client = clientRepository.findById(id).get();
        return client;
    }

    @Override
    public Client saveClient(ClientDto clientDto) {
        Client client = Client.builder()
                .name(clientDto.getName())
                .birthDate(clientDto.getBirthDate())
                .score(50)
                .build();
        Client savedClient = clientRepository.save(client);
        Login login = Login.builder()
                .id(clientDto.getId())
                .pw(clientDto.getPw())
                .uid(client.getId())
                .build();
        loginRepository.save(login);
        return savedClient;
    }

    @Override
    public Client tryLogin(LoginRequestDto loginRequestDto) {
        Login foundLogin = loginRepository.findById(loginRequestDto.getId()).get();
        if (foundLogin == null)
            return null;

        if (loginRequestDto.getPw() != foundLogin.getPw())
            return null;
        else{
            Client foundClient = clientRepository.findById(foundLogin.getUid()).get();
            return foundClient;
        }

    }
}
