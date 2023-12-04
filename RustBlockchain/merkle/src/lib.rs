pub mod hash {
    pub fn hash(input: &str) -> String {
        let mut s = String::new();
        s.push_str(&input);
        let output = sha256::digest(&s);
        output
    }
}

pub struct DataHash {
    address: Option<String>,
    balance: u32,
    hash_value: String,
}

impl DataHash {
    pub fn new(address: String, balance: u32, hash_value: String) -> DataHash {
        DataHash {
            address: Some(address),
            balance: balance,
            hash_value: hash_value,
        }
    }

    pub fn new_hv(hash_value: String) -> DataHash {
        DataHash {
            address: None,
            balance: 0,
            hash_value: hash_value,
        }
    }

    pub fn to_string(&self) -> String {
        let mut s = String::new();
        if let Some(address) = &self.address {
            s.push_str(&address);
            s.push_str(&String::from(" "));
            s.push_str(&self.balance.to_string());
        }
        s.push_str(&self.hash_value);
        s
    }

}


pub struct Leaf {
    left: Option<Box<Leaf>>,
    right: Option<Box<Leaf>>,
    data: DataHash,
}

impl Leaf {
    pub fn new(l: Option<Box<Leaf>>, r: Option<Box<Leaf>>, d: DataHash) -> Leaf {
        Leaf {
            left: l,
            right: r,
            data: d,
        }
    }

    pub fn get_hash(&self) -> String {
        let s = self.data.hash_value.clone();
        s
    }

    pub fn to_string(&self) -> String {
        let mut s = String::new();
        if let Some(left) = &self.left {
            s.push_str(&left.to_string());
        }
        if let Some(right) = &self.right {
            s.push_str(&right.to_string());
        }
        s.push_str("\n");
        s.push_str(&self.data.to_string());
        s
    }

}


pub struct MerkleTree {
    root: Leaf,
}

impl MerkleTree {

    fn generate_tree(data: Vec<DataHash>) -> Leaf {

        let mut children: Vec<Leaf> = Vec::new();
        for d in data {
            children.push(Leaf::new(None, None, d));
        }
        let mut par: Vec<Leaf> = Vec::new();

        while children.len() > 1 {
            while children.len() > 0 {
                let left_child = Some(Box::new(children.remove(0)));
                let mut right_child = None;

                let mut _parent_hash = DataHash::new_hv(String::new());
                if children.len() > 0 {
                    right_child = Some(Box::new(children.remove(0)));
                    let mut s = String::new();
                    s.push_str(&left_child.as_ref().expect("").get_hash());
                    s.push_str(&right_child.as_ref().expect("").get_hash());
                    _parent_hash = DataHash::new_hv(hash::hash(&s));
                }else{
                    _parent_hash = DataHash::new_hv(hash::hash(&left_child.as_ref().expect("").get_hash()));
                }
                let parent = Leaf::new(left_child, right_child, _parent_hash);
                par.push(parent);
            }
            children = par;
            par = Vec::new();
        }
        children.pop().expect("")
    }


    pub fn new(data: Vec<DataHash>) -> MerkleTree {
        let root = Self::generate_tree(data);
        MerkleTree {
            root: root,
        }
    }

    pub fn get_root(&self) -> String {
        let s = self.root.get_hash();
        s
    }

    pub fn to_string(&self) -> String {
        let mut s = String::new();
        s.push_str(&self.root.to_string());
        s
    }

}

