//amountERC20TokenSentOrReceived / contractERC20TokenBalance = amountEthSentOrReceived / contractEthBalance
//amountTokenSentOrReceived / contractTokenBalance = liquidityPositionsIssuedOrBurned / totalLiquidityPositions
//K = contractEthBalance * contractERC20TokenBalance

// SPDX-License-Identifier: UNLICENSED 
pragma solidity ^0.8.7; 
import "@openzeppelin/contracts/token/ERC20/ERC20.sol"; 

contract Exchange {

    //Contract variables
    ERC20 ERC20Token;
    uint256 contractERC20TokenBalance;
    uint256 contractEthBalance;
    uint256 totalLiquidityPositions;
    uint256 K;
    //User Variables
    mapping(address => uint256) liquidityPosition;
    mapping(address => uint256) amountERC20Token;
    mapping(address => uint256) amountEth;

    //Done
    constructor(address _tokenAddress) {
        ERC20Token = ERC20(_tokenAddress);
    }

    //Done
    function provideLiquidity(uint _amountERC20Token) external payable returns (uint liquidity) {
        require(msg.value > 0, "You did not provide any Eth");
        require(_amountERC20Token > 0, "Must provide a positive amount of this token");
        require(_amountERC20Token <= ERC20Token.balanceOf(msg.sender), "You did not send enough of this token for this transaction");
        if(totalLiquidityPositions == 0){
            liquidity = 100;
            totalLiquidityPositions = 100;
            ERC20Token.transferFrom(msg.sender, address(this), _amountERC20Token);
            contractERC20TokenBalance += _amountERC20Token;
            amountERC20Token[msg.sender] += _amountERC20Token;
            contractEthBalance += msg.value;
            amountEth[msg.sender] += msg.value;
        }else{
            require((_amountERC20Token / msg.value) == (contractERC20TokenBalance / contractEthBalance), "Must provide the correct ratio of ERC20 to Eth");
            ERC20Token.transferFrom(msg.sender, address(this), _amountERC20Token);
            contractERC20TokenBalance += _amountERC20Token;
            amountERC20Token[msg.sender] += _amountERC20Token;
            contractEthBalance += msg.value;
            amountEth[msg.sender] += msg.value;
            liquidity = totalLiquidityPositions * amountERC20Token[msg.sender] / contractERC20TokenBalance;
        }
        K = contractERC20TokenBalance * contractEthBalance;
        liquidityPosition[msg.sender] = liquidity;
        emit LiquidityProvided(_amountERC20Token, msg.value, liquidity);

    }

    //Done
    function estimateEthToProvide(uint _amountERC20Token) external view returns (uint amountEthToProvide){
        require(_amountERC20Token > 0, "Must provide a positive token amount");
        amountEthToProvide = (_amountERC20Token / contractERC20TokenBalance) * contractEthBalance;
    }

    //Done
    function estimateERC20TokenToProvide(uint _amountEth) external view returns (uint amountERC20TokenToProvide){
        require(_amountEth > 0, "Must provide a positive token amount");
        amountERC20TokenToProvide = (_amountEth / contractEthBalance) * contractERC20TokenBalance;
    }

    //Done
    function getMyLiquidityPositions() external view returns (uint256 liquidity){
        require(totalLiquidityPositions > 0, "There is no liquidity in this contract");
        liquidity = liquidityPosition[msg.sender];
    }

    //Done
    function withdrawLiquidity(uint _liquidityPositionsToBurn) external payable returns(uint256 ERC20TokenSent, uint256 EthSent){
        require(totalLiquidityPositions > 0, "Contract has no liquidity");
        require(_liquidityPositionsToBurn > 0, "Must provide amount to burn");
        require(liquidityPosition[msg.sender] >= _liquidityPositionsToBurn, "Cannot burn more liquidity than owned");
        require(_liquidityPositionsToBurn < totalLiquidityPositions, "Cannot liquidate entire contract");

        ERC20TokenSent = _liquidityPositionsToBurn * contractERC20TokenBalance / totalLiquidityPositions;
        EthSent = _liquidityPositionsToBurn * contractEthBalance / totalLiquidityPositions;
        liquidityPosition[msg.sender] -= _liquidityPositionsToBurn;
        totalLiquidityPositions -= _liquidityPositionsToBurn;
        contractEthBalance -= EthSent;
        contractERC20TokenBalance -= ERC20TokenSent;
        ERC20Token.transferFrom(address(this), msg.sender, ERC20TokenSent);
        K = contractEthBalance * contractERC20TokenBalance;
        amountEth[msg.sender] -= EthSent;
        amountERC20Token[msg.sender] -= ERC20TokenSent;
        payable(msg.sender).transfer(EthSent);
        emit LiquidityWithdrew(ERC20TokenSent, EthSent, _liquidityPositionsToBurn);


    }

    function swapForEth(uint _amountERC20Token) external payable returns (uint EthSent){
        require(_amountERC20Token > 0, "Must provide a positive token amount");
        require(ERC20Token.balanceOf(msg.sender) > 0, "Must have token in order to deposit");
        require(_amountERC20Token <= ERC20Token.balanceOf(msg.sender), "Cannot deposit more than owned");

        EthSent = contractEthBalance - K / (contractERC20TokenBalance + _amountERC20Token);
        ERC20Token.transferFrom(msg.sender, address(this), _amountERC20Token);
        contractERC20TokenBalance += _amountERC20Token;
        payable(msg.sender).transfer(EthSent);
        contractEthBalance -= EthSent;
        emit SwapForEth(_amountERC20Token,EthSent);

    }

    function estimateSwapForEth(uint _amountERC20Token) external view returns (uint EthSent){
        EthSent = contractEthBalance - K / (contractERC20TokenBalance + _amountERC20Token);
    }

    function swapForERC20Token() external payable returns (uint ERC20TokenSent){
        require(msg.value > 0, "Must provide a positive amount of Eth");
        require(msg.sender.balance > 0, "Must have token in order to deposit");
        require(msg.value <= msg.sender.balance, "Cannot deposit more than sent");

        ERC20TokenSent = contractERC20TokenBalance - K / (contractEthBalance + msg.value);
        ERC20Token.transferFrom(address(this), msg.sender, ERC20TokenSent);
        contractERC20TokenBalance -= ERC20TokenSent;
        contractEthBalance += msg.value;
        emit SwapForERC20Token(ERC20TokenSent, msg.value);
    }

    function estimateSwapForERC20Token(uint _amountEth) external view returns (uint ERC20TokenSent){
        ERC20TokenSent = contractERC20TokenBalance - K / (contractEthBalance + _amountEth);
    }

    event LiquidityProvided(uint amountERC20TokenDeposited, uint amountEthDeposited, uint liquidityPositionsIssued);
    event LiquidityWithdrew(uint amountERC20TokenWithdrew, uint amountEthWithdrew, uint liquidityPositionsBurned);
    event SwapForEth(uint amountERC20TokenDeposited, uint amountEthWithdrew);
    event SwapForERC20Token(uint amountERC20TokenWithdrew, uint amountEthDeposited);

}