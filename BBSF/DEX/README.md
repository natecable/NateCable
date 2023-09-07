# Instructions on how to run this decentralized exchange workload:

## Prerequisites 
* Ensure you have a working SUI wallet address with sufficient SUI devnet coins. If not go to SUI wallet and request for more. You may also go to their discord and they have instructions on how to get more of these coins. 
* Export your active wallet address to an environment variable called sui_address using this command:
```export sui_address=<active sui address>```

# Creating and minting tokens onto your wallet
* Publish the package and save the output to a file called published:
```
sui client publish --gas-budget 200000000 <path/to/folder_containing_Move.toml> [> published]
```

## **Using the mint.sh script**
* Grab the 2 TreasuryCap IDs for the coins and the package ID from the published file; they will look like this:

    ***CableCoin***
    ```
    Object {
        "type": String("created"),
        "sender": String("0x19093b0acc8a7492783320d238b97ab0329d2a2e0d41d9ec4aede87dac8f91ef"),
        "owner": Object {
            "AddressOwner": String("0x19093b0acc8a7492783320d238b97ab0329d2a2e0d41d9ec4aede87dac8f91ef"),
        },
        "objectType": String("0x2::coin::TreasuryCap<0x6624ab278428366bd0253dfefacdba0bff49653bf8fa816b8c8d1e8b12724cda::cablecoin::CABLECOIN>"),
    --->"objectId": String("0xc9ee0db7a420a8877da021c9f6942cf4881e591a9e51dedf72a2b21093749b6a"), <---
        "version": String("43"),
        "digest": String("89gyyxwNgWF1fMjyt3w6yi7NKQTBDCHMt7Xd22ha82z7"),
    },
    ```
    ***KorCoin***
    ```
    Object {
        "type": String("created"),
        "sender": String("0x19093b0acc8a7492783320d238b97ab0329d2a2e0d41d9ec4aede87dac8f91ef"),
        "owner": Object {
            "AddressOwner": String("0x19093b0acc8a7492783320d238b97ab0329d2a2e0d41d9ec4aede87dac8f91ef"),
        },
        "objectType": String("0x2::coin::TreasuryCap<0x6624ab278428366bd0253dfefacdba0bff49653bf8fa816b8c8d1e8b12724cda::korcoin::KORCOIN>"),
    --->"objectId": String("0x566805731e375bb53f26d294a55b730f845b1334c9bb0819532d6531b1c6595d"), <---
        "version": String("43"),
        "digest": String("Ug4FV9i9JLNadLrEh3QNu6R5PJBZSpwDtr7movmu7Xn"),
    },
    ```
    ***Package ID***
    ```
    Object {
        "type": String("published"),
    --->"packageId": String("0x6624ab278428366bd0253dfefacdba0bff49653bf8fa816b8c8d1e8b12724cda"), <---
        "version": String("1"),
        "digest": String("GVTg1mhuTFjRBTJg3RyApTngwcc2LkZnjB86ZQ7Mf9Fs"),
        "modules": Array [
            String("cablecoin"),
            String("korcoin"),
            String("liquiditypool"),
            String("pair"),
        ],
    },
    ```
* Now run the mint.sh script using these IDs with the following command:

```
sh mint.sh [CableCoin treasury cap ID] [KorCoin treasury cap ID] [packageID]
```
* This will mint 200000000 of each token onto your wallet, and also save the following environment variables:
    - $type_args (will be used in nearly every function call)
    - $package (saves your packageID)

# Creating the pair
* Now lets create the pair object with these two tokens:

```
sui client call --package $package --module pair --function new_pair --gas-budget 61000000 --type-args $type_args > pairCreated
```
* Find the objectID of the pair in the pairCreated file and save it to an environment variable called $pair

# Add liquidity to the pool

* After we create the pair object we are able to add liquidity to the pool with this command:

```
sui client call --package $package --module liquiditypool --function add_liquidity --gas-budget 61000000 --args $pair [cablecoin object ID] [korcoin object ID] [amount cablecoin desired] [amount korcoin desired] [min cablecoin to add] [min korcoin to add] --type-args $type_args > addedLiquidity
```

# Remove liquidity from the pool
* We can now remove liquidity after we have added it to the pool with this command:

```
sui client call --package $package --module liquiditypool --function remove_liquidity --gas-budget 61000000 --args $pair [liquidity token object id from addedLiquidity file] [Amount of liquidity tokens to add] [Minimum token1 to receive] [Minimum token2 to receive] --type-args $type_args > remove_liquidity
```

# Swapping tokens
* There are 4 functions for swapping tokens
    - swap_exact_tokenA_for_tokenB
    - swap_exact_tokenB_for_tokenA
    - swap_tokenA_for_exact_tokenB
    - swap_tokenB_for_exact_tokenA

## Swapping Exact Tokens for Tokens
* Input the token you are swapping, a set value for a token you input, and a minimum amount of the token you want to receive 

**Cablecoin for Korcoin**
```
sui client call --package $package --module liquiditypool --function swap_exact_tokensA_for_tokensB --args $pair [Object ID of CableCoin] [Amount of cablecoin providing] [Min amount of korcoin to receive] --type-args $type_args --gas-budget 200000000
```

**Korcoin for Cablecoin**
```
sui client call --package $package --module liquiditypool --function swap_exact_tokensB_for_tokensA --args $pair [Object ID of KorCoin] [Amount of korcoincoin providing] [Min amount of cablecoin to receive] --type-args $type_args --gas-budget 200000000
```

## Swapping Tokens for Exact Tokens
* Input the token you are swapping, a set value for a token you want to receive, and a maximum amount of the token you are providing

**Cablecoin for Korcoin**
```
sui client call --package $package --module liquiditypool --function swap_tokensA_for_exact_tokensB --args $pair [CableCoin Object ID] [Amount korcoin wanted] [Max Cablecoin to provide] --type-args $type_args --gas-budget 200000000
```

**Korcoin for Cablecoin**
```
sui client call --package $package --module liquiditypool --function swap_tokensB_for_exact_tokensA --args $pair [KorCoin Object ID] [Amount cablecoin wanted] [Max Korcoin to provide] --type-args $type_args --gas-budget 200000000
```
