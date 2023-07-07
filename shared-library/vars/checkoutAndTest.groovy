def call(String repository, String credentialsId, String name, String registry, String branch, registry_secret_file) {
  checkout scm: [ 
    $class: 'GitSCM', \
    branches: [[ name: '*/' + branch]],  \
    extensions: [
      [ $class: 'RelativeTargetDirectory', relativeTargetDir: name ],
      [ $class: 'CleanCheckout']
    ],
    userRemoteConfigs: [[ credentialsId: credentialsId , url: repository ]]
  ]
  sh getTestScript(name)
}
