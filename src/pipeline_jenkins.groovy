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
                sh 'mvn clean package'
                // clean elimina los archivos generados en compilaciones anteriores
                // package empaqueta el código en un archivo .jar después de compilarlo
            }
        }

        stage('Unit Test') {
            steps {
                echo "Executing unit Tests"
                sh 'mvn compile validate test -Dmaben.test.failure.ignore=true'
            }
            post {
                // Registramos los errores de Maven
                success {
                    junit '*/target/surefire-reports/TEST-.xml'
                    archiveArtifacts 'target/.jar'
                }
            } 
        }
        /
        stage('SonarQube Analysis') {
            steps {
                echo "Executing Sonar Analysis"
                def scannerHome = tool 'Sonar Scanner 6.2'
                withSonarQubeEnv('SonarQube') {
                    sh "mvn package sonar:sonar -Dsonar.projectKey=squ_d0357d000a0cf03c941914b2b2759ac4fb157991"
                }
            }
        }
        */
        // Minikube stage pendiente

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
