python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. ./bank.proto

cd ../Service
npm install