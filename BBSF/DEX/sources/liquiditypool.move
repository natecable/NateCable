#[allow(unused_use)]
module dex::liquiditypool {
    use sui::coin::{Self, Coin};
    use sui::object::{Self, UID};
    use sui::transfer;
    use sui::tx_context::{Self, TxContext};
    use sui::balance::{Self, Supply, Balance};
    use sui::math;
    use dex::pair::{Self, PAIR};


    const EZeroAmount: u64 = 0;
    const EZeroMinAmount: u64 = 1;
    const EMinExceedsBalance: u64 = 2;
    const EInsufficientBalance: u64 = 3;
    const EInsufficientBalanceToken1 : u64 = 4;
    const EInsufficientBalanceToken2 : u64 = 5;

    fun init(_ctx: &mut TxContext){}


    public entry fun addLiquidity<T, P>(pair: &mut PAIR<T,P>, token1: Coin<T>, token2: Coin<P>, amount1desired: u64, amount2desired: u64, amount1min: u64, amount2min: u64, ctx: &mut TxContext){
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
    
    fun quote(amount1: u64, r1: u64, r2: u64): u64{
        (amount1 * r2 / r1)
    }




}   