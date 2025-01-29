package com.smofs.wsfs.blockchain

import com.smofs.wsfs.aws.AwsApi
import io.grpc.CallOptions
import io.grpc.Grpc
import io.grpc.TlsChannelCredentials
import org.hyperledger.fabric.client.Gateway
import org.hyperledger.fabric.client.Hash
import org.hyperledger.fabric.client.identity.Identities
import org.hyperledger.fabric.client.identity.Signers
import org.hyperledger.fabric.client.identity.X509Identity
import org.junit.jupiter.api.Test
import software.amazon.awssdk.utils.StringInputStream
import java.util.concurrent.TimeUnit
import java.util.function.UnaryOperator


class BlockTest {
    @Test
    fun fetchTest() {
        val tlsCert = AwsApi.getSecret("WSFS-Blockchain-TLS")
        var identityCert =  AwsApi.getSecret("WSFS-Blockchain-Identity")
        var signerKey = AwsApi.getSecret("WSFS-Blockchain-Signer")
        val secrets = AwsApi.getSecretMap("WSFS-Blockchain")
        var credentials = TlsChannelCredentials.newBuilder()
            .trustManager(StringInputStream(tlsCert))
            .build()
        val grpcConnection = Grpc.newChannelBuilder(secrets["PeerEndpoint"], credentials).build()
        val identity = X509Identity(secrets["MSP_ID"], Identities.readX509Certificate(identityCert))
        val signer =  Signers.newPrivateKeySigner(Identities.readPrivateKey(signerKey))
        requireNotNull(grpcConnection)
        requireNotNull(identity)
        requireNotNull(signer)

        val builder = Gateway.newInstance()
            .identity(identity)
            .signer(signer)
            .hash(Hash.SHA256)
            .connection(grpcConnection) // Default timeouts for different gRPC calls
            .evaluateOptions(UnaryOperator { options: CallOptions ->
                options.withDeadlineAfter(5, TimeUnit.SECONDS)
            })
            .endorseOptions(UnaryOperator { options: CallOptions ->
                options.withDeadlineAfter(5, TimeUnit.SECONDS)
            })
            .submitOptions(UnaryOperator { options: CallOptions ->
                options.withDeadlineAfter(5, TimeUnit.SECONDS)
            })
            .commitStatusOptions(UnaryOperator { options: CallOptions ->
                options.withDeadlineAfter(1, TimeUnit.MINUTES)
            })
        requireNotNull(builder.connect())
    }
}
