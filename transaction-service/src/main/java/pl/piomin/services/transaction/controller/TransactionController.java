package pl.piomin.services.transaction.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthCoinbase;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import pl.piomin.services.transaction.model.TransactionRequest;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);
    public static final BigInteger GAS_PRICE = BigInteger.valueOf(1L);
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(21_000L);
    
    @Value("${contract-service.url}")
    String url;
    @Autowired
    Web3j web3j;
    @Autowired
    RestTemplate template;
    Credentials credentials;
    
    @PostConstruct
    public void init() throws IOException, CipherException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {    	
    	EthCoinbase coinbase = web3j.ethCoinbase().send();
    	EthAccounts accounts = web3j.ethAccounts().send();
    	for (int i = 1; i < accounts.getAccounts().size(); i++) {
    		EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(coinbase.getAddress(), DefaultBlockParameterName.LATEST).send();
            Transaction transaction = Transaction.createEtherTransaction(coinbase.getAddress(), transactionCount.getTransactionCount(), GAS_PRICE, GAS_LIMIT, accounts.getAccounts().get(i), BigInteger.valueOf(25_000_000_000L));
            web3j.ethSendTransaction(transaction).send();
            EthGetBalance balance = web3j.ethGetBalance(accounts.getAccounts().get(i), DefaultBlockParameterName.LATEST).send();
        	LOGGER.info("Balance: address={}, amount={}", accounts.getAccounts().get(i), balance.getBalance().longValue());
		}
    }
    
    @PostMapping
    public String performTransaction(@RequestBody TransactionRequest request) throws Exception {
    	EthAccounts accounts = web3j.ethAccounts().send();
    	String owner = template.getForObject(url, String.class);
    	EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(accounts.getAccounts().get(request.getFromId()), DefaultBlockParameterName.LATEST).send();
        Transaction transaction = Transaction.createEtherTransaction(accounts.getAccounts().get(request.getFromId()), transactionCount.getTransactionCount(), GAS_PRICE, GAS_LIMIT, owner, BigInteger.valueOf(request.getAmount()));
        EthSendTransaction response = web3j.ethSendTransaction(transaction).send();
        if (response.getError() != null) {
        	LOGGER.error("Transaction error: {}", response.getError().getMessage());
        	return "ERR";
        }
        LOGGER.info("Transaction: {}", response.getResult());
        EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(response.getTransactionHash()).send();
        if (receipt.getTransactionReceipt().isPresent()) {
        	TransactionReceipt r = receipt.getTransactionReceipt().get();
            LOGGER.info("Tx receipt: from={}, to={}, gas={}, cumulativeGas={}", r.getFrom(), r.getTo(), r.getGasUsed().intValue(), r.getCumulativeGasUsed().intValue());
        }        
        EthGetBalance balance = web3j.ethGetBalance(accounts.getAccounts().get(request.getFromId()), DefaultBlockParameterName.LATEST).send();
    	LOGGER.info("Balance: address={}, amount={}", accounts.getAccounts().get(request.getFromId()), balance.getBalance().longValue());
    	balance = web3j.ethGetBalance(owner, DefaultBlockParameterName.LATEST).send();
    	LOGGER.info("Balance: address={}, amount={}", owner, balance.getBalance().longValue());
    	return response.getTransactionHash();
    }    
    
}
