package pl.piomin.services.contract.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthCoinbase;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import pl.piomin.services.contract.model.Contract;
import pl.piomin.services.contract.model.Transactionfee;

@RestController
@RequestMapping("/contract")
public class ContractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractController.class);
    public static final BigInteger GAS_PRICE = BigInteger.valueOf(1L);
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(500_000L);
    int ownerId = 1;
    
    @Autowired
    Web3j web3j;
    Credentials credentials;
    
    @PostConstruct
    public void init() throws IOException, CipherException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
    	String file = WalletUtils.generateLightNewWalletFile("piot123", null);
    	credentials = WalletUtils.loadCredentials("piot123", file);
    	LOGGER.info("Credentials created: file={}, address={}", file, credentials.getAddress());
    	EthCoinbase coinbase = web3j.ethCoinbase().send();
    	EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(coinbase.getAddress(), DefaultBlockParameterName.LATEST).send();
    	Transaction transaction = Transaction.createEtherTransaction(coinbase.getAddress(), transactionCount.getTransactionCount(), BigInteger.valueOf(20_000_000_000L), BigInteger.valueOf(21_000), credentials.getAddress(),BigInteger.valueOf(25_000_000_000_000_000L));
    	web3j.ethSendTransaction(transaction).send();
    	EthGetBalance balance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
    	LOGGER.info("Balance: {}", balance.getBalance().longValue());
    	
    	EthAccounts accounts = web3j.ethAccounts().send();
    	balance = web3j.ethGetBalance(accounts.getAccounts().get(ownerId), DefaultBlockParameterName.LATEST).send();
    	LOGGER.info("Owner balance: {}", balance.getBalance().longValue());
    }
    
    @PostMapping
    public String createContract(@RequestBody Contract newContract) throws Exception {
    	Transactionfee contract = Transactionfee.deploy(web3j, credentials, GAS_PRICE, GAS_LIMIT, BigInteger.valueOf(newContract.getAmount())).send();
    	Optional<TransactionReceipt> tr = contract.getTransactionReceipt();
    	if (tr.isPresent()) {
    		LOGGER.info("Transaction receipt: from={}, to={}, gas={}", tr.get().getFrom(), tr.get().getTo(), tr.get().getGasUsed().intValue());
    	}
    	return contract.getContractAddress();
    }
    
    @PostMapping("/process")
    public String processContract(@RequestBody pl.piomin.services.contract.model.Transaction transaction) throws Exception {
    	EthAccounts accounts = web3j.ethAccounts().send();
    	Transactionfee contract = Transactionfee.load(transaction.getContract(), web3j, credentials, GAS_PRICE, GAS_LIMIT);
    	LOGGER.info("Sending to: {}", accounts.getAccounts().get(ownerId));
    	TransactionReceipt tr = contract.sendTrx(accounts.getAccounts().get(ownerId), BigInteger.valueOf(transaction.getAmount())).send();
    	LOGGER.info("Transaction receipt: from={}, to={}, gas={}", tr.getFrom(), tr.getTo(), tr.getGasUsed().intValue());
    	EthGetBalance balance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
    	LOGGER.info("Current sender balance: {}", balance.getBalance().longValue());
    	
    	
    	balance = web3j.ethGetBalance(tr.getTo(), DefaultBlockParameterName.LATEST).send();
    	LOGGER.info("Owner balance: {}", balance.getBalance().longValue());
    	balance = web3j.ethGetBalance(tr.getFrom(), DefaultBlockParameterName.LATEST).send();
    	LOGGER.info("Sender balance: {}", balance.getBalance().longValue());
    	
    	LOGGER.info("Contract sender balance: {}", contract.getBalances().send().longValue());
    	LOGGER.info("Contract owner balance: {}", contract.balances(accounts.getAccounts().get(ownerId)).send().longValue());
    	return tr.getTransactionHash();
    }
    
}
