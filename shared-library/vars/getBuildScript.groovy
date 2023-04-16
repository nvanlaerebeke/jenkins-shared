def call(String name, String registry, String branch, String registry_secret, String version) {
    String buildScript = '#!/bin/sh\n'
    buildScript += "cd '$name' \n"
    buildScript += "VERSION=`echo '" + version + "' | tr -d '\\n'` \n"
    buildScript += 'TAG=$GIT_BRANCH\n'
    buildScript += 'if [ "$GIT_BRANCH" == "master" ]; then \n'
    buildScript += "    TAG=latest\n"
    buildScript += "fi\n\n"

    buildScript += 'if [ "$GIT_BRANCH" == "main" ]; then \n'
    buildScript += "    TAG=latest\n"
    buildScript += "fi\n\n"

    buildScript += "mkdir -p ~/.docker \n"
    buildScript += "/bin/cp -f ${registry_secret} ~/.docker/config.json \n"
    
    buildScript += "buildctl build --frontend dockerfile.v0 --local context=. --local dockerfile=. --export-cache type=inline "
    buildScript += "--import-cache type=registry,ref=${registry}/${name} "
    buildScript += '--output type=image,\\"name=' + registry + '/' + name + ':$VERSION,' + registry + '/' + name + ':$TAG\\",push=true \n\n'
        
    println "${buildScript}"
    return buildScript
}
