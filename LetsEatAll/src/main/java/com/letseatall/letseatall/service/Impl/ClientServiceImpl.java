package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.Client;
import com.letseatall.letseatall.data.Entity.Login;
import com.letseatall.letseatall.data.dto.Client.ClientDto;
import com.letseatall.letseatall.data.dto.Client.ClientResponseDto;
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
    public ClientResponseDto getClient(Long id) {
        Client client = clientRepository.getById(id);
        ClientResponseDto responseClient = ClientResponseDto.builder()
                .id(client.getId())
                .name(client.getName())
                .score(client.getScore())
                .birthDate(client.getBirthDate())
                .build();
        return responseClient;
    }

    @Override
    public ClientResponseDto saveClient(ClientDto clientDto) {
        if (loginRepository.existsById(clientDto.getId()))
            return ClientResponseDto.builder().name("id_duplication").score(-101).build();
        Client client = Client.builder()
                .name(clientDto.getName())
                .birthDate(clientDto.getBirthDate())
                .score(50)
                .build();
        Client savedClient = clientRepository.save(client);
        System.out.println(savedClient.getId());

        Login login = Login.builder()
                .id(clientDto.getId())
                .pw(clientDto.getPw())
                .uid(savedClient.getId())
                .build();
        loginRepository.save(login);


        return ClientResponseDto.builder()
                .id(savedClient.getId())
                .name(savedClient.getName())
                .score(savedClient.getScore())
                .birthDate(savedClient.getBirthDate())
                .build();
    }

    @Override
    public ClientResponseDto tryLogin(LoginRequestDto loginRequestDto) {
        Login foundLogin = loginRepository.findById(loginRequestDto.getId()).get();
        if (foundLogin == null)
            return null;

        if (loginRequestDto.getPw() != foundLogin.getPw())
            return ClientResponseDto.builder()
                    .id(444L)
                    .build();
        else{
            Client foundClient = clientRepository.findById(foundLogin.getUid()).get();

            return ClientResponseDto.builder()
                    .id(foundClient.getId())
                    .name(foundClient.getName())
                    .score(foundClient.getScore())
                    .birthDate(foundClient.getBirthDate())
                    .build();
        }
    }

    @Override
    public String deleteClient(Long uid) {
        clientRepository.deleteById(uid);
        String id = loginRepository.findIdByUid(uid);
        loginRepository.deleteById(id);

        return id;
    }
}
