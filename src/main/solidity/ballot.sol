// SPDX-License-Identifier: GPL-3.0
// solc ./src/main/solidity/ballot.sol.sol --bin --abi --optimize -o ./src/main/resources/ --overwrite
// web3j generate solidity -b ./src/main/resources/xmlStorage.bin -a ./src/main/resources/xmlStorage.abi -o ./src/main/java -p com.smofs

pragma solidity >=0.4.16 <0.9.0;

contract ballot {
    /* Define variable owner of the type address*/
    address private owner;
    string public xmlBallot;

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

    function set(string memory smofMLballot) public {
        bytes memory grump = bytes(xmlBallot);
        if (grump.length == 0) {
            xmlBallot = smofMLballot;
        }
    }

    function get() public view returns (string memory) {
        return xmlBallot;
    }
}
