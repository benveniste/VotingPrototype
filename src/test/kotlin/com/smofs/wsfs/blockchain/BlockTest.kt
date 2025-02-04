package com.smofs.wsfs.blockchain

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.smofs.XmlStorage
import com.smofs.wsfs.aws.AwsApi
import io.grpc.Grpc
import io.grpc.TlsChannelCredentials
import mu.KotlinLogging
import org.hyperledger.fabric.client.identity.Identities
import org.hyperledger.fabric.client.identity.Signers
import org.hyperledger.fabric.client.identity.X509Identity
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import software.amazon.awssdk.utils.StringInputStream
import java.io.File


private val logger = KotlinLogging.logger {}

class BlockTest {
    @Test
    @Disabled
    fun fetchTest() {
        val tlsCert = AwsApi.getSecret("WSFS-Blockchain-TLS")
        var identityCert = AwsApi.getSecret("WSFS-Blockchain-Identity")
        var signerKey = AwsApi.getSecret("WSFS-Blockchain-Signer")
        val secrets = AwsApi.getSecretMap("WSFS-Blockchain")
        var credentials = TlsChannelCredentials.newBuilder()
            .trustManager(StringInputStream(tlsCert))
            .build()
        val grpcConnection =
            Grpc.newChannelBuilder(secrets["PeerEndpoint"], credentials)
                .overrideAuthority(secrets["PeerHostname"])
                .build()
        val identity = X509Identity(secrets["MSP_ID"], Identities.readX509Certificate(identityCert))
        val signer = Signers.newPrivateKeySigner(Identities.readPrivateKey(signerKey))
        requireNotNull(grpcConnection)
        requireNotNull(identity)
        requireNotNull(signer)
    }

    @Test
    fun web3jTest() {
        val contractAddr = "0x90a5f9bd4d6c5d8a0f383cc21142e698f521c9c3"
        val web3j = Web3j.build(HttpService("http://localhost:8545"))
        val web3ClientVersion = web3j.web3ClientVersion().send()
        val clientVersion = web3ClientVersion.web3ClientVersion
        requireNotNull(clientVersion)
        requireNotNull(contractAddr)
        val credMap = AwsApi.getSecretMap("WSFS-Blockchain")

        val resourceUrl = javaClass.getClassLoader().getResource("AccountKey.json")!!
        val credentials = WalletUtils.loadCredentials(credMap["AccountPassword"], File(resourceUrl.file))
        val chainId = web3j.ethChainId().send().chainId.toLong()
        val mangler = RawTransactionManager(web3j, credentials, chainId)
//        val smartContract = XmlStorage.deploy(web3j, mangler, DefaultGasProvider()).send()
        val smartContract = XmlStorage.load(contractAddr, web3j, mangler, DefaultGasProvider())
        smartContract.set("<Initial/>").send()
        val result = smartContract.cast("<Ballot>" + System.currentTimeMillis() + "</Ballot>").send()
        val fetch =  smartContract.get().send()
        logger.info { fetch }
        requireNotNull(result)
        assertThat("Bad fetch", fetch.size, equalTo(2))
        smartContract.set("<Initial/>").send()
    }
}
