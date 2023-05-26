package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.Entity.Client;
import com.letseatall.letseatall.data.dto.Client.ClientDto;
import com.letseatall.letseatall.data.dto.Client.ClientResponseDto;
import com.letseatall.letseatall.data.dto.Client.LoginRequestDto;
import com.letseatall.letseatall.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }
    @GetMapping()
    public ResponseEntity<ClientResponseDto> getClient(Long id){
        ClientResponseDto returnClient = clientService.getClient(id);
        return ResponseEntity.status(HttpStatus.OK).body(returnClient);
    }
    @PostMapping()
    public ResponseEntity<ClientResponseDto> register(@RequestBody ClientDto clientDto){
        ClientResponseDto returnClient = clientService.saveClient(clientDto);
        if(returnClient.getScore() == -101)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(returnClient);
        else if (returnClient != null)
            return ResponseEntity.status(HttpStatus.OK).body(returnClient);

        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    @PostMapping("/login")
    public ResponseEntity<ClientResponseDto> tryLogin(@RequestBody LoginRequestDto loginRequestDto){
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
    @DeleteMapping()
    public ResponseEntity<String> deleteClient(Long id) throws Exception{
        String deletedId = clientService.deleteClient(id);

        return ResponseEntity.status(HttpStatus.OK).body("ID: "+ deletedId + "삭제 완료되었습니다.");
    }
}
