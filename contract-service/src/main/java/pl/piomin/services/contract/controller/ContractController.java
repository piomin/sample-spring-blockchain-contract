package pl.piomin.services.contract.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.piomin.services.contract.model.Contract;
import pl.piomin.services.contract.service.ContractService;

@RestController
@RequestMapping("/contract")
public class ContractController {

    @Autowired
    ContractService service;
    
    @GetMapping("/owner")
    public String getOwnerAccount() {
    	return service.getOwnerAccount();
    }
    
    @PostMapping
    public Contract createContract(@RequestBody Contract newContract) throws Exception {
    	return service.createContract(newContract);
    }
    
}
