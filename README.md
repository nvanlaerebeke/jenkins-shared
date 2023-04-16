# Jenkins Shared Library

Shared library for the Jenkins instance running for `Crazyzone`

Contains:

- getPodTemplate: gets the template for the k8s pod that does the build
- checkoutAndBuild: checks out the source and builds + pushes a Dockerfile
- getBuildScript: gets the logic used to build the docker image using `buildkit`
- getHelmBuildScript: gets the build script to package and push a helm chart

## GetPodTemplate

This method returns a `yaml` of a kubernetes pod description that matches the kind of build being done, currently supports:

- buildkit: to build and push images
- buildkit_with_helm: to build, push both docker files and helm packages

## checkoutAndBuild

When building a `Dockerfile` this returns the command line code to:

- Checkout the source
- Build the container and push it to the registry

## getBuildScript

Used by steps that need to build & push a container image.  

## getHelmBuildScript

Used by steps that need to create a helm package and push it to the `oci` registry.

## Jenkins Configuration

The shared library and credentials need to be added in Jenkins.  

To add this shared library, see [here](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)

## Example Jenkinsfile

```
@Library("podTemplates") _
pipeline{
  environment {
    NAME = 'my-project'
    REPOSITORY = 'ssh://git@github.com/<user>/<project>'
    REGISTRY = "registry.example.com"
    REGISTRY_HELM = "helm.example.com"
    REPOSITORY_CREDENTIAL_ID = '<repository_jenkins_credentail_id>'
    REGISTRY_SECRET_FILE = credentials('<registry_credential_secret_id>')
    REGISTRY_SECRET_FILE_HELM = credentials('<helm_registry_credential_secret_id>')
    
  }
  parameters { booleanParam(name: 'FORCE_PUSH', defaultValue: false, description: 'Will force the publishing of the packages to the registry & repository') }
  agent {
    kubernetes {
      yaml getPodTemplate("buildkit_with_helm")
    }
  }
  stages {
    stage('build') {
      when { anyOf {
        branch "master";
        branch "stable";
        branch "dev";
        expression{params.FORCE_PUSH == true }
      } }
      steps {
        container(name: "buildkit", shell: '/bin/sh') {
          checkoutAndBuild(env.REPOSITORY, env.REPOSITORY_CREDENTIAL_ID, env.NAME, env.REGISTRY, env.BRANCH_NAME, env.REGISTRY_SECRET_FILE)
        }
        container(name: "helm", shell: '/bin/sh') {
          sh getHelmBuildScript(env.NAME, env.REGISTRY_HELM, REGISTRY_SECRET_FILE_HELM)
        }
      }
    }
  }
}
```