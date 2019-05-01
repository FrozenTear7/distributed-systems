const grpc = require('grpc')
const protoLoader = require('@grpc/proto-loader')

const PROTO_PATH = __dirname + '/bank.proto'

const packageDefinition = protoLoader.loadSync(
  PROTO_PATH,
  {
    keepCase: true,
    longs: String,
    enums: String,
    defaults: true,
    oneofs: true,
  })

const bank_proto = grpc.loadPackageDefinition(packageDefinition).bank

const sayHello = (call, callback) => {
  callback(null, {message: 'Hello ' + call.request.name})
}

const main = () => {
  const server = new grpc.Server()
  server.addService(bank_proto.Greeter.service, {sayHello: sayHello})
  server.bind('0.0.0.0:8080', grpc.ServerCredentials.createInsecure())
  server.start()
}

main()
