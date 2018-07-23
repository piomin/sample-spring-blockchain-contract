package pl.piomin.services.contract;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
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

import pl.piomin.services.contract.model.Transactionfee;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ContractTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractTest.class);
	public static final BigInteger GAS_PRICE = BigInteger.valueOf(1L);
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(500_000L);
    public static final BigInteger AMOUNT = BigInteger.valueOf(10_000L);
	
	@Autowired
    Web3j web3j;
	Credentials credentials;
	
	@Before
	public void before() throws Exception {
		String file = WalletUtils.generateLightNewWalletFile("piot123", null);
    	credentials = WalletUtils.loadCredentials("piot123", file);
    	LOGGER.info("Credentials: address={}", credentials.getAddress());
    	EthCoinbase coinbase = web3j.ethCoinbase().send();
    	EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(coinbase.getAddress(), DefaultBlockParameterName.LATEST).send();
    	Transaction transaction = Transaction.createEtherTransaction(coinbase.getAddress(), transactionCount.getTransactionCount(), BigInteger.valueOf(20_000_000_000L), BigInteger.valueOf(21_000), credentials.getAddress(),BigInteger.valueOf(25_000_000_000_000_000L));
    	web3j.ethSendTransaction(transaction).send();
    	EthGetBalance balance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
    	LOGGER.info("Balance: {}", balance.getBalance().longValue());
	}
	
	@Test
	public void testTransactionFeeContract() throws Exception {
		Transactionfee contract = Transactionfee.deploy(web3j, credentials, GAS_PRICE, GAS_LIMIT).send();
		EthAccounts accounts = web3j.ethAccounts().send();
		EthGetBalance balance = web3j.ethGetBalance(accounts.getAccounts().get(1), DefaultBlockParameterName.LATEST).send();
    	LOGGER.info("Sending to: account={}, balance={}", accounts.getAccounts().get(1), balance.getBalance().longValue());
    	for (int i=0; i<5; i++) {
	    	TransactionReceipt tr = contract.send(accounts.getAccounts().get(1), AMOUNT).send();
	    	LOGGER.info("Transaction receipt: from={}, to={}, gas={}", tr.getFrom(), tr.getTo(), tr.getGasUsed().intValue());
	    	balance = web3j.ethGetBalance(accounts.getAccounts().get(1), DefaultBlockParameterName.LATEST).send();
	    	LOGGER.info("Sent to: account={}, balance={}", accounts.getAccounts().get(1), balance.getBalance().longValue());
	    	LOGGER.info("Contract: account={}, balance={}", accounts.getAccounts().get(1), contract.balances(accounts.getAccounts().get(1)).send().longValue());
	    	LOGGER.info("Contract To: account={}, balance={}", tr.getTo(), contract.balances(tr.getTo()).send().longValue());
    	}
	}
	
}
