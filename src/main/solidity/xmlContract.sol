// SPDX-License-Identifier: GPL-3.0
// solc ./src/main/solidity/xmlContract.sol --bin --abi --optimize -o ./src/main/resources/ --overwrite
// web3j generate solidity -b ./src/main/resources/xmlStorage.bin -a ./src/main/resources/xmlStorage.abi -o ./src/main/java -p com.smofs

pragma solidity >=0.4.16 <0.9.0;

contract xmlStorage {
    string private storedXml;

    function set(string memory xml) public {
        storedXml = xml;
    }

    function get() public view returns (string memory) {
        return storedXml;
    }
}
