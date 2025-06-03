def call(Map config = [:]) {
    pipeline {
        agent any

        environment {
            DOCKER_IMAGE = "${config.dockerImage ?: 'penguintandinzangmo/my-node-app'}"
        }

        stages {
            stage('Install Dependencies') {
                steps {
                    echo 'Installing npm dependencies...'
                    sh 'npm install'
                }
            }

            stage('Run Tests') {
                steps {
                    echo 'Running tests...'
                    sh 'npm test'
                }
            }

            stage('Build Docker Image') {
                steps {
                    echo "Building Docker image ${DOCKER_IMAGE}"
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }

            stage('Push Docker Image') {
                steps {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh """
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                            docker push ${DOCKER_IMAGE}
                        """
                    }
                }
            }
        }
    }
}
