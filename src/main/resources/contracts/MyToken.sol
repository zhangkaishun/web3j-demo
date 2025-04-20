// SPDX-Licence-Identifier: MIT
pragma solidity ~0.8.20;


// 导入 OpenZeppelin 提供的 ERC20 标准合约
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract MyToken is ERC20 {
     // 构造函数将初始化 ERC20 供应量和代币名称
    constructor(uint256 initialSupply) ERC20("MyToken", "MTK") {
        // 通过 _mint 函数铸造初始供应量的代币到部署合约的地址
        _mint(msg.sender, initialSupply);
    }

}