package com.smofs.wsfs.blockchain

import com.smofs.wsfs.aws.AwsApi
import io.grpc.Grpc
import io.grpc.TlsChannelCredentials
import org.hyperledger.fabric.client.identity.Identities
import org.hyperledger.fabric.client.identity.Signers
import org.hyperledger.fabric.client.identity.X509Identity
import org.junit.jupiter.api.Test
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import software.amazon.awssdk.utils.StringInputStream


class BlockTest {
    @Test
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
        val web3 = Web3j.build(HttpService("http://localhost:8545"))
        val web3ClientVersion = web3.web3ClientVersion().send()
        val clientVersion = web3ClientVersion.web3ClientVersion
        requireNotNull(clientVersion)
    }
}
