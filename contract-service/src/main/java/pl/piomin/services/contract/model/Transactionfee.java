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
import org.web3j.abi.datatypes.Bool;
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
    private static final String BINARY = "608060405234801561001057600080fd5b506040516040806102c783398101604052805160209091015160018054600160a060020a03909316600160a060020a03199093169290921790915560005561026a8061005d6000396000f30060806040526004361061006c5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166310fb7bc6811461007157806327e235e314610098578063d0f452dd146100c6578063ddca3f43146100d0578063f7260d3e146100e5575b600080fd5b34801561007d57600080fd5b50610086610123565b60408051918252519081900360200190f35b3480156100a457600080fd5b5061008673ffffffffffffffffffffffffffffffffffffffff60043516610140565b6100ce610152565b005b3480156100dc57600080fd5b5061008661021c565b3480156100f157600080fd5b506100fa610222565b6040805173ffffffffffffffffffffffffffffffffffffffff9092168252519081900360200190f35b60015473ffffffffffffffffffffffffffffffffffffffff163190565b60026020526000908152604090205481565b600080548190606490340260015460405192909104935073ffffffffffffffffffffffffffffffffffffffff169083156108fc029084906000818181858888f16001805473ffffffffffffffffffffffffffffffffffffffff90811660009081526002602090815260409182902080548c01905592548151338152921692820192909252808201899052821515606082015290519196507f4a7ca93981c43b6021cfef8fa7764ad1a6abd748f97622b8fc50d887bf603c6495508190036080019350915050a15050565b60005481565b60015473ffffffffffffffffffffffffffffffffffffffff16815600a165627a7a7230582056902cd076d831b0bb2b8ffad0441a608f0fc2f58ca92718e460b34e34f24cad0029";

    public static final String FUNC_GETRECEIVERBALANCE = "getReceiverBalance";

    public static final String FUNC_BALANCES = "balances";

    public static final String FUNC_SENDTRX = "sendTrx";

    public static final String FUNC_FEE = "fee";

    public static final String FUNC_RECEIVER = "receiver";

    public static final Event SENT_EVENT = new Event("Sent", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
    ;

    protected Transactionfee(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Transactionfee(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<BigInteger> getReceiverBalance() {
        final Function function = new Function(FUNC_GETRECEIVERBALANCE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> balances(String param0) {
        final Function function = new Function(FUNC_BALANCES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> sendTrx(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_SENDTRX, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<BigInteger> fee() {
        final Function function = new Function(FUNC_FEE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> receiver() {
        final Function function = new Function(FUNC_RECEIVER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<Transactionfee> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _receiver, BigInteger _fee) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_receiver), 
                new org.web3j.abi.datatypes.generated.Uint256(_fee)));
        return deployRemoteCall(Transactionfee.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<Transactionfee> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _receiver, BigInteger _fee) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_receiver), 
                new org.web3j.abi.datatypes.generated.Uint256(_fee)));
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
            typedResponse.sent = (Boolean) eventValues.getNonIndexedValues().get(3).getValue();
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
                typedResponse.sent = (Boolean) eventValues.getNonIndexedValues().get(3).getValue();
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

        public Boolean sent;
    }
}
