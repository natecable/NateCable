module nftmarketplace::marketplace {
    use sui::dynamic_object_field as ofield;
    use sui::tx_context::{Self, TxContext};
    use sui::object::{Self, ID, UID};
    use sui::coin::{Self, Coin};
    use sui::transfer;
    use sui::event;

    /// For when amount paid does not match the expected.
    const EAmountIncorrect: u64 = 0;
    /// For when someone tries to delist without ownership.
    const ENotOwner: u64 = 1;

    //EVENTS
    struct Listed has copy, drop{
        itemID: ID,
        price: u64,
        seller: address,
    }

    struct Delist has copy, drop{
        itemID: ID,
        price: u64,
        seller: address,
    }

    struct Sale has copy, drop{
        itemID: ID,
        price: u64,
        seller: address,
        buyer: address,
    }



    /// A shared `Marketplace`. Can be created by anyone using the
    /// `create` function. One instance of `Marketplace` accepts
    /// only one type of Coin - `COIN` for all its listings.
    struct Marketplace<phantom COIN> has key {
        id: UID,
    }

    /// A single listing which contains the listed item and its
    /// price in [`Coin<COIN>`].
    struct Listing has key, store {
        id: UID,
        ask: u64,
        owner: address,
    }

    /// Create a new shared Marketplace.
    public entry fun create<COIN>(ctx: &mut TxContext) {
        let id = object::new(ctx);
        transfer::share_object(Marketplace<COIN> { id })
    }

    /// List an item at the Marketplace.
    public entry fun list<T: key + store, COIN>(
        marketplace: &mut Marketplace<COIN>,
        item: T,
        ask: u64,
        ctx: &mut TxContext
    ) {
        let item_id = object::id(&item);
        let listing = Listing {
            ask,
            id: object::new(ctx),
            owner: tx_context::sender(ctx),
        };

        ofield::add(&mut listing.id, true, item);
        ofield::add(&mut marketplace.id, item_id, listing);
        event::emit(Listed{
            itemID: item_id,
            price: ask,
            seller: tx_context::sender(ctx),
        })
    }

    /// Remove listing and get an item back. Only owner can do that.
    public fun delist<T: key + store, COIN>(
        marketplace: &mut Marketplace<COIN>,
        item_id: ID,
        ctx: &mut TxContext
    ): T {
        let Listing {
            id,
            owner,
            ask,
        } = ofield::remove(&mut marketplace.id, item_id);

        assert!(tx_context::sender(ctx) == owner, ENotOwner);

        let item = ofield::remove(&mut id, true);
        object::delete(id);
        event::emit(Delist{
            itemID: item_id,
            price: ask,
            seller: tx_context::sender(ctx),
        });
        item
    }

    /// Call [`delist`] and transfer item to the sender.
    public entry fun delist_and_take<T: key + store, COIN>(
        marketplace: &mut Marketplace<COIN>,
        item_id: ID,
        ctx: &mut TxContext
    ) {
        let item = delist<T, COIN>(marketplace, item_id, ctx);
        transfer::public_transfer(item, tx_context::sender(ctx));
    }

    /// Purchase an item using a known Listing. Payment is done in Coin<C>.
    /// Amount paid must match the requested amount. If conditions are met,
    /// owner of the item gets the payment and buyer receives their item.
    public fun buy<T: key + store, COIN>(
        marketplace: &mut Marketplace<COIN>,
        item_id: ID,
        paid: Coin<COIN>,
        ctx: &mut TxContext
    ): T {
        let Listing {
            id,
            ask,
            owner
        } = ofield::remove(&mut marketplace.id, item_id);

        assert!(ask == coin::value(&paid), EAmountIncorrect);

        // Check if there's already a Coin hanging and merge `paid` with it.
        // Otherwise attach `paid` to the `Marketplace` under owner's `address`.
        if (ofield::exists_<address>(&marketplace.id, owner)) {
            coin::join(
                ofield::borrow_mut<address, Coin<COIN>>(&mut marketplace.id, owner),
                paid
            )
        } else {
            ofield::add(&mut marketplace.id, owner, paid)
        };

        let item = ofield::remove(&mut id, true);
        object::delete(id);
        event::emit(Sale{
            itemID: item_id,
            price: ask,
            seller: owner,
            buyer: tx_context::sender(ctx),
        });
        item
    }

    /// Call [`buy`] and transfer item to the sender.
    public entry fun buy_and_take<T: key + store, COIN>(
        marketplace: &mut Marketplace<COIN>,
        item_id: ID,
        paid: Coin<COIN>,
        ctx: &mut TxContext
    ) {
        transfer::public_transfer(
            buy<T, COIN>(marketplace, item_id, paid, ctx),
            tx_context::sender(ctx)
        )
    }

    /// Take profits from selling items on the `Marketplace`.
    public fun take_profits<COIN>(
        marketplace: &mut Marketplace<COIN>,
        ctx: &mut TxContext
    ): Coin<COIN> {
        ofield::remove<address, Coin<COIN>>(&mut marketplace.id, tx_context::sender(ctx))
    }

    /// Call [`take_profits`] and transfer Coin to the sender.
    public entry fun take_profits_and_keep<COIN>(
        marketplace: &mut Marketplace<COIN>,
        ctx: &mut TxContext
    ) {
        transfer::public_transfer(
            take_profits(marketplace, ctx),
            tx_context::sender(ctx)
        )
    }
}
