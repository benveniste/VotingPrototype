package com.smofs;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
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
    public static final String BINARY = "6080604052348015600e575f5ffd5b506106328061001c5f395ff3fe608060405234801561000f575f5ffd5b5060043610610034575f3560e01c80634ed3885e146100385780636d4ce63c14610054575b5f5ffd5b610052600480360381019061004d9190610260565b610072565b005b61005c610084565b6040516100699190610307565b60405180910390f35b805f9081610080919061052d565b5050565b60605f805461009290610354565b80601f01602080910402602001604051908101604052809291908181526020018280546100be90610354565b80156101095780601f106100e057610100808354040283529160200191610109565b820191905f5260205f20905b8154815290600101906020018083116100ec57829003601f168201915b5050505050905090565b5f604051905090565b5f5ffd5b5f5ffd5b5f5ffd5b5f5ffd5b5f601f19601f8301169050919050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52604160045260245ffd5b6101728261012c565b810181811067ffffffffffffffff821117156101915761019061013c565b5b80604052505050565b5f6101a3610113565b90506101af8282610169565b919050565b5f67ffffffffffffffff8211156101ce576101cd61013c565b5b6101d78261012c565b9050602081019050919050565b828183375f83830152505050565b5f6102046101ff846101b4565b61019a565b9050828152602081018484840111156102205761021f610128565b5b61022b8482856101e4565b509392505050565b5f82601f83011261024757610246610124565b5b81356102578482602086016101f2565b91505092915050565b5f602082840312156102755761027461011c565b5b5f82013567ffffffffffffffff81111561029257610291610120565b5b61029e84828501610233565b91505092915050565b5f81519050919050565b5f82825260208201905092915050565b8281835e5f83830152505050565b5f6102d9826102a7565b6102e381856102b1565b93506102f38185602086016102c1565b6102fc8161012c565b840191505092915050565b5f6020820190508181035f83015261031f81846102cf565b905092915050565b7f4e487b71000000000000000000000000000000000000000000000000000000005f52602260045260245ffd5b5f600282049050600182168061036b57607f821691505b60208210810361037e5761037d610327565b5b50919050565b5f819050815f5260205f209050919050565b5f6020601f8301049050919050565b5f82821b905092915050565b5f600883026103e07fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff826103a5565b6103ea86836103a5565b95508019841693508086168417925050509392505050565b5f819050919050565b5f819050919050565b5f61042e61042961042484610402565b61040b565b610402565b9050919050565b5f819050919050565b61044783610414565b61045b61045382610435565b8484546103b1565b825550505050565b5f5f905090565b610472610463565b61047d81848461043e565b505050565b5b818110156104a0576104955f8261046a565b600181019050610483565b5050565b601f8211156104e5576104b681610384565b6104bf84610396565b810160208510156104ce578190505b6104e26104da85610396565b830182610482565b50505b505050565b5f82821c905092915050565b5f6105055f19846008026104ea565b1980831691505092915050565b5f61051d83836104f6565b9150826002028217905092915050565b610536826102a7565b67ffffffffffffffff81111561054f5761054e61013c565b5b6105598254610354565b6105648282856104a4565b5f60209050601f831160018114610595575f8415610583578287015190505b61058d8582610512565b8655506105f4565b601f1984166105a386610384565b5f5b828110156105ca578489015182556001820191506020850194506020810190506105a5565b868310156105e757848901516105e3601f8916826104f6565b8355505b6001600288020188555050505b50505050505056fea26469706673582212203ff3daa807172f30b8a4d0f8f4f08ee07556a36e02edd6f21468c98f115808b664736f6c634300081c0033";

    private static String librariesLinkedBinary;

    public static final String FUNC_GET = "get";

    public static final String FUNC_SET = "set";

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

    public RemoteFunctionCall<String> get() {
        final Function function = new Function(FUNC_GET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> set(String xml) {
        final Function function = new Function(
                FUNC_SET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(xml)), 
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

    @Deprecated
    public static RemoteCall<XmlStorage> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(XmlStorage.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static RemoteCall<XmlStorage> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(XmlStorage.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
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
}
