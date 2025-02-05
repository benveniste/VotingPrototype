// SPDX-License-Identifier: GPL-3.0
// solc ./src/main/solidity/xmlContract.sol --bin --abi --optimize -o ./src/main/resources/ --overwrite
// web3j generate solidity -b ./src/main/resources/xmlStorage.bin -a ./src/main/resources/xmlStorage.abi -o ./src/main/java -p com.smofs

pragma solidity >=0.4.16 <0.9.0;

contract ballotBox {
    /* Define variable owner of the type address*/
    address private owner;
    string[] public ballots;

    /* this function is executed at initialization and sets the owner of the contract */
    constructor () {
        owner = msg.sender;
    }

    event BallotCast(string ballot);

    modifier onlyOwner {
        require(
            msg.sender == owner,
            "Only owner can call this function."
        );
        _;
    }

    function cast(string memory ballot) public {
        ballots.push(ballot);
        emit BallotCast(ballot);
    }

    function set(string memory initialBallot) public {
        delete ballots;
        cast(initialBallot);
    }

    function get() public view returns (string[] memory) {
        return ballots;
    }
}
