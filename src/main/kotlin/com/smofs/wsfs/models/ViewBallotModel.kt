package com.smofs.wsfs.models

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.smofs.Ballot
import com.smofs.wsfs.aws.AwsApi
import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.DefaultGasProvider

data class ViewBallotModel(val ballotContract: String, val form: WebForm) : ViewModel {
    companion object {
        private val credMap = AwsApi.getSecretMap("WSFS-Blockchain")
        private val web3j: Web3j = Web3j.build(HttpService(credMap["NodeURL"]))
        private val wallet: WalletFile = jacksonObjectMapper().readValue(credMap["AccountKey"], WalletFile::class.java)
        private val credentials: Credentials = Credentials.create(Wallet.decrypt(credMap["AccountPassword"], wallet))
        private val chainId = web3j.ethChainId().send().chainId.toLong()
        private val mangler = RawTransactionManager(web3j, credentials, chainId)
    }

    private val contract = Ballot.load(ballotContract, web3j, mangler, DefaultGasProvider())
    val ballot = contract.get().send() ?: "None found."
}
