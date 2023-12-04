// Removes warnings for unused imports
#[allow(unused_use)]

module dex::pair {
    
    use sui::coin::{Self, Coin, TreasuryCap};
    use sui::object::{Self, UID};
    use sui::transfer;
    use sui::tx_context::{Self, TxContext};
    use sui::balance::{Self, Supply, Balance};
    use sui::math;
    use std::option;
    use sui::event;

    const EInsufficientLiquidityBurned: u64 = 1;
    const EInsufficientAmountTokenA: u64 = 2;
    const EInsufficientAmountTokenB: u64 = 3;

    // Liquidity is a struct that holds the state of a liquidity token
    struct LIQUIDITY<phantom T, phantom P> has drop{}

    // Pair is a struct that holds the state of a pair of tokens
    struct PAIR<phantom T, phantom P> has key {
        id: UID,
        token1: Balance<T>,
        token2: Balance<P>,
        liq_supply: Supply<LIQUIDITY<T, P>>,
    }

    // EVENTS 
    struct Mint has copy, drop{
        sender: address,
        amount1: u64,
        amount2: u64,
    }

    struct Burn has copy, drop{
        sender: address,
        amount1: u64,
        amount2: u64,
    }

    struct Swap has copy, drop{
        sender: address,
        amount1In: u64,
        amount2In: u64,
        amount1Out: u64,
        amount2Out: u64,
    }

    struct Sync has copy, drop{
        reserve1: u64,
        reserve2: u64,
    }

    struct GetPairReserves has copy, drop{
        reserve1: u64,
        reserve2: u64,
    }

    fun init(_: &mut TxContext) {}

    // This function creates a new pair
    public fun new_pair<T, P>(_ctx: &mut TxContext){
        let pair = PAIR{
            id: object::new(_ctx),
            token1: balance::zero<T>(),
            token2: balance::zero<P>(),
            liq_supply: balance::create_supply(LIQUIDITY<T, P>{})
        };
        transfer::share_object(pair);
    }

    // This function swaps token a For token B based on the balances of token A
    public fun swapAforB<T, P>(pair: &mut PAIR<T, P>, amountA: Balance<T>, amountB: u64, ctx: &mut TxContext): Coin<P>{
        let amountAin = balance::value(&amountA);
        balance::join(&mut pair.token1, amountA);
        let ret = coin::from_balance(balance::split(&mut pair.token2, amountB), ctx);
        event::emit(Swap{
            sender: tx_context::sender(ctx),
            amount1In: amountAin,
            amount2In: 0,
            amount1Out: 0,
            amount2Out: amountB,
        });
        event::emit(Sync{
            reserve1: balance::value(&pair.token1),
            reserve2: balance::value(&pair.token2),
        });
        ret
    }

    // This function swaps token B For token A based on the balances of token B
    public fun swapBforA<T, P>(pair: &mut PAIR<T, P>, amountB: Balance<P>, amountA: u64, ctx: &mut TxContext): Coin<T>{
        let amountBin = balance::value(&amountB);
        balance::join(&mut pair.token2, amountB);
        let ret = coin::from_balance(balance::split(&mut pair.token1, amountA), ctx);
        event::emit(Swap{
            sender: tx_context::sender(ctx),
            amount1In: 0,
            amount2In: amountBin,
            amount1Out: amountA,
            amount2Out: 0,
        });
        event::emit(Sync{
            reserve1: balance::value(&pair.token1),
            reserve2: balance::value(&pair.token2),
        });
        ret
    }

    //HELPER FUNCTIONS

    // This function gets the reserves of the pair
    public fun get_reserves<T, P>(pair: &mut PAIR<T, P>): (u64, u64){
        event::emit(GetPairReserves{
            reserve1: balance::value(&pair.token1),
            reserve2: balance::value(&pair.token2),
        });
        (
            balance::value(&pair.token1),
            balance::value(&pair.token2)
        )
    }

    // This function gets the tokens of the pair
    public fun get_toks<T, P>(pair: &mut PAIR<T, P>, ctx: &mut TxContext): (&mut Balance<T>, &mut Balance<P>){
        (
            &mut pair.token1,
            &mut pair.token2
        )
    }

    // This function mints
    public entry fun mint<T, P>(pair: &mut PAIR<T, P>, amount1: u64, amount2: u64, ctx: &mut TxContext){
        transfer::public_transfer(
            mint_<T,P>(pair, amount1, amount2, ctx),
            tx_context::sender(ctx),
        );
        event::emit(Mint{
            sender: tx_context::sender(ctx),
            amount1: amount1,
            amount2: amount2,
        });
        event::emit(Sync{
            reserve1: balance::value(&pair.token1),
            reserve2: balance::value(&pair.token2),
        });
    }

    fun mint_<T, P>(pair: &mut PAIR<T, P>, amount1: u64, amount2: u64, ctx: &mut TxContext): Coin<LIQUIDITY<T, P>>{
        let liq = amount1 + amount2;
        let bal = balance::increase_supply(&mut pair.liq_supply, liq);
        coin::from_balance(bal, ctx)

    }

    // This function burns tokens
    public fun burn_supply<T, P>(pair: &mut PAIR<T, P>, amount: Balance<LIQUIDITY<T,P>>, amount1min: u64, amount2min: u64, ctx: &mut TxContext){
        let initialSupply = balance::supply_value(&pair.liq_supply); 
        let (balance1, balance2) = get_reserves(pair);
        let totalBalance = balance1 + balance2;
        let providedLiquidity = balance::value(&amount);
        let amount1 = (providedLiquidity * balance1) / totalBalance;
        let amount2 = (providedLiquidity * balance2) / totalBalance;

        assert!(amount1 > 0 && amount2 > 0, EInsufficientLiquidityBurned);
        assert!(amount1 >= amount1min, EInsufficientAmountTokenA);
        assert!(amount2 >= amount2min, EInsufficientAmountTokenB);
        let bal = balance::decrease_supply(&mut pair.liq_supply, amount);

        let coin1 = coin::from_balance(balance::split(&mut pair.token1, amount1), ctx);
        let coin2 = coin::from_balance(balance::split(&mut pair.token2, amount2), ctx);
        transfer::public_transfer(coin1, tx_context::sender(ctx));
        transfer::public_transfer(coin2, tx_context::sender(ctx));
        event::emit(Burn{
            sender: tx_context::sender(ctx),
            amount1: amount1,
            amount2: amount2,
        });
        event::emit(Sync{
            reserve1: balance::value(&pair.token1),
            reserve2: balance::value(&pair.token2),
        });
    }
}
