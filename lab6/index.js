const zookeeper = require('node-zookeeper-client')
const readline = require('readline')

const client = zookeeper.createClient('localhost:2181')

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    terminal: false,
})

const setWatcher1 = () => {
    const path = '/z'

    client.exists(
        path,
        (event) => {
            if (event.getName() === 'NODE_CREATED') {
                console.log('_______________________________________')
                setWatchers()
            }
        },
        () => {
        },
    )
}


const setWatcher2 = () => {
    const path = '/z'

    client.exists(
        path,
        (event) => {
            if (event.getName() === 'NODE_CREATED') {
                console.log('_______________________________________')
                setWatchers()
            }
        },
        () => {
        },
    )

    client.getChildren(
        path,
        (event) => {
            if (event.getName() === 'NODE_CHILDREN_CHANGED') {
                setWatchers()
                showNodes()
            }
            else if (event.getName() === 'NODE_DELETED') {
                console.log('++++++++++++++++++++++++++++++++++++++++++')
                setWatchers()
            }
        },
        () => {
        },
    )
}


const setWatcher3 = () => {
    const path = '/z'

    client.exists(
        path,
        (event) => {
            if (event.getName() === 'NODE_CREATED') {
                console.log('_______________________________________')
                setWatchers()
            }
        },
        () => {
        },
    )

    client.getChildren(
        path,
        (event) => {
            if (event.getName() === 'NODE_CHILDREN_CHANGED') {
                setWatchers()
                showNodes()
            }
            else if (event.getName() === 'NODE_DELETED') {
                console.log('++++++++++++++++++++++++++++++++++++++++++')
                setWatchers()
            }
        },
        () => {
        },
    )
}


const setWatchers = () => {
    const path = '/z'

    client.exists(
        path,
        (event) => {
            if (event.getName() === 'NODE_CREATED') {
                console.log('_______________________________________')
                setWatchers()
            }
        },
        () => {
        },
    )

    client.getChildren(
        path,
        (event) => {
            if (event.getName() === 'NODE_CHILDREN_CHANGED') {
                setWatchers()
                showNodes()
            }
            else if (event.getName() === 'NODE_DELETED') {
                console.log('++++++++++++++++++++++++++++++++++++++++++')
                setWatchers()
            }
        },
        () => {
        },
    )
}

const showNodes = () => {
    const path = '/z'

    client.getChildren(path,
        () => {
        },
        (error, children) => {
            if (!error) {
                console.log('%s Children: %j\n', children.length, children)
                // setWatcher2()
            }
        })
}

const createZNode = () => {
    const path = '/z'

    client.create(path, (error) => {
        if (error) {
            // console.log('Node /z already exists\n')
        } else {
            // console.log('Node %s successfully created\n', path)
            // setWatcher2()
        }
    })
}

const createZChild = (childPath) => {
    const path = '/z/' + childPath

    client.create(path, (error) => {
        if (error) {
            // console.log('Could not add %s', path)
        } else {
            // console.log('Node %s successfully created\n', path)
            // setWatcher2()
        }
    })
}

const removeZNode = () => {
    const path = '/z'

    client.exists(
        path,
        (event) => {
            if (event.getName() === 'NODE_DELETED') {
                console.log('++++++++++++++++++++++++++++++++++++++++++')
                // setWatchers()
            }
        },
        () => {
            client.getChildren(path,
                () => {
                },
                (error, children) => {
                    if (!error)
                        children.forEach(child => {
                            client.remove(path + '/' + child, -1, (error) => {
                                if (error) {
                                    // console.log('Failed to remove node %s', path + '/' + child + '\n')
                                }
                            })
                        })

                    client.remove(path, -1, (error) => {
                        if (error) {
                            // console.log('Node /z does not exist\n')
                        } else {
                            // console.log('Nodes successfully removed\n')
                            // setWatcher1()
                        }
                    })
                })
        },
    )
}

client.once('connected', () => {
    console.log('Connected to the server.')
    // setWatcher1()
    // setWatcher2()
    setWatchers()
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
                console.log('Please provide valid arguments\n')
            } else {
                createZChild(path)
            }
        } else if (line.startsWith('q')) {
            client.close()
            process.exit()
        } else {
            console.log('Please provide valid arguments\n')
        }
    })
})

client.connect()
