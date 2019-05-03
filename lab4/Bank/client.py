import sys
import traceback

import Ice

import Demo

Ice.loadSlice('bank.ice')

try:
    communicator = Ice.initialize()
    bank = Demo.HelloPrx.checkedCast(communicator.stringToProxy("bank:default -h localhost -p " + sys.argv[1]))
    bank.sayHello()
    communicator.destroy()
except:
    traceback.print_exc()
    sys.exit(1)
