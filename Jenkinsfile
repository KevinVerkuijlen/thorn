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
        stage('Sonarqube') {
            environment {
                scannerHome = tool 'SonarQubeScanner'
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "${scannerHome}/bin/sonar-scanner"
                }
                timeout(time: 10, unit: 'MINUTES') {
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
    }
}
