package pl.piomin.services.contract.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class Transactionfee extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b506040516020806101e883398101604052516000556101b4806100346000396000f3006080604052600436106100565763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166327e235e3811461005b57806363d70ec71461009b578063ddca3f43146100ce575b600080fd5b34801561006757600080fd5b5061008973ffffffffffffffffffffffffffffffffffffffff600435166100e3565b60408051918252519081900360200190f35b3480156100a757600080fd5b506100cc73ffffffffffffffffffffffffffffffffffffffff600435166024356100f5565b005b3480156100da57600080fd5b50610089610182565b60016020526000908152604090205481565b6000805433808352600160209081526040808520805460649588028690049003905573ffffffffffffffffffffffffffffffffffffffff871680865281862080548801905594548151938452918301949094528402919091048183015290517f3990db2d31862302a685e8086b5755072a6e2b5b780af1ee81ece35ee3cd33459181900360600190a15050565b600054815600a165627a7a72305820358088d7158ca7943293b603366a8446871367db1f00857b2274dbece5e710f00029";

    public static final String FUNC_BALANCES = "balances";

    public static final String FUNC_SENDFEE = "sendFee";

    public static final String FUNC_FEE = "fee";

    public static final Event SENT_EVENT = new Event("Sent", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    protected Transactionfee(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Transactionfee(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<BigInteger> balances(String param0) {
        final Function function = new Function(FUNC_BALANCES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<TransactionReceipt> sendFee(String owner, BigInteger amount) {
        final Function function = new Function(
                FUNC_SENDFEE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @SuppressWarnings("rawtypes")
    public RemoteCall<BigInteger> fee() {
        final Function function = new Function(FUNC_FEE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @SuppressWarnings("rawtypes")
    public static RemoteCall<Transactionfee> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger _fee) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_fee)));
        return deployRemoteCall(Transactionfee.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @SuppressWarnings("rawtypes")
    public static RemoteCall<Transactionfee> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger _fee) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_fee)));
        return deployRemoteCall(Transactionfee.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<SentEventResponse> getSentEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SENT_EVENT, transactionReceipt);
        ArrayList<SentEventResponse> responses = new ArrayList<SentEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SentEventResponse typedResponse = new SentEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<SentEventResponse> sentEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, SentEventResponse>() {
            @Override
            public SentEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SENT_EVENT, log);
                SentEventResponse typedResponse = new SentEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<SentEventResponse> sentEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SENT_EVENT));
        return sentEventObservable(filter);
    }

    public static Transactionfee load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Transactionfee(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Transactionfee load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Transactionfee(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class SentEventResponse {
        public Log log;

        public String from;

        public String to;

        public BigInteger amount;
    }
}
