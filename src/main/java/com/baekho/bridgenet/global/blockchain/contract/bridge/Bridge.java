package com.baekho.bridgenet.global.blockchain.contract.bridge;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
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
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple7;
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
 * <p>Generated with web3j version 1.8.0.
 */
@SuppressWarnings("rawtypes")
@Generated("org.web3j.codegen.SolidityFunctionWrapperGenerator")
public class Bridge extends Contract {
    public static final String BINARY = "0x6080604052600180556509184e72a00060025566b1a2bc2ec500006003553480156200002a57600080fd5b5033600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1603620000a15760006040517f1e4fbdf70000000000000000000000000000000000000000000000000000000081526004016200009891906200021a565b60405180910390fd5b620000b2816200011160201b60201c565b506001600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff02191690831515021790555062000237565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050816000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b60006200020282620001d5565b9050919050565b6200021481620001f5565b82525050565b600060208201905062000231600083018462000209565b92915050565b6117b980620002476000396000f3fe6080604052600436106100ec5760003560e01c80638da5cb5b1161008a578063c26104f411610059578063c26104f4146102ef578063d845a4b31461031a578063da93040114610336578063f2fde38b14610361576100f3565b80638da5cb5b1461023357806395ea641c1461025e5780639ba954951461029b578063b8077ebb146102c4576100f3565b8063372c12b1116100c6578063372c12b11461018d5780636b0d2782146101ca578063715018a6146101f35780638d14e1271461020a576100f3565b806302e9d43b146100f857806323c314731461013b5780632e6dc58f14610164576100f3565b366100f357005b600080fd5b34801561010457600080fd5b5061011f600480360381019061011a91906110ac565b61038a565b60405161013297969594939291906111a0565b60405180910390f35b34801561014757600080fd5b50610162600480360381019061015d9190611234565b6103f9565b005b34801561017057600080fd5b5061018b600480360381019061018691906110ac565b6106ab565b005b34801561019957600080fd5b506101b460048036038101906101af91906112a0565b61074a565b6040516101c191906112e8565b60405180910390f35b3480156101d657600080fd5b506101f160048036038101906101ec9190611341565b61076a565b005b3480156101ff57600080fd5b50610208610970565b005b34801561021657600080fd5b50610231600480360381019061022c91906113d4565b610984565b005b34801561023f57600080fd5b50610248610a35565b6040516102559190611414565b60405180910390f35b34801561026a57600080fd5b506102856004803603810190610280919061142f565b610a5e565b60405161029291906112e8565b60405180910390f35b3480156102a757600080fd5b506102c260048036038101906102bd91906110ac565b610a8d565b005b3480156102d057600080fd5b506102d9610b2c565b6040516102e6919061146f565b60405180910390f35b3480156102fb57600080fd5b50610304610b32565b604051610311919061146f565b60405180910390f35b610334600480360381019061032f91906110ac565b610b38565b005b34801561034257600080fd5b5061034b610e2d565b604051610358919061146f565b60405180910390f35b34801561036d57600080fd5b50610388600480360381019061038391906112a0565b610e33565b005b60056020528060005260406000206000915090508060000154908060010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16908060020154908060030154908060040154908060050154908060060160009054906101000a900460ff16905087565b610401610eb9565b6000600560008481526020019081526020016000209050600081600001540361046157826040517f88d3e72a000000000000000000000000000000000000000000000000000000008152600401610458919061146f565b60405180910390fd5b6000600281111561047557610474611129565b5b82600281111561048857610487611129565b5b036104ca57816040517f7c127b720000000000000000000000000000000000000000000000000000000081526004016104c1919061148a565b60405180910390fd5b600060028111156104de576104dd611129565b5b8160060160009054906101000a900460ff16600281111561050257610501611129565b5b14610555578060060160009054906101000a900460ff166040517f4217355a00000000000000000000000000000000000000000000000000000000815260040161054c919061148a565b60405180910390fd5b818160060160006101000a81548160ff0219169083600281111561057c5761057b611129565b5b021790555060008160010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905060008260030154905060008273ffffffffffffffffffffffffffffffffffffffff16826040516105d9906114d6565b60006040518083038185875af1925050503d8060008114610616576040519150601f19603f3d011682016040523d82523d6000602084013e61061b565b606091505b50509050806106635782826040517fe3e9273500000000000000000000000000000000000000000000000000000000815260040161065a9291906114eb565b60405180910390fd5b84600281111561067657610675611129565b5b867f0517eac5faceac3227e54a79beb665d1cb2d6abd65ed64af2b35a7a6b7dd8a2c60405160405180910390a3505050505050565b6106b3610eb9565b60008114806106c457506002548111155b1561070657806040517fcafeba900000000000000000000000000000000000000000000000000000000081526004016106fd919061146f565b60405180910390fd5b7ff12e2ff57515910152e51b92d36f158f77380eb5116da8a297a42d44d3c93ce360018260405161073892919061155c565b60405180910390a18060038190555050565b60046020528060005260406000206000915054906101000a900460ff1681565b610772610eb9565b60066000858152602001908152602001600020600084815260200190815260200160002060009054906101000a900460ff16156107e85783836040517f465cdf2c0000000000000000000000000000000000000000000000000000000081526004016107df929190611585565b60405180910390fd5b47811061082e5780476040517fcf479181000000000000000000000000000000000000000000000000000000008152600401610825929190611585565b60405180910390fd5b600160066000868152602001908152602001600020600085815260200190815260200160002060006101000a81548160ff02191690831515021790555060008273ffffffffffffffffffffffffffffffffffffffff1682604051610891906114d6565b60006040518083038185875af1925050503d80600081146108ce576040519150601f19603f3d011682016040523d82523d6000602084013e6108d3565b606091505b505090508061091b5782826040517fe3e9273500000000000000000000000000000000000000000000000000000000815260040161091292919061160d565b60405180910390fd5b8273ffffffffffffffffffffffffffffffffffffffff167f0f5a6d8fcb3a306da0606b5fa92e6794e091d096c590a5d042034dfe75b5154283604051610961919061146f565b60405180910390a25050505050565b610978610eb9565b6109826000610f40565b565b61098c610eb9565b80600460008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055508173ffffffffffffffffffffffffffffffffffffffff167ff93f9a76c1bf3444d22400a00cb9fe990e6abe9dbb333fda48859cfee864543d82604051610a2991906112e8565b60405180910390a25050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905090565b60066020528160005260406000206020528060005260406000206000915091509054906101000a900460ff1681565b610a95610eb9565b6000811480610aa657506003548110155b15610ae857806040517fb5d9c360000000000000000000000000000000000000000000000000000000008152600401610adf919061146f565b60405180910390fd5b7ff12e2ff57515910152e51b92d36f158f77380eb5116da8a297a42d44d3c93ce3600082604051610b1a92919061155c565b60405180910390a18060028190555050565b60015481565b60035481565b600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16610bc657336040517fdd5d9041000000000000000000000000000000000000000000000000000000008152600401610bbd9190611414565b60405180910390fd5b600354341115610c1157600354346040517fe800f979000000000000000000000000000000000000000000000000000000008152600401610c08929190611585565b60405180910390fd5b600254341015610c5c57600254346040517f900b8bb1000000000000000000000000000000000000000000000000000000008152600401610c53929190611585565b60405180910390fd5b610c6461100c565b600060016000815480929190610c7990611665565b9190505590508082600001818152505033826020019073ffffffffffffffffffffffffffffffffffffffff16908173ffffffffffffffffffffffffffffffffffffffff168152505046826040018181525050828260800181815250503482606001818152505060008260c001906002811115610cf857610cf7611129565b5b90816002811115610d0c57610d0b611129565b5b81525050348260a001818152505081600560008381526020019081526020016000206000820151816000015560208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160020155606082015181600301556080820151816004015560a0820151816005015560c08201518160060160006101000a81548160ff02191690836002811115610dd257610dd1611129565b5b02179055509050503373ffffffffffffffffffffffffffffffffffffffff167f7c3289f9a7fa5a378a44c4e08cfe9775f21a661f3f1b4690ff2f951b593eba3083604051610e209190611768565b60405180910390a2505050565b60025481565b610e3b610eb9565b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1603610ead5760006040517f1e4fbdf7000000000000000000000000000000000000000000000000000000008152600401610ea49190611414565b60405180910390fd5b610eb681610f40565b50565b610ec1611004565b73ffffffffffffffffffffffffffffffffffffffff16610edf610a35565b73ffffffffffffffffffffffffffffffffffffffff1614610f3e57610f02611004565b6040517f118cdaa7000000000000000000000000000000000000000000000000000000008152600401610f359190611414565b60405180910390fd5b565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050816000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b600033905090565b6040518060e0016040528060008152602001600073ffffffffffffffffffffffffffffffffffffffff168152602001600081526020016000815260200160008152602001600081526020016000600281111561106b5761106a611129565b5b81525090565b600080fd5b6000819050919050565b61108981611076565b811461109457600080fd5b50565b6000813590506110a681611080565b92915050565b6000602082840312156110c2576110c1611071565b5b60006110d084828501611097565b91505092915050565b6110e281611076565b82525050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000611113826110e8565b9050919050565b61112381611108565b82525050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602160045260246000fd5b6003811061116957611168611129565b5b50565b600081905061117a82611158565b919050565b600061118a8261116c565b9050919050565b61119a8161117f565b82525050565b600060e0820190506111b5600083018a6110d9565b6111c2602083018961111a565b6111cf60408301886110d9565b6111dc60608301876110d9565b6111e960808301866110d9565b6111f660a08301856110d9565b61120360c0830184611191565b98975050505050505050565b6003811061121c57600080fd5b50565b60008135905061122e8161120f565b92915050565b6000806040838503121561124b5761124a611071565b5b600061125985828601611097565b925050602061126a8582860161121f565b9150509250929050565b61127d81611108565b811461128857600080fd5b50565b60008135905061129a81611274565b92915050565b6000602082840312156112b6576112b5611071565b5b60006112c48482850161128b565b91505092915050565b60008115159050919050565b6112e2816112cd565b82525050565b60006020820190506112fd60008301846112d9565b92915050565b600061130e826110e8565b9050919050565b61131e81611303565b811461132957600080fd5b50565b60008135905061133b81611315565b92915050565b6000806000806080858703121561135b5761135a611071565b5b600061136987828801611097565b945050602061137a87828801611097565b935050604061138b8782880161132c565b925050606061139c87828801611097565b91505092959194509250565b6113b1816112cd565b81146113bc57600080fd5b50565b6000813590506113ce816113a8565b92915050565b600080604083850312156113eb576113ea611071565b5b60006113f98582860161128b565b925050602061140a858286016113bf565b9150509250929050565b6000602082019050611429600083018461111a565b92915050565b6000806040838503121561144657611445611071565b5b600061145485828601611097565b925050602061146585828601611097565b9150509250929050565b600060208201905061148460008301846110d9565b92915050565b600060208201905061149f6000830184611191565b92915050565b600081905092915050565b50565b60006114c06000836114a5565b91506114cb826114b0565b600082019050919050565b60006114e1826114b3565b9150819050919050565b6000604082019050611500600083018561111a565b61150d60208301846110d9565b9392505050565b6002811061152557611524611129565b5b50565b600081905061153682611514565b919050565b600061154682611528565b9050919050565b6115568161153b565b82525050565b6000604082019050611571600083018561154d565b61157e60208301846110d9565b9392505050565b600060408201905061159a60008301856110d9565b6115a760208301846110d9565b9392505050565b6000819050919050565b60006115d36115ce6115c9846110e8565b6115ae565b6110e8565b9050919050565b60006115e5826115b8565b9050919050565b60006115f7826115da565b9050919050565b611607816115ec565b82525050565b600060408201905061162260008301856115fe565b61162f60208301846110d9565b9392505050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b600061167082611076565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82036116a2576116a1611636565b5b600182019050919050565b6116b681611076565b82525050565b6116c581611108565b82525050565b6116d48161117f565b82525050565b60e0820160008201516116f060008501826116ad565b50602082015161170360208501826116bc565b50604082015161171660408501826116ad565b50606082015161172960608501826116ad565b50608082015161173c60808501826116ad565b5060a082015161174f60a08501826116ad565b5060c082015161176260c08501826116cb565b50505050565b600060e08201905061177d60008301846116da565b9291505056fea26469706673582212204cd1ee8befd52c2dee2209eb54d61666ecf6df171db6ea51a3c526acfc8de0d764736f6c63430008140033";

    private static String librariesLinkedBinary;

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PAYOUTLIST = "payoutList";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_REQUEST = "request";

    public static final String FUNC_REQUESTIDCOUNT = "requestIdCount";

    public static final String FUNC_REQUESTLIST = "requestList";

    public static final String FUNC_REQUESTMAXIMUMVALUE = "requestMaximumValue";

    public static final String FUNC_REQUESTMINIMUMVALUE = "requestMinimumValue";

    public static final String FUNC_SETMAXIMUMVALUE = "setMaximumValue";

    public static final String FUNC_SETMINIMUMVALUE = "setMinimumValue";

    public static final String FUNC_SETREQUEST = "setRequest";

    public static final String FUNC_SETWHITELIST = "setWhiteList";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_TRIGGERPAYOUT = "triggerPayout";

    public static final String FUNC_WHITELIST = "whiteList";

    public static final CustomError ALREADYPROCESSED_ERROR = new CustomError("AlreadyProcessed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final CustomError BELOWMINIMUMVALUE_ERROR = new CustomError("BelowMinimumValue", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final CustomError EXCEEDSMAXIMUMVALUE_ERROR = new CustomError("ExceedsMaximumValue", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final CustomError INCORRECTREQUESTID_ERROR = new CustomError("IncorrectRequestId", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final CustomError INCORRECTREQUESTSTATUS_ERROR = new CustomError("IncorrectRequestStatus", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
    ;

    public static final CustomError INSUFFICIENTBALANCE_ERROR = new CustomError("InsufficientBalance", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final CustomError INVALIDMAXIMUMVALUE_ERROR = new CustomError("InvalidMaximumValue", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final CustomError INVALIDMINIMUMVALUE_ERROR = new CustomError("InvalidMinimumValue", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final CustomError OWNABLEINVALIDOWNER_ERROR = new CustomError("OwnableInvalidOwner", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final CustomError OWNABLEUNAUTHORIZEDACCOUNT_ERROR = new CustomError("OwnableUnauthorizedAccount", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final CustomError PAYOUTFAILED_ERROR = new CustomError("PayoutFailed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final CustomError REQUESTALREADYFINALIZED_ERROR = new CustomError("RequestAlreadyFinalized", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
    ;

    public static final CustomError WHITELISTUNAUTHORIZEDACCOUNT_ERROR = new CustomError("WhiteListUnauthorizedAccount", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
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

    public static final Event SETVALUERANGE_EVENT = new Event("setValueRange", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}, new TypeReference<Uint256>() {}));
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

    public static List<SetValueRangeEventResponse> getSetValueRangeEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(SETVALUERANGE_EVENT, transactionReceipt);
        ArrayList<SetValueRangeEventResponse> responses = new ArrayList<SetValueRangeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SetValueRangeEventResponse typedResponse = new SetValueRangeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.valueRange = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static SetValueRangeEventResponse getSetValueRangeEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(SETVALUERANGE_EVENT, log);
        SetValueRangeEventResponse typedResponse = new SetValueRangeEventResponse();
        typedResponse.log = log;
        typedResponse.valueRange = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<SetValueRangeEventResponse> setValueRangeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getSetValueRangeEventFromLog(log));
    }

    public Flowable<SetValueRangeEventResponse> setValueRangeEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SETVALUERANGE_EVENT));
        return setValueRangeEventFlowable(filter);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> payoutList(BigInteger param0, BigInteger param1) {
        final Function function = new Function(FUNC_PAYOUTLIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0), 
                new org.web3j.abi.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> request(BigInteger toChainId,
            BigInteger weiValue) {
        final Function function = new Function(
                FUNC_REQUEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(toChainId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<BigInteger> requestIdCount() {
        final Function function = new Function(FUNC_REQUESTIDCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple7<BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>> requestList(
            BigInteger param0) {
        final Function function = new Function(FUNC_REQUESTLIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
        return new RemoteFunctionCall<Tuple7<BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>(function,
                new Callable<Tuple7<BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple7<BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> call(
                            ) throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> requestMaximumValue() {
        final Function function = new Function(FUNC_REQUESTMAXIMUMVALUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> requestMinimumValue() {
        final Function function = new Function(FUNC_REQUESTMINIMUMVALUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setMaximumValue(BigInteger max) {
        final Function function = new Function(
                FUNC_SETMAXIMUMVALUE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(max)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setMinimumValue(BigInteger min) {
        final Function function = new Function(
                FUNC_SETMINIMUMVALUE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(min)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteFunctionCall<TransactionReceipt> triggerPayout(BigInteger fromChainId,
            BigInteger requestId, String _address, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRIGGERPAYOUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(fromChainId), 
                new org.web3j.abi.datatypes.generated.Uint256(requestId), 
                new org.web3j.abi.datatypes.Address(160, _address), 
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

    public static RemoteCall<Bridge> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Bridge.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    public static RemoteCall<Bridge> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Bridge.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<Bridge> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Bridge.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<Bridge> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Bridge.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static void linkLibraries(List<Contract.LinkReference> references) {
        librariesLinkedBinary = linkBinaryWithReferences(BINARY, references);
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class RequestInfo extends StaticStruct {
        public BigInteger id;

        public String requestBy;

        public BigInteger fromChainId;

        public BigInteger fromValue;

        public BigInteger toChainId;

        public BigInteger toValue;

        public BigInteger status;

        public RequestInfo(BigInteger id, String requestBy, BigInteger fromChainId,
                BigInteger fromValue, BigInteger toChainId, BigInteger toValue, BigInteger status) {
            super(new org.web3j.abi.datatypes.generated.Uint256(id), 
                    new org.web3j.abi.datatypes.Address(160, requestBy), 
                    new org.web3j.abi.datatypes.generated.Uint256(fromChainId), 
                    new org.web3j.abi.datatypes.generated.Uint256(fromValue), 
                    new org.web3j.abi.datatypes.generated.Uint256(toChainId), 
                    new org.web3j.abi.datatypes.generated.Uint256(toValue), 
                    new org.web3j.abi.datatypes.generated.Uint8(status));
            this.id = id;
            this.requestBy = requestBy;
            this.fromChainId = fromChainId;
            this.fromValue = fromValue;
            this.toChainId = toChainId;
            this.toValue = toValue;
            this.status = status;
        }

        public RequestInfo(Uint256 id, Address requestBy, Uint256 fromChainId, Uint256 fromValue,
                Uint256 toChainId, Uint256 toValue, Uint8 status) {
            super(id, requestBy, fromChainId, fromValue, toChainId, toValue, status);
            this.id = id.getValue();
            this.requestBy = requestBy.getValue();
            this.fromChainId = fromChainId.getValue();
            this.fromValue = fromValue.getValue();
            this.toChainId = toChainId.getValue();
            this.toValue = toValue.getValue();
            this.status = status.getValue();
        }
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

    public static class SetValueRangeEventResponse extends BaseEventResponse {
        public BigInteger valueRange;

        public BigInteger value;
    }
}
