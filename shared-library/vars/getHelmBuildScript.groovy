def call(String name, String registry, String registry_secret) {
    String buildScript = '#!/bin/sh\n'
    buildScript += "cd '$name' \n"

    buildScript += "mkdir -p ~/.docker \n"
    buildScript += "/bin/cp -f '${registry_secret}' ~/.docker/config.json \n"

    buildScript += "helm package ./chart \n"
    buildScript += "helm push *.tgz oci://${registry}"

    println "${buildScript}"
    return buildScript
}
