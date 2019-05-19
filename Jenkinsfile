pipeline {
    agent any
    tools{
        jdk '11'
    }
    options {
        skipStagesAfterUnstable()
    }

    stages {
        stage('Cleanup'){
            steps{
                sh '''
                docker rmi $(docker images -f 'dangling=true' -q) || true
                docker rmi $(docker images | sed 1,2d | awk '{print $3}') || true
                '''
            }
         }
        stage('Test') {
            steps {
                sh 'mvn -f ./pom.xml test'
            }
        }
        stage("build & SonarQube analysis") {
              agent any
              steps {
                withSonarQubeEnv('SonarQube') {
                  sh 'mvn clean package sonar:sonar'
                }
              }
        }
        stage("Quality Gate") {
          steps {
            timeout(time: 1, unit: 'HOURS') {
              waitForQualityGate abortPipeline: true
            }
          }
        }
        stage('Package') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker build . -t kevinverkuijlenfontys/thorntail-example:test'
            }
        }
        stage('Docker publish') {
            steps {
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId:'dockerhub', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                  sh 'docker login -u $USERNAME -p $PASSWORD'
                  sh 'docker push kevinverkuijlenfontys/thorntail-example:test'
                  sh 'docker logout'
                }
            }
        }
        stage('GCP Kubernetes') {
             steps {
                 withCredentials([file(credentialsId: 'gke-rekeningrijden', variable: 'JENKINSGCLOUDCREDENTIAL')])
                         {
                         sh '''
                             pwd
                             gcloud auth activate-service-account --key-file=${JENKINSGCLOUDCREDENTIAL}
                             gcloud config set compute/zone europe-west1-b
                             gcloud config set project rekeningrijden-project
                             gcloud container clusters get-credentials test-cluster

                             kubectl apply --force=true --all=true --record=true -f ./k8s/
                             kubectl rollout status --watch=true --v=8 -f ./k8s/deployment.yaml
                             gcloud auth revoke --all
                             '''
                         }
             }

        }
        stage('jfrog'){
            steps {
            def server = Artifactory.newServer url: 'https://myartifactory:8081', credentialsId: 'jfrog-login'
            def rtMaven = Artifactory.newMavenBuild()
            rtMaven.resolver server: server, releaseRepo: 'libs-release', snapshotRepo: 'libs-snapshot'
            rtMaven.deployer server: server, releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local'
            // Optionally include or exclude artifacts to be deployed to Artifactory:
            rtMaven.deployer.artifactDeploymentPatterns.addExclude("*.zip")
            // Set a Maven Tool defined in Jenkins "Manage":
            rtMaven.tool = MAVEN_TOOL
            // Optionally set Maven Ops
            rtMaven.opts = '-Xms1024m -Xmx4096m'
            // Run Maven:
            def buildInfo = rtMaven.run pom: 'maven-example/pom.xml', goals: 'clean install'
            // Alternatively, you can pass an existing build-info instance to the run method:
            // rtMaven.run pom: './pom.xml', goals: 'clean install', buildInfo: buildInfo

            // Publish the build-info to Artifactory:
            server.publishBuildInfo buildInfo
            }
        }
    }
}
