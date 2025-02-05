package com.smofs.wsfs.models

import com.smofs.Ballot
import com.smofs.BallotBox
import com.smofs.wsfs.aws.AwsApi
import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import java.io.File

@Suppress("unused")
data class ViewBallotBoxModel(val ballotBoxContract: String, val form: WebForm) : ViewModel {
    companion object {
        private val web3j: Web3j = Web3j.build(HttpService("http://localhost:8545"))
        private val credMap = AwsApi.getSecretMap("WSFS-Blockchain")
        private val resourceUrl = javaClass.getClassLoader().getResource("AccountKey.json")!!
        private val credentials: Credentials =
            WalletUtils.loadCredentials(credMap["AccountPassword"], File(resourceUrl.file))
        private val chainId = web3j.ethChainId().send().chainId.toLong()
        private val mangler = RawTransactionManager(web3j, credentials, chainId)
    }

    private val contract = BallotBox.load(ballotBoxContract, web3j, mangler, DefaultGasProvider())

    val ballots = contract.get().send().map {
        val ballotContract = Ballot.load(it.toString(), web3j, mangler, DefaultGasProvider())
        Pair(it.toString(), ballotContract.get().send())
    }
}
