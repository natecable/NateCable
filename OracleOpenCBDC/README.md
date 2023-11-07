## Oracle x OpenCBDC
- The goal of this project was to port [OpenCBDC](https://dci.mit.edu/opencbdc) to utilize an Oracle Database
- This project was done in collaboration with Zee Khan, Dan McClellan, and Zihan Wang, and was sponsored by Oracle through Lehigh University

- [OracleConnStruct](OracleConnStruct/) - A C Struct that can be utilized within OpenCDBC to send transaction data to an Oracle Database
- [Locking Shard](./locking_shard.cpp) - Contains an example of how we use the OracleConnStruct to send transaction data from shards to our Database

### [Project Repository](https://github.com/zek224/oracle-port-opencbdc-tx)
