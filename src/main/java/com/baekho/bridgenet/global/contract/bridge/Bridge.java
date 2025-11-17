package com.baekho.bridgenet.global.blockchain.bridgenet;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.CustomError;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple9;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/LFDT-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.7.0.
 */
@SuppressWarnings("rawtypes")
public class Bridge extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_ADDCHAIN = "addChain";

    public static final String FUNC_CANCELREQUEST = "cancelRequest";

    public static final String FUNC_CHAINLIST = "chainList";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_REMOVECHAIN = "removeChain";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_REQUEST = "request";

    public static final String FUNC_REQUESTLIST = "requestList";

    public static final String FUNC_SETREQUEST = "setRequest";

    public static final String FUNC_SETWHITELIST = "setWhiteList";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_TRIGGERPAYOUT = "triggerPayout";

    public static final String FUNC_WHITELIST = "whiteList";

    public static final CustomError CHAINALREADYEXISTS_ERROR = new CustomError("ChainAlreadyExists", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final CustomError CHAINALREADYREMOVED_ERROR = new CustomError("ChainAlreadyRemoved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final CustomError INCORRECTCHAINID_ERROR = new CustomError("IncorrectChainId", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final CustomError INCORRECTREQUESTID_ERROR = new CustomError("IncorrectRequestId", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final CustomError OWNABLEINVALIDOWNER_ERROR = new CustomError("OwnableInvalidOwner", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final CustomError OWNABLEUNAUTHORIZEDACCOUNT_ERROR = new CustomError("OwnableUnauthorizedAccount", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final CustomError WHITELISTUNAUTHORIZEDACCOUNT_ERROR = new CustomError("WhiteListUnauthorizedAccount", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final Event CHAINLISTUPDATED_EVENT = new Event("ChainListUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Bool>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event REQUESTED_EVENT = new Event("Requested", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<RequestInfo>() {}));
    ;

    public static final Event SETREQUESTED_EVENT = new Event("SetRequested", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint8>(true) {}));
    ;

    public static final Event TRIGGERPAYOUTED_EVENT = new Event("TriggerPayouted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event WHITELISTUPDATED_EVENT = new Event("WhitelistUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bool>() {}));
    ;

    @Deprecated
    protected Bridge(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Bridge(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Bridge(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Bridge(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<ChainListUpdatedEventResponse> getChainListUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CHAINLISTUPDATED_EVENT, transactionReceipt);
        ArrayList<ChainListUpdatedEventResponse> responses = new ArrayList<ChainListUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ChainListUpdatedEventResponse typedResponse = new ChainListUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.chainId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.status = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ChainListUpdatedEventResponse getChainListUpdatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CHAINLISTUPDATED_EVENT, log);
        ChainListUpdatedEventResponse typedResponse = new ChainListUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.chainId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.status = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ChainListUpdatedEventResponse> chainListUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getChainListUpdatedEventFromLog(log));
    }

    public Flowable<ChainListUpdatedEventResponse> chainListUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CHAINLISTUPDATED_EVENT));
        return chainListUpdatedEventFlowable(filter);
    }

    public static List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnershipTransferredEventResponse getOwnershipTransferredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
        OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
        typedResponse.log = log;
        typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnershipTransferredEventFromLog(log));
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public static List<RequestedEventResponse> getRequestedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REQUESTED_EVENT, transactionReceipt);
        ArrayList<RequestedEventResponse> responses = new ArrayList<RequestedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RequestedEventResponse typedResponse = new RequestedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.requestAddress = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.request = (RequestInfo) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RequestedEventResponse getRequestedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(REQUESTED_EVENT, log);
        RequestedEventResponse typedResponse = new RequestedEventResponse();
        typedResponse.log = log;
        typedResponse.requestAddress = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.request = (RequestInfo) eventValues.getNonIndexedValues().get(0);
        return typedResponse;
    }

    public Flowable<RequestedEventResponse> requestedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRequestedEventFromLog(log));
    }

    public Flowable<RequestedEventResponse> requestedEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REQUESTED_EVENT));
        return requestedEventFlowable(filter);
    }

    public static List<SetRequestedEventResponse> getSetRequestedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(SETREQUESTED_EVENT, transactionReceipt);
        ArrayList<SetRequestedEventResponse> responses = new ArrayList<SetRequestedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SetRequestedEventResponse typedResponse = new SetRequestedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.requestId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.requestStatus = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static SetRequestedEventResponse getSetRequestedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(SETREQUESTED_EVENT, log);
        SetRequestedEventResponse typedResponse = new SetRequestedEventResponse();
        typedResponse.log = log;
        typedResponse.requestId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.requestStatus = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<SetRequestedEventResponse> setRequestedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getSetRequestedEventFromLog(log));
    }

    public Flowable<SetRequestedEventResponse> setRequestedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SETREQUESTED_EVENT));
        return setRequestedEventFlowable(filter);
    }

    public static List<TriggerPayoutedEventResponse> getTriggerPayoutedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TRIGGERPAYOUTED_EVENT, transactionReceipt);
        ArrayList<TriggerPayoutedEventResponse> responses = new ArrayList<TriggerPayoutedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TriggerPayoutedEventResponse typedResponse = new TriggerPayoutedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._address = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TriggerPayoutedEventResponse getTriggerPayoutedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TRIGGERPAYOUTED_EVENT, log);
        TriggerPayoutedEventResponse typedResponse = new TriggerPayoutedEventResponse();
        typedResponse.log = log;
        typedResponse._address = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<TriggerPayoutedEventResponse> triggerPayoutedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTriggerPayoutedEventFromLog(log));
    }

    public Flowable<TriggerPayoutedEventResponse> triggerPayoutedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRIGGERPAYOUTED_EVENT));
        return triggerPayoutedEventFlowable(filter);
    }

    public static List<WhitelistUpdatedEventResponse> getWhitelistUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(WHITELISTUPDATED_EVENT, transactionReceipt);
        ArrayList<WhitelistUpdatedEventResponse> responses = new ArrayList<WhitelistUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            WhitelistUpdatedEventResponse typedResponse = new WhitelistUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._address = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.status = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static WhitelistUpdatedEventResponse getWhitelistUpdatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(WHITELISTUPDATED_EVENT, log);
        WhitelistUpdatedEventResponse typedResponse = new WhitelistUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse._address = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.status = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<WhitelistUpdatedEventResponse> whitelistUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getWhitelistUpdatedEventFromLog(log));
    }

    public Flowable<WhitelistUpdatedEventResponse> whitelistUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(WHITELISTUPDATED_EVENT));
        return whitelistUpdatedEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> addChain(BigInteger chainId) {
        final Function function = new Function(
                FUNC_ADDCHAIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(chainId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> cancelRequest(BigInteger requestId) {
        final Function function = new Function(
                FUNC_CANCELREQUEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(requestId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, Boolean>> chainList(BigInteger param0) {
        final Function function = new Function(FUNC_CHAINLIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, Boolean>>(function,
                new Callable<Tuple2<BigInteger, Boolean>>() {
                    @Override
                    public Tuple2<BigInteger, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, Boolean>(
                                (BigInteger) results.get(0).getValue(), 
                                (Boolean) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> removeChain(BigInteger chainId) {
        final Function function = new Function(
                FUNC_REMOVECHAIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(chainId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> request(BigInteger toChainId, BigInteger _value,
            BigInteger weiValue) {
        final Function function = new Function(
                FUNC_REQUEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(toChainId), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<Tuple9<BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger>> requestList(
            BigInteger param0) {
        final Function function = new Function(FUNC_REQUESTLIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple9<BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger>>(function,
                new Callable<Tuple9<BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger>>() {
                    @Override
                    public Tuple9<BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> call(
                            ) throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple9<BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue(), 
                                (String) results.get(7).getValue(), 
                                (BigInteger) results.get(8).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> setRequest(BigInteger requestId,
            BigInteger status) {
        final Function function = new Function(
                FUNC_SETREQUEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(requestId), 
                new org.web3j.abi.datatypes.generated.Uint8(status)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setWhiteList(String _address, Boolean status) {
        final Function function = new Function(
                FUNC_SETWHITELIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _address), 
                new org.web3j.abi.datatypes.Bool(status)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> triggerPayout(String _address,
            BigInteger _value) {
        final Function function = new Function(
                FUNC_TRIGGERPAYOUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _address), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> whiteList(String param0) {
        final Function function = new Function(FUNC_WHITELIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    @Deprecated
    public static Bridge load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new Bridge(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Bridge load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Bridge(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Bridge load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new Bridge(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Bridge load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Bridge(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class RequestInfo extends StaticStruct {
        public BigInteger id;

        public String requestBy;

        public BigInteger fromChainId;

        public BigInteger fromValue;

        public BigInteger toChainId;

        public BigInteger toValue;

        public BigInteger status;

        public String statusDecidedBy;

        public BigInteger exchangedAt;

        public RequestInfo(BigInteger id, String requestBy, BigInteger fromChainId,
                BigInteger fromValue, BigInteger toChainId, BigInteger toValue, BigInteger status,
                String statusDecidedBy, BigInteger exchangedAt) {
            super(new org.web3j.abi.datatypes.generated.Uint256(id), 
                    new org.web3j.abi.datatypes.Address(160, requestBy), 
                    new org.web3j.abi.datatypes.generated.Uint256(fromChainId), 
                    new org.web3j.abi.datatypes.generated.Uint256(fromValue), 
                    new org.web3j.abi.datatypes.generated.Uint256(toChainId), 
                    new org.web3j.abi.datatypes.generated.Uint256(toValue), 
                    new org.web3j.abi.datatypes.generated.Uint8(status), 
                    new org.web3j.abi.datatypes.Address(160, statusDecidedBy), 
                    new org.web3j.abi.datatypes.generated.Uint256(exchangedAt));
            this.id = id;
            this.requestBy = requestBy;
            this.fromChainId = fromChainId;
            this.fromValue = fromValue;
            this.toChainId = toChainId;
            this.toValue = toValue;
            this.status = status;
            this.statusDecidedBy = statusDecidedBy;
            this.exchangedAt = exchangedAt;
        }

        public RequestInfo(Uint256 id, Address requestBy, Uint256 fromChainId, Uint256 fromValue,
                Uint256 toChainId, Uint256 toValue, Uint8 status, Address statusDecidedBy,
                Uint256 exchangedAt) {
            super(id, requestBy, fromChainId, fromValue, toChainId, toValue, status, statusDecidedBy, exchangedAt);
            this.id = id.getValue();
            this.requestBy = requestBy.getValue();
            this.fromChainId = fromChainId.getValue();
            this.fromValue = fromValue.getValue();
            this.toChainId = toChainId.getValue();
            this.toValue = toValue.getValue();
            this.status = status.getValue();
            this.statusDecidedBy = statusDecidedBy.getValue();
            this.exchangedAt = exchangedAt.getValue();
        }
    }

    public static class ChainListUpdatedEventResponse extends BaseEventResponse {
        public BigInteger chainId;

        public Boolean status;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class RequestedEventResponse extends BaseEventResponse {
        public String requestAddress;

        public RequestInfo request;
    }

    public static class SetRequestedEventResponse extends BaseEventResponse {
        public BigInteger requestId;

        public BigInteger requestStatus;
    }

    public static class TriggerPayoutedEventResponse extends BaseEventResponse {
        public String _address;

        public BigInteger value;
    }

    public static class WhitelistUpdatedEventResponse extends BaseEventResponse {
        public String _address;

        public Boolean status;
    }
}
