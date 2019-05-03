python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. ./bank.proto
move bank_pb2.py ../Bank
move bank_pb2_grpc.py ../Bank