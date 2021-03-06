const zookeeper = require('node-zookeeper-client')
const readline = require('readline')
const {exec} = require('child_process')

const client = zookeeper.createClient('localhost:2181,locahost:2182,localhost:2183')

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    terminal: false,
})

const setEpicj = () => {
    const path = '/z'

    client.exists(
        path,
        (event) => {
            if (event.getName() === 'NODE_CREATED') {
                exec('calc', () => {
                })
                setEpicj2()
            }
        },
        () => {
        },
    )
}

const setEpicj2 = () => {
    const path = '/z'

    client.getChildren(
        path,
        (event) => {
            if (event.getName() === 'NODE_CHILDREN_CHANGED') {
                showNodes()
                setEpicj2()
            }
        },
        () => {
        },
    )
}

const setWatcher1 = () => {
    const path = '/z'

    client.exists(
        path,
        (event) => {
            if (event.getName() === 'NODE_CREATED') {
                exec('calc', () => {
                })
                setWatcher2()
            }
        },
        () => {
        },
    )
}

const setWatcher2 = () => {
    const path = '/z'

    client.getChildren(
        path,
        (event) => {
            if (event.getName() === 'NODE_CHILDREN_CHANGED') {
                showNodes()
                setWatcher1()
                setWatcher2()
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
                console.log('%s Children: %j', children.length, children)
            }
        })
}

const createZNode = () => {
    const path = '/z'

    client.create(path, () => {
    })
}

const createZChild = (childPath) => {
    const path = '/z/' + childPath

    client.create(path, () => {
    })
}

const removeZNode = () => {
    const path = '/z'

    client.exists(
        path,
        (event) => {
            setEpicj()
            if (event.getName() === 'NODE_DELETED') {
                exec('taskkill /f /im calculator.exe', () => {
                })
            }
        },
        () => {
            client.getChildren(path,
                () => {
                },
                (error, children) => {
                    if (!error)
                        children.forEach(child => {
                            client.remove(path + '/' + child, -1, () => {
                            })
                        })

                    client.remove(path, -1, () => {
                    })
                })
        },
    )
}

client.once('connected', () => {
    console.log('Connected to the server.')
    setWatcher1()
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
