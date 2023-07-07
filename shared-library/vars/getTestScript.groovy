def call(String name) {
    String buildScript = '#!/bin/sh\n'
    buildScript += "cd '$name' \n"
    buildScript += "echo 'PARTY TIME!!!' \n"
    buildScript += "ls -lah \n"       
    println "${buildScript}"
    return buildScript
}
