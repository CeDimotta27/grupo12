pipeline {
    agent any
    
    stages {

        stage('Git Checkout') {
            steps {
                echo "Get source code from GitHub"
                git url: 'https://github.com/CeDimotta27/grupo12.git', 
                    branch: 'main',
                    credentialsId: 'GitHubID'
            }
        }

        stage('Build') {
            steps {
                echo "Building App"
                 sh 'mvn clean package -Dmaven.test.failure.ignore=true'
                // clean elimina los archivos generados en compilaciones anteriores
                // package empaqueta el código en un archivo .jar después de compilarlo
                //-Dmaven.test.failure.ignore=true ignora los test que fallan y compila igual
            }
        }

        stage('Unit Test') {
            steps {
                echo "Executing unit Tests"
                sh 'mvn validate test'
            }
            post {
                // Registramos los errores de Maven
                success {
                    junit '**/target/surefire-reports/*.xml'
                }   
            } 
        }
        
        stage('SonarQube Analysis') {
            steps {
                echo "Executing SonarQube Analysis"
                withSonarQubeEnv('SonarQube') { 
                    sh "mvn package sonar:sonar"
                }
            }
        }

        stage('Checkout remoto'){
            agent{
                 label 'minikube'
            }
            steps{
                script{
                    echo 'Cheking out SCM Jobs Project'
                        git branch: "main", credentialsId: 'GitHubID', url: 'https://github.com/CeDimotta27/grupo12.git'
                }
            }
        }

        stage('Docker Build'){
            agent{
                label 'minikube'
            }
            steps{
                echo 'Building Docker Image'
                dir("${env.WORKSPACE}"){
                    sh 'ls -l'
                    sh 'docker build -t appx-api:latest .'
                }
            }
        }

        stage('Docker Push'){
            agent{
                label 'minikube'
            }
            steps{
                echo 'Pushing Docker Image'
                sh '''
                docker tag appx-api:latest micaelaa45/ddsdeploy
                docker push micaelaa45/ddsdeploy
                '''
            }
        }

        stage('Restart Deployment'){
            agent{
                label 'minikube'
            }
            steps{
                sh '''
                kubectl rollout restart deployment appx-api-deployment
                '''
            }
        }

    } // Fin de stages

    post {
        always {
            // Limpia después de la ejecución
            echo 'Cleaning up...'
            cleanWs()
        }
        failure {
            // Envía notificación o realiza acciones si hay fallos
            echo 'Pipeline failed!'
        }
    }
}
