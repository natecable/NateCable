// To get rid of warnings
#[allow(unused_use)]

module dex::liquiditypool {
    // Necessary imports
    use sui::coin::{Self, Coin};
    use sui::object::{Self, UID};
    use sui::transfer;
    use sui::tx_context::{Self, TxContext};
    use sui::balance::{Self, Supply, Balance};
    use sui::math;
    use sui::address;
    use dex::pair::{Self, PAIR, LIQUIDITY};


    const EZeroAmount: u64 = 0;
    const EZeroMinAmount: u64 = 1;
    const EMinExceedsBalance: u64 = 2;
    const EInsufficientBalance: u64 = 3;
    const EInsufficientBalanceToken1 : u64 = 4;
    const EInsufficientBalanceToken2 : u64 = 5;
    const EInsufficientBalanceLiquidity : u64 = 6;
    const EInvalidToken: u64 = 7;
    const EEmptyReserves: u64 = 8;
    const EMinNotReached: u64 = 9;
    const EMaxExceeded: u64 = 10;

    fun init(_ctx: &mut TxContext){}

    // Adds liquidity to the liquidity pool and mints liquidity tokens
    // Parameters: pair object id, token1 object id, token2 object id, amount of token1 to add, amount of token2 to add, minimum amount of token1 to receive, minimum amount of token2 to receive
    public entry fun add_liquidity<T, P>(pair: &mut PAIR<T,P>, token1: Coin<T>, token2: Coin<P>, amount1desired: u64, amount2desired: u64, amount1min: u64, amount2min: u64, ctx: &mut TxContext){
        assert!(amount1desired > 0 && amount2desired > 0, EZeroAmount);
        assert!(coin::value(&token1) >= amount1desired, EInsufficientBalance);
        assert!(coin::value(&token2) >= amount2desired, EInsufficientBalance);
        assert!(amount1min > 0 && amount2min > 0, EZeroMinAmount);
        assert!(amount1min <= amount1desired, EMinExceedsBalance);
        assert!(amount2min <= amount2desired, EMinExceedsBalance);
        let finalAmount1: u64 = 0;
        let finalAmount2: u64 = 0;

        let (reserve1, reserve2) = pair::get_reserves(pair);
        if(reserve1 == 0 && reserve2 == 0){
            (finalAmount1, finalAmount2) = (amount1desired, amount2desired);
        }else{
            let amount2optimal = quote(amount1desired, reserve1, reserve2);
            if(amount2optimal <= amount2desired){
                assert!(amount2optimal >= amount2min, EInsufficientBalanceToken2);
                finalAmount1 = amount1desired;
                finalAmount2 = amount2optimal;
            }else{
                let amount1optimal = quote(amount2desired, reserve2, reserve1);
                assert!(amount1optimal <= amount1desired, EInsufficientBalanceToken1);
                assert!(amount1optimal >= amount1min, EInsufficientBalanceToken1);
                finalAmount1 = amount1optimal;
                finalAmount2 = amount2desired;
            }
        };

        let t1 = coin::split(&mut token1, finalAmount1, ctx);
        let t2 = coin::split(&mut token2, finalAmount2, ctx);
        let (b1, b2) = pair::get_toks(pair, ctx);
        balance::join(b1, coin::into_balance(t1));
        balance::join(b2, coin::into_balance(t2));
        transfer::public_transfer(token1, tx_context::sender(ctx));
        transfer::public_transfer(token2, tx_context::sender(ctx));
        pair::mint(pair, finalAmount1, finalAmount2, ctx);

    }

    // Removes liquidity from the liquidity pool and burns liquidity tokens
    // Parameters: pair object id, liquidity tokens id, amount of liquidity tokens to burn, minimum amount of token1 to receive, minimum amount of token2 to receive
    public entry fun remove_liquidity<T,P>(pair: &mut PAIR<T,P>, liquidityTokens: &mut Coin<LIQUIDITY<T, P>>, amountLiquidity: u64, amount1min: u64, amount2min: u64, ctx: &mut TxContext){
        let userLiquidity = coin::value(liquidityTokens);
        assert!(userLiquidity >= amountLiquidity, EInsufficientBalanceLiquidity);
        pair::burn_supply(pair, coin::into_balance(coin::split(liquidityTokens, amountLiquidity, ctx)), amount1min, amount2min, ctx);
    }

    // Swaps exact amount of token A for token B
    public entry fun swap_exact_tokensA_for_tokensB<T, P>(pair: &mut PAIR<T,P>, token: &mut Coin<T>, amountIn: u64, amountOutMin: u64, ctx: &mut TxContext){
        assert!(amountIn > 0, EZeroAmount);
        assert!(coin::value(token) >= amountIn, EInsufficientBalance); 
        let amountOut = get_amount_out(pair, amountIn, 0); // A for B, so 0
        assert!(amountOut >= amountOutMin, EMinNotReached);
        let swappedToks = pair::swapAforB(pair, coin::into_balance(coin::split(token, amountIn, ctx)), amountOut, ctx);
        transfer::public_transfer(swappedToks, tx_context::sender(ctx));
    }

    // Swaps exact amount of token B for token A
    public entry fun swap_exact_tokensB_for_tokensA<T, P>(pair: &mut PAIR<T,P>, token: &mut Coin<P>, amountIn: u64, amountOutMin: u64, ctx: &mut TxContext){
        assert!(amountIn > 0, EZeroAmount);
        assert!(coin::value(token) >= amountIn, EInsufficientBalance); 
        let amountOut = get_amount_out(pair, amountIn, 1); // B for A, so 1
        assert!(amountOut >= amountOutMin, EMinNotReached);
        let swappedToks = pair::swapBforA(pair, coin::into_balance(coin::split(token, amountIn, ctx)), amountOut, ctx);
        transfer::public_transfer(swappedToks, tx_context::sender(ctx));
    }

    public entry fun swap_tokensA_for_exact_tokensB<T, P>(pair: &mut PAIR<T,P>, token: &mut Coin<T>, amountOut: u64, amountInMax: u64, ctx: &mut TxContext){
        assert!(amountOut > 0, EZeroAmount);
        assert!(coin::value(token) >= amountInMax, EInsufficientBalance);
        let amountIn = get_amount_in(pair, amountOut, 0); // A for B, so 0
        assert!(amountIn <= amountInMax, EMaxExceeded);
        let swappedToks = pair::swapAforB(pair, coin::into_balance(coin::split(token, amountIn, ctx)), amountOut, ctx);
        transfer::public_transfer(swappedToks, tx_context::sender(ctx));
    }

    public entry fun swap_tokensB_for_exact_tokensA<T, P>(pair: &mut PAIR<T,P>, token: &mut Coin<P>, amountOut: u64, amountInMax: u64, ctx: &mut TxContext){
        assert!(amountOut > 0, EZeroAmount);
        assert!(coin::value(token) >= amountInMax, EInsufficientBalance);
        let amountIn = get_amount_in(pair, amountOut, 1); // B for A, so 1
        assert!(amountIn <= amountInMax, EMaxExceeded);
        let swappedToks = pair::swapBforA(pair, coin::into_balance(coin::split(token, amountIn, ctx)), amountOut, ctx);
        transfer::public_transfer(swappedToks, tx_context::sender(ctx));
    }

    
    fun quote(amount1: u64, r1: u64, r2: u64): u64{
        (amount1 * r2 / r1)
    }

    //If IN is token1, use 0, if IN is token2, use 1
    fun get_amount_out<T, P>(pair: &mut PAIR<T,P>, amountIn: u64, inType: u8): u64{
        assert!(amountIn > 0, EZeroAmount);
        let(reserve1, reserve2) = pair::get_reserves(pair);
        assert!(reserve1 > 0 && reserve2 > 0, EEmptyReserves);
        assert!(inType == 0 || inType == 1, EInvalidToken);
        let ret = 0;
        if(inType == 0){
            let amountIn = amountIn * 1000;
            let numerator = amountIn * reserve2;
            let denominator = (reserve1 * 1000) + amountIn;
            ret = (numerator / denominator);
        }else{
            let amountIn = amountIn * 1000;
            let numerator = amountIn * reserve1;
            let denominator = (reserve2 * 1000) + amountIn;
            ret = (numerator / denominator);
        };
        ret
    }

    fun get_amount_in<T, P>(pair: &mut PAIR<T,P>, amountOut: u64, inType: u8): u64{
        assert!(amountOut > 0, EZeroAmount);
        let(reserve1, reserve2) = pair::get_reserves(pair);
        assert!(reserve1 > 0 && reserve2 > 0, EEmptyReserves);
        assert!(inType == 0 || inType == 1, EInvalidToken);
        let ret = 0;
        if(inType == 0){
            let numerator = reserve1 * amountOut * 1000;
            let denominator = (reserve2 - amountOut) * 1000;
            ret = (numerator / denominator) + 1;
        }else{
            let numerator = reserve2 * amountOut * 1000;
            let denominator = (reserve1 - amountOut) * 1000;
            ret = (numerator / denominator) + 1;
        };
        ret
    }
}
