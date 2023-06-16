#[allow(unused_use)]
module dex::pair {
    
    use sui::coin::{Self, Coin, TreasuryCap};
    use sui::object::{Self, UID};
    use sui::transfer;
    use sui::tx_context::{Self, TxContext};
    use sui::balance::{Self, Supply, Balance};
    use sui::math;
    use std::option;
    use sui::dynamic_object_field as ofield;

    

    struct LIQUIDITY<phantom T, phantom P> has drop{}


    struct PAIR<phantom T, phantom P> has key {
        id: UID,
        token1: Balance<T>,
        token2: Balance<P>,
        liq_supply: Supply<LIQUIDITY<T, P>>,
    }


    fun init(_: &mut TxContext) {}


    public fun new_pair<T, P>(_ctx: &mut TxContext){
        let pair = PAIR{
            id: object::new(_ctx),
            token1: balance::zero<T>(),
            token2: balance::zero<P>(),
            liq_supply: balance::create_supply(LIQUIDITY<T, P>{})
        };
        transfer::share_object(pair);
    }


    public fun get_reserves<T, P>(pair: &mut PAIR<T, P>): (u64, u64){
        (
            balance::value(&pair.token1),
            balance::value(&pair.token2)
        )
    }

    public fun get_toks<T, P>(pair: &mut PAIR<T, P>, ctx: &mut TxContext): (&mut Balance<T>, &mut Balance<P>){
        (
            &mut pair.token1,
            &mut pair.token2
        )
    }

    public entry fun mint<T, P>(pair: &mut PAIR<T, P>, amount1: u64, amount2: u64, ctx: &mut TxContext){
        transfer::public_transfer(
            mint_<T,P>(pair, amount1, amount2, ctx),
            tx_context::sender(ctx),
        )
    }

    fun mint_<T, P>(pair: &mut PAIR<T, P>, amount1: u64, amount2: u64, ctx: &mut TxContext): Coin<LIQUIDITY<T, P>>{
        let liq = amount1 + amount2;
        let bal = balance::increase_supply(&mut pair.liq_supply, liq);
        coin::from_balance(bal, ctx)

    }


    



    // public fun mint<T, P>(pair: &mut PAIR<T,P>, amount1: u64, amount2: u64){
    //     let (reserve1, reserve2) = get_reserves(pair);
    //     let amount1 = math::sqrt(amount1 * reserve1);
    //     let amount2 = math::sqrt(amount2 * reserve2);
    //     let liq_amount = math::min(amount1, amount2);
    //     let liq_supply = balance::value(&pair.liq_supply);
    //     let amount1 = math::div(amount1 * liq_supply, liq_amount);
    //     let amount2 = math::div(amount2 * liq_supply, liq_amount);
    //     balance::mint(&mut pair.token1, amount1);
    //     balance::mint(&mut pair.token2, amount2);
    //     balance::mint(&mut pair.liq_supply, liq_amount);
    // }

    


}