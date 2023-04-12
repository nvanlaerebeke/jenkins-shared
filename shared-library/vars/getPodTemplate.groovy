def call(String template) {
  def containers = [];
    String yaml = ""

    switch(template) {
        case "buildkit_with_helm":
          containers.add("helm");
        case 'buildkit':
          containers.add("buildkit");
          break;
        case 'dotnet5':
            break;
        case 'dotnet6':
            break;
        case 'dotnet7':
            break;
        case 'rpmbuild':
            break;
        case 'php':
            break;
        default:
          throw new Exception("'${container}' is not a known container type")
    }

    yaml = getYaml(template, containers);
    println "${yaml}"
    return yaml;
}

def getYaml(String name, List<String> containers) {
  def containersYaml = "";
  for(String container in containers) {
    if(container == "buildkit") {
      containersYaml += getBuildKitContainer();
    }
    if(container == "helm") {
      containersYaml += getHelmContainer();
    }
  }

    return '''apiVersion: v1
kind: Pod
metadata:
  name: ''' + name + '''
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

def getDotnet7() {
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