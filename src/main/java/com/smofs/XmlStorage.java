package com.smofs;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
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
public class XmlStorage extends Contract {
    public static final String BINARY = "6080604052348015600e575f5ffd5b50335f5f6101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550610a038061005b5f395ff3fe608060405234801561000f575f5ffd5b506004361061004a575f3560e01c80634ed3885e1461004e5780635c632b381461006a5780636d4ce63c1461009a578063d78385fc146100b8575b5f5ffd5b610068600480360381019061006391906104b9565b6100d4565b005b610084600480360381019061007f9190610533565b6100ed565b60405161009191906105be565b60405180910390f35b6100a2610193565b6040516100af91906106e1565b60405180910390f35b6100d260048036038101906100cd91906104b9565b610267565b005b60015f6100e191906102d3565b6100ea81610267565b50565b600181815481106100fc575f80fd5b905f5260205f20015f9150905080546101149061072e565b80601f01602080910402602001604051908101604052809291908181526020018280546101409061072e565b801561018b5780601f106101625761010080835404028352916020019161018b565b820191905f5260205f20905b81548152906001019060200180831161016e57829003601f168201915b505050505081565b60606001805480602002602001604051908101604052809291908181526020015f905b8282101561025e578382905f5260205f200180546101d39061072e565b80601f01602080910402602001604051908101604052809291908181526020018280546101ff9061072e565b801561024a5780601f106102215761010080835404028352916020019161024a565b820191905f5260205f20905b81548152906001019060200180831161022d57829003601f168201915b5050505050815260200190600101906101b6565b50505050905090565b600181908060018154018082558091505060019003905f5260205f20015f90919091909150908161029891906108fe565b507f66d5c1a41d3b9253adbda4f3acde44d6029986db637dafe0c7a573933f09c3da816040516102c891906105be565b60405180910390a150565b5080545f8255905f5260205f20908101906102ee91906102f1565b50565b5b80821115610310575f81816103079190610314565b506001016102f2565b5090565b5080546103209061072e565b5f825580601f10610331575061034e565b601f0160209004905f5260205f209081019061034d9190610351565b5b50565b5b80821115610368575f815f905550600101610352565b5090565b5f604051905090565b5f5ffd5b5f5ffd5b5f5ffd5b5f5ffd5b5f601f19601f8301169050919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52604160045260245ffd5b6103cb82610385565b810181811067ffffffffffffffff821117156103ea576103e9610395565b5b80604052505050565b5f6103fc61036c565b905061040882826103c2565b919050565b5f67ffffffffffffffff82111561042757610426610395565b5b61043082610385565b9050602081019050919050565b828183375f83830152505050565b5f61045d6104588461040d565b6103f3565b90508281526020810184848401111561047957610478610381565b5b61048484828561043d565b509392505050565b5f82601f8301126104a05761049f61037d565b5b81356104b084826020860161044b565b91505092915050565b5f602082840312156104ce576104cd610375565b5b5f82013567ffffffffffffffff8111156104eb576104ea610379565b5b6104f78482850161048c565b91505092915050565b5f819050919050565b61051281610500565b811461051c575f5ffd5b50565b5f8135905061052d81610509565b92915050565b5f6020828403121561054857610547610375565b5b5f6105558482850161051f565b91505092915050565b5f81519050919050565b5f82825260208201905092915050565b8281835e5f83830152505050565b5f6105908261055e565b61059a8185610568565b93506105aa818560208601610578565b6105b381610385565b840191505092915050565b5f6020820190508181035f8301526105d68184610586565b905092915050565b5f81519050919050565b5f82825260208201905092915050565b5f819050602082019050919050565b5f82825260208201905092915050565b5f6106218261055e565b61062b8185610607565b935061063b818560208601610578565b61064481610385565b840191505092915050565b5f61065a8383610617565b905092915050565b5f602082019050919050565b5f610678826105de565b61068281856105e8565b935083602082028501610694856105f8565b805f5b858110156106cf57848403895281516106b0858261064f565b94506106bb83610662565b925060208a01995050600181019050610697565b50829750879550505050505092915050565b5f6020820190508181035f8301526106f9818461066e565b905092915050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602260045260245ffd5b5f600282049050600182168061074557607f821691505b60208210810361075857610757610701565b5b50919050565b5f819050815f5260205f209050919050565b5f6020601f8301049050919050565b5f82821b905092915050565b5f600883026107ba7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8261077f565b6107c4868361077f565b95508019841693508086168417925050509392505050565b5f819050919050565b5f6107ff6107fa6107f584610500565b6107dc565b610500565b9050919050565b5f819050919050565b610818836107e5565b61082c61082482610806565b84845461078b565b825550505050565b5f5f905090565b610843610834565b61084e81848461080f565b505050565b5b81811015610871576108665f8261083b565b600181019050610854565b5050565b601f8211156108b6576108878161075e565b61089084610770565b8101602085101561089f578190505b6108b36108ab85610770565b830182610853565b50505b505050565b5f82821c905092915050565b5f6108d65f19846008026108bb565b1980831691505092915050565b5f6108ee83836108c7565b9150826002028217905092915050565b6109078261055e565b67ffffffffffffffff8111156109205761091f610395565b5b61092a825461072e565b610935828285610875565b5f60209050601f831160018114610966575f8415610954578287015190505b61095e85826108e3565b8655506109c5565b601f1984166109748661075e565b5f5b8281101561099b57848901518255600182019150602085019450602081019050610976565b868310156109b857848901516109b4601f8916826108c7565b8355505b6001600288020188555050505b50505050505056fea264697066735822122067bbb92bf11e1f6de9a8b8f5b971335e7db8f0692cb68c23b47021c5ac9c0b5564736f6c634300081c0033";

    private static String librariesLinkedBinary;

    public static final String FUNC_BALLOTS = "ballots";

    public static final String FUNC_CAST = "cast";

    public static final String FUNC_GET = "get";

    public static final String FUNC_SET = "set";

    public static final Event BALLOTCAST_EVENT = new Event("BallotCast", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    ;

    @Deprecated
    protected XmlStorage(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected XmlStorage(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected XmlStorage(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected XmlStorage(String contractAddress, Web3j web3j, TransactionManager transactionManager,
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

    public RemoteFunctionCall<String> ballots(BigInteger param0) {
        final Function function = new Function(FUNC_BALLOTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> cast(String ballot) {
        final Function function = new Function(
                FUNC_CAST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(ballot)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<List> get() {
        final Function function = new Function(FUNC_GET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> set(String initialBallot) {
        final Function function = new Function(
                FUNC_SET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(initialBallot)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static XmlStorage load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new XmlStorage(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static XmlStorage load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new XmlStorage(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static XmlStorage load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new XmlStorage(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static XmlStorage load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new XmlStorage(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<XmlStorage> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(XmlStorage.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    public static RemoteCall<XmlStorage> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(XmlStorage.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<XmlStorage> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(XmlStorage.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<XmlStorage> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(XmlStorage.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
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
