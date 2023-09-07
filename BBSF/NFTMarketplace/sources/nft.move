#[allow(unused_variable)]

module nftmarketplace::nft{
    use sui::tx_context::{Self, TxContext};
    use sui::object::{Self, UID};
    use sui::transfer;

    struct BlockbenchToken has key, store{
        id: UID,
        number: u64,
        owner: address
    }

    fun init(ctx: &mut TxContext){ }

    public fun mint(number: u64, ctx: &mut TxContext){
        let token = BlockbenchToken{
            id: object::new(ctx),
            number: number,
            owner: tx_context::sender(ctx),
        };
        transfer::public_transfer(token, tx_context::sender(ctx));
    }
}
