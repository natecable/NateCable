# Instructions on how to run this NFT Marketplace workload:

## Prerequisites 
* Ensure you have a working SUI wallet address with sufficient SUI coins. If not go to SUI wallet and request for more. You may also go to their discord and they have instructions on how to get more of these coins. 
* Export your active wallet address to an environment variable called sui_address using this command:
```export sui_address=[active sui address]```

# Publishing the contracts to the blockchain
* Publish the package and save the output to a file called published:
```
sui client publish --gas-budget 200000000 > published
```

# Creating the NFT Marketplace
* Create the NFT Marketplace using the following command:
```
sui client call --package [packageID] --module marketplace --function create --type-args "0x2::coin::Coin<0x2::sui::SUI>" --gas-budget 20000000 > marketplace
```
In the output file, you will see the marketplace address. Save this address to an environment variable called marketplace_address using this command:
```export marketplace_address=[marketplace address]```

# Creating the NFT
* Create the NFT using the following command:
```
sui client call --package [packageID] --module nft --function mint --args [id number for NFT] --gas-budget 20000000 > nft
```
In the output file, you will see the NFT object ID.

# Listing the NFT
* List the NFT using the following command:
```
sui client call --package [packageID] --module marketplace --function list --args [marketplace_address] [NFT Object ID] [asking price] --gas-budget 20000000 --type-args "[NFT Type]" "0x2::coin::Coin<0x2::sui::SUI>"
```

# Buying the NFT
* Buy the NFT using the following command:
```
sui client call --package [packageID] --module marketplace --function buy_and_take --args [marketplace_address] [NFT Object ID] [SUI Coin Object ID] --gas-budget 20000000 --type-args "[NFT Type]" "0x2::coin::Coin<0x2::sui::SUI>"