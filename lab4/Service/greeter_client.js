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

const main = () => {
  const client = new bank_proto.Greeter('localhost:8080', grpc.credentials.createInsecure())
  let user
  if (process.argv.length >= 3) {
    user = process.argv[2]
  } else {
    user = 'world'
  }

  client.sayHello({name: user}, (err, response) => {
    console.log('Greeting:', response.message)
  })
}

main()
