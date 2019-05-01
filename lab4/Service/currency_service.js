const grpc = require('grpc')
const protoLoader = require('@grpc/proto-loader')
const currency = require('../Utils/currency')
const parallel = require('run-parallel')

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

let currencyTable = currency.map(currency => {
  return {
    type: currency,
    value: Math.random() * 5 + 1,
  }
})

const sleep = (ms) => {
  return new Promise(resolve => setTimeout(resolve, ms))
}

const simulateChanges = async () => {
  while (true) {
    await sleep((Math.random() * 5 + 1) * 1000)

    currencyTable = currencyTable.map(currency => {
      return {
        ...currency,
        value: currency.value + ((Math.random() < 0.5 ? -1 : 1) * currency.value * Math.random() * 0.025),
      }
    })
  }
}

const getCurrency = async (call) => {
  while (true) {
    let resCurrencyTable = []

    call.request.currency.forEach(reqCurrency => {
      const currencyValue = currencyTable.find(currency => currency.type === reqCurrency).value
      console.log('Request for: ' + reqCurrency + ': ' + currencyValue)
      resCurrencyTable = [...resCurrencyTable, {currency: reqCurrency, value: currencyValue}]
    })

    call.write({currencyTable: resCurrencyTable})

    await sleep(2000)
  }
}

const main = () => {
  const server = new grpc.Server()

  server.addService(bank_proto.CurrencyService.service, {getCurrency: getCurrency})
  server.bind('0.0.0.0:8080', grpc.ServerCredentials.createInsecure())
  server.start()

  console.log('Currency service server started')
}

parallel([
  simulateChanges,
  main,
])