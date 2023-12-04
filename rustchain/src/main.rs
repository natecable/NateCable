use num_bigint::BigUint;
use rug::Float;
use merkle::{MerkleTree, DataHash};
use std::fs::File;
use std::io::Write;
use std::io::BufRead;
use std::env::current_dir;

pub struct Block {
    pre_head: String,
    root: String,
    timestamp: u64,
    diff_target: String,
    nonce: u32,
    header_hash: Option<String>,
}

impl Block {
    fn hash_header(&self) -> String{
        let mut to_hash = String::new();
        to_hash.push_str(&self.pre_head);
        to_hash.push_str(&self.root);
        to_hash.push_str(&self.timestamp.to_string());
        to_hash.push_str(&self.diff_target);
        to_hash.push_str(&self.nonce.to_string());
        merkle::hash::hash(&to_hash)
    }

    fn set_nonce(&mut self, nonce: u32) -> bool {
        self.nonce = nonce;
        let check = String::from(nonce.to_string() + &self.root);
        let hash = merkle::hash::hash(&check);
        if &hash > &self.diff_target{
            self.header_hash = Some(self.hash_header());
            true
        } else {
            self.header_hash = None;
            false
        }
    }

    fn find_target(p_of_failure: &str) -> String {
        let _max = BigUint::parse_bytes(b"ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16).unwrap();
        
        let max_decimal = f64::MAX;
        let fail_rate = Float::with_val(256, p_of_failure.parse::<f64>().unwrap());
        
        let result = max_decimal * fail_rate;
        let bound = result.to_integer();
        let base16 = bound.unwrap().to_string_radix(16);
        
        base16
    }

    pub fn new(pre_head: String, root: String, nonce: u32) -> Block {
        let ts = std::time::SystemTime::now().duration_since(std::time::UNIX_EPOCH).unwrap().as_secs();
        let diff_target = Block::find_target("0.99");
        let check = String::from(nonce.to_string() + &root);
        let hash = merkle::hash::hash(&check);
        let mut to_be_header: Option<String> = None;
        if &hash > &diff_target {
            let mut hold = String::new();
            hold.push_str(&pre_head);
            hold.push_str(&root);
            hold.push_str(&ts.to_string());
            hold.push_str(&diff_target);
            hold.push_str(&nonce.to_string());
            to_be_header = Some(merkle::hash::hash(&hold));
        }
        Block {
            pre_head: pre_head,
            root: root,
            timestamp: ts,
            diff_target: diff_target,
            nonce: nonce,
            header_hash: to_be_header,
        }
    }

    pub fn to_string(&self) -> String {
        let mut s = String::new();
        s.push_str("\n\tHash of previous block: ");
        s.push_str(&self.pre_head);
        s.push_str("\n\tHash of merkle root: ");
        s.push_str(&self.root);
        s.push_str("\n\tTimestamp: ");
        s.push_str(&self.timestamp.to_string());
        s.push_str("\n\tDifficulty target: ");
        s.push_str(&self.diff_target);
        s.push_str("\n\tNonce: ");
        s.push_str(&self.nonce.to_string());
        s.push_str("\n\tHash of header: ");
        s.push_str(&self.header_hash.clone().unwrap());
        s
    }



}


pub fn read_file(path: &str) -> Vec<DataHash> {
    let mut data: Vec<DataHash> = Vec::new();
    let file = std::fs::File::open(path).unwrap();
    let reader = std::io::BufReader::new(file);
    for line in reader.lines() {
        let line = line.unwrap();
        //Split the line into two parts, the address and the balance
        let mut split = line.split_whitespace();
        let address = split.next().unwrap();
        let balance = split.next().unwrap();
        let mut to_hash = String::from(address);
        to_hash.push_str(" ");
        to_hash.push_str(&String::from(balance));
        let hash = merkle::hash::hash(&to_hash);
        data.push(DataHash::new(String::from(address), balance.parse::<u32>().unwrap(), hash));
    }

    data
}


pub fn write_output(blockchain: Vec<Block>, trees: Vec<MerkleTree>, output_file: String) {
    let mut file = File::create(output_file).unwrap();
    for i in 0..blockchain.len() {
        let _ = file.write_all("\nBEGIN BLOCK \n".as_bytes());
        _ = file.write_all("BEGIN HEADER \n".as_bytes());
        _ = file.write_all(blockchain[i].to_string().as_bytes());
        _ = file.write_all("\nEND HEADER \n".as_bytes());
        _ = file.write_all(trees[i].to_string().as_bytes());
        _ = file.write_all("\nEND BLOCK \n".as_bytes());
    }
}


fn main() {
    let args: Vec<String> = std::env::args().collect();
    if args.len() != 3 {
        println!("Usage: {} [./path/to/data/] [Output File Name]", args[0]);
        return;
    }
    let mut blockchain: Vec<Block> = Vec::new();
    let mut trees: Vec<MerkleTree> = Vec::new();
    let mut prehead_temp: String = String::from("0");

    let mut path = current_dir().expect("").clone().as_mut_os_str().to_str().unwrap().to_string();
    path.push_str(args[1].clone().as_str());

    //Within a directory, create a loop that goes through each file one by one
    for entry in std::fs::read_dir(&path).unwrap() {
        let entry = entry.unwrap();
        let path = entry.path();
        if path.is_file() {
            let data = read_file(path.to_str().unwrap());
            let merkle_tree = MerkleTree::new(data);
            let mut nonce = 0;
            let mut mblock = Block::new(prehead_temp.clone(), merkle_tree.get_root(), nonce);
            println!("Mining block {}", blockchain.len());
            while mblock.header_hash.is_none() {
                nonce += 1;
                mblock.set_nonce(nonce);
            }
            prehead_temp = mblock.header_hash.clone().unwrap();
            trees.push(merkle_tree);
            blockchain.push(mblock);
        }
    }

    write_output(blockchain, trees, args[2].clone());


}
