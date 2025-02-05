package com.smofs;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/hyperledger-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.6.2.
 */
@SuppressWarnings("rawtypes")
public class Ballot extends Contract {
    public static final String BINARY = "6080604052348015600e575f5ffd5b50335f5f6101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555061077f8061005b5f395ff3fe608060405234801561000f575f5ffd5b506004361061003f575f3560e01c806340bd325f146100435780634ed3885e146100615780636d4ce63c1461007d575b5f5ffd5b61004b61009b565b60405161005891906102d0565b60405180910390f35b61007b6004803603810190610076919061042d565b610127565b005b6100856101d0565b60405161009291906102d0565b60405180910390f35b600180546100a8906104a1565b80601f01602080910402602001604051908101604052809291908181526020018280546100d4906104a1565b801561011f5780601f106100f65761010080835404028352916020019161011f565b820191905f5260205f20905b81548152906001019060200180831161010257829003601f168201915b505050505081565b5f60018054610135906104a1565b80601f0160208091040260200160405190810160405280929190818152602001828054610161906104a1565b80156101ac5780601f10610183576101008083540402835291602001916101ac565b820191905f5260205f20905b81548152906001019060200180831161018f57829003601f168201915b505050505090505f8151036101cc5781600190816101ca919061067a565b505b5050565b6060600180546101df906104a1565b80601f016020809104026020016040519081016040528092919081815260200182805461020b906104a1565b80156102565780601f1061022d57610100808354040283529160200191610256565b820191905f5260205f20905b81548152906001019060200180831161023957829003601f168201915b5050505050905090565b5f81519050919050565b5f82825260208201905092915050565b8281835e5f83830152505050565b5f601f19601f8301169050919050565b5f6102a282610260565b6102ac818561026a565b93506102bc81856020860161027a565b6102c581610288565b840191505092915050565b5f6020820190508181035f8301526102e88184610298565b905092915050565b5f604051905090565b5f5ffd5b5f5ffd5b5f5ffd5b5f5ffd5b7f4e487b71000000000000000000000000000000000000000000000000000000005f52604160045260245ffd5b61033f82610288565b810181811067ffffffffffffffff8211171561035e5761035d610309565b5b80604052505050565b5f6103706102f0565b905061037c8282610336565b919050565b5f67ffffffffffffffff82111561039b5761039a610309565b5b6103a482610288565b9050602081019050919050565b828183375f83830152505050565b5f6103d16103cc84610381565b610367565b9050828152602081018484840111156103ed576103ec610305565b5b6103f88482856103b1565b509392505050565b5f82601f83011261041457610413610301565b5b81356104248482602086016103bf565b91505092915050565b5f60208284031215610442576104416102f9565b5b5f82013567ffffffffffffffff81111561045f5761045e6102fd565b5b61046b84828501610400565b91505092915050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602260045260245ffd5b5f60028204905060018216806104b857607f821691505b6020821081036104cb576104ca610474565b5b50919050565b5f819050815f5260205f209050919050565b5f6020601f8301049050919050565b5f82821b905092915050565b5f6008830261052d7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff826104f2565b61053786836104f2565b95508019841693508086168417925050509392505050565b5f819050919050565b5f819050919050565b5f61057b6105766105718461054f565b610558565b61054f565b9050919050565b5f819050919050565b61059483610561565b6105a86105a082610582565b8484546104fe565b825550505050565b5f5f905090565b6105bf6105b0565b6105ca81848461058b565b505050565b5b818110156105ed576105e25f826105b7565b6001810190506105d0565b5050565b601f82111561063257610603816104d1565b61060c846104e3565b8101602085101561061b578190505b61062f610627856104e3565b8301826105cf565b50505b505050565b5f82821c905092915050565b5f6106525f1984600802610637565b1980831691505092915050565b5f61066a8383610643565b9150826002028217905092915050565b61068382610260565b67ffffffffffffffff81111561069c5761069b610309565b5b6106a682546104a1565b6106b18282856105f1565b5f60209050601f8311600181146106e2575f84156106d0578287015190505b6106da858261065f565b865550610741565b601f1984166106f0866104d1565b5f5b82811015610717578489015182556001820191506020850194506020810190506106f2565b868310156107345784890151610730601f891682610643565b8355505b6001600288020188555050505b50505050505056fea26469706673582212202e0ff9b75868bb6d057f054983beb2bf29f8f93a45377f83fe22e6295b53b40664736f6c634300081c0033";

    private static String librariesLinkedBinary;

    public static final String FUNC_GET = "get";

    public static final String FUNC_SET = "set";

    public static final String FUNC_XMLBALLOT = "xmlBallot";

    public static final Event BALLOTCAST_EVENT = new Event("BallotCast", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    ;

    @Deprecated
    protected Ballot(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Ballot(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Ballot(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Ballot(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<BallotCastEventResponse> getBallotCastEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BALLOTCAST_EVENT, transactionReceipt);
        ArrayList<BallotCastEventResponse> responses = new ArrayList<BallotCastEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BallotCastEventResponse typedResponse = new BallotCastEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.ballot = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BallotCastEventResponse getBallotCastEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BALLOTCAST_EVENT, log);
        BallotCastEventResponse typedResponse = new BallotCastEventResponse();
        typedResponse.log = log;
        typedResponse.ballot = (String) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<BallotCastEventResponse> ballotCastEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBallotCastEventFromLog(log));
    }

    public Flowable<BallotCastEventResponse> ballotCastEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BALLOTCAST_EVENT));
        return ballotCastEventFlowable(filter);
    }

    public RemoteFunctionCall<String> get() {
        final Function function = new Function(FUNC_GET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> set(String smofMLballot) {
        final Function function = new Function(
                FUNC_SET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(smofMLballot)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> xmlBallot() {
        final Function function = new Function(FUNC_XMLBALLOT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    @Deprecated
    public static Ballot load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new Ballot(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Ballot load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Ballot(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Ballot load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new Ballot(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Ballot load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Ballot(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Ballot> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Ballot.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    public static RemoteCall<Ballot> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Ballot.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<Ballot> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Ballot.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<Ballot> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Ballot.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
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

    public static class BallotCastEventResponse extends BaseEventResponse {
        public String ballot;
    }
}
