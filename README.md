# VotingPrototype
Prototype for Auditable Voting.

## **Dependencies:**

* A machine or instance running Linux or MacOS.  It might be possible to do all of this
under Windows; if you manage to do so please create a branch and update this document. I
developed this using the community edition of IntelliJ, but nothing in this project
  should depend on that.
* Java -- JDK 21
* Kotlin -- Version 2.0
* Gradle -- Version 8.12.1  (I made it out of clay)
* PostgreSQL -- Version 15.1
* pgAdmin or some other database management tool.  (Optional but recommended).
* Geth -- Version 1.14
* Clef -- Version 1.14
* web3j -- Version 1.6.2
* solc -- Version 0.8.28  (Scary!)
* Various open source packages defined in build.gradle.kts.  Currently, there are some
artifacts from my initial attempt to use hyperledger -- ignore these for now.

## **Configuration:**
AWS Secrets is used to store configuration information.  Currently, there are two sets
of secrets in use.  

### WSFS-Blockchain
* NodeURL -- The URL to access an Ethereum Blockchain node.  Currently, this is set up to
use a local node run under geth, but it should work on a real blockchain as well.
* AccountKey -- This is the Ethereum "wallet" which owns the smart contracts.  For
demonstration purposes, I created an account using clef and transferred the JSON to this
entry.
* AccountPassword -- This is the password to the Ethereum "wallet" above.

### WSFS-Database
* username -- The user name used to access the PostgreSQL database
* password -- The password used for access.  Imaginative, no?
* host -- A wafer or piece of bread that is consecrated during the Eucharist.  Also, the
host name of the postgreSQL instance.
* port -- The port number used to access the database instance on the host.
* dbname -- The database instance used for day-to-day operations.
* testdbname -- A database instance for testing.  It should have the same schema as the
main database.
* aesBase64 -- A base 64 encoded private key to use with AES encryption.

### Setup
Starting point:  https://geth.ethereum.org/docs/developers/dapp-developer/dev-mode

Create your database instances.  From the root of this project, run the following psql
commands, replacing the $SECRETS placeholders with whatever you configured in AWS:  
`psql -U $SECRETS_username -d $SECRETS_testdbname -f ./docs/schema.sql`  
`psql -U $SECRETS_username -d $SECRETS_dbname -f ./docs/schema.sql`

To start a local Ethereum style blockchain node define an environment variable LOCUS,
as a directory for storing the various "bits."  Then start your node by running:  
`geth --dev --http --http.api eth,web3,net --http.corsdomain "https://remix.ethereum.org"  --datadir $LOCUS`  
  
Next while geth is running, create an account by running:
`clef newaccount --keystore $LOCUS/keystore`
clef will ask for a password and create a second file in $LOCUS/keystore with a name beginning
with UTF--.  Transfer this password and the contents of the file to AWS secrets.

The next step is to "fund" the account you just created.  While geth is running, connect
to with the following command.  
`geth attach $LOCUS/geth.ipc`

You can then exit this program by typing control-D.  (Old-school Unix/Linux geeks
rejoice)
`eth.sendTransaction({from: eth.accounts[0], to: eth.accounts[1], value: web3.toWei(50, "ether")})
`  
  
