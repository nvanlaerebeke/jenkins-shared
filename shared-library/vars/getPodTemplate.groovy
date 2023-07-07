def call(List<String> containers) {
    containers.each { string ->
        println(string)
    }
    
    String yaml = getYaml(name, containers);
    println "${yaml}"
    return yaml;
}

def getYaml(List<String> containers) {
  def containersYaml = "";
  for(String container in containers) {
    if(container == "buildkit") {
      containersYaml += getBuildKitContainer();
    }
    if(container == "helm") {
      containersYaml += getHelmContainer();
    }
    if(container == "dotnet7") {
      containersYaml += getDotNet7Container();
    }
  }

    return '''apiVersion: v1
kind: Pod
metadata:
  name: JenkinsBuildPod
spec:''' +
    getVolumes() +
'''
  containers: ''' +
    containersYaml +
    //getVolumeMounts() +
    getEnvVars()
}
def getDotnet5() {
    return "";
}

def getDotnet6() {
    return "";
}

def getRpmBuild() {
    return "";
}

def getPhp() {
    return "";
}
def getBuildKitContainer() {
  return '''
  - name: buildkit
    image: moby/buildkit:master
    readinessProbe:
      exec:
        command:
          - buildctl
          - debug
          - workers
      initialDelaySeconds: 5
      periodSeconds: 30
    livenessProbe:
      exec:
        command:
          - buildctl
          - debug
          - workers
      initialDelaySeconds: 5
      periodSeconds: 30
    securityContext:
      privileged: true  
'''
}

def getHelmContainer() {
  return '''
  - name: helm
    image: alpine/helm
    command: ["bash", "-c"]
    args:
      - "sleep infinity"
    readinessProbe:
      exec:
        command:
          - helm
          - version
      initialDelaySeconds: 5
      periodSeconds: 30
    livenessProbe:
      exec:
        command:
          - helm
          - version
      initialDelaySeconds: 5
      periodSeconds: 30
    securityContext:
      privileged: false
'''
}

def getDotNet7Container() {
  return '''
  - name: dotnet7
    image: moby/buildkit:master
    readinessProbe:
      exec:
        command:
          - buildctl
          - debug
          - workers
      initialDelaySeconds: 5
      periodSeconds: 30
    livenessProbe:
      exec:
        command:
          - buildctl
          - debug
          - workers
      initialDelaySeconds: 5
      periodSeconds: 30
    securityContext:
      privileged: true  
'''
}

def getVolumes() {
  return '''
  volumes:
    - name: buildkit-cache
      nfs: 
        server: nas.crazyzone.be 
        path: /volume1/docker-storage/jenkins-cache/
'''
}

def getVolumeMounts() {
  return '''
    volumeMounts:
    - name: buildkit-cache
      mountPath: /var/lib/buildkit
'''
}

def getEnvVars() {
  return '''
    env:
    - name: COMPOSER_HOME
      value: /root/.composer/
'''
}