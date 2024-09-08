pipeline {
    agent any
    tools {
        jdk 'Jdk17'
        maven 'maven-3.8.6'
    }
    stages {
        stage('Git Checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'github-secret', url: 'https://github.com/Angad-Raut/authentication-service.git']])
                bat 'mvn clean install -DskipTests'
                echo 'Git Checkout Completed'
            }
        }
        stage('Code Compile') {
            steps {
                bat 'mvn clean compile'
            }
        }
        stage('Build Artifact') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }
        stage('Archive Artifacts'){
                    steps {
                        archiveArtifacts artifacts: 'target/*.war'
                    }
        }
        stage('Deploy on Tomcat') {
                     steps {
                          deploy adapters: [tomcat9(url: 'http://localhost:8085/',
                                 credentialsId: 'tomcat-credentials')],
                                 war: 'target/*.war',
                                 contextPath: 'authentication-service'
                     }
        }
        stage('Notification'){
             steps {
                   emailext(
                          subject: 'Authentication Service Microservice Deployed',
                          body: 'Authentication microservice successfully deployed on tomcat server',
                          to: 'angadraut89@gmail.com'
                   )
                   echo 'SUCCESS'
            }
        }
    }
}
