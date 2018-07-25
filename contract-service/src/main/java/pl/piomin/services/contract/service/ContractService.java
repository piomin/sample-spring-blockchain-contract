package pl.piomin.services.contract.service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCoinbase;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import pl.piomin.services.contract.model.Contract;
import pl.piomin.services.contract.model.Transactionfee;

@Service
public class ContractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractService.class);
    private static final String PASSWORD = "piot123";
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(1L);
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(500_000L);
    
    @Autowired
    Web3j web3j;
    Credentials credentials;
    private List<String> contracts = new ArrayList<>();
 
    @PostConstruct
    public void init() throws IOException, CipherException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
    	String file = WalletUtils.generateLightNewWalletFile(PASSWORD, null);
    	credentials = WalletUtils.loadCredentials(PASSWORD, file);
    	LOGGER.info("Credentials created: file={}, address={}", file, credentials.getAddress());
    	EthCoinbase coinbase = web3j.ethCoinbase().send();
    	EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(coinbase.getAddress(), DefaultBlockParameterName.LATEST).send();
    	Transaction transaction = Transaction.createEtherTransaction(coinbase.getAddress(), transactionCount.getTransactionCount(), BigInteger.valueOf(20_000_000_000L), BigInteger.valueOf(21_000), credentials.getAddress(),BigInteger.valueOf(25_000_000_000_000_000L));
    	web3j.ethSendTransaction(transaction).send();
    	EthGetBalance balance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
    	LOGGER.info("Balance: {}", balance.getBalance().longValue());
    }
    
    public String getOwnerAccount() {
    	return credentials.getAddress();
    }
    
    public Contract createContract(Contract newContract) throws Exception {
    	String file = WalletUtils.generateLightNewWalletFile(PASSWORD, null);
    	Credentials receiverCredentials = WalletUtils.loadCredentials(PASSWORD, file);
    	LOGGER.info("Credentials created: file={}, address={}", file, credentials.getAddress());
    	Transactionfee contract = Transactionfee.deploy(web3j, credentials, GAS_PRICE, GAS_LIMIT, receiverCredentials.getAddress(), BigInteger.valueOf(newContract.getFee())).send();
    	newContract.setReceiver(receiverCredentials.getAddress());
    	newContract.setAddress(contract.getContractAddress());
    	contracts.add(contract.getContractAddress());
    	LOGGER.info("New contract deployed: address={}", contract.getContractAddress());
    	Optional<TransactionReceipt> tr = contract.getTransactionReceipt();
    	if (tr.isPresent()) {
    		LOGGER.info("Transaction receipt: from={}, to={}, gas={}", tr.get().getFrom(), tr.get().getTo(), tr.get().getGasUsed().intValue());
    	}
    	return newContract;
    }
    
    public void processContracts(long transactionAmount) {
    	contracts.forEach(it -> {
    		Transactionfee contract = Transactionfee.load(it, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    		try {
				TransactionReceipt tr = contract.sendTrx(BigInteger.valueOf(transactionAmount)).send();
				LOGGER.info("Transaction receipt: from={}, to={}, gas={}", tr.getFrom(), tr.getTo(), tr.getGasUsed().intValue());
				LOGGER.info("Get receiver: {}", contract.getReceiverBalance().send().longValue());
				EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, contract.getContractAddress());
		    	web3j.ethLogObservable(filter).subscribe(log -> {
		    	    LOGGER.info("Log: {}", log.getData());
		    	});
			} catch (Exception e) {
				LOGGER.error("Error during contract execution", e);
			}
    	});
    }
    
}
