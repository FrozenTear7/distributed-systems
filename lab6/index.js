const zookeeper = require('node-zookeeper-client')
const readline = require('readline')

const client = zookeeper.createClient('localhost:2181')

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    terminal: false,
})

const setWatcher = () => {
    const path = '/z'

    client.exists(
        path,
        (event) => {
            if (event.getName() === 'NODE_CREATED') {
                console.log('Odpal cos xd')
            } else if (event.getName() === 'NODE_DELETED') {
                console.log('Zatrzymaj cos xd')
            } else if (event.getName() === 'NODE_CHILDREN_CHANGED') {
                showNodes()
            }
        },
        () => {
        },
    )
}

const showNodes = () => {
    const path = '/z'

    client.getChildren(path, null, (error, children, stats) => {
        if (error) {
            console.log('Failed to fetch children of %s due to: %s', path, error)
            return
        }

        console.log('%s Children of /z are: %j.', children.length, children)
    })
}

const createZNode = () => {
    const path = '/z'

    client.create(path, (error) => {
        if (error) {
            console.log('Failed to create node: %s due to: %s', path, error)
        } else {
            console.log('Node: %s is successfully created', path)
        }
    })
}

const createZChild = (childPath) => {
    const path = '/z/' + childPath

    client.create(path, (error) => {
        if (error) {
            console.log('Failed to create node: %s due to: %s', path, error)
        } else {
            console.log('Node: %s is successfully created', path)
        }
    })
}

const removeZNode = () => {
    const path = '/z'

    client.getChildren(path, null, (error, children, stats) => {
        if (error) {
            console.log('Failed to fetch children of %s due to: %s', path, error)
            return
        }

        children.forEach(child => {
            client.remove(path + '/' + child, -1, (error) => {
                if (error) {
                    console.log('Failed to remove node: %s due to: %s', path + '/' + child, error)
                } else {
                    console.log('Node: %s is successfully removed', path + '/' + child)
                }
            })
        })

        client.remove(path, -1, (error) => {
            if (error) {
                console.log('Failed to remove node: %s due to: %s', path, error)
            } else {
                console.log('Node: %s is successfully removed', path)
            }
        })
    })
}

client.once('connected', () => {
    console.log('Connected to the server.')
    console.log(`
        1 - show 'z' tree structure
        2 - create 'z' node
        3 - remove 'z' node
        4 'path' - create 'path' node as a child of 'z'
        q - quit
    `)

    rl.on('line', (line) => {
        if (line.startsWith('1')) {
            showNodes()
        } else if (line.startsWith('2')) {
            createZNode()
        } else if (line.startsWith('3')) {
            removeZNode()
        } else if (line.startsWith('4')) {
            const path = line.split(' ')[1]

            if (!path) {
                console.log('Please provide valid arguments')
            } else {
                createZChild(path)
            }
        } else if (line.startsWith('q')) {
            client.close()
            process.exit()
        } else {
            console.log('Please provide valid arguments')
        }
    })
})

client.connect()
