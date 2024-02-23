pragma solidity ^0.4.21;

contract TransactionFee {

    // (1)
    uint public fee;
    // (2)
    address public receiver;
    // (3)
    mapping (address => uint) public balances;
    // (4)
    event Sent(address from, address to, uint amount, bool sent);

    // (5)
    constructor(address _receiver, uint _fee) public {
        receiver = _receiver;
        fee = _fee;
    }

    // (6)
    function getReceiverBalance() public view returns(uint) {
        return receiver.balance;
    }

    // (7)
    function sendTrx() public payable {
        uint value = msg.value * fee / 100;
        bool sent = receiver.send(value);
        balances[receiver] += (value);
        emit Sent(msg.sender, receiver, value, sent);
    }

}