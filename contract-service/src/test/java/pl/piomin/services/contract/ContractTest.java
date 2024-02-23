package pl.piomin.services.contract;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
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
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import pl.piomin.services.contract.model.TransactionFee;

import java.math.BigInteger;

@SpringBootTest
@Testcontainers
public class ContractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractTest.class);
    public static final BigInteger GAS_PRICE = BigInteger.valueOf(1L);
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(500_000L);
    public static final BigInteger AMOUNT = BigInteger.valueOf(10_000L);
    public static final BigInteger FEE = BigInteger.valueOf(10L);

    @Autowired
    Web3j web3j;
    Credentials credentialsFrom;
    Credentials credentialsTo;

    @Container
    static final GenericContainer clientEthereum = new GenericContainer("ethereum/client-go")
            .withCommand("--http", "--http.corsdomain=*", "--http.addr=0.0.0.0", "--dev")
            .withExposedPorts(8545);

    @DynamicPropertySource
    static void registerCeProperties(DynamicPropertyRegistry registry) {
        registry.add("web3j.client-address",
                () -> String.format("http://localhost:%d", clientEthereum.getFirstMappedPort()));
    }

    @BeforeEach
    public void before() throws Exception {
//		String file = WalletUtils.generateLightNewWalletFile("piot123", null);
//		String file = "src/test/resources/john_smith.json";
        credentialsFrom = WalletUtils.loadCredentials("piot123", "src/test/resources/john_smith.json");
        credentialsTo = WalletUtils.loadCredentials("piot123", "src/test/resources/jason_sullivan.json");
        LOGGER.info("Credentials: address={}", credentialsFrom.getAddress());
        EthCoinbase coinbase = web3j.ethCoinbase().send();
        EthGetTransactionCount transactionCount = web3j.ethGetTransactionCount(coinbase.getAddress(), DefaultBlockParameterName.LATEST).send();
        Transaction transaction = Transaction.createEtherTransaction(coinbase.getAddress(), transactionCount.getTransactionCount(), BigInteger.valueOf(20_000_000_000L), BigInteger.valueOf(21_000), credentialsFrom.getAddress(), BigInteger.valueOf(25_000_000_000_000_000L));
        web3j.ethSendTransaction(transaction).send();
        EthGetBalance balance = web3j.ethGetBalance(credentialsFrom.getAddress(), DefaultBlockParameterName.LATEST).send();
        LOGGER.info("Balance: {}", balance.getBalance().longValue());
    }

    @Test
    public void testTransactionFeeContract() throws Exception {
        long chainId = 56;
        TransactionManager txManager = new RawTransactionManager(web3j, credentialsFrom, chainId);
        TransactionFee contract = TransactionFee.deploy(web3j, credentialsFrom, GAS_PRICE, GAS_LIMIT, "0xd7850bd94f189ce38ce5729052926094997de310", FEE).send();
        EthGetBalance balance = web3j.ethGetBalance(credentialsTo.getAddress(), DefaultBlockParameterName.LATEST).send();
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, contract.getContractAddress());
        LOGGER.info("Sending to: account={}, balance={}", "0xd7850bd94f189ce38ce5729052926094997de310", balance.getBalance().longValue());
        for (int i = 0; i < 3; i++) {
            TransactionReceipt tr = contract.sendTrx(AMOUNT).send();
            LOGGER.info("Transaction receipt: from={}, to={}, gas={}", tr.getFrom(), tr.getTo(), tr.getGasUsed().intValue());
            balance = web3j.ethGetBalance(credentialsFrom.getAddress(), DefaultBlockParameterName.LATEST).send();
            LOGGER.info("From balance after: account={}, balance={}", "0xd7850bd94f189ce38ce5729052926094997de310", balance.getBalance().longValue());
            balance = web3j.ethGetBalance(credentialsTo.getAddress(), DefaultBlockParameterName.LATEST).send();
            LOGGER.info("To balance after: account={}, balance={}", "0xd7850bd94f189ce38ce5729052926094997de310", balance.getBalance().longValue());
            LOGGER.info("Contract: account={}, balance={}", "0xd7850bd94f189ce38ce5729052926094997de310", contract.balances(credentialsTo.getAddress()).send().longValue());
            LOGGER.info("Get receiver: {}", contract.getReceiverBalance().send().longValue());
//	    	LOGGER.info("Contract To: account={}, balance={}", tr.getTo(), contract.balances(tr.getTo()).send().longValue());
//	    	balance = web3j.ethGetBalance(tr.getTo(), DefaultBlockParameterName.LATEST).send();
//	    	LOGGER.info("Contract To 2: account={}, balance={}", credentialsTo.getAddress(), balance.getBalance().longValue());
        }

        web3j.ethLogFlowable(filter).subscribe(log -> {
            LOGGER.info("Log: {}", log.getData());
        });
        Thread.sleep(2000);
    }

}
