const grpc = require('grpc')
const protoLoader = require('@grpc/proto-loader')

const PROTO_PATH = '../Bank/bank.proto'

const packageDefinition = protoLoader.loadSync(PROTO_PATH, {
    keepCase: true,
    longs: String,
    enums: String,
    defaults: true,
    oneofs: true,
  },
)

const bank_proto = grpc.loadPackageDefinition(packageDefinition).bank

const main = () => {
  const client = new bank_proto.CurrencyService('localhost:8080', grpc.credentials.createInsecure())

  const call = client.getCurrency({currency: ['EURO', 'USD']})

  call.on('data', (currencyTable) => {
    console.log(currencyTable)
  })

  call.on('end', () => {
    console.log('End')
  })
}

main()
